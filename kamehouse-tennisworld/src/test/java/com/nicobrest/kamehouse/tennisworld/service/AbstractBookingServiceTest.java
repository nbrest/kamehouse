package com.nicobrest.kamehouse.tennisworld.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.utils.DateUtils;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse.Status;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import com.nicobrest.kamehouse.tennisworld.model.SessionType;
import com.nicobrest.kamehouse.tennisworld.model.Site;
import com.nicobrest.kamehouse.tennisworld.model.perfectgym.RequestBody;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingRequestTestUtils;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingResponseTestUtils;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingScheduleConfigTestUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test class for the AbstractBookingService.
 *
 * @author nbrest
 */
class AbstractBookingServiceTest {

  private BookingRequestTestUtils bookingRequestTestUtils = new BookingRequestTestUtils();
  private BookingResponseTestUtils bookingResponseTestUtils = new BookingResponseTestUtils();
  private BookingScheduleConfigTestUtils bookingScheduleConfigTestUtils =
      new BookingScheduleConfigTestUtils();
  private static final StatusLine STATUS_LINE_200 = new BasicStatusLine(
      new ProtocolVersion("http", 1, 1), 200, "OK");
  private static final String RESPONSE_BODY = "{\"response\":\"dane\"}";

  @Mock
  private BookingScheduleConfigService bookingScheduleConfigService;

  @Mock
  private BookingRequestService bookingRequestService;

  @Mock
  private BookingResponseService bookingResponseService;

  @Mock
  HttpClient httpClientMock;

  @Mock
  HttpResponse httpResponseMock;

  @InjectMocks
  private SampleBookingService sampleBookingService;

  private MockedStatic<HttpClientUtils> httpClientUtilsMock;
  private MockedStatic<DateUtils> dateUtilsMock;
  private MockedStatic<EncryptionUtils> encryptionUtilsMock;

  /**
   * Tests setup.
   */
  @BeforeEach
  public void init() throws Exception {
    bookingRequestTestUtils.initTestData();
    SampleBookingService bookingService = new SampleBookingService(bookingScheduleConfigService,
        bookingRequestService, bookingResponseService);
    sampleBookingService = Mockito.spy(bookingService);
    SampleBookingService.setSleepMs(0);
    bookingResponseTestUtils.initTestData();
    bookingScheduleConfigTestUtils.initTestData();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(httpClientMock);
    Mockito.reset(httpResponseMock);

    httpClientUtilsMock = Mockito.mockStatic(HttpClientUtils.class);
    when(HttpClientUtils.getClient(any(), any())).thenReturn(httpClientMock);
    when(HttpClientUtils.execRequest(any(), any())).thenReturn(httpResponseMock);
    when(HttpClientUtils.httpGet(any())).thenCallRealMethod();
    when(HttpClientUtils.getHeader(any(), any())).thenCallRealMethod();
    when(HttpClientUtils.getStatusCode(any())).thenCallRealMethod();
    when(HttpClientUtils.getStatusLine(any())).thenReturn(STATUS_LINE_200);
    when(HttpClientUtils.hasResponseBody(any())).thenReturn(true);
    when(HttpClientUtils.getInputStream(any())).thenReturn(
        new ByteArrayInputStream(RESPONSE_BODY.getBytes(Charsets.UTF_8)));
    when(HttpClientUtils.hasHeaders(any(HttpRequest.class))).thenReturn(true);
    HttpRequest httpRequest = new HttpGet("http://goku.gohan");
    httpRequest.setHeader("goku", "gohan");
    when(HttpClientUtils.getAllHeaders(any(HttpRequest.class))).thenReturn(
        httpRequest.getAllHeaders());
    when(HttpClientUtils.hasHeaders(any(HttpResponse.class))).thenCallRealMethod();
    when(HttpClientUtils.getAllHeaders(any(HttpResponse.class))).thenCallRealMethod();

    dateUtilsMock = Mockito.mockStatic(DateUtils.class);
    when(DateUtils.getCurrentDate()).thenCallRealMethod();
    when(DateUtils.getTwoWeeksFrom(any())).thenCallRealMethod();
    when(DateUtils.getDate(any(), any(), any())).thenCallRealMethod();
    when(DateUtils.getDate(any(), any(), any(), any(), any(), any())).thenCallRealMethod();
    when(DateUtils.getFormattedDate(any())).thenCallRealMethod();
    when(DateUtils.getFormattedDate(any(), any())).thenCallRealMethod();
    when(DateUtils.getCurrentDayOfWeek()).thenCallRealMethod();
    when(DateUtils.getDayOfWeek(any())).thenCallRealMethod();
    when(DateUtils.convertTime(any(), any(), any())).thenCallRealMethod();
    when(DateUtils.convertTime(any(), any(), any(), any(Boolean.class))).thenCallRealMethod();
    when(DateUtils.isOnOrAfter(any(), any())).thenReturn(true);

    encryptionUtilsMock = Mockito.mockStatic(EncryptionUtils.class);
    when(EncryptionUtils.decrypt(any(), any())).thenReturn(new byte[2]);

    when(bookingScheduleConfigService.readAll())
        .thenReturn(bookingScheduleConfigTestUtils.getTestDataList());
    when(bookingRequestService.create((any()))).thenReturn(1L);
    when(bookingResponseService.create((any()))).thenReturn(1L);
    when(bookingResponseService.readAll()).thenReturn(bookingResponseTestUtils.getTestDataList());
  }

