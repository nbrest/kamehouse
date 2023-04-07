package com.nicobrest.kamehouse.tennisworld.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.utils.DateUtils;
import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse.Status;
import com.nicobrest.kamehouse.tennisworld.model.SessionType;
import com.nicobrest.kamehouse.tennisworld.model.perfectgym.BookConfirmationRequest;
import com.nicobrest.kamehouse.tennisworld.model.perfectgym.CalendarFiltersRequest;
import com.nicobrest.kamehouse.tennisworld.model.perfectgym.DailyClassesRequest;
import com.nicobrest.kamehouse.tennisworld.model.perfectgym.LoginRequest;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Service to execute tennis world bookings through PerfectGym.
 *
 * @author nbrest
 */
@Service
public class PerfectGymBookingService extends BookingService {

  // URLs
  public static final String ROOT_URL = "https://tennisworld.perfectgym.com.au";
  private static final String LOGIN_URL = ROOT_URL + "/ClientPortal2/Auth/Login";
  private static final String CLASSES_CLUBS_URL =
      ROOT_URL + "/ClientPortal2/Clubs/GetAvailableClassesClubs";
  private static final String CALENDAR_FILTERS_URL =
      ROOT_URL + "/ClientPortal2/Classes/ClassCalendar/GetCalendarFilters";
  private static final String DAILY_CLASSES_URL =
      ROOT_URL + "/ClientPortal2/Classes/ClassCalendar/DailyClasses";
  private static final String COMPLETE_CLASS_BOOKING_URL =
      ROOT_URL + "/ClientPortal2/Classes/ClassCalendar/BookClass";
  private static final long INVALID_CLASS_ID = -9999L;

  @Override
  protected BookingResponse executeBookingRequestOnTennisWorld(BookingRequest bookingRequest) {
    SessionType sessionType = bookingRequest.getSessionType();
    try {
      switch (sessionType) {
        case ADULT_MATCH_PLAY_DOUBLES:
        case ADULT_MATCH_PLAY_SINGLES:
        case ADULT_SOCIAL_PLAY:
        case CARDIO:
        case CARDIO_ACTIV8:
          return bookClass(bookingRequest);
        case NTC_CLAY_COURTS:
        case NTC_INDOOR:
        case NTC_OUTDOOR:
        case ROD_LAVER_OUTDOOR_EASTERN:
        case ROD_LAVER_OUTDOOR_WESTERN:
        case ROD_LAVER_SHOW_COURTS:
          return bookCourt(bookingRequest);
        case UNKNOWN:
        default:
          return buildResponse(
              Status.INTERNAL_ERROR,
              "Unhandled sessionType: " + sessionType.name(),
              bookingRequest);

      }
    } catch (KameHouseBadRequestException e) {
      return buildResponse(Status.ERROR, e.getMessage(), bookingRequest);
    } catch (KameHouseServerErrorException e) {
      return buildResponse(Status.INTERNAL_ERROR, e.getMessage(), bookingRequest);
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
      return buildResponse(
          Status.INTERNAL_ERROR,
          "Error executing booking request to tennis world Message: " + e.getMessage(),
          bookingRequest);
    }
  }

  /**
   * Handle a tennis world booking for classes. The current process to complete this type of booking
   * is:
   * <ul>
   *   <li>1) POST Initial login request
   *   <li>2) GET class clubs
   *   <li>3) POST Get calendar filters
   *   <li>4) POST Get daily classes
   *   <li>5) POST Complete a class booking
   * </ul>
   */
  private BookingResponse bookClass(BookingRequest bookingRequest) throws IOException {
    HttpClient httpClient = HttpClientUtils.getClient(null, null);
    loginToTennisWorld(httpClient, bookingRequest);
    JsonNode[] clubs = getClassesClubs(httpClient);
    long clubId = getClubId(clubs, bookingRequest);
    getCalendarFilters(httpClient, clubId);
    JsonNode dailyClasses = getDailyClasses(httpClient, clubId, bookingRequest);
    long classId = getClassId(dailyClasses, bookingRequest);
    if (!bookingRequest.isDryRun()) {
      completeClassBooking(httpClient, clubId, classId);
      return buildResponse(Status.SUCCESS, SUCCESSFUL_BOOKING, bookingRequest);
    } else {
      return buildResponse(Status.SUCCESS, SUCCESSFUL_BOOKING_DRY_RUN, bookingRequest);
    }
  }

