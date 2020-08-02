package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.main.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.main.utils.HttpClientUtils;
import com.nicobrest.kamehouse.main.utils.JsonUtils;
import com.nicobrest.kamehouse.main.utils.StringUtils;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingRequest.CardDetails;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingResponse.Status;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service to execute tennis world bookings.
 *
 * @author nbrest
 */
public class TennisWorldBookingService {

  // URLs
  public static final String ROOT_URL = "https://bookings.tennisworld.net.au";
  private static final String INITIAL_LOGIN_URL = ROOT_URL + "/customer/mobile/login";
  private static final String SITE_LINK_HREF = "/customer/mobile/login/complete_login/";
  private static final String DASHBOARD_URL = ROOT_URL + "/customer/mobile/dashboard";
  private static final String BOOK_OVERLAY_AJAX_URL = ROOT_URL + "/customer/mobile/facility"
      + "/book_overlay_ajax";
  private static final String CONFIRM_BOOKING_URL = ROOT_URL + "/customer/mobile/facility/confirm";
  // Headers
  private static final String LOCATION = "Location";
  private static final String REFERER = "Referer";
  private static final String X_REQUESTED_WITH = "X-Requested-With";
  private static final String XML_HTTP_REQUEST = "XMLHttpRequest";
  // HTML Attributes
  private static final String ATTR_DATA_ROLE = "data-role";
  private static final String ATTR_SITE_FACILITYGROUP_ID = "site_facilitygroup_id";
  private static final String ATTR_BOOKING_TIME = "booking_time";
  private static final String ATTR_EVENT_DURATION = "event_duration";
  // HTML Ids
  private static final String ID_BOOK_NOW_OVERLAY_FACILITY = "book_now_overlayfacility";
  private static final String ID_ERROR_STACK = "error-stack";
  private static final String ID_ERROR_MESSAGE = "error-message";
  // Other constants
  private static int sleepMs = 500;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Set the sleep ms between requests.
   */
  public static void setSleepMs(int sleepMs) {
    TennisWorldBookingService.sleepMs = sleepMs;
  }

  public TennisWorldBookingResponse book(TennisWorldBookingRequest tennisWorldBookingRequest) {
    return bookFacilityOverlayRequest(tennisWorldBookingRequest);
  }

  /**
   * Handle a tennis world booking for facility overlay bookings. Currently those are session types:
   * <ul>
   *  <li>National Tennis Outdoor</li>
   *  <li>NTC Clay Courts</li>
   *  <li>Rod Laver Arena Outdoor</li>
   *  <li>Rod Laver Arena Show Courts</li>
   * </ul>
   * The current process to complete a facility overlay booking is:
   * <ul>
   * <li>1.1) POST Initial login request</li>
   * <li>1.2) GET Complete login - show all sites</li>
   * <li>1.3) GET Complete login in specific site (ej. Melbourne Park)</li>
   * <li>2) GET Specific session type page</li>
   * <li>3) GET Specific session date page</li>
   * <li>4) GET Specific session page (session type, date and time)</li>
   * <li>5) POST Load book_overlay_ajax. Check if there's any errors in the response</li>
   * <li>6) GET Confirm booking url to submit the final booking request</li>
   * <li>
   *   7) POST Confirm booking. Send the final booking request (with payment details when required)
   * </li>
   * <li>8) GET Confirm booking url. Check the final result of the booking request</li>
   * </ul>
   */
  private TennisWorldBookingResponse bookFacilityOverlayRequest(
      TennisWorldBookingRequest tennisWorldBookingRequest) {
    HttpClient httpClient = HttpClientUtils.getClient(null, null);
    try {
      // 1
      Document dashboard = loginToTennisWorld(httpClient, tennisWorldBookingRequest);
      String selectedSessionTypeId = getSessionTypeId(dashboard,
          tennisWorldBookingRequest.getSessionType());
      // 2
      Document sessionTypePage = getSessionTypePage(httpClient, selectedSessionTypeId);
      String selectedSessionDatePath = getSelectedSessionDatePath(sessionTypePage,
          selectedSessionTypeId, tennisWorldBookingRequest.getDate());
      // 3
      Document sessionDatePage = getSessionDatePage(httpClient, selectedSessionDatePath);
      String selectedSessionPath = getSelectedSessionPath(sessionDatePage,
          tennisWorldBookingRequest.getTime());
      // 4
      Document sessionPage = getSessionPage(httpClient, selectedSessionPath);
      String siteFacilityGroupId = getSiteFacilityGroupId(sessionPage);
      String bookingTime = getBookingTime(sessionPage);
      // 5
      postBookOverlayAjax(httpClient, selectedSessionDatePath, siteFacilityGroupId, bookingTime,
          tennisWorldBookingRequest.getDuration());
      // 6
      getConfirmBookingUrl(httpClient, selectedSessionDatePath);
      if (!tennisWorldBookingRequest.isDryRun()) {
        // 7
        String confirmBookingRedirectUrl = postBookingRequest(httpClient,
            tennisWorldBookingRequest.getCardDetails());
        // 8
        confirmBookingResult(httpClient, confirmBookingRedirectUrl);
        return buildResponse(Status.SUCCESS, "Completed the booking request successfully");
      } else {
        return buildResponse(Status.SUCCESS, "Completed the booking request DRY-RUN successfully");
      }
    } catch (KameHouseBadRequestException e) {
      return buildResponse(Status.ERROR, e.getMessage());
    } catch (KameHouseServerErrorException e) {
      return buildResponse(Status.INTERNAL_ERROR, e.getMessage());
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
      return buildResponse(Status.INTERNAL_ERROR, "Error executing booking request to tennis "
          + "world Message: " + e.getMessage());
    }
  }

