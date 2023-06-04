package com.nicobrest.kamehouse.tennisworld.service;

import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.utils.DateUtils;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.commons.utils.ThreadUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse.Status;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.perfectgym.RequestBody;
import com.nicobrest.kamehouse.tennisworld.model.scheduler.job.ScheduledBookingJob;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class extended by all tennis world booking services.
 *
 * @author nbrest
 */
public abstract class BookingService {

  public static final String SUCCESSFUL_BOOKING = "Completed the booking request successfully";
  public static final String SUCCESSFUL_BOOKING_DRY_RUN =
      "Completed the booking request DRY-RUN successfully";
  public static final String BOOKING_FINISHED = "Booking to tennis world finished: {}";
  public static final String NO_BOOKABLE_CLASS_FOUND =
      "No bookable class was found for this booking request";
  public static final String TIME_PATTERN = "[0-9]{2}:[0-9]{2}";

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private static int sleepMs = 500;
  private static int retrySleepMs = 5000;

  @Autowired
  private BookingScheduleConfigService bookingScheduleConfigService;

  @Autowired
  private BookingRequestService bookingRequestService;

  @Autowired
  private BookingResponseService bookingResponseService;

  /**
   * Build and send the booking response to the active tennis world implementation.
   */
  protected abstract BookingResponse executeBookingRequest(BookingRequest bookingRequest);

  /**
   * Set the sleep ms between requests.
   */
  public static void setSleepMs(int sleepMs) {
    BookingService.sleepMs = sleepMs;
  }

  /**
   * Set the sleep ms between booking request retries.
   */
  public static void setRetrySleepMs(int retrySleepMs) {
    BookingService.retrySleepMs = retrySleepMs;
  }

  /**
   * Initiate a booking request to tennis world.
   */
  public BookingResponse book(BookingRequest bookingRequest) {
    validateRequest(bookingRequest);
    Long requestId = persistBookingRequest(bookingRequest);
    setThreadName(requestId);
    logger.info("Booking tennis world request: {}", bookingRequest);
    return executeBookingRequest(bookingRequest);
  }

  /**
   * Execute the scheduled bookings based on the BookingScheduleConfigs set in the database. This
   * method is to be triggered by the {@link ScheduledBookingJob}. It can also be manually triggered
   * through an API.
   */
  public List<BookingResponse> bookScheduledSessions() {
    logger.info("Started processing scheduled booking configs");
    List<BookingScheduleConfig> bookingScheduleConfigs = getBookingScheduleConfigs();
    List<BookingResponse> bookingResponses = new ArrayList<>();
    if (bookingScheduleConfigs == null || bookingScheduleConfigs.isEmpty()) {
      logger.info("No scheduled booking configurations setup in this server");
      return bookingResponses;
    }
    List<BookingResponse> todaySuccessfulBookingResponses = getTodaySuccessfulBookingResponses();
    for (BookingScheduleConfig bookingScheduleConfig : bookingScheduleConfigs) {
      try {
        BookingResponse bookingResponse = processScheduledBookingConfig(bookingScheduleConfig,
            todaySuccessfulBookingResponses);
        if (bookingResponse != null) {
          bookingResponses.add(bookingResponse);
        }
      } catch (KameHouseException e) {
        logger.error("Error executing scheduled booking for {}", bookingScheduleConfig, e);
      }
    }
    logScheduledBookingResponses(bookingResponses, bookingScheduleConfigs.size());
    return bookingResponses;
  }

  /**
   * Build a tennis world response with the specified status and message.
   */
  protected BookingResponse buildResponse(Status status, String message, BookingRequest request) {
    BookingResponse bookingResponse = new BookingResponse();
    bookingResponse.setStatus(status);
    bookingResponse.setMessage(message);
    request.setPassword(null);
    request.setCardDetails(null);
    bookingResponse.setRequest(request);
    if (bookingResponse.getStatus() != Status.SUCCESS) {
      logger.error(BOOKING_FINISHED, bookingResponse);
    } else {
      logger.info(BOOKING_FINISHED, bookingResponse);
    }
    return bookingResponse;
  }

  /**
   * Persist the booking response.
   */
  protected void storeBookingResponse(BookingResponse bookingResponse) {
    Long responseId = bookingResponseService.create(bookingResponse.buildDto());
    bookingResponse.setId(responseId);
  }