  /**
   * Handle a tennis world court booking.<br> The current process to complete a court booking is:
   * <ul>
   *   <li>1) POST Initial login request
   *   <li>2) GET clubs
   *   <li>3) POST Get club zone types
   *   <li>4) POST Get weekly schedule
   *   <li>5) GET Start booking modal
   *   <li>6) POST Set court booking details
   *   <li>7) POST Complete court booking
   * </ul>
   */
  private BookingResponse bookCourt(BookingRequest bookingRequest) throws IOException {
    HttpClient httpClient = HttpClientUtils.getClient(null, null);
    loginToTennisWorld(httpClient, bookingRequest);
    if (!bookingRequest.isDryRun()) {
      return buildResponse(Status.SUCCESS, SUCCESSFUL_BOOKING, bookingRequest);
    } else {
      return buildResponse(Status.SUCCESS, SUCCESSFUL_BOOKING_DRY_RUN, bookingRequest);
    }
  }

  /**
   * Login to PerfectGym Tennis World.
   */
  private void loginToTennisWorld(HttpClient httpClient, BookingRequest bookingRequest)
      throws IOException {
    LoginRequest loginRequestBody = new LoginRequest();
    loginRequestBody.setRememberMe(false);
    loginRequestBody.setUsername(bookingRequest.getUsername());
    loginRequestBody.setPassword(bookingRequest.getPassword());
    HttpPost loginRequest = new HttpPost(LOGIN_URL);
    loginRequest.setEntity(new StringEntity(JsonUtils.toJsonString(loginRequestBody, null, false),
        ContentType.APPLICATION_JSON));
    logger.info("Step 1: POST request to: {}", LOGIN_URL);
    HttpResponse loginResponse = executeRequest(httpClient, loginRequest, loginRequestBody);
    validateHttpResponseCode(loginResponse, LOGIN_URL);
    String responseBody = getResponseBody(loginResponse);
    if (!isSuccessfulLogin(responseBody)) {
      logger.error("Login to tennis world unsuccessful. Error response: {}", responseBody);
      throw new KameHouseBadRequestException("Invalid login to tennis world.");
    }
    logResponseBody(responseBody);
  }

  /**
   * Checks if the login response is has errors.
   */
  private boolean isSuccessfulLogin(String responseBody) {
    if (StringUtils.isEmpty(responseBody) || !JsonUtils.isJsonObject(responseBody)) {
      return false;
    }
    JsonNode responseBodyJson = JsonUtils.toJson(responseBody);
    if (!responseBodyJson.has("User")) {
      return false;
    }
    logger.debug("Login to tennis world successful");
    return true;
  }

  /**
   * Get classes clubs from tennis world PerfectGym.
   */
  private JsonNode[] getClassesClubs(HttpClient httpClient) throws IOException {
    HttpGet getClassesClubsRequest = new HttpGet(CLASSES_CLUBS_URL);
    logger.info("Step 2: GET request to: {}", CLASSES_CLUBS_URL);
    logRequestHeaders(getClassesClubsRequest);
    HttpResponse httpResponse = executeRequest(httpClient, getClassesClubsRequest);
    validateHttpResponseCode(httpResponse, CLASSES_CLUBS_URL);
    String responseBody = getResponseBody(httpResponse);
    logResponseBody(responseBody);
    if (!JsonUtils.isJsonArray(responseBody)) {
      throw new KameHouseServerErrorException("Invalid classes clubs response from PerfectGym");
    }
    return JsonUtils.toJsonArray(responseBody);
  }

  /**
   * Get club id from the clubs json array returned by tennis world PerfectGym.
   */
  private long getClubId(JsonNode[] clubs, BookingRequest bookingRequest) {
    String bookingRequestClub = bookingRequest.getSite().getPerfectGymName();
    for (JsonNode club : clubs) {
      if (bookingRequestClub.equals(JsonUtils.getText(club, "Name"))) {
        long clubId = JsonUtils.getLong(club, "Id");
        logger.debug("booking request clubId {}", clubId);
        return clubId;
      }
    }
    throw new KameHouseServerErrorException(
        "Unable to find club id for " + bookingRequestClub + "from PerfectGym response");
  }

  /**
   * Get calendar filters returned by tennis world PerfectGym.
   */
  private JsonNode getCalendarFilters(HttpClient httpClient, long clubId) throws IOException {
    CalendarFiltersRequest requestBody = new CalendarFiltersRequest();
    requestBody.setClubId(clubId);
    HttpPost getCalendarFiltersRequest = new HttpPost(CALENDAR_FILTERS_URL);
    getCalendarFiltersRequest.setEntity(new StringEntity(
        JsonUtils.toJsonString(requestBody, null, false), ContentType.APPLICATION_JSON));
    logger.info("Step 3: POST request to: {}", CALENDAR_FILTERS_URL);
    HttpResponse getCalendarFiltersResponse = executeRequest(httpClient, getCalendarFiltersRequest,
        requestBody);
    validateHttpResponseCode(getCalendarFiltersResponse, CALENDAR_FILTERS_URL);
    String responseBody = getResponseBody(getCalendarFiltersResponse);
    logResponseBody(responseBody);
    if (!JsonUtils.isJsonObject(responseBody)) {
      throw new KameHouseServerErrorException("Invalid calendar filters response from PerfectGym");
    }
    return JsonUtils.toJson(responseBody);
  }