  /**
   * Tests cleanup.
   */
  @AfterEach
  public void close() {
    httpClientUtilsMock.close();
    dateUtilsMock.close();
    encryptionUtilsMock.close();
  }

  /**
   * Test booking success flow.
   */
  @Test
  void bookSuccessTest() {
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = sampleBookingService.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking error flow.
   */
  @Test
  void bookErrorTest() throws IOException {
    when(HttpClientUtils.hasResponseBody(any())).thenReturn(false);
    when(HttpClientUtils.hasHeaders(any(HttpRequest.class))).thenCallRealMethod();
    when(HttpClientUtils.getAllHeaders(any(HttpRequest.class))).thenCallRealMethod();

    BookingRequest request = bookingRequestTestUtils.getTestDataList().get(2);
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(2);
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = sampleBookingService.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a recurring scheduled session.
   */
  @Test
  void bookRecurringScheduledSessionSuccessTest() {
    Date currentDate = DateUtils.getDate(2021, Calendar.JULY, 11, 23, 30, 15);
    Date bookingDate = DateUtils.getDate(2021, Calendar.JULY, 26);
    when(DateUtils.getCurrentDate()).thenReturn(currentDate);
    when(DateUtils.getDateFromToday(any())).thenReturn(bookingDate);
    when(DateUtils.getDay(any(Date.class))).thenReturn(DateUtils.Day.MONDAY);

    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.getRequest().setScheduled(true);
    expected.getRequest().setRetries(BookingService.MAX_BOOKING_RETRIES);

    BookingScheduleConfig bookingScheduleConfig =
        bookingScheduleConfigTestUtils.getSingleTestData();
    bookingScheduleConfig.setBookingDate(null);
    bookingScheduleConfig.setDay(DateUtils.Day.MONDAY);
    bookingScheduleConfig.setTime("19:15");
    bookingScheduleConfig.setId(1L);
    bookingScheduleConfig.setBookAheadDays(14);
    bookingScheduleConfig.setEnabled(true);
    bookingScheduleConfig.setSessionType(SessionType.CARDIO);
    bookingScheduleConfig.setSite(Site.MELBOURNE_PARK);
    BookingResponseTestUtils.updateResponseWithRequestData(
        expected, bookingDate, "19:15", SessionType.CARDIO, "45");
    expected.getRequest().setCardDetails(null);

    List<BookingResponse> response = sampleBookingService.bookScheduledSessions();
    BookingResponseTestUtils.matchDynamicFields(response.get(0), expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response.get(0));
  }

  /**
   * Test booking a one off scheduled session success flow.
   */
  @Test
  void bookOneOffScheduledSessionSuccessTest() {
    Date currentDate = DateUtils.getDate(2021, Calendar.JULY, 11, 23, 30, 15);
    Date bookingDate = DateUtils.getDate(2021, Calendar.JULY, 26);
    when(DateUtils.getCurrentDate()).thenReturn(currentDate);
    when(DateUtils.getDateFromToday(any())).thenReturn(bookingDate);
    when(DateUtils.isOnOrAfter(any(), any())).thenReturn(true);
    when(DateUtils.getDaysBetweenDates(any(), any())).thenReturn(14L);
    when(DateUtils.getDay(any(Date.class))).thenReturn(DateUtils.Day.MONDAY);

    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.getRequest().setScheduled(true);
    expected.getRequest().setRetries(BookingService.MAX_BOOKING_RETRIES);

    BookingScheduleConfig bookingScheduleConfig =
        bookingScheduleConfigTestUtils.getSingleTestData();
    bookingScheduleConfig.setBookingDate(bookingDate);
    bookingScheduleConfig.setDay(DateUtils.Day.MONDAY);
    bookingScheduleConfig.setTime("19:15");
    bookingScheduleConfig.setId(1L);
    bookingScheduleConfig.setBookAheadDays(14);
    bookingScheduleConfig.setEnabled(true);
    bookingScheduleConfig.setSessionType(SessionType.CARDIO);
    bookingScheduleConfig.setSite(Site.MELBOURNE_PARK);
    BookingResponseTestUtils.updateResponseWithRequestData(
        expected, bookingDate, "19:15", SessionType.CARDIO, "45");

    List<BookingResponse> response = sampleBookingService.bookScheduledSessions();
    BookingResponseTestUtils.matchDynamicFields(response.get(0), expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response.get(0));
  }

  /**
   * Test booking a one off scheduled session error flow.
   */
  @Test
  void bookOneOffScheduledSessionErrorTest() {
    Date currentDate = DateUtils.getDate(2021, Calendar.JULY, 11, 23, 30, 15);
    Date bookingDate = DateUtils.getDate(2021, Calendar.JULY, 26);
    when(DateUtils.getCurrentDate()).thenReturn(currentDate);
    when(DateUtils.getDateFromToday(any())).thenReturn(bookingDate);
    when(DateUtils.isOnOrAfter(any(), any())).thenReturn(true);
    when(DateUtils.getDaysBetweenDates(any(), any())).thenReturn(14L);
    when(DateUtils.getDay(any(Date.class))).thenReturn(DateUtils.Day.MONDAY);
    when(bookingResponseService.readAll()).thenReturn(null);

    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(2);
    expected.getRequest().setScheduled(true);
    expected.getRequest().setRetries(BookingService.MAX_BOOKING_RETRIES);

    BookingScheduleConfig bookingScheduleConfig =
        bookingScheduleConfigTestUtils.getSingleTestData();
    bookingScheduleConfig.setBookingDate(bookingDate);
    bookingScheduleConfig.setDay(DateUtils.Day.MONDAY);
    bookingScheduleConfig.setTime("19:15");
    bookingScheduleConfig.setId(1L);
    bookingScheduleConfig.setBookAheadDays(14);
    bookingScheduleConfig.setEnabled(true);
    bookingScheduleConfig.setSessionType(SessionType.ROD_LAVER_SHOW_COURTS);
    bookingScheduleConfig.setSite(Site.MELBOURNE_PARK);
    BookingResponseTestUtils.updateResponseWithRequestData(
        expected, bookingDate, "19:15", SessionType.ROD_LAVER_SHOW_COURTS, "45");

    List<BookingResponse> response = sampleBookingService.bookScheduledSessions();
    BookingResponseTestUtils.matchDynamicFields(response.get(0), expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response.get(0));
  }

  /**
   * Test booking a one off scheduled session on valid date but before session time flow. The
   * booking schedule request should be skipped.
   */
  @Test
  void bookOneOffScheduledSessionValidDateBeforeSessionTimeTest() {
    Date currentDate = DateUtils.getDate(2021, Calendar.JULY, 11, 0, 15, 30);
    Date bookingDate = DateUtils.getDate(2021, Calendar.JULY, 26);
    when(DateUtils.getCurrentDate()).thenReturn(currentDate);
    when(DateUtils.getDateFromToday(any())).thenReturn(bookingDate);
    when(DateUtils.isOnOrAfter(any(), any())).thenReturn(true);
    when(DateUtils.getDaysBetweenDates(any(), any())).thenReturn(14L);
    when(DateUtils.getDay(any(Date.class))).thenReturn(DateUtils.Day.MONDAY);

    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(2);
    expected.getRequest().setScheduled(true);

    BookingScheduleConfig bookingScheduleConfig =
        bookingScheduleConfigTestUtils.getSingleTestData();
    bookingScheduleConfig.setBookingDate(bookingDate);
    bookingScheduleConfig.setDay(DateUtils.Day.MONDAY);
    bookingScheduleConfig.setTime("19:15");
    bookingScheduleConfig.setId(1L);
    bookingScheduleConfig.setBookAheadDays(14);
    bookingScheduleConfig.setEnabled(true);
    bookingScheduleConfig.setSessionType(SessionType.ROD_LAVER_SHOW_COURTS);
    bookingScheduleConfig.setSite(Site.MELBOURNE_PARK);
    BookingResponseTestUtils.updateResponseWithRequestData(
        expected, bookingDate, "19:15", SessionType.ROD_LAVER_SHOW_COURTS, "45");

    List<BookingResponse> response = sampleBookingService.bookScheduledSessions();

    assertTrue(response.isEmpty());
  }

  /**
   * Test booking a session that has been already successfully booked that same day.
   */
  @Test
  void bookAlreadySuccessfullyBookedSessionTest() {
    Date currentDate = DateUtils.getDate(2020, Calendar.JULY, 28, 20, 15, 30);
    when(DateUtils.getCurrentDate()).thenReturn(currentDate);
    BookingResponse successfulBookingResponse = bookingResponseTestUtils.getSingleTestData();
    successfulBookingResponse.getRequest().setScheduled(true);
    successfulBookingResponse.getRequest().setSessionType(SessionType.CARDIO);
    successfulBookingResponse.getRequest().setDuration("45");
    successfulBookingResponse.getRequest().setCreationDate(DateUtils.getCurrentDate());

    BookingScheduleConfig bookingScheduleConfig =
        bookingScheduleConfigTestUtils.getSingleTestData();
    bookingScheduleConfig.setEnabled(true);
    bookingScheduleConfig.setTime("18:45");
    bookingScheduleConfig.setBookingDate(DateUtils.getCurrentDate());
    bookingScheduleConfig.setBookAheadDays(0);
    when(bookingScheduleConfigService.readAll()).thenReturn(List.of(bookingScheduleConfig));

    List<BookingResponse> response = sampleBookingService.bookScheduledSessions();

    assertTrue(response.isEmpty());
  }

  /**
   * Test booking empty scheduled sessions.
   */
  @Test
  void bookEmptyScheduledSessionTest() {
    when(bookingScheduleConfigService.readAll()).thenReturn(null);

    List<BookingResponse> response = sampleBookingService.bookScheduledSessions();

    assertTrue(response.isEmpty());
  }

  /**
   * Test booking a disabled scheduled session.
   */
  @Test
  void bookDisabledScheduledSessionTest() {
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.getRequest().setScheduled(true);

    BookingScheduleConfig bookingScheduleConfig =
        bookingScheduleConfigTestUtils.getSingleTestData();
    bookingScheduleConfig.setEnabled(false);
    when(bookingScheduleConfigService.readAll()).thenReturn(List.of(bookingScheduleConfig));

    List<BookingResponse> response = sampleBookingService.bookScheduledSessions();

    assertTrue(response.isEmpty());
  }

  /**
   * Test booking an invalid scheduled session that throws a KameHouseException while validating the
   * request.
   */
  @Test
  void bookInvalidScheduledSessionTest() {
    Date currentDate = DateUtils.getDate(2021, Calendar.JULY, 11, 23, 59, 59);
    when(DateUtils.getCurrentDate()).thenReturn(currentDate);

    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.getRequest().setScheduled(true);
    expected.getRequest().setCourtNumber(2);

    BookingScheduleConfig invalidConfig = bookingScheduleConfigTestUtils.getSingleTestData();
    invalidConfig.setEnabled(true);
    invalidConfig.setTime("00:003");
    BookingScheduleConfig validConfig1 = new BookingScheduleConfig();
    validConfig1.setTennisWorldUser(invalidConfig.getTennisWorldUser());
    validConfig1.setBookingDate(bookingRequestTestUtils.getSingleTestData().getDate());
    validConfig1.setBookAheadDays(0);
    validConfig1.setDay(DateUtils.Day.FRIDAY);
    validConfig1.setDuration("50");
    validConfig1.setEnabled(true);
    validConfig1.setSessionType(SessionType.ROD_LAVER_OUTDOOR_WESTERN);
    validConfig1.setSite(Site.MELBOURNE_PARK);
    validConfig1.setTime("18:45");
    validConfig1.setCourtNumber(0);

    BookingScheduleConfig validConfig2 = new BookingScheduleConfig();
    validConfig2.setTennisWorldUser(invalidConfig.getTennisWorldUser());
    validConfig2.setBookingDate(bookingRequestTestUtils.getSingleTestData().getDate());
    validConfig2.setBookAheadDays(0);
    validConfig2.setDay(DateUtils.Day.FRIDAY);
    validConfig2.setDuration("45");
    validConfig2.setEnabled(true);
    validConfig2.setSessionType(SessionType.CARDIO);
    validConfig2.setSite(Site.MELBOURNE_PARK);
    validConfig2.setTime("18:45");
    validConfig2.setCourtNumber(0);

    BookingScheduleConfig validConfig3 = new BookingScheduleConfig();
    validConfig3.setTennisWorldUser(invalidConfig.getTennisWorldUser());
    validConfig3.setBookingDate(bookingRequestTestUtils.getSingleTestData().getDate());
    validConfig3.setBookAheadDays(0);
    validConfig3.setDay(DateUtils.Day.FRIDAY);
    validConfig3.setDuration("45");
    validConfig3.setEnabled(true);
    validConfig3.setSessionType(SessionType.ROD_LAVER_OUTDOOR_WESTERN);
    validConfig3.setSite(Site.ALBERT_RESERVE);
    validConfig3.setTime("18:45");
    validConfig3.setCourtNumber(0);

    List<BookingScheduleConfig> configs = new ArrayList<>();
    configs.add(invalidConfig);
    configs.add(validConfig1);
    configs.add(validConfig2);
    configs.add(validConfig3);

    when(bookingScheduleConfigService.readAll()).thenReturn(configs);

    List<BookingResponse> response = sampleBookingService.bookScheduledSessions();

    assertEquals(3, response.size());
  }

  /**
   * Test booking with invalid session type flow.
   */
  @Test
  void bookInvalidSessionTypeTest() {
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    request.setSessionType(null);
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          sampleBookingService.book(request);
        });
  }