  /**
   * Sleep for the specified ms by sleepMs.
   */
  protected static void sleep() {
    try {
      Thread.sleep(sleepMs);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Sleep for the specified ms by sleepMs between booking request retries.
   */
  protected static void retrySleep() {
    try {
      Thread.sleep(retrySleepMs);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Execute http request.
   */
  protected HttpResponse executeRequest(HttpClient httpClient, HttpRequestBase httpRequest)
      throws IOException {
    return executeRequest(httpClient, httpRequest, null);
  }

  /**
   * Execute http request.
   */
  protected HttpResponse executeRequest(HttpClient httpClient, HttpRequestBase httpRequest,
      RequestBody requestBody)
      throws IOException {
    if (requestBody != null) {
      logger.trace("Request body: {}", requestBody);
    }
    logRequestHeaders(httpRequest);
    HttpResponse httpResponse = HttpClientUtils.execRequest(httpClient, httpRequest);
    logHttpResponseCode(httpResponse);
    return httpResponse;
  }

  /**
   * Log request headers.
   */
  protected void logRequestHeaders(HttpRequest httpRequest) {
    logger.debug("Request headers:");
    if (!HttpClientUtils.hasHeaders(httpRequest)) {
      logger.debug("No request headers set");
    } else {
      for (Header header : HttpClientUtils.getAllHeaders(httpRequest)) {
        logger.debug("{} : {}", header.getName(), header.getValue());
      }
    }
  }

  /**
   * Log the response code received from tennis world.
   */
  protected void logHttpResponseCode(HttpResponse httpResponse) {
    logger.info("Response code: {}", HttpClientUtils.getStatusLine(httpResponse));
  }

  /**
   * Log response body.
   */
  protected void logResponseBody(String responseBody) {
    if (!StringUtils.isEmpty(responseBody)) {
      logger.trace("Response body: {}", responseBody);
    }
  }

  /**
   * Get response body from http response. Returns null if there's no response body.
   */
  protected String getResponseBody(HttpResponse httpResponse) throws IOException {
    if (HttpClientUtils.hasResponseBody(httpResponse)) {
      return IOUtils.toString(HttpClientUtils.getInputStream(httpResponse),
          Charsets.UTF_8);
    }
    logger.trace("Http response doesn't contain a body");
    return null;
  }

  /**
   * Get booking schedule configs sorted reversely by time. So the bookings with the latest time
   * gets executed first. This is to take advantage of the bug in PerfectGym that allows you to do a
   * 2 hours bookings in certain courts, but doing the latest booking first.
   */
  private List<BookingScheduleConfig> getBookingScheduleConfigs() {
    List<BookingScheduleConfig> configs = bookingScheduleConfigService.readAll();
    if (configs != null) {
      return configs.stream()
          .sorted((o1, o2) -> o2.getTime().compareTo(o1.getTime())) // sort reverse by time
          .collect(Collectors.toList());
    }
    return null;
  }

  /**
   * get all scheduled successful bookings executed today.
   */
  private List<BookingResponse> getTodaySuccessfulBookingResponses() {
    List<BookingResponse> bookingResponses = bookingResponseService.readAll();
    String currentDate = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD,
        DateUtils.getCurrentDate());
    if (bookingResponses == null || bookingResponses.isEmpty()) {
      logger.debug("No booking responses stored in the system yet");
      return null;
    }
    List<BookingResponse> successfulBookingResponses = bookingResponses.stream()
        .filter(br -> br.getRequest().isScheduled())
        .filter(br -> Status.SUCCESS.equals(br.getStatus()))
        .filter(br -> {
          String creationDate = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD,
              br.getRequest().getCreationDate());
          return currentDate.equals(creationDate);
        })
        .collect(Collectors.toList());
    if (successfulBookingResponses.isEmpty()) {
      logger.debug("No successfully executed booking requests scheduled today");
    } else {
      logger.debug("Successful booking requests executed today {}", successfulBookingResponses);
    }
    return successfulBookingResponses;
  }

  /**
   * Log the output of the scheduled booking responses.
   */
  private void logScheduledBookingResponses(List<BookingResponse> bookingResponses,
      int scheduledConfigsCount) {
    int successfulBookings = 0;
    int failedBookings = 0;
    for (BookingResponse bookingResponse : bookingResponses) {
      if (Status.SUCCESS.equals(bookingResponse.getStatus())) {
        successfulBookings++;
      }
      if (!Status.SUCCESS.equals(bookingResponse.getStatus())) {
        failedBookings++;
      }
    }
    logger.info("Finished processing {} scheduled configs. Executed {} booking requests. {} "
            + "successful requests and {} failed requests", scheduledConfigsCount,
        bookingResponses.size(), successfulBookings, failedBookings);
    logger.debug("Scheduled booking responses {}", bookingResponses);
  }

  /**
   * Determine if a booking request should be triggered for the specified booking schedule config
   * and execute the valid booking requests for the current date.
   */
  private BookingResponse processScheduledBookingConfig(
      BookingScheduleConfig bookingScheduleConfig,
      List<BookingResponse> todaySuccessfulBookingResponses) {
    logger.debug("Processing bookingScheduleConfig {}", bookingScheduleConfig);
    if (!bookingScheduleConfig.isEnabled()) {
      logger.debug("BookingScheduleConfig id {} is disabled. Skipping",
          bookingScheduleConfig.getId());
      return null;
    }
    if (!shouldScheduledBookingBeExecutedToday(bookingScheduleConfig)) {
      return null;
    }
    if (!isBookingTimeValid(bookingScheduleConfig)) {
      return null;
    }
    if (isSuccessfulBookingExecutedAlready(todaySuccessfulBookingResponses,
        bookingScheduleConfig)) {
      return null;
    }
    BookingRequest bookingRequest = createScheduledBookingRequest(bookingScheduleConfig);
    return book(bookingRequest);
  }

  /**
   * Returns true if the booking schedule config should be executed today.
   */
  private boolean shouldScheduledBookingBeExecutedToday(
      BookingScheduleConfig bookingScheduleConfig) {
    if (bookingScheduleConfig.getBookingDate() != null) {
      /*
       * One-off scheduledBookingDate is set in the config. Check if the one-off booking should be
       * executed today.
       */
      Date scheduledBookingDate = bookingScheduleConfig.getBookingDate();
      if (isValidBookingDate(scheduledBookingDate)
          && scheduledBookingDateMatchesBookAheadDays(bookingScheduleConfig)) {
        logger.debug("One-off scheduled booking date '{}' set and should be executed today",
            scheduledBookingDate);
        return true;
      }
    } else {
      /*
       * One-off BookingDate is not set in the config. Checking if recurring booking should be
       * executed today.
       */
      int bookAheadDays = bookingScheduleConfig.getBookAheadDays();
      Date calculatedBookingDate = DateUtils.getDateFromToday(bookAheadDays);
      DateUtils.Day configDay = bookingScheduleConfig.getDay();
      DateUtils.Day bookingDateDay = DateUtils.getDay(calculatedBookingDate);
      if (configDay == bookingDateDay) {
        logger.debug("Recurring booking should be executed today for booking date {}",
            calculatedBookingDate);
        return true;
      }
    }
    logger.debug("BookingScheduleConfig id {} is not scheduled to execute today",
        bookingScheduleConfig.getId());
    return false;
  }

  /**
   * Returns true if the current time is at or after the request booking time.
   */
  private boolean isBookingTimeValid(BookingScheduleConfig bookingScheduleConfig) {
    String scheduledBookingTime = bookingScheduleConfig.getTime();
    String currentTime = DateUtils.getFormattedDate(DateUtils.HH_MM_24HS);
    if (scheduledBookingTime.compareTo(currentTime) <= 0) {
      return true;
    }
    logger.debug("BookingScheduleConfig id {} is scheduled for today but should be executed at"
        + " a later time", bookingScheduleConfig.getId());
    return false;
  }

  /**
   * Checks if there is already a successful booking executed today with the same parameters as the
   * current scheduled booking config.
   */
  private boolean isSuccessfulBookingExecutedAlready(List<BookingResponse> bookingResponses,
      BookingScheduleConfig bookingScheduleConfig) {
    if (bookingResponses == null || bookingResponses.isEmpty()) {
      return false;
    }
    String scheduleConfigDate = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD,
        getBookingDateFromBookingScheduleConfig(bookingScheduleConfig));
    Integer scheduleConfigCourtNumber = bookingScheduleConfig.getCourtNumber();
    for (BookingResponse successfulBookingResponse : bookingResponses) {
      BookingRequest bookingRequest = successfulBookingResponse.getRequest();
      String bookingDate = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD,
          bookingRequest.getDate());
      String username = bookingScheduleConfig.getTennisWorldUser().getEmail();
      Integer bookingRequestCourtNumber = bookingRequest.getCourtNumber();
      if (bookingRequest.getTime().equals(bookingScheduleConfig.getTime())
          && bookingRequest.getSite().equals(bookingScheduleConfig.getSite())
          && bookingRequest.getSessionType().equals(bookingScheduleConfig.getSessionType())
          && bookingRequest.getUsername().equals(username)
          && scheduleConfigDate.equals(bookingDate)
          && courtNumbersMatch(bookingRequestCourtNumber, scheduleConfigCourtNumber)
          && bookingRequest.getDuration().equals(bookingScheduleConfig.getDuration())
      ) {
        logger.debug("Booking scheduled config id {} has a successful booking response id {} "
                + "already executed today with the same booking criteria as the scheduled config",
            bookingScheduleConfig.getId(), successfulBookingResponse.getId());
        return true;
      }
    }
    return false;
  }

