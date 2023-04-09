package com.nicobrest.kamehouse.tennisworld.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import com.nicobrest.kamehouse.tennisworld.model.perfectgym.ClubZoneTypesRequest;
import com.nicobrest.kamehouse.tennisworld.model.perfectgym.CourtWeeklyScheduleRequest;
import com.nicobrest.kamehouse.tennisworld.model.perfectgym.DailyClassesRequest;
import com.nicobrest.kamehouse.tennisworld.model.perfectgym.FinalizeCourtBookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.perfectgym.LoginRequest;
import com.nicobrest.kamehouse.tennisworld.model.perfectgym.SetCourtBookingDetailsRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
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

  public static final String CP_BOOK_FACILITY_SESSION_ID_HEADER = "CP-BOOK-FACILITY-SESSION-ID";
  // URLs
  public static final String ROOT_URL = "https://tennisworld.perfectgym.com.au";
  public static final String LOGIN_URL = ROOT_URL + "/ClientPortal2/Auth/Login";
  private static final String CLASSES_CLUBS_URL =
      ROOT_URL + "/ClientPortal2/Clubs/GetAvailableClassesClubs";
  private static final String CALENDAR_FILTERS_URL =
      ROOT_URL + "/ClientPortal2/Classes/ClassCalendar/GetCalendarFilters";
  private static final String DAILY_CLASSES_URL =
      ROOT_URL + "/ClientPortal2/Classes/ClassCalendar/DailyClasses";
  private static final String COMPLETE_CLASS_BOOKING_URL =
      ROOT_URL + "/ClientPortal2/Classes/ClassCalendar/BookClass";
  private static final String CLUBS_URL =
      ROOT_URL + "/ClientPortal2/Clubs/GetClubs";
  private static final String CLUB_ZONE_TYPES_URL =
      ROOT_URL + "/ClientPortal2/FacilityBookings/FacilityCalendar/GetClubZoneTypes";
  private static final String COURT_WEEKLY_SCHEDULE_URL =
      ROOT_URL + "/ClientPortal2/FacilityBookings/FacilityCalendar/GetWeeklySchedule";
  private static final String START_COURT_BOOKING_MODAL_URL =
      ROOT_URL + "/ClientPortal2/FacilityBookings/BookFacility/Start";
  private static final String SELECT_COURT_BOOKING_URL = ROOT_URL
      + "/ClientPortal2/FacilityBookings/WizardSteps/SetFacilityBookingDetailsWizardStep/Next";
  private static final String FINALIZE_COURT_BOOKING_URL =
      ROOT_URL + "/FacilityBookings/WizardSteps/ChooseBookingRuleStep/Next";
  private static final long INVALID_ID = -9999L;
  private static final List<Integer> CLIENT_ERROR_STATUS = List.of(499,
      HttpStatus.BAD_REQUEST.value());


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
    JsonNode[] clubs = getClubs(httpClient);
    long clubId = getClubId(clubs, bookingRequest);
    JsonNode clubZoneTypes = getClubZoneTypes(httpClient, clubId);
    long zoneTypeId = getZoneTypeId(bookingRequest, clubZoneTypes);
    getWeeklySchedule(httpClient, bookingRequest, clubId, zoneTypeId);
    JsonNode startBookingModalResponse = startBookingModal(httpClient, bookingRequest, clubId,
        zoneTypeId);
    String sessionId = startBookingModalResponse.get(CP_BOOK_FACILITY_SESSION_ID_HEADER).asText();
    long zoneId = getZoneId(startBookingModalResponse);
    long userId = getUserId(startBookingModalResponse);
    JsonNode rules = setCourtBookingDetails(httpClient, bookingRequest, zoneId, userId, sessionId);
    long ruleId = getRuleId(rules);
    if (!bookingRequest.isDryRun()) {
      finalizeCourtBooking(httpClient, ruleId, sessionId);
      return buildResponse(Status.SUCCESS, SUCCESSFUL_BOOKING, bookingRequest);
    } else {
      return buildResponse(Status.SUCCESS, SUCCESSFUL_BOOKING_DRY_RUN, bookingRequest);
    }
  }

  /**
   * Class/Court Step 1: Login to PerfectGym Tennis World.
   */
  private void loginToTennisWorld(HttpClient httpClient, BookingRequest bookingRequest)
      throws IOException {
    LoginRequest loginRequestBody = new LoginRequest();
    loginRequestBody.setRememberMe(false);
    loginRequestBody.setUsername(bookingRequest.getUsername());
    loginRequestBody.setPassword(bookingRequest.getPassword());
    HttpPost httpPost = new HttpPost(LOGIN_URL);
    httpPost.setEntity(new StringEntity(JsonUtils.toJsonString(loginRequestBody, null, false),
        ContentType.APPLICATION_JSON));
    logger.info("Step 1: POST request to: {}", LOGIN_URL);
    HttpResponse httpResponse = executeRequest(httpClient, httpPost, loginRequestBody);
    validateHttpResponseCode(httpResponse, LOGIN_URL);
    String responseBody = getResponseBody(httpResponse);
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
   * Class Step 2: Get classes clubs from tennis world PerfectGym.
   */
  private JsonNode[] getClassesClubs(HttpClient httpClient) throws IOException {
    HttpGet httpGet = new HttpGet(CLASSES_CLUBS_URL);
    logger.info("Step 2: GET request to: {}", CLASSES_CLUBS_URL);
    logRequestHeaders(httpGet);
    HttpResponse httpResponse = executeRequest(httpClient, httpGet);
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
        "Unable to find club id for " + bookingRequestClub + " from PerfectGym response");
  }

  /**
   * Class Step 3: Get calendar filters returned by tennis world PerfectGym.
   */
  private JsonNode getCalendarFilters(HttpClient httpClient, long clubId) throws IOException {
    CalendarFiltersRequest requestBody = new CalendarFiltersRequest();
    requestBody.setClubId(clubId);
    HttpPost httpPost = new HttpPost(CALENDAR_FILTERS_URL);
    httpPost.setEntity(new StringEntity(JsonUtils.toJsonString(requestBody, null, false),
        ContentType.APPLICATION_JSON));
    logger.info("Step 3: POST request to: {}", CALENDAR_FILTERS_URL);
    HttpResponse httpResponse = executeRequest(httpClient, httpPost, requestBody);
    validateHttpResponseCode(httpResponse, CALENDAR_FILTERS_URL);
    String responseBody = getResponseBody(httpResponse);
    logResponseBody(responseBody);
    if (!JsonUtils.isJsonObject(responseBody)) {
      throw new KameHouseServerErrorException("Invalid calendar filters response from PerfectGym");
    }
    return JsonUtils.toJson(responseBody);
  }

  /**
   * Class Step 4: Get the daily classes for the booking request date from tennis world PerfectGym.
   */
  private JsonNode getDailyClasses(HttpClient httpClient, long clubId,
      BookingRequest bookingRequest)
      throws IOException {
    DailyClassesRequest requestBody = new DailyClassesRequest();
    requestBody.setClubId(clubId);
    requestBody.setDate(DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD, bookingRequest.getDate()));
    HttpPost httpPost = new HttpPost(DAILY_CLASSES_URL);
    httpPost.setEntity(new StringEntity(JsonUtils.toJsonString(requestBody, null, false),
        ContentType.APPLICATION_JSON));
    logger.info("Step 4: POST request to: {}", DAILY_CLASSES_URL);
    HttpResponse httpResponse = executeRequest(httpClient, httpPost, requestBody);
    validateHttpResponseCode(httpResponse, DAILY_CLASSES_URL);
    String responseBody = getResponseBody(httpResponse);
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
    AtomicLong classId = new AtomicLong(INVALID_ID);
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
    if (classId.get() == INVALID_ID) {
      logger.error(NO_BOOKABLE_CLASS_FOUND);
      throw new KameHouseBadRequestException(NO_BOOKABLE_CLASS_FOUND);
    }
    return classId.get();
  }

  /**
   * Class Step 5: Complete the booking request on tennis world PerfectGym for the specified class.
   */
  private void completeClassBooking(HttpClient httpClient, long clubId, long classId)
      throws IOException {
    BookConfirmationRequest requestBody = new BookConfirmationRequest();
    requestBody.setClubId(clubId);
    requestBody.setClassId(classId);
    HttpPost httpPost = new HttpPost(COMPLETE_CLASS_BOOKING_URL);
    httpPost.setEntity(new StringEntity(JsonUtils.toJsonString(requestBody, null, false),
        ContentType.APPLICATION_JSON));
    logger.info("Step 5: POST request to: {}", COMPLETE_CLASS_BOOKING_URL);
    HttpResponse httpResponse = executeRequest(httpClient, httpPost, requestBody);
    validateHttpResponseCode(httpResponse, COMPLETE_CLASS_BOOKING_URL);
    String responseBody = getResponseBody(httpResponse);
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
   * Court Step 2: Get clubs.
   */
  private JsonNode[] getClubs(HttpClient httpClient)
      throws IOException {
    HttpGet httpGet = new HttpGet(CLUBS_URL);
    logger.info("Step 2: GET request to: {}", CLUBS_URL);
    logRequestHeaders(httpGet);
    HttpResponse httpResponse = executeRequest(httpClient, httpGet);
    validateHttpResponseCode(httpResponse, CLUBS_URL);
    String responseBody = getResponseBody(httpResponse);
    logResponseBody(responseBody);
    if (!JsonUtils.isJsonArray(responseBody)) {
      throw new KameHouseServerErrorException("Invalid clubs response from PerfectGym");
    }
    return JsonUtils.toJsonArray(responseBody);
  }

  /**
   * Court Step 3: Get club zone types.
   */
  private JsonNode getClubZoneTypes(HttpClient httpClient, long clubId) throws IOException {
    ClubZoneTypesRequest requestBody = new ClubZoneTypesRequest();
    requestBody.setClubId(clubId);
    HttpPost httpPost = new HttpPost(CLUB_ZONE_TYPES_URL);
    httpPost.setEntity(new StringEntity(JsonUtils.toJsonString(requestBody, null, false),
        ContentType.APPLICATION_JSON));
    logger.info("Step 3: POST request to: {}", CLUB_ZONE_TYPES_URL);
    HttpResponse httpResponse = executeRequest(httpClient, httpPost, requestBody);
    validateHttpResponseCode(httpResponse, CLUB_ZONE_TYPES_URL);
    String responseBody = getResponseBody(httpResponse);
    logResponseBody(responseBody);
    if (!JsonUtils.isJsonObject(responseBody)) {
      throw new KameHouseServerErrorException("Invalid club zone types response from PerfectGym");
    }
    return JsonUtils.toJson(responseBody);
  }

  /**
   * Get the zone type id from the club.
   */
  private long getZoneTypeId(BookingRequest bookingRequest, JsonNode clubZoneTypes) {
    String bookingZone = bookingRequest.getSessionType().getPerfectGymName().replaceAll(" ", "");
    AtomicLong zoneTypeId = new AtomicLong(INVALID_ID);
    clubZoneTypes.forEach(node -> {
      if (bookingZone.equals(node.get("Name").asText().replaceAll(" ", ""))) {
        zoneTypeId.set(node.get("Id").asLong());
      }
    });
    if (INVALID_ID == zoneTypeId.get()) {
      throw new KameHouseServerErrorException("Unable to determine zoneTypeId");
    }
    return zoneTypeId.get();
  }

  /**
   * Court Step 4: Get weekly schedule.
   */
  private JsonNode getWeeklySchedule(HttpClient httpClient, BookingRequest bookingRequest,
      long clubId, long zoneTypeId) throws IOException {
    CourtWeeklyScheduleRequest requestBody = new CourtWeeklyScheduleRequest();
    requestBody.setClubId(clubId);
    requestBody.setZoneTypeId(zoneTypeId);
    requestBody.setDate(DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD, bookingRequest.getDate()));
    HttpPost httpPost = new HttpPost(COURT_WEEKLY_SCHEDULE_URL);
    httpPost.setEntity(new StringEntity(JsonUtils.toJsonString(requestBody, null, false),
        ContentType.APPLICATION_JSON));
    logger.info("Step 4: POST request to: {}", COURT_WEEKLY_SCHEDULE_URL);
    HttpResponse httpResponse = executeRequest(httpClient, httpPost, requestBody);
    validateHttpResponseCode(httpResponse, COURT_WEEKLY_SCHEDULE_URL);
    String responseBody = getResponseBody(httpResponse);
    logResponseBody(responseBody);
    if (!JsonUtils.isJsonObject(responseBody)) {
      throw new KameHouseServerErrorException(
          "Invalid court weekly schedule response from PerfectGym");
    }
    return JsonUtils.toJson(responseBody);
  }

  /**
   * Court Step 5: Start booking modal.
   */
  private JsonNode startBookingModal(HttpClient httpClient, BookingRequest bookingRequest,
      long clubId, long zoneTypeId) throws IOException {
    HttpGet httpGet = new HttpGet(START_COURT_BOOKING_MODAL_URL);
    String startDate = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD,
        bookingRequest.getDate()) + "T" + bookingRequest.getTime() + ":00";
    URI uri = null;
    try {
      uri = new URIBuilder(httpGet.getURI())
          .addParameter("clubId", String.valueOf(clubId))
          .addParameter("zoneTypeId", String.valueOf(zoneTypeId))
          .addParameter("startDate", startDate)
          .build();
    } catch (URISyntaxException e) {
      throw new KameHouseServerErrorException("Unable to build start booking modal request", e);
    }
    httpGet.setURI(uri);
    logger.info("Step 5: GET request to: {}", START_COURT_BOOKING_MODAL_URL);
    logRequestHeaders(httpGet);
    HttpResponse httpResponse = executeRequest(httpClient, httpGet);
    validateHttpResponseCode(httpResponse, START_COURT_BOOKING_MODAL_URL);
    String responseBody = getResponseBody(httpResponse);
    logResponseBody(responseBody);
    if (!JsonUtils.isJsonObject(responseBody)) {
      throw new KameHouseServerErrorException(
          "Invalid start booking modal response from PerfectGym");
    }
    JsonNode responseBodyJson = JsonUtils.toJson(responseBody);
    String sessionId = HttpClientUtils.getHeader(httpResponse, CP_BOOK_FACILITY_SESSION_ID_HEADER);
    if (StringUtils.isEmpty(sessionId)) {
      throw new KameHouseServerErrorException(
          "Unable to get session id from start booking modal request to PerfectGym");
    }
    if (responseBodyJson.isObject()) {
      ((ObjectNode) responseBodyJson).put(CP_BOOK_FACILITY_SESSION_ID_HEADER, sessionId);
    }
    return responseBodyJson;
  }

  /**
   * Get zoneId for the court booking request.
   */
  private long getZoneId(JsonNode response) {
    if (response.get("Data") != null && response.get("Data").get("ZoneId") != null) {
      return response.get("Data").get("ZoneId").asLong();
    }
    throw new KameHouseServerErrorException("Unable to determine zoneId");
  }

  /**
   * Get userId for the court booking request.
   */
  private long getUserId(JsonNode response) {
    logger.info(response.toString());
    if (response.get("Data") != null
        && response.get("Data").get("Users") != null
        && response.get("Data").get("Users").isArray()) {
      ArrayNode users = (ArrayNode) response.get("Data").get("Users");
      if (users.get(0) != null && users.get(0).get("UserId") != null) {
        return users.get(0).get("UserId").asLong();
      }
    }
    throw new KameHouseServerErrorException("Unable to determine userId");
  }

  /**
   * Court Step 6: Set court booking details.
   */
  private JsonNode setCourtBookingDetails(HttpClient httpClient, BookingRequest bookingRequest,
      long zoneId, long userId, String sessionId) throws IOException {
    String startTime = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD,
        bookingRequest.getDate()) + "T" + bookingRequest.getTime() + ":00";
    SetCourtBookingDetailsRequest requestBody = new SetCourtBookingDetailsRequest();
    requestBody.setUserId(userId);
    requestBody.setZoneId(zoneId);
    requestBody.setStartTime(startTime);
    requestBody.setDuration(Integer.parseInt(bookingRequest.getDuration()));
    HttpPost httpPost = new HttpPost(SELECT_COURT_BOOKING_URL);
    httpPost.setEntity(new StringEntity(JsonUtils.toJsonString(requestBody, null, false),
        ContentType.APPLICATION_JSON));
    httpPost.setHeader(CP_BOOK_FACILITY_SESSION_ID_HEADER, sessionId);
    logger.info("Step 6: POST request to: {}", SELECT_COURT_BOOKING_URL);
    HttpResponse httpResponse = executeRequest(httpClient, httpPost, requestBody);
    validateHttpResponseCode(httpResponse, SELECT_COURT_BOOKING_URL);
    String responseBody = getResponseBody(httpResponse);
    logResponseBody(responseBody);
    if (!JsonUtils.isJsonObject(responseBody)) {
      throw new KameHouseServerErrorException(
          "Invalid select court booking response from PerfectGym");
    }
    return JsonUtils.toJson(responseBody);
  }

  /**
   * Get ruleId to finalize court booking request.
   */
  private long getRuleId(JsonNode rules) {
    if (rules.get("Data") != null && rules.get("Data").get("RuleId") != null) {
      return rules.get("Data").get("RuleId").asLong();
    }
    throw new KameHouseServerErrorException("Unable to determine ruleId");
  }

  /**
   * Court Step 7: Finalize court booking.
   */
  private JsonNode finalizeCourtBooking(HttpClient httpClient, long ruleId, String sessionId)
      throws IOException {
    FinalizeCourtBookingRequest requestBody = new FinalizeCourtBookingRequest();
    requestBody.setRuleId(ruleId);
    requestBody.setOtherCalendarEventBookedAtRequestedTime(false);
    requestBody.setHasUserRequiredProducts(true);
    HttpPost httpPost = new HttpPost(FINALIZE_COURT_BOOKING_URL);
    httpPost.setEntity(new StringEntity(JsonUtils.toJsonString(requestBody, null, false),
        ContentType.APPLICATION_JSON));
    httpPost.setHeader(CP_BOOK_FACILITY_SESSION_ID_HEADER, sessionId);
    logger.info("Step 7: POST request to: {}", FINALIZE_COURT_BOOKING_URL);
    HttpResponse httpResponse = executeRequest(httpClient, httpPost, requestBody);
    validateHttpResponseCode(httpResponse, FINALIZE_COURT_BOOKING_URL);
    String responseBody = getResponseBody(httpResponse);
    logResponseBody(responseBody);
    if (!JsonUtils.isJsonObject(responseBody)) {
      throw new KameHouseServerErrorException(
          "Invalid finalize court booking response from PerfectGym");
    }
    return JsonUtils.toJson(responseBody);
  }

  /**
   * Validates that the http status code is 200.
   */
  private static void validateHttpResponseCode(HttpResponse httpResponse, String url) {
    int httpStatus = HttpClientUtils.getStatusCode(httpResponse);
    if (CLIENT_ERROR_STATUS.contains(httpStatus)) {
      throw new KameHouseBadRequestException("Invalid http client error response code: "
          + HttpClientUtils.getStatusCode(httpResponse) + " for request to " + url);
    }
    if (HttpClientUtils.getStatusCode(httpResponse) != HttpStatus.OK.value()) {
      throw new KameHouseServerErrorException("Invalid http server error response code: "
          + HttpClientUtils.getStatusCode(httpResponse) + " for request to " + url);
    }
  }
}
