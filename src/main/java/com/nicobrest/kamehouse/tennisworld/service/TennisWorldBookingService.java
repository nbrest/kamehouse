package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.main.exception.KameHouseException;
import com.nicobrest.kamehouse.main.utils.HttpClientUtils;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingResponse;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to execute tennis world bookings.
 *
 * @author nbrest
 */
public class TennisWorldBookingService {

  private static final String ROOT_URL = "https://bookings.tennisworld.net.au";
  private static final String INITIAL_LOGIN_URL = ROOT_URL + "/customer/mobile/login";
  private static final String SITE_LINK_HREF = "/customer/mobile/login/complete_login/";
  private static final String DASHBOARD_URL = ROOT_URL + "/customer/mobile/dashboard";
  private static final String BOOK_OVERLAY_AJAX_URL = ROOT_URL + "/customer/mobile/facility" +
      "/book_overlay_ajax";
  private static final String CONFIRM_BOOKING_URL = ROOT_URL + "/customer/mobile/facility/confirm";
  private final Logger logger = LoggerFactory.getLogger(getClass());

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
    boolean debugMode = tennisWorldBookingRequest.isDebugMode();
    HttpClient httpClient = HttpClientUtils.getClient(null, null);
    try {
      // 1
      Document dashboard = loginToTennisWorld(httpClient, tennisWorldBookingRequest, debugMode);
      String selectedSessionTypeId = getSessionTypeId(dashboard,
          tennisWorldBookingRequest.getSessionType(), debugMode);
      // 2
      Document sessionTypePage = getSessionTypePage(httpClient, selectedSessionTypeId, debugMode);
      String selectedSessionDatePath = getSelectedSessionDatePath(sessionTypePage,
          selectedSessionTypeId, tennisWorldBookingRequest.getDate(), debugMode);
      // 3
      Document sessionDatePage = getSessionDatePage(httpClient, selectedSessionDatePath, debugMode);
      String selectedSessionPath = getSelectedSessionPath(sessionDatePage,
          tennisWorldBookingRequest.getTime(), debugMode);
      // 4
      Document sessionPage = getSessionPage(httpClient, selectedSessionPath, debugMode);
      String siteFacilityGroupId = getSiteFacilityGroupId(sessionPage, debugMode);
      String bookingTime = getBookingTime(sessionPage, debugMode);
      // 5
      postBookOverlayAjax(httpClient, selectedSessionDatePath, siteFacilityGroupId, bookingTime,
          tennisWorldBookingRequest.getDuration(), debugMode);
      // 6
      getConfirmBookingUrl(httpClient, selectedSessionDatePath, debugMode);
      // 7
      String confirmBookingRedirectUrl = postBookingRequest(httpClient,
          tennisWorldBookingRequest.getCardDetails(), debugMode);
      // 8
      confirmBookingResult(httpClient, confirmBookingRedirectUrl, debugMode);
      return buildSuccessResponse();
    } catch (KameHouseException e) {
      return buildErrorResponse(e.getMessage());
    }
  }

  /**
   * Steps 1.1, 1.2 and 1.3.
   * Attempts to login to tennis world using the specified site and credentials in the
   * TennisWorldBookingRequest and returns the dashboard page if the login is successful.
   */
  private Document loginToTennisWorld(HttpClient httpClient,
                                      TennisWorldBookingRequest tennisWorldBookingRequest,
                                      boolean debugMode) {
    try {
      // 1.1
      List<NameValuePair> params = new ArrayList<>();
      params.add(new BasicNameValuePair("username", tennisWorldBookingRequest.getUsername()));
      params.add(new BasicNameValuePair("password", tennisWorldBookingRequest.getPassword()));

      HttpPost initialLoginPostRequest = new HttpPost(INITIAL_LOGIN_URL);
      initialLoginPostRequest.setEntity(new UrlEncodedFormEntity(params));
      logger.info("Step 1.1: POST request to: " + INITIAL_LOGIN_URL);
      HttpResponse initialLoginPostResponse = httpClient.execute(initialLoginPostRequest);
      logHttpResponseCode(initialLoginPostResponse);
      logHtmlResponse(IOUtils.toString(initialLoginPostResponse.getEntity().getContent()),
          debugMode);
      // 1.2
      Thread.sleep(1000);
      String completeLoginUrl = initialLoginPostResponse.getFirstHeader("Location").getValue();
      HttpGet completeLoginAllSitesGetRequest = HttpClientUtils.httpGet(completeLoginUrl);
      logger.info("Step 1.2: GET request to: " + completeLoginUrl);
      logRequestHeaders(completeLoginAllSitesGetRequest, debugMode);
      HttpResponse completeLoginAllSitesGetResponse =
          httpClient.execute(completeLoginAllSitesGetRequest);
      logHttpResponseCode(completeLoginAllSitesGetResponse);
      String completeLoginAllSitesResponseHtml =
          IOUtils.toString(completeLoginAllSitesGetResponse.getEntity().getContent());
      logHtmlResponse(completeLoginAllSitesResponseHtml, debugMode);
      Document completeLoginAllSitesResponseDocument =
          Jsoup.parse(completeLoginAllSitesResponseHtml);
      Elements tennisWorldSiteLinks = completeLoginAllSitesResponseDocument
          .getElementsByAttributeValueMatching("href", SITE_LINK_HREF);
      String selectedSiteId = null;
      for (Element tennisWorldSite : tennisWorldSiteLinks) {
        String siteName = tennisWorldSite.getElementsByTag("p").text();
        String siteId = tennisWorldSite.attr("href").substring(SITE_LINK_HREF.length());
        logDebug("siteName:" + siteName + "; siteId:" + siteId, debugMode);
        if (siteName != null && siteName.equalsIgnoreCase(tennisWorldBookingRequest.getSite())) {
          selectedSiteId = siteId;
        }
      }
      // 1.3
      Thread.sleep(1000);
      String completeLoginSelectedSiteUrl = completeLoginUrl + "/" + selectedSiteId;
      HttpGet completeLoginSelectedSiteGetRequest =
          HttpClientUtils.httpGet(completeLoginSelectedSiteUrl);
      logger.info("Step 1.3: GET request to: " + completeLoginSelectedSiteUrl);
      logRequestHeaders(completeLoginSelectedSiteGetRequest, debugMode);
      HttpResponse completeLoginSelectedSiteResponse =
          httpClient.execute(completeLoginSelectedSiteGetRequest);
      logHttpResponseCode(completeLoginSelectedSiteResponse);
      String completeLoginSelectedSiteResponseHtml =
          IOUtils.toString(completeLoginSelectedSiteResponse.getEntity().getContent());
      logHtmlResponse(completeLoginSelectedSiteResponseHtml, debugMode);
      Document completeLoginSelectedSiteResponseDocument =
          Jsoup.parse(completeLoginSelectedSiteResponseHtml);
      return completeLoginSelectedSiteResponseDocument;
    } catch (IOException | InterruptedException e) {
      logger.error(e.getMessage(), e);
      throw new KameHouseBadRequestException("Error executing login to tennis world", e);
    }
  }

  /**
   * Get the session type id from the specified session type.
   */
  private String getSessionTypeId(Document dashboard, String sessionType, boolean debugMode) {
    String selectedSessionTypeId = null;
    Elements sessionTypes = dashboard.getElementsByAttributeValue("data-role", "page");
    for (Element sessionTypeElement : sessionTypes) {
      String sessionTypeName = sessionTypeElement.getElementsByTag("h1").text();
      String sessionTypeId = sessionTypeElement.attr("id");
      logDebug("sessionTypeName:" + sessionTypeName + "; sessionTypeId:" + sessionTypeId,
          debugMode);
      if (sessionTypeName != null
          && sessionTypeName.equalsIgnoreCase(sessionType)) {
        selectedSessionTypeId = sessionTypeId;
      }
    }
    logDebug("selectedSessionTypeId:" + selectedSessionTypeId, debugMode);
    return selectedSessionTypeId;
  }

  /**
   * Step 2. Get the session type page.
   */
  private Document getSessionTypePage(HttpClient httpClient, String selectedSessionTypeId,
                                      boolean debugMode) {
    try {
      Thread.sleep(1000);
      String selectedSessionTypeUrl = DASHBOARD_URL + "#" +  selectedSessionTypeId;
      HttpGet httpGet = HttpClientUtils.httpGet(selectedSessionTypeUrl);
      logger.info("Step 2: GET request to: " + selectedSessionTypeUrl);
      logRequestHeaders(httpGet, debugMode);
      HttpResponse httpResponse = httpClient.execute(httpGet);
      logHttpResponseCode(httpResponse);
      String html = IOUtils.toString(httpResponse.getEntity().getContent());
      logHtmlResponse(html, debugMode);
      Document sessionTypePage = Jsoup.parse(html);
      return sessionTypePage;
    } catch (IOException | InterruptedException e) {
      logger.error(e.getMessage(), e);
      throw new KameHouseBadRequestException("Error getting the session type page", e);
    }
  }

  /**
   * Get the selected session date path from the session type page returned from tennis world.
   */
  private String getSelectedSessionDatePath(Document sessionTypePage, String selectedSessionTypeId,
                                            String date, boolean debugMode) {
    Elements sessionsForTheSelectedSessionType =
        sessionTypePage.getElementById(selectedSessionTypeId).getElementsByTag("a");
    String selectedSessionDatePath = null;
    for (Element sessionForTheSelectedSessionType : sessionsForTheSelectedSessionType) {
      String href = sessionForTheSelectedSessionType.attr("href");
      logDebug("SessionDatePath:" + href, debugMode);
      if (href != null && href.contains(date)) {
        selectedSessionDatePath = href;
      }
    }
    logDebug("selectedSessionDatePath:" + selectedSessionDatePath, debugMode);
    return selectedSessionDatePath;
  }

  /**
   * Step 3. Get the selected session date page.
   */
  private Document getSessionDatePage(HttpClient httpClient, String selectedSessionDatePath,
                                      boolean debugMode) {
    try {
      Thread.sleep(1000);
      String selectedSessionDateUrl = ROOT_URL + selectedSessionDatePath;
      HttpGet httpGet = HttpClientUtils.httpGet(selectedSessionDateUrl);
      logger.info("Step 3: GET request to: " + selectedSessionDateUrl);
      logRequestHeaders(httpGet, debugMode);
      HttpResponse httpResponse = httpClient.execute(httpGet);
      logHttpResponseCode(httpResponse);
      String html = IOUtils.toString(httpResponse.getEntity().getContent());
      logHtmlResponse(html, debugMode);
      Document sessionDatePage = Jsoup.parse(html);
      return sessionDatePage;
    } catch (IOException | InterruptedException e) {
      logger.error(e.getMessage(), e);
      throw new KameHouseBadRequestException("Error getting the session date page", e);
    }
  }

  /**
   * Get the selected session path.
   */
  private String getSelectedSessionPath(Document doc, String bookingTime, boolean debugMode) {
    Elements sessionsForTheSelectedSessionDate =
        doc.getElementsByAttributeValue("data-role", "listview").get(0).getElementsByTag("a");
    String selectedSessionPath = null;
    for (Element sessionForTheSelectedSessionDate : sessionsForTheSelectedSessionDate) {
      String sessionHref = sessionForTheSelectedSessionDate.attr("href");
      String sessionTime = sessionForTheSelectedSessionDate.text();
      logDebug("SessionPath:time:" + sessionTime + "; href:" + sessionHref, debugMode);
      if (sessionTime != null && sessionTime.equalsIgnoreCase(bookingTime)) {
        selectedSessionPath = sessionHref;
      }
    }
    logDebug("selectedSessionPath:" + selectedSessionPath, debugMode);
    return selectedSessionPath;
  }

  /**
   * Step 4. Get the selected session page (session type, date and time).
   */
  private Document getSessionPage(HttpClient httpClient, String selectedSessionPath,
                                  boolean debugMode) {
    try {
      Thread.sleep(1000);
      String selectedSessionUrl = ROOT_URL + selectedSessionPath;
      HttpGet httpGet = HttpClientUtils.httpGet(selectedSessionUrl);
      logger.info("Step 4: GET request to: " + selectedSessionUrl);
      logRequestHeaders(httpGet, debugMode);
      HttpResponse httpResponse = httpClient.execute(httpGet);
      logHttpResponseCode(httpResponse);
      String html = IOUtils.toString(httpResponse.getEntity().getContent());
      logHtmlResponse(html, debugMode);
      Document doc = Jsoup.parse(html);
      return doc;
    } catch (IOException | InterruptedException e) {
      logger.error(e.getMessage(), e);
      throw new KameHouseBadRequestException("Error getting the session page", e);
    }
  }

  /**
   * Get the site facility group id from the session page.
   */
  private String getSiteFacilityGroupId(Document sessionPage, boolean debugMode) {
    String siteFacilityGroupId = sessionPage.getElementById("book_now_overlayfacility")
        .attr("site_facilitygroup_id");
    logDebug("siteFacilityGroupId:" + siteFacilityGroupId, debugMode);
    return siteFacilityGroupId;
  }

  /**
   * Get the booking time from the session page.
   */
  private String getBookingTime(Document sessionPage, boolean debugMode) {
    String bookingTime = sessionPage.getElementById("book_now_overlayfacility")
        .attr("booking_time");
    logDebug("bookingTime:" + bookingTime, debugMode);
    return bookingTime;
  }

  /**
   * Step 5. Post the book overlay ajax request and confirm that the response is not empty and
   * that it doesn't contain errors.
   */
  private void postBookOverlayAjax(HttpClient httpClient, String selectedSessionDatePath,
                                   String siteFacilityGroupId, String bookingTime,
                                   String bookingDuration, boolean debugMode) {
    try {
      Thread.sleep(1000);
      List<NameValuePair> loadSessionAjaxParams = new ArrayList<>();
      loadSessionAjaxParams.add(new BasicNameValuePair("site_facilitygroup_id",
          siteFacilityGroupId));
      loadSessionAjaxParams.add(new BasicNameValuePair("booking_time", bookingTime));
      loadSessionAjaxParams.add(new BasicNameValuePair("event_duration", bookingDuration));
      HttpPost loadSessionAjaxRequestHttpPost = new HttpPost(BOOK_OVERLAY_AJAX_URL);
      loadSessionAjaxRequestHttpPost.setEntity(new UrlEncodedFormEntity(loadSessionAjaxParams));
      loadSessionAjaxRequestHttpPost.setHeader("Referer", ROOT_URL + selectedSessionDatePath);
      loadSessionAjaxRequestHttpPost.setHeader("X-Requested-With", "XMLHttpRequest");
      logger.info("Step 5: POST request to: " + BOOK_OVERLAY_AJAX_URL);
      logRequestHeaders(loadSessionAjaxRequestHttpPost, debugMode);
      HttpResponse httpResponse = httpClient.execute(loadSessionAjaxRequestHttpPost);
      logHttpResponseCode(httpResponse);
      String html = IOUtils.toString(httpResponse.getEntity().getContent());
      logHtmlResponse(html, debugMode);
      //TODO parse json response to check that error:false, throw exception if error
    } catch (IOException | InterruptedException e) {
      logger.error(e.getMessage(), e);
      throw new KameHouseBadRequestException("Error executing post overlay ajax request", e);
    }
  }

  /**
   * Step 6. Go to the confirm booking url.
   */
  private void getConfirmBookingUrl(HttpClient httpClient, String selectedSessionDatePath,
                                    boolean debugMode) {
    try {
      Thread.sleep(1000);
      String selectedSessionDateUrl = ROOT_URL + selectedSessionDatePath;
      HttpGet httpGet = HttpClientUtils.httpGet(CONFIRM_BOOKING_URL);
      httpGet.setHeader("Referer", selectedSessionDateUrl);
      httpGet.setHeader("X-Requested-With", "XMLHttpRequest");
      logger.info("Step 6: GET request to: " + CONFIRM_BOOKING_URL);
      logRequestHeaders(httpGet, debugMode);
      HttpResponse httpResponse = httpClient.execute(httpGet);
      logHttpResponseCode(httpResponse);
      String html = IOUtils.toString(httpResponse.getEntity().getContent());
      logHtmlResponse(html, debugMode);
    } catch (IOException | InterruptedException e) {
      logger.error(e.getMessage(), e);
      throw new KameHouseBadRequestException("Error getting confirm booking url", e);
    }
  }

  /**
   * Step 7. Post the booking request (finally!) and check if there's any errors.
   * I expect a 302 redirect response, so if that's not the case, mark the request as error.
   */
  private String postBookingRequest(HttpClient httpClient,
                                          TennisWorldBookingRequest.CardDetails cardDetails,
                                          boolean debugMode) {
    try {
      Thread.sleep(1000);
      List<NameValuePair> confirmBookingParams = new ArrayList<>();
      if (cardDetails != null) {
        confirmBookingParams.add(new BasicNameValuePair("name_on_card",
            cardDetails.getName()));
        confirmBookingParams.add(new BasicNameValuePair("credit_card_number",
            cardDetails.getNumber()));
        confirmBookingParams.add(new BasicNameValuePair("expiry_month",
            cardDetails.getExpiryMonth()));
        confirmBookingParams.add(new BasicNameValuePair("expiry_year",
            cardDetails.getExpiryYear()));
        confirmBookingParams.add(new BasicNameValuePair("cvv_number",
            cardDetails.getCvv()));
      }
      confirmBookingParams.add(new BasicNameValuePair("submit", "Confirm and pay"));
      HttpPost confirmBookingHttpPost = new HttpPost(CONFIRM_BOOKING_URL);
      confirmBookingHttpPost.setEntity(new UrlEncodedFormEntity(confirmBookingParams));
      confirmBookingHttpPost.setHeader("Referer", CONFIRM_BOOKING_URL);
      confirmBookingHttpPost.setHeader("X-Requested-With", "XMLHttpRequest");
      logger.info("Step 7: POST request to: " + CONFIRM_BOOKING_URL);
      logRequestHeaders(confirmBookingHttpPost, debugMode);
      HttpResponse httpResponse = httpClient.execute(confirmBookingHttpPost);
      logHttpResponseCode(httpResponse);
      String html = IOUtils.toString(httpResponse.getEntity().getContent());
      logHtmlResponse(html, debugMode);
      Document doc = Jsoup.parse(html);
      if (doc != null && doc.getElementById("error-stack") != null) {
        Elements errorMessages = doc.getElementById("error-stack").getElementsByTag("p");
        for (Element errorMessage : errorMessages) {
          logger.error("Error executing request: " + errorMessage.text());
        }
        //TODO: if there's errors, throw an exception
      }
      if (httpResponse.getStatusLine().getStatusCode() != 302) {
        throw new KameHouseBadRequestException("Error posting booking request");
      }
      return httpResponse.getFirstHeader("Location").getValue();
    } catch (IOException | InterruptedException e) {
      logger.error(e.getMessage(), e);
      throw new KameHouseBadRequestException("Error posting booking request", e);
    }
  }

  /**
   * Step 8. Confirm the booking final result.
   */
  private void confirmBookingResult(HttpClient httpClient, String confirmBookingRedirectUrl,
                                    boolean debugMode) {
    try {
      Thread.sleep(1000);
      HttpGet httpGet = HttpClientUtils.httpGet(confirmBookingRedirectUrl);
      httpGet.setHeader("Referer", CONFIRM_BOOKING_URL);
      httpGet.setHeader("X-Requested-With", "XMLHttpRequest");
      logger.info("Step 8: GET request to: " + confirmBookingRedirectUrl);
      logRequestHeaders(httpGet, debugMode);
      HttpResponse httpResponse = httpClient.execute(httpGet);
      logHttpResponseCode(httpResponse);
      String html = IOUtils.toString(httpResponse.getEntity().getContent());
      logHtmlResponse(html, debugMode);
      Document doc = Jsoup.parse(html);
      if (doc != null && doc.getElementById("error-stack") != null) {
        Elements errorMessages = doc.getElementById("error-stack").getElementsByTag("p");
        for (Element errorMessage : errorMessages) {
          logger.error("Error executing request: " + errorMessage.text());
        }
        //TODO: if there's errors, throw an exception
      }
    } catch (IOException | InterruptedException e) {
      logger.error(e.getMessage(), e);
      throw new KameHouseBadRequestException("Error confirming booking result", e);
    }
  }

  /**
   * Build a success response when the booking is confirmed with no errors.
   */
  private TennisWorldBookingResponse buildSuccessResponse() {
    TennisWorldBookingResponse tennisWorldBookingResponse = new TennisWorldBookingResponse();
    tennisWorldBookingResponse.setStatus("SUCCESS");
    tennisWorldBookingResponse.setMessage("Completed the request");
    return tennisWorldBookingResponse;
  }

  /**
   * Build an error response with the specified error message.
   */
  private TennisWorldBookingResponse buildErrorResponse(String message) {
    TennisWorldBookingResponse tennisWorldBookingResponse = new TennisWorldBookingResponse();
    tennisWorldBookingResponse.setStatus("ERROR");
    tennisWorldBookingResponse.setMessage(message);
    return tennisWorldBookingResponse;
  }

  /**
   * Log request headers.
   */
  private void logRequestHeaders(HttpRequest httpRequest, boolean debugMode) {
    if (debugMode) {
      logger.debug("Request headers:");
      for (Header header : httpRequest.getAllHeaders()) {
        logger.debug(header.getName() + ":" + header.getValue());
      }
    }
  }

  /**
   * Log the html response body from tennis world.
   */
  private void logHtmlResponse(String html, boolean debugMode) {
    logDebug(html, debugMode);
  }

  /**
   * Log the response code received from tennis world.
   */
  private void logHttpResponseCode(HttpResponse httpResponse) {
    logger.info(String.valueOf(httpResponse.getStatusLine()));
  }

  /**
   * Log the specified message if debug mode is on.
   */
  private void logDebug(String message, boolean debugMode) {
    if (debugMode) {
      logger.debug(message);
    }
  }
}