  /**
   * Check if booking request and scheduled config court numbers match.
   */
  private static boolean courtNumbersMatch(Integer bookingRequestCourtNumber,
      Integer scheduleConfigCourtNumber) {
    if (bookingRequestCourtNumber == null) {
      return scheduleConfigCourtNumber == null || scheduleConfigCourtNumber == 0;
    }
    if (scheduleConfigCourtNumber == null) {
      return bookingRequestCourtNumber == 0;
    }
    return bookingRequestCourtNumber.equals(scheduleConfigCourtNumber);
  }

  /**
   * Returns true if the bookingDate is on or after the current date.
   */
  private boolean isValidBookingDate(Date bookingDate) {
    return DateUtils.isOnOrAfter(DateUtils.getCurrentDate(), bookingDate);
  }

  /**
   * Returns true if the bookingDate is 'bookAheadDays' days ahead from the current date.
   */
  private boolean scheduledBookingDateMatchesBookAheadDays(
      BookingScheduleConfig bookingScheduleConfig) {
    Date bookingDate = bookingScheduleConfig.getBookingDate();
    long daysToBookingDate = DateUtils.getDaysBetweenDates(DateUtils.getCurrentDate(), bookingDate);
    return daysToBookingDate == bookingScheduleConfig.getBookAheadDays();
  }

