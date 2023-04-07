package com.nicobrest.kamehouse.tennisworld.service;

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
import com.nicobrest.kamehouse.tennisworld.testutils.BookingRequestTestUtils;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingResponseTestUtils;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingScheduleConfigTestUtils;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

/**
 * Test class for the AbstractBookingService.
 *
 * @author nbrest
 */
public class AbstractBookingServiceTest {

  private BookingRequestTestUtils bookingRequestTestUtils = new BookingRequestTestUtils();
  private BookingResponseTestUtils bookingResponseTestUtils = new BookingResponseTestUtils();
  private BookingScheduleConfigTestUtils bookingScheduleConfigTestUtils =
      new BookingScheduleConfigTestUtils();

  @InjectMocks
  private SampleBookingService sampleBookingService;

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

  private MockedStatic<HttpClientUtils> httpClientUtilsMock;
  private MockedStatic<DateUtils> dateUtilsMock;
  private MockedStatic<EncryptionUtils> encryptionUtilsMock;

  /**
   * Tests setup.
   */
  @BeforeEach
  public void init() throws Exception {
    bookingRequestTestUtils.initTestData();
    SampleBookingService bookingService = new SampleBookingService();
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
    when(HttpClientUtils.getHeader(any(), any())).thenReturn(ActiveCarrotBookingService.ROOT_URL);
    when(HttpClientUtils.getStatusCode(any())).thenReturn(HttpStatus.FOUND.value());

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

    encryptionUtilsMock = Mockito.mockStatic(EncryptionUtils.class);
    when(EncryptionUtils.decrypt(any(), any())).thenReturn(new byte[2]);

    when(bookingScheduleConfigService.readAll())
        .thenReturn(bookingScheduleConfigTestUtils.getTestDataList());
    when(bookingRequestService.create((any()))).thenReturn(1L);
    when(bookingResponseService.create((any()))).thenReturn(1L);
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
  public void bookSuccessTest() {
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = sampleBookingService.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a recurring scheduled session.
   */
  @Test
  public void bookRecurringScheduledSessionSuccessTest() {
    Date currentDate = DateUtils.getDate(2021, Calendar.JULY, 11);
    Date bookingDate = DateUtils.getDate(2021, Calendar.JULY, 26);
    when(DateUtils.getCurrentDate()).thenReturn(currentDate);
    when(DateUtils.getDateFromToday(any())).thenReturn(bookingDate);
    when(DateUtils.getDay(any(Date.class))).thenReturn(DateUtils.Day.MONDAY);

    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.getRequest().setScheduled(true);

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
    bookingResponseTestUtils.updateResponseWithCardioRequestData(
        expected, bookingDate, "19:15", SessionType.CARDIO, "45");
    expected.getRequest().setCardDetails(null);

    List<BookingResponse> response = sampleBookingService.bookScheduledSessions();
    bookingResponseTestUtils.matchDynamicFields(response.get(0), expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response.get(0));
  }

  /**
   * Test booking a one off scheduled session.
   */
  @Test
  public void bookOneOffScheduledSessionSuccessTest() {
    Date currentDate = DateUtils.getDate(2021, Calendar.JULY, 11);
    Date bookingDate = DateUtils.getDate(2021, Calendar.JULY, 26);
    when(DateUtils.getCurrentDate()).thenReturn(currentDate);
    when(DateUtils.getDateFromToday(any())).thenReturn(bookingDate);
    when(DateUtils.isOnOrAfter(any(), any())).thenReturn(true);
    when(DateUtils.getDaysBetweenDates(any(), any())).thenReturn(14L);
    when(DateUtils.getDay(any(Date.class))).thenReturn(DateUtils.Day.MONDAY);

    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.getRequest().setScheduled(true);

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
    bookingResponseTestUtils.updateResponseWithCardioRequestData(
        expected, bookingDate, "19:15", SessionType.CARDIO, "45");

    List<BookingResponse> response = sampleBookingService.bookScheduledSessions();
    bookingResponseTestUtils.matchDynamicFields(response.get(0), expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response.get(0));
  }

  /**
   * Test booking empty scheduled sessions.
   */
  @Test
  public void bookEmptyScheduledSessionTest() {
    when(bookingScheduleConfigService.readAll()).thenReturn(null);

    List<BookingResponse> response = sampleBookingService.bookScheduledSessions();

    assertTrue(response.isEmpty());
  }

  /**
   * Test booking a disabled scheduled session.
   */
  @Test
  public void bookDisabledScheduledSessionTest() {
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
  public void bookInvalidScheduledSessionTest() {
    when(DateUtils.isOnOrAfter(any(), any())).thenReturn(true);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.getRequest().setScheduled(true);

    BookingScheduleConfig bookingScheduleConfig =
        bookingScheduleConfigTestUtils.getSingleTestData();
    bookingScheduleConfig.setEnabled(true);
    bookingScheduleConfig.setTime(null);
    bookingScheduleConfig.setBookingDate(DateUtils.getCurrentDate());
    bookingScheduleConfig.setBookAheadDays(0);
    when(bookingScheduleConfigService.readAll()).thenReturn(List.of(bookingScheduleConfig));

    List<BookingResponse> response = sampleBookingService.bookScheduledSessions();

    assertTrue(response.isEmpty());
  }

  /**
   * Test booking with invalid session type flow.
   */
  @Test
  public void bookInvalidSessionTypeTest() {
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          BookingRequest request = bookingRequestTestUtils.getSingleTestData();
          request.setSessionType(null);

          sampleBookingService.book(request);
        });
  }

  /**
   * Test booking with invalid date flow.
   */
  @Test
  public void bookInvalidDateTest() {
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          BookingRequest request = bookingRequestTestUtils.getSingleTestData();
          request.setDate(null);

          sampleBookingService.book(request);
        });
  }

  /**
   * Test booking with invalid time flow.
   */
  @Test
  public void bookInvalidTimeTest() {
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          BookingRequest request = bookingRequestTestUtils.getSingleTestData();
          request.setTime(null);

          sampleBookingService.book(request);
        });
  }

  /**
   * Test booking with invalid date flow.
   */
  @Test
  public void bookInvalidSiteTest() {
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          BookingRequest request = bookingRequestTestUtils.getSingleTestData();
          request.setSite(null);

          sampleBookingService.book(request);
        });
  }

  /**
   * Test booking with invalid username flow.
   */
  @Test
  public void bookInvalidUsernameTest() {
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          BookingRequest request = bookingRequestTestUtils.getSingleTestData();
          request.setUsername(null);

          sampleBookingService.book(request);
        });
  }

  private static class SampleBookingService extends BookingService {

    @Override
    protected BookingResponse executeBookingRequestOnTennisWorld(BookingRequest bookingRequest) {
      BookingResponse bookingResponse = new BookingResponse();
      bookingResponse.setRequest(bookingRequest);
      bookingResponse.setMessage(SUCCESSFUL_BOOKING);
      bookingResponse.setStatus(Status.SUCCESS);
      bookingResponse.setId(1000L);
      return bookingResponse;
    }
  }
}