  /**
   * Steps 1.1, 1.2 and 1.3.
   * Attempts to login to tennis world using the specified site and credentials in the
   * TennisWorldBookingRequest and returns the dashboard page if the login is successful.
   */
  private Document loginToTennisWorld(HttpClient httpClient,
                                      TennisWorldBookingRequest tennisWorldBookingRequest)
      throws IOException {
    // 1.1
    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("username", tennisWorldBookingRequest.getUsername()));
    params.add(new BasicNameValuePair("password", tennisWorldBookingRequest.getPassword()));

    HttpPost initialLoginPostRequest = new HttpPost(INITIAL_LOGIN_URL);
    initialLoginPostRequest.setEntity(new UrlEncodedFormEntity(params));
    logger.info("Step 1.1: POST request to: {}", INITIAL_LOGIN_URL);
    HttpResponse initialLoginPostResponse =
        HttpClientUtils.execRequest(httpClient, initialLoginPostRequest);
    logHttpResponseCode(initialLoginPostResponse);
    String initialLoginHtml =
        IOUtils.toString(HttpClientUtils.getInputStream(initialLoginPostResponse));
    logger.debug(initialLoginHtml);
    if (HttpClientUtils.getStatusCode(initialLoginPostResponse) != HttpStatus.FOUND.value()
        || hasError(Jsoup.parse(initialLoginHtml))) {
      throw new KameHouseBadRequestException("Invalid login to tennis world");
    }
    // 1.2
    sleep();
    String completeLoginUrl = HttpClientUtils.getHeader(initialLoginPostResponse, LOCATION);
    HttpGet completeLoginAllSitesGetRequest = HttpClientUtils.httpGet(completeLoginUrl);
    logger.info("Step 1.2: GET request to: {}", completeLoginUrl);
    logRequestHeaders(completeLoginAllSitesGetRequest);
    HttpResponse completeLoginAllSitesGetResponse =
        HttpClientUtils.execRequest(httpClient, completeLoginAllSitesGetRequest);
    logHttpResponseCode(completeLoginAllSitesGetResponse);
    String completeLoginAllSitesResponseHtml = IOUtils.toString(
            HttpClientUtils.getInputStream(completeLoginAllSitesGetResponse));
    logger.debug(completeLoginAllSitesResponseHtml);
    Document completeLoginAllSitesResponsePage =
        Jsoup.parse(completeLoginAllSitesResponseHtml);
    Elements tennisWorldSiteLinks = completeLoginAllSitesResponsePage
        .getElementsByAttributeValueMatching("href", SITE_LINK_HREF);
    String selectedSiteId = null;
    for (Element tennisWorldSite : tennisWorldSiteLinks) {
      String siteName = tennisWorldSite.getElementsByTag("p").text();
      String siteId = tennisWorldSite.attr("href").substring(SITE_LINK_HREF.length());
      logger.debug("siteName:" + siteName + "; siteId:" + siteId);
      if (siteName != null && siteName.equalsIgnoreCase(tennisWorldBookingRequest.getSite())) {
        selectedSiteId = siteId;
      }
    }
    if (hasError(completeLoginAllSitesResponsePage) || selectedSiteId == null) {
      throw new KameHouseBadRequestException("Unable to determine the site id for "
          + tennisWorldBookingRequest.getSite());
    }
    // 1.3
    sleep();
    String completeLoginSelectedSiteUrl = completeLoginUrl + "/" + selectedSiteId;
    HttpGet completeLoginSelectedSiteGetRequest =
        HttpClientUtils.httpGet(completeLoginSelectedSiteUrl);
    logger.info("Step 1.3: GET request to: {}", completeLoginSelectedSiteUrl);
    logRequestHeaders(completeLoginSelectedSiteGetRequest);
    HttpResponse completeLoginSelectedSiteResponse =
        HttpClientUtils.execRequest(httpClient, completeLoginSelectedSiteGetRequest);
    logHttpResponseCode(completeLoginSelectedSiteResponse);
    String completeLoginSelectedSiteResponseHtml = IOUtils.toString(
            HttpClientUtils.getInputStream(completeLoginSelectedSiteResponse));
    logger.debug(completeLoginSelectedSiteResponseHtml);
    Document completeLoginSelectedSiteResponsePage =
        Jsoup.parse(completeLoginSelectedSiteResponseHtml);
    if (hasError(completeLoginSelectedSiteResponsePage)
        || completeLoginSelectedSiteResponsePage == null) {
      throw new KameHouseServerErrorException("Unable to complete login to siteId "
          + selectedSiteId);
    }
    return completeLoginSelectedSiteResponsePage;
  }

  /**
   * Get the session type id from the specified session type.
   */
  private String getSessionTypeId(Document dashboard, String sessionType) {
    String selectedSessionTypeId = null;
    Elements sessionTypes = dashboard.getElementsByAttributeValue(ATTR_DATA_ROLE, "page");
    for (Element sessionTypeElement : sessionTypes) {
      String sessionTypeName = sessionTypeElement.getElementsByTag("h1").text();
      String sessionTypeId = sessionTypeElement.attr("id");
      logger.debug("sessionTypeName:" + sessionTypeName + "; sessionTypeId:" + sessionTypeId);
      if (sessionTypeName != null
          && sessionTypeName.equalsIgnoreCase(sessionType)) {
        selectedSessionTypeId = sessionTypeId;
      }
    }
    logger.debug("selectedSessionTypeId:" + selectedSessionTypeId);
    if (selectedSessionTypeId == null) {
      throw new KameHouseBadRequestException("Unable to get the selectedSessionTypeId");
    }
    return selectedSessionTypeId;
  }

  /**
   * Step 2. Get the session type page.
   */
  private Document getSessionTypePage(HttpClient httpClient, String selectedSessionTypeId)
      throws IOException {
    String selectedSessionTypeUrl = DASHBOARD_URL + "#" +  selectedSessionTypeId;
    HttpGet httpGet = HttpClientUtils.httpGet(selectedSessionTypeUrl);
    logger.info("Step 2: GET request to: {}", selectedSessionTypeUrl);
    logRequestHeaders(httpGet);
    HttpResponse httpResponse = HttpClientUtils.execRequest(httpClient, httpGet);
    logHttpResponseCode(httpResponse);
    String html = IOUtils.toString(HttpClientUtils.getInputStream(httpResponse));
    logger.debug(html);
    Document sessionTypePage = Jsoup.parse(html);
    if (hasError(sessionTypePage) || sessionTypePage == null) {
      throw new KameHouseServerErrorException("Error getting session type page");
    }
    return sessionTypePage;
  }

  /**
   * Get the selected session date path from the session type page returned from tennis world.
   */
  private String getSelectedSessionDatePath(Document sessionTypePage, String selectedSessionTypeId,
                                            String date) {
    Elements sessionsForTheSelectedSessionType =
        sessionTypePage.getElementById(selectedSessionTypeId).getElementsByTag("a");
    String selectedSessionDatePath = null;
    for (Element sessionForTheSelectedSessionType : sessionsForTheSelectedSessionType) {
      String href = sessionForTheSelectedSessionType.attr("href");
      logger.debug("SessionDatePath:" + href);
      if (href != null && href.contains(date)) {
        selectedSessionDatePath = href;
      }
    }
    logger.debug("selectedSessionDatePath:" + selectedSessionDatePath);
    if (selectedSessionDatePath == null) {
      throw new KameHouseBadRequestException("Error getting the selectedSessionDatePath");
    }
    return selectedSessionDatePath;
  }

  /**
   * Step 3. Get the selected session date page.
   */
  private Document getSessionDatePage(HttpClient httpClient, String selectedSessionDatePath)
      throws IOException {
    sleep();
    String selectedSessionDateUrl = ROOT_URL + selectedSessionDatePath;
    HttpGet httpGet = HttpClientUtils.httpGet(selectedSessionDateUrl);
    logger.info("Step 3: GET request to: {}", selectedSessionDateUrl);
    logRequestHeaders(httpGet);
    HttpResponse httpResponse = HttpClientUtils.execRequest(httpClient, httpGet);
    logHttpResponseCode(httpResponse);
    String html = IOUtils.toString(HttpClientUtils.getInputStream(httpResponse));
    logger.debug(html);
    Document sessionDatePage = Jsoup.parse(html);
    if (hasError(sessionDatePage) || sessionDatePage == null) {
      throw new KameHouseServerErrorException("Error detected getting the session date page");
    }
    return sessionDatePage;
  }

  /**
   * Get the selected session path.
   */
  private String getSelectedSessionPath(Document sessionDatePage, String bookingTime) {
    Elements listView = sessionDatePage.getElementsByAttributeValue(ATTR_DATA_ROLE, "listview");
    String selectedSessionPath = null;
    if (listView != null) {
      Elements sessionsForTheSelectedSessionDate = listView.first().getElementsByTag("a");
      for (Element sessionForTheSelectedSessionDate : sessionsForTheSelectedSessionDate) {
        String sessionHref = sessionForTheSelectedSessionDate.attr("href");
        String sessionTime = sessionForTheSelectedSessionDate.text();
        logger.debug("SessionPath:time:" + sessionTime + "; href:" + sessionHref);
        if (sessionTime != null && sessionTime.equalsIgnoreCase(bookingTime)) {
          selectedSessionPath = sessionHref;
        }
      }
    }
    logger.debug("selectedSessionPath:" + selectedSessionPath);
    if (selectedSessionPath == null) {
      throw new KameHouseBadRequestException("Unable to get the selectedSessionPath");
    }
    return selectedSessionPath;
  }

  /**
   * Step 4. Get the selected session page (session type, date and time).
   */
  private Document getSessionPage(HttpClient httpClient, String selectedSessionPath)
      throws IOException {
    sleep();
    String selectedSessionUrl = ROOT_URL + selectedSessionPath;
    HttpGet httpGet = HttpClientUtils.httpGet(selectedSessionUrl);
    logger.info("Step 4: GET request to: {}", selectedSessionUrl);
    logRequestHeaders(httpGet);
    HttpResponse httpResponse = HttpClientUtils.execRequest(httpClient, httpGet);
    logHttpResponseCode(httpResponse);
    String html = IOUtils.toString(HttpClientUtils.getInputStream(httpResponse));
    logger.debug(html);
    Document sessionPage = Jsoup.parse(html);
    if (hasError(sessionPage) || sessionPage == null) {
      throw new KameHouseServerErrorException("Unable to get the session page");
    }
    return sessionPage;
  }

  /**
   * Get the site facility group id from the session page.
   */
  private String getSiteFacilityGroupId(Document sessionPage) {
    String siteFacilityGroupId = sessionPage.getElementById(ID_BOOK_NOW_OVERLAY_FACILITY)
        .attr(ATTR_SITE_FACILITYGROUP_ID);
    logger.debug("siteFacilityGroupId:" + siteFacilityGroupId);
    if (siteFacilityGroupId == null) {
      throw new KameHouseBadRequestException("Unable to get the siteFacilityGroupId");
    }
    return siteFacilityGroupId;
  }

  /**
   * Get the booking time from the session page.
   */
  private String getBookingTime(Document sessionPage) {
    String bookingTime = sessionPage.getElementById(ID_BOOK_NOW_OVERLAY_FACILITY)
        .attr(ATTR_BOOKING_TIME);
    logger.debug("bookingTime:" + bookingTime);
    if (bookingTime == null) {
      throw new KameHouseBadRequestException("Unable to get the bookingTime");
    }
    return bookingTime;
  }

  /**
   * Step 5. Post the book overlay ajax request and confirm that the response is not empty and
   * that it doesn't contain errors.
   */
  private void postBookOverlayAjax(HttpClient httpClient, String selectedSessionDatePath,
                                   String siteFacilityGroupId, String bookingTime,
                                   String bookingDuration) throws IOException {
    sleep();
    List<NameValuePair> loadSessionAjaxParams = new ArrayList<>();
    loadSessionAjaxParams.add(new BasicNameValuePair(ATTR_SITE_FACILITYGROUP_ID,
        siteFacilityGroupId));
    loadSessionAjaxParams.add(new BasicNameValuePair(ATTR_BOOKING_TIME, bookingTime));
    loadSessionAjaxParams.add(new BasicNameValuePair(ATTR_EVENT_DURATION, bookingDuration));
    HttpPost loadSessionAjaxRequestHttpPost = new HttpPost(BOOK_OVERLAY_AJAX_URL);
    loadSessionAjaxRequestHttpPost.setEntity(new UrlEncodedFormEntity(loadSessionAjaxParams));
    loadSessionAjaxRequestHttpPost.setHeader(REFERER, ROOT_URL + selectedSessionDatePath);
    loadSessionAjaxRequestHttpPost.setHeader(X_REQUESTED_WITH, XML_HTTP_REQUEST);
    logger.info("Step 5: POST request to: {}", BOOK_OVERLAY_AJAX_URL);
    logRequestHeaders(loadSessionAjaxRequestHttpPost);
    HttpResponse httpResponse = HttpClientUtils.execRequest(httpClient,
        loadSessionAjaxRequestHttpPost);
    logHttpResponseCode(httpResponse);
    String html = IOUtils.toString(HttpClientUtils.getInputStream(httpResponse));
    logger.debug(html);
    if (StringUtils.isEmpty(html) || hasError(Jsoup.parse(html))
        || JsonUtils.getBoolean(JsonUtils.toJson(html), "error")) {
      throw new KameHouseServerErrorException("Error posting book overlay ajax");
    }
  }

  /**
   * Step 6. Go to the confirm booking url.
   */
  private void getConfirmBookingUrl(HttpClient httpClient, String selectedSessionDatePath)
      throws IOException {
    sleep();
    String selectedSessionDateUrl = ROOT_URL + selectedSessionDatePath;
    HttpGet httpGet = HttpClientUtils.httpGet(CONFIRM_BOOKING_URL);
    httpGet.setHeader(REFERER, selectedSessionDateUrl);
    httpGet.setHeader(X_REQUESTED_WITH, XML_HTTP_REQUEST);
    logger.info("Step 6: GET request to: {}", CONFIRM_BOOKING_URL);
    logRequestHeaders(httpGet);
    HttpResponse httpResponse = HttpClientUtils.execRequest(httpClient, httpGet);
    logHttpResponseCode(httpResponse);
    String html = IOUtils.toString(HttpClientUtils.getInputStream(httpResponse));
    logger.debug(html);
    if (StringUtils.isEmpty(html) || hasError(Jsoup.parse(html))) {
      throw new KameHouseServerErrorException("Error getting the confirm booking page");
    }
  }

  /**
   * Step 7. Post the booking request (finally!) and check if there's any errors.
   * I expect a 302 redirect response, so if that's not the case, mark the request as error.
   */
  private String postBookingRequest(HttpClient httpClient, CardDetails cardDetails)
      throws IOException {
    sleep();
    List<NameValuePair> confirmBookingParams = new ArrayList<>();
    populateCardDetails(confirmBookingParams, cardDetails);
    confirmBookingParams.add(new BasicNameValuePair("submit", "Confirm and pay"));
    HttpPost confirmBookingHttpPost = new HttpPost(CONFIRM_BOOKING_URL);
    confirmBookingHttpPost.setEntity(new UrlEncodedFormEntity(confirmBookingParams));
    confirmBookingHttpPost.setHeader(REFERER, CONFIRM_BOOKING_URL);
    confirmBookingHttpPost.setHeader(X_REQUESTED_WITH, XML_HTTP_REQUEST);
    logger.info("Step 7: POST request to: {}", CONFIRM_BOOKING_URL);
    logRequestHeaders(confirmBookingHttpPost);
    HttpResponse httpResponse = HttpClientUtils.execRequest(httpClient, confirmBookingHttpPost);
    logHttpResponseCode(httpResponse);
    String html = IOUtils.toString(HttpClientUtils.getInputStream(httpResponse));
    logger.debug(html);
    Document postBookingResponsePage = Jsoup.parse(html);
    List<String> errors = getErrorStackMessages(postBookingResponsePage);
    if (!errors.isEmpty()) {
      throw new KameHouseServerErrorException("Error posting booking request: "
          + Arrays.toString(errors.toArray()));
    }
    if (StringUtils.isEmpty(html) || hasError(postBookingResponsePage)) {
      throw new KameHouseServerErrorException("Error posting the booking request");
    }
    if (HttpClientUtils.getStatusCode(httpResponse) != HttpStatus.FOUND.value()) {
      throw new KameHouseServerErrorException("Error posting booking request. Expected a "
          + "redirect response");
    }
    return HttpClientUtils.getHeader(httpResponse, LOCATION);
  }

  private void populateCardDetails(List<NameValuePair> params, CardDetails cardDetails) {
    if (cardDetails != null) {
      params.add(new BasicNameValuePair("name_on_card", cardDetails.getName()));
      params.add(new BasicNameValuePair("credit_card_number", cardDetails.getNumber()));
      params.add(new BasicNameValuePair("expiry_month", cardDetails.getExpiryMonth()));
      params.add(new BasicNameValuePair("expiry_year", cardDetails.getExpiryYear()));
      params.add(new BasicNameValuePair("cvv_number", cardDetails.getCvv()));
    }
  }
  /**
   * Step 8. Confirm the booking final result.
   */
  private void confirmBookingResult(HttpClient httpClient, String confirmBookingRedirectUrl)
      throws IOException {
    sleep();
    HttpGet httpGet = HttpClientUtils.httpGet(confirmBookingRedirectUrl);
    httpGet.setHeader(REFERER, CONFIRM_BOOKING_URL);
    httpGet.setHeader(X_REQUESTED_WITH, XML_HTTP_REQUEST);
    logger.info("Step 8: GET request to: {}", confirmBookingRedirectUrl);
    logRequestHeaders(httpGet);
    HttpResponse httpResponse = HttpClientUtils.execRequest(httpClient, httpGet);
    logHttpResponseCode(httpResponse);
    String html = IOUtils.toString(HttpClientUtils.getInputStream(httpResponse));
    logger.debug(html);
    Document confirmFinalBookingPage = Jsoup.parse(html);
    List<String> errors = getErrorStackMessages(confirmFinalBookingPage);
    if (!errors.isEmpty()) {
      throw new KameHouseServerErrorException("Error confirming booking result: "
            + Arrays.toString(errors.toArray()));
    }
    if (StringUtils.isEmpty(html) || hasError(confirmFinalBookingPage)) {
      throw new KameHouseServerErrorException("Errors detected confirming booking result");
    }
  }

  /**
   * Build a tennis world response with the specified status and message.
   */
  private TennisWorldBookingResponse buildResponse(Status status, String message) {
    TennisWorldBookingResponse tennisWorldBookingResponse = new TennisWorldBookingResponse();
    tennisWorldBookingResponse.setStatus(status);
    tennisWorldBookingResponse.setMessage(message);
    return tennisWorldBookingResponse;
  }

  /**
   * Log request headers.
   */
  private void logRequestHeaders(HttpRequest httpRequest) {
    logger.debug("Request headers:");
    for (Header header : httpRequest.getAllHeaders()) {
      logger.debug("{} : {}", header.getName(), header.getValue());
    }
  }

  /**
   * Log the response code received from tennis world.
   */
  private void logHttpResponseCode(HttpResponse httpResponse) {
    logger.info("{}", HttpClientUtils.getStatusLine(httpResponse));
  }

  /**
   * Returns the list of errors in the error stack or an empty list if no errors.
   */
  private List<String> getErrorStackMessages(Document page) {
    List<String> errors = new ArrayList<>();
    if (page != null
        && page.getElementById(ID_ERROR_STACK) != null) {
      Elements errorMessages = page.getElementById(ID_ERROR_STACK).getElementsByTag("p");
      if (errorMessages != null && !errorMessages.isEmpty()) {
        logger.error("The following errors were detected in the current step:");
        for (Element errorMessage : errorMessages) {
          logger.error(errorMessage.text());
          errors.add(errorMessage.text());
        }
      }
    }
    return errors;
  }

  /**
   * Checks if the specified html string contains error messages from tennis world.
   */
  private boolean hasError(Document page) {
    if (page != null && page.body() != null && page.body().outerHtml() != null) {
      String bodyHtml = page.body().outerHtml();
      if (bodyHtml.contains("An error has occured") || bodyHtml.contains("An error has occurred")) {
        Element errorMessage = page.body().getElementById(ID_ERROR_MESSAGE);
        if (errorMessage != null && errorMessage.text() != null
            && !StringUtils.isEmpty(errorMessage.text().trim())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Sleep for the specified ms by sleepMs.
   */
  private void sleep() {
    try {
      Thread.sleep(sleepMs);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