  /**
   * Sets the current processing thread id from the tennisworld request id.
   */
  private static void setThreadName(Long requestId) {
    StringBuilder sb = new StringBuilder();
    sb.append("twb-");
    sb.append(requestId);
    ThreadUtils.setCurrentThreadName(sb.toString());
  }

  /**
   * Persist the booking request in the database.
   */
  private Long persistBookingRequest(BookingRequest bookingRequest) {
    Long requestId = bookingRequestService.create(bookingRequest.buildDto());
    bookingRequest.setId(requestId);
    return requestId;
  }

  /**
   * Validate the format of the booking request fields.
   */
  private void validateRequest(BookingRequest bookingRequest) {
    if (bookingRequest.getDate() == null) {
      throw new KameHouseInvalidDataException("date not set");
    }
    if (bookingRequest.getTime() == null) {
      throw new KameHouseInvalidDataException("time not set");
    }
    if (!bookingRequest.getTime().matches(TIME_PATTERN)) {
      throw new KameHouseInvalidDataException(
          "time has an invalid value of " + bookingRequest.getTime());
    }
    if (bookingRequest.getSite() == null) {
      throw new KameHouseInvalidDataException("site not set");
    }
    if (bookingRequest.getSessionType() == null) {
      throw new KameHouseInvalidDataException("sessionType not set");
    }
    if (bookingRequest.getUsername() == null) {
      throw new KameHouseInvalidDataException("username not set");
    }
  }

  /**
   * Create a tennisworld booking request based on the schedule config.
   */
  private BookingRequest createScheduledBookingRequest(
      BookingScheduleConfig bookingScheduleConfig) {
    BookingRequest request = new BookingRequest();
    request.setDate(getBookingDateFromBookingScheduleConfig(bookingScheduleConfig));
    TennisWorldUser tennisWorldUser = bookingScheduleConfig.getTennisWorldUser();
    request.setUsername(tennisWorldUser.getEmail());
    request.setPassword(getDecryptedPassword(tennisWorldUser));
    request.setDryRun(false);
    request.setDuration(bookingScheduleConfig.getDuration());
    request.setSessionType(bookingScheduleConfig.getSessionType());
    request.setSite(bookingScheduleConfig.getSite());
    request.setTime(bookingScheduleConfig.getTime());
    request.setScheduled(true);
    return request;
  }

  /**
   * Get the bookingDate calculated from the schedule config.
   */
  private Date getBookingDateFromBookingScheduleConfig(
      BookingScheduleConfig bookingScheduleConfig) {
    if (bookingScheduleConfig.getBookingDate() != null) {
      return bookingScheduleConfig.getBookingDate();
    } else {
      int bookAheadDays = bookingScheduleConfig.getBookAheadDays();
      return DateUtils.getDateFromToday(bookAheadDays);
    }
  }

  /**
   * Gets the decrypted password for the tennisworld user.
   */
  private String getDecryptedPassword(TennisWorldUser tennisWorldUser) {
    return EncryptionUtils.decryptToString(
        tennisWorldUser.getPassword(), EncryptionUtils.getKameHousePrivateKey());
  }
}
