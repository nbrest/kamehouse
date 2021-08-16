package com.nicobrest.kamehouse.tennisworld.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import com.nicobrest.kamehouse.commons.utils.DateUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.SessionType;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingRequestTestUtils;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingResponseTestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * Test class for the BookingService.
 * 
 * @author nbrest
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpClientUtils.class, DateUtils.class, PropertiesUtils.class, FileUtils.class })
public class BookingServiceTest {

  private BookingRequestTestUtils bookingRequestTestUtils =
      new BookingRequestTestUtils();
  private BookingResponseTestUtils bookingResponseTestUtils =
      new BookingResponseTestUtils();
  private BookingService bookingServiceSpy;

  private static final String[] BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES = {
      "facility-booking-responses/step-1.1.html",
      "facility-booking-responses/step-1.2.html",
      "facility-booking-responses/step-1.3.html",
      "facility-booking-responses/step-2.html",
      "facility-booking-responses/step-3.html",
      "facility-booking-responses/step-4.html",
      "facility-booking-responses/step-5.html",
      "facility-booking-responses/step-6.html",
      "facility-booking-responses/step-7.html",
      "facility-booking-responses/step-8.html"
  };

  private static final String[] BOOK_FACILITY_OVERLAY_PAYMENT_ERROR_RESPONSES = {
      "facility-booking-responses/step-1.1.html",
      "facility-booking-responses/step-1.2.html",
      "facility-booking-responses/step-1.3.html",
      "facility-booking-responses/step-2.html",
      "facility-booking-responses/step-3.html",
      "facility-booking-responses/step-4.html",
      "facility-booking-responses/step-5.html",
      "facility-booking-responses/step-6.html",
      "facility-booking-responses/step-7.html",
      "facility-booking-responses/step-8-error.html"};

  private static final String[] BOOK_FACILITY_OVERLAY_LOGIN_ERROR_RESPONSES = {
      "facility-booking-responses/step-1.1-error.html"
  };

  private static final String[] BOOK_SESSION_OVERLAY_STANDARD_RESPONSES = {
      "session-booking-responses/step-1.1.html",
      "session-booking-responses/step-1.2.html",
      "session-booking-responses/step-1.3.html",
      "session-booking-responses/step-2.html",
      "session-booking-responses/step-3.html",
      "session-booking-responses/step-4.html",
      "session-booking-responses/step-5.html",
      "session-booking-responses/step-6.html",
      "session-booking-responses/step-7.html",
  };

  private static final String[] BOOK_CARDIO_SESSION_SUNDAY_STANDARD_RESPONSES = {
      "sunday-cardio-booking-responses/step-1.1.html",
      "sunday-cardio-booking-responses/step-1.2.html",
      "sunday-cardio-booking-responses/step-1.3.html",
      "sunday-cardio-booking-responses/step-2.html",
      "sunday-cardio-booking-responses/step-3.html",
      "sunday-cardio-booking-responses/step-4.html",
      "sunday-cardio-booking-responses/step-5.html",
      "sunday-cardio-booking-responses/step-6.html",
      "sunday-cardio-booking-responses/step-7.html",
  };

  private static final String[] BOOK_CARDIO_SESSION_MONDAY_STANDARD_RESPONSES = {
      "monday-cardio-booking-responses/step-1.1.html",
      "monday-cardio-booking-responses/step-1.2.html",
      "monday-cardio-booking-responses/step-1.3.html",
      "monday-cardio-booking-responses/step-2.html",
      "monday-cardio-booking-responses/step-3.html",
      "monday-cardio-booking-responses/step-4.html",
      "monday-cardio-booking-responses/step-5.html",
      "monday-cardio-booking-responses/step-6.html",
      "monday-cardio-booking-responses/step-7.html",
  };

  @Mock
  HttpClient httpClientMock;

  @Mock
  HttpResponse httpResponseMock;