  /**
   * Test booking with invalid date flow.
   */
  @Test
  void bookInvalidDateTest() {
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    request.setDate(null);
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          sampleBookingService.book(request);
        });
  }

  /**
   * Test booking with invalid time flow.
   */
  @Test
  void bookInvalidTimeTest() {
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    request.setTime(null);
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          sampleBookingService.book(request);
        });

    BookingRequest request2 = bookingRequestTestUtils.getSingleTestData();
    request2.setTime("00:001");
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          sampleBookingService.book(request2);
        });
  }

  /**
   * Test booking with invalid date flow.
   */
  @Test
  void bookInvalidSiteTest() {
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    request.setSite(null);
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          sampleBookingService.book(request);
        });
  }

  /**
   * Test booking with invalid username flow.
   */
  @Test
  void bookInvalidUsernameTest() {
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    request.setUsername(null);
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          sampleBookingService.book(request);
        });
  }

  private static class SampleBookingService extends BookingService {

    private static final String REQUEST = "{\"request\":\"mada mada\"}";

    public SampleBookingService(BookingScheduleConfigService bookingScheduleConfigService,
        BookingRequestService bookingRequestService,
        BookingResponseService bookingResponseService) {
      super(bookingScheduleConfigService, bookingRequestService, bookingResponseService);
    }

    @Override
    protected BookingResponse executeBookingRequest(BookingRequest bookingRequest) {
      HttpClient httpClient = HttpClientUtils.getClient("", "");
      try {
        executeRequest(httpClient, new HttpGet(""));
        setSleepMs(1);
        sleep();
        HttpResponse response = executeRequest(httpClient, new HttpPost(), new RequestBody() {
          @Override
          public String toString() {
            return REQUEST;
          }
        });
        String responseBody = getResponseBody(response);
        HttpClientUtils.logResponseBody(responseBody);
      } catch (IOException e) {
        logger.info(e.getMessage(), e);
      }
      if (SessionType.ROD_LAVER_SHOW_COURTS.equals(bookingRequest.getSessionType())) {
        return buildResponse(Status.INTERNAL_ERROR, "Server error", bookingRequest);
      }
      return buildResponse(Status.SUCCESS, SUCCESSFUL_BOOKING, bookingRequest);
    }
  }
}