  /**
   * Get the daily classes for the booking request date from tennis world PerfectGym.
   */
  public JsonNode getDailyClasses(HttpClient httpClient, long clubId, BookingRequest bookingRequest)
      throws IOException {
    DailyClassesRequest requestBody = new DailyClassesRequest();
    requestBody.setClubId(clubId);
    requestBody.setDate(
        DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD, bookingRequest.getDate()));
    HttpPost getCalendarFiltersRequest = new HttpPost(DAILY_CLASSES_URL);
    getCalendarFiltersRequest.setEntity(new StringEntity(
        JsonUtils.toJsonString(requestBody, null, false), ContentType.APPLICATION_JSON));
    logger.info("Step 4: POST request to: {}", DAILY_CLASSES_URL);
    HttpResponse getDailyClassesResponse = executeRequest(httpClient, getCalendarFiltersRequest,
        requestBody);
    validateHttpResponseCode(getDailyClassesResponse, DAILY_CLASSES_URL);
    String responseBody = getResponseBody(getDailyClassesResponse);
    logResponseBody(responseBody);
    if (!JsonUtils.isJsonObject(responseBody)) {
      throw new KameHouseServerErrorException("Invalid daily classes response from PerfectGym");
    }
    return JsonUtils.toJson(responseBody);
  }

  /**
   * Get the id of the class to book from the daily classes response from tennis world PerfectGym.
   */
  private long getClassId(JsonNode dailyClasses, BookingRequest bookingRequest) {
    AtomicLong classId = new AtomicLong(INVALID_CLASS_ID);
    String bookingRequestClassType = bookingRequest.getSessionType().getPerfectGymName();
    String bookingRequestTime = bookingRequest.getTime();
    dailyClasses.get("CalendarData").forEach(node -> {
      node.get("Classes").forEach(clubClass -> {
        String status = clubClass.get("Status").asText();
        String classType = clubClass.get("Name").asText();
        String classTime = clubClass.get("StartTime").asText();
        if ("Bookable".equals(status) && bookingRequestClassType.equals(classType)
            && classTime != null && classTime.contains(bookingRequestTime)) {
          logger.debug("classId to book: {}", clubClass.get("Id").asLong());
          classId.set(clubClass.get("Id").asLong());
        }
      });
    });
    if (classId.get() == INVALID_CLASS_ID) {
      logger.error(NO_BOOKABLE_CLASS_FOUND);
      throw new KameHouseBadRequestException(NO_BOOKABLE_CLASS_FOUND);
    }
    return classId.get();
  }

  /**
   * Complete the booking request on tennis world PerfectGym for the specified class.
   */
  private void completeClassBooking(HttpClient httpClient, long clubId, long classId)
      throws IOException {
    BookConfirmationRequest requestBody = new BookConfirmationRequest();
    requestBody.setClubId(clubId);
    requestBody.setClassId(classId);
    HttpPost completeBookingRequest = new HttpPost(COMPLETE_CLASS_BOOKING_URL);
    completeBookingRequest.setEntity(new StringEntity(
        JsonUtils.toJsonString(requestBody, null, false), ContentType.APPLICATION_JSON));
    logger.info("Step 5: POST request to: {}", COMPLETE_CLASS_BOOKING_URL);
    HttpResponse completeClassBookingResponse = executeRequest(httpClient,
        completeBookingRequest, requestBody);
    validateHttpResponseCode(completeClassBookingResponse, COMPLETE_CLASS_BOOKING_URL);
    String responseBody = getResponseBody(completeClassBookingResponse);
    logResponseBody(responseBody);
    if (!JsonUtils.isJsonObject(responseBody)) {
      throw new KameHouseServerErrorException("Invalid book class response from PerfectGym");
    }
    JsonNode bookingCompleteResponseBody = JsonUtils.toJson(responseBody);
    if (bookingCompleteResponseBody.get("Tickets") == null) {
      throw new KameHouseServerErrorException(
          "Book class response from PerfectGym doesn't contain a Tickets entry");
    }
  }

  /**
   * Validates that the http status code is 200.
   */
  private static void validateHttpResponseCode(HttpResponse httpResponse, String url) {
    if (HttpClientUtils.getStatusCode(httpResponse) != HttpStatus.OK.value()) {
      throw new KameHouseServerErrorException("Invalid http response code: "
          + HttpClientUtils.getStatusCode(httpResponse) + " for request to " + url);
    }
  }
}