  @Before
  public void init() throws Exception {
    bookingRequestTestUtils.initTestData();
    BookingService bookingService = new BookingService();
    bookingServiceSpy = PowerMockito.spy(bookingService);
    bookingService.setSleepMs(0);
    bookingResponseTestUtils.initTestData();

    MockitoAnnotations.initMocks(this);
    Mockito.reset(httpClientMock);
    Mockito.reset(httpResponseMock);

    PowerMockito.mockStatic(HttpClientUtils.class);
    when(HttpClientUtils.getClient(any(), any())).thenReturn(httpClientMock);
    when(HttpClientUtils.execRequest(any(), any())).thenReturn(httpResponseMock);
    when(HttpClientUtils.httpGet(any())).thenCallRealMethod();
    when(HttpClientUtils.getHeader(any(), any())).thenCallRealMethod();
    when(HttpClientUtils.getHeader(any(), any())).thenReturn(BookingService.ROOT_URL);
    when(HttpClientUtils.getStatusCode(any())).thenReturn(HttpStatus.FOUND.value());

    PowerMockito.mockStatic(DateUtils.class);
    when(DateUtils.getCurrentDate()).thenCallRealMethod();
    when(DateUtils.getTwoWeeksFromToday()).thenCallRealMethod();
    when(DateUtils.getTwoWeeksFrom(any())).thenCallRealMethod();
    when(DateUtils.getDate(any(), any(), any())).thenCallRealMethod();
    when(DateUtils.getDate(any(), any(), any(), any(), any(), any())).thenCallRealMethod();
    when(DateUtils.getFormattedDate(any())).thenCallRealMethod();
    when(DateUtils.getFormattedDate(any(), any())).thenCallRealMethod();
    when(DateUtils.getCurrentDayOfWeek()).thenCallRealMethod();
    when(DateUtils.getDayOfWeek(any())).thenCallRealMethod();

    PowerMockito.mockStatic(PropertiesUtils.class);
    PowerMockito.when(PropertiesUtils.getHostname()).thenReturn("saiyajin-host");
    PowerMockito.when(PropertiesUtils.getProperty("booking.server")).thenReturn("saiyajin-host");
    PowerMockito.when(PropertiesUtils.getProperty("scheduled.cardio.user.file"))
        .thenCallRealMethod();
    PowerMockito.when(PropertiesUtils.getProperty("scheduled.cardio.pwd.file")).thenCallRealMethod();
    PowerMockito.when(PropertiesUtils.getUserHome()).thenCallRealMethod();

    PowerMockito.mockStatic(FileUtils.class);
    PowerMockito.when(FileUtils.getDecodedFileContent((any()))).thenReturn("saiyajin");
  }

