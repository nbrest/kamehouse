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

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private static int sleepMs = 500;

  @Autowired
  private BookingScheduleConfigService bookingScheduleConfigService;

  @Autowired
  private BookingRequestService bookingRequestService;

  @Autowired
  private BookingResponseService bookingResponseService;

  /**
   * Build and send the booking response to the active tennis world implementation.
   */
  protected abstract BookingResponse executeBookingRequestOnTennisWorld(
      BookingRequest bookingRequest);

  /**
   * Set the sleep ms between requests.
   */
  public static void setSleepMs(int sleepMs) {
    BookingService.sleepMs = sleepMs;
  }

  /**
   * Initiate a booking request to tennis world.
   */
  public BookingResponse book(BookingRequest bookingRequest) {
    validateRequest(bookingRequest);
    Long requestId = persistBookingRequest(bookingRequest);
    setThreadName(requestId);
    logger.info("Booking tennis world request: {}", bookingRequest);
    return executeBookingRequestOnTennisWorld(bookingRequest);
  }

  /**
   * Execute the scheduled bookings based on the BookingScheduleConfigs set in the database. This
   * method is to be triggered by the {@link ScheduledBookingJob}. It can also be manually triggered
   * through an API.
   */
  public List<BookingResponse> bookScheduledSessions() {
    List<BookingScheduleConfig> bookingScheduleConfigs = bookingScheduleConfigService.readAll();
    List<BookingResponse> bookingResponses = new ArrayList<>();
    if (bookingScheduleConfigs == null || bookingScheduleConfigs.isEmpty()) {
      logger.info("No scheduled booking configurations setup in this server");
      return bookingResponses;
    }
    for (BookingScheduleConfig bookingScheduleConfig : bookingScheduleConfigs) {
      try {
        BookingResponse bookingResponse = processScheduledBookingConfig(bookingScheduleConfig);
        if (bookingResponse != null) {
          bookingResponses.add(bookingResponse);
        }
      } catch (KameHouseException e) {
        logger.error("Error executing scheduled booking for {}", bookingScheduleConfig, e);
      }
    }
    logScheduledBookingResponses(bookingResponses);
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
    Long responseId = bookingResponseService.create(bookingResponse.buildDto());
    bookingResponse.setId(responseId);
    return bookingResponse;
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
   * Log the output of the scheduled booking responses.
   */
  private void logScheduledBookingResponses(List<BookingResponse> bookingResponses) {
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
    logger.info("Booking scheduled sessions finished. Executed {} booking requests. {} successful "
            + "requests and {} failed requests", bookingResponses.size(), successfulBookings,
        failedBookings);
    logger.debug("Scheduled booking responses {}", bookingResponses);
  }

  /**
   * Determine if a booking request should be triggered for the specified booking schedule config
   * and execute the valid booking requests for the current date.
   */
  private BookingResponse processScheduledBookingConfig(
      BookingScheduleConfig bookingScheduleConfig) {
    logger.trace("Processing {}", bookingScheduleConfig);
    if (!bookingScheduleConfig.getEnabled()) {
      logger.debug("BookingScheduleConfig id {} is disabled. Skipping.",
          bookingScheduleConfig.getId());
      return null;
    }
    Date bookingDate = calculateBookingDate(bookingScheduleConfig);
    if (bookingDate == null) {
      logger.debug("No scheduled booking to be executed today for BookingScheduleConfig id {}",
          bookingScheduleConfig.getId());
      return null;
    }
    BookingRequest bookingRequest =
        createScheduledBookingRequest(bookingScheduleConfig, bookingDate);
    return book(bookingRequest);
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
   * Get the bookingDate calculated from the schedule config. If null is returned then no booking
   * should be executed.
   */
  private Date calculateBookingDate(BookingScheduleConfig bookingScheduleConfig) {
    if (bookingScheduleConfig.getBookingDate() != null) {
      /*
       * One-off bookingDate is set in the config. Check if it's should be used to book.
       */
      Date bookingDate = bookingScheduleConfig.getBookingDate();
      if (isActiveBookingDate(bookingDate)
          && bookingDateMatchesBookAheadDays(bookingScheduleConfig)) {
        logger.trace("One-off bookingDate {}", bookingDate);
        return bookingDate;
      }
    } else {
      /*
       * One-off bookingDate is not set in the config. Checking recurring booking
       * schedule config
       */
      int bookAheadDays = bookingScheduleConfig.getBookAheadDays();
      Date bookingDate = DateUtils.getDateFromToday(bookAheadDays);
      DateUtils.Day configDay = bookingScheduleConfig.getDay();
      DateUtils.Day bookingDateDay = DateUtils.getDay(bookingDate);
      if (configDay == bookingDateDay) {
        logger.trace("Calculated recurring bookingDate {}", bookingDate);
        return bookingDate;
      }
    }
    return null;
  }

  /**
   * Returns true if the bookingDate is on or after the current date.
   */
  private boolean isActiveBookingDate(Date bookingDate) {
    if (bookingDate == null) {
      return false;
    }
    return DateUtils.isOnOrAfter(DateUtils.getCurrentDate(), bookingDate);
  }

  /**
   * Returns true if the bookingDate is 'bookAheadDays' days ahead from the current date.
   */
  private boolean bookingDateMatchesBookAheadDays(BookingScheduleConfig bookingScheduleConfig) {
    Date bookingDate = bookingScheduleConfig.getBookingDate();
    long daysToBookingDate = DateUtils.getDaysBetweenDates(DateUtils.getCurrentDate(), bookingDate);
    return daysToBookingDate == bookingScheduleConfig.getBookAheadDays();
  }

  /**
   * Create a tennisworld booking request based on the schedule config.
   */
  private BookingRequest createScheduledBookingRequest(
      BookingScheduleConfig bookingScheduleConfig, Date bookingDate) {
    BookingRequest request = new BookingRequest();
    request.setDate(bookingDate);
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
   * Gets the decrypted password for the tennisworld user.
   */
  private String getDecryptedPassword(TennisWorldUser tennisWorldUser) {
    return EncryptionUtils.decryptToString(
        tennisWorldUser.getPassword(), EncryptionUtils.getKameHousePrivateKey());
  }
}