  /**
   * Test booking a facility overlay request success flow.
   */
  @Test
  public void bookFacilityOverlayRequestSuccessTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = bookingServiceSpy.book(request);
    bookingResponseTestUtils.matchIds(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request dry run flow.
   */
  @Test
  public void bookFacilityOverlayRequestDryRunTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    request.setDryRun(true);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setMessage(BookingService.SUCCESSFUL_BOOKING_DRY_RUN);
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = bookingServiceSpy.book(request);
    bookingResponseTestUtils.matchIds(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request invalid site flow.
   */
  @Test
  public void bookFacilityOverlayRequestInvalidSiteTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    request.setSite("Melbourne Park - Invalid Site");
    BookingResponse expected =
        bookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Invalid site: Melbourne Park - Invalid Site");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = bookingServiceSpy.book(request);
    bookingResponseTestUtils.matchIds(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request invalid session type flow.
   */
  @Test
  public void bookFacilityOverlayRequestInvalidSessionTypeTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    request.setSessionType("Rod Laver Arena Center Court");
    BookingResponse expected =
        bookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Invalid sessionType: Rod Laver Arena Center Court");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = bookingServiceSpy.book(request);
    bookingResponseTestUtils.matchIds(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request invalid date flow.
   */
  @Test
  public void bookFacilityOverlayRequestInvalidDateTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    request.setDate("1800-12-10");
    BookingResponse expected =
        bookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Error getting the selectedSessionDatePath");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = bookingServiceSpy.book(request);
    bookingResponseTestUtils.matchIds(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request invalid time flow.
   */
  @Test
  public void bookFacilityOverlayRequestInvalidTimeTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    request.setTime("00:00am");
    BookingResponse expected =
        bookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Unable to get the selectedSessionPath");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = bookingServiceSpy.book(request);
    bookingResponseTestUtils.matchIds(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request invalid login flow.
   */
  @Test
  public void bookFacilityOverlayRequestInvalidLoginTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_LOGIN_ERROR_RESPONSES);
    when(HttpClientUtils.getStatusCode(any())).thenReturn(HttpStatus.OK.value());
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    BookingResponse expected =
        bookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Invalid login to tennis world");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = bookingServiceSpy.book(request);
    bookingResponseTestUtils.matchIds(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request error confirming pay flow.
   */
  @Test
  public void bookFacilityOverlayRequestErrorConfirmingPayTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_PAYMENT_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    BookingResponse expected =
        bookingResponseTestUtils.getTestDataList().get(2);
    expected.setMessage("Error confirming booking result: [Name on card is required, Credit card "
       + "number is invalid, CVV number is required, Expiry month is required, Expiry year is "
       + "required]");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = bookingServiceSpy.book(request);
    bookingResponseTestUtils.matchIds(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request IOException error.
   */
  @Test
  public void bookFacilityOverlayRequestIOExceptionTest() throws IOException {
    when(HttpClientUtils.execRequest(any(), any())).thenThrow(new IOException("IO Error"));
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    BookingResponse expected =
        bookingResponseTestUtils.getTestDataList().get(2);
    expected.setMessage("Error executing booking request to tennis world Message: IO Error");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = bookingServiceSpy.book(request);
    bookingResponseTestUtils.matchIds(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a session overlay request success flow.
   */
  @Test
  public void bookSessionOverlayRequestSuccessTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_SESSION_OVERLAY_STANDARD_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getSessionRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = bookingServiceSpy.book(request);
    bookingResponseTestUtils.matchIds(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a session overlay request dry run flow.
   */
  @Test
  public void bookSessionOverlayRequestDryRunTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_SESSION_OVERLAY_STANDARD_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getSessionRequest();
    request.setDryRun(true);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setMessage(BookingService.SUCCESSFUL_BOOKING_DRY_RUN);
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = bookingServiceSpy.book(request);
    bookingResponseTestUtils.matchIds(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test unhandled session type error flow.
   */
  @Test
  public void bookSessionOverlayRequestErrorTest() {
    BookingRequest request = bookingRequestTestUtils.getSessionRequest();
    request.setSessionType(SessionType.UNKNOWN.name());
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(BookingResponse.Status.INTERNAL_ERROR);
    expected.setMessage("Unhandled sessionType: UNKNOWN");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = bookingServiceSpy.book(request);
    bookingResponseTestUtils.matchIds(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a scheduled cardio session for sundays.
   */
  @Test
  public void bookScheduledCardioSessionSundaySuccessTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_CARDIO_SESSION_SUNDAY_STANDARD_RESPONSES);
    Date currentDate = DateUtils.getDate(2021, Calendar.JULY, 11);
    when(DateUtils.getCurrentDate()).thenReturn(currentDate);
    when(DateUtils.getCurrentDayOfWeek()).thenReturn(Calendar.SUNDAY);
    Date bookingDate = DateUtils.getDate(2021, Calendar.JULY, 25);
    when(DateUtils.getTwoWeeksFromToday()).thenReturn(bookingDate);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    bookingResponseTestUtils.updateResponseWithCardioRequestData(expected,
        "12:00pm", "2021-07-25");

    BookingResponse response = bookingServiceSpy.bookScheduledCardioSession();
    bookingResponseTestUtils.matchIds(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a scheduled cardio session for mondays.
   */
  @Test
  public void bookScheduledCardioSessionMondaySuccessTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_CARDIO_SESSION_MONDAY_STANDARD_RESPONSES);
    Date currentDate = DateUtils.getDate(2021, Calendar.JULY, 11);
    when(DateUtils.getCurrentDate()).thenReturn(currentDate);
    when(DateUtils.getCurrentDayOfWeek()).thenReturn(Calendar.MONDAY);
    Date bookingDate = DateUtils.getDate(2021, Calendar.JULY, 26);
    when(DateUtils.getTwoWeeksFromToday()).thenReturn(bookingDate);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    bookingResponseTestUtils.updateResponseWithCardioRequestData(expected,
        "07:15pm", "2021-07-26");

    BookingResponse response = bookingServiceSpy.bookScheduledCardioSession();
    bookingResponseTestUtils.matchIds(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a scheduled cardio session for unscheduled days.
   */
  @Test
  public void bookScheduledCardioSessionUnscheduledDaysSuccessTest() {
    when(DateUtils.getCurrentDayOfWeek()).thenReturn(Calendar.TUESDAY);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setMessage("Today is Tuesday. No cardio booking is scheduled.");
    bookingResponseTestUtils.updateResponseWithCardioRequestData(expected,
        null, null);

    BookingResponse response = bookingServiceSpy.bookScheduledCardioSession();

    expected.setDate(response.getDate());
    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a scheduled cardio session from an invalid booking server.
   */
  @Test
  public void bookScheduledCardioSessionFromInvalidBookingServerTest() {
    PowerMockito.when(PropertiesUtils.getProperty("booking.server")).thenReturn("namek-host");
    when(DateUtils.getCurrentDayOfWeek()).thenReturn(Calendar.MONDAY);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(BookingResponse.Status.INTERNAL_ERROR);
    expected.setMessage(BookingService.INVALID_BOOKING_SERVER);

    BookingResponse response = bookingServiceSpy.bookScheduledCardioSession();
    bookingResponseTestUtils.matchIds(response, expected);

    expected.setDate(response.getDate());
    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Setup http response input stream mocks from files in test resources.
   */
  private void setupHttpResponseInputStreamMocks(String[] testFilenames) throws Exception {
    OngoingStubbing ongoingStubbing = when(HttpClientUtils.getInputStream(any()));
    for (String testFilename : testFilenames) {
      InputStream testInputStream = bookingRequestTestUtils.getInputStream(testFilename);
      ongoingStubbing = ongoingStubbing.thenReturn(testInputStream);
    }
  }
}
