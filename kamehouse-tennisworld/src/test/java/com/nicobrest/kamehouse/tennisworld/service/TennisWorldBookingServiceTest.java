package com.nicobrest.kamehouse.tennisworld.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import com.nicobrest.kamehouse.commons.utils.DateUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldSessionType;
import com.nicobrest.kamehouse.tennisworld.testutils.TennisWorldBookingRequestTestUtils;
import com.nicobrest.kamehouse.tennisworld.testutils.TennisWorldBookingResponseTestUtils;
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
 * Test class for the TennisWorldBookingService.
 * 
 * @author nbrest
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpClientUtils.class, DateUtils.class, PropertiesUtils.class, FileUtils.class })
public class TennisWorldBookingServiceTest {

  private TennisWorldBookingRequestTestUtils tennisWorldBookingRequestTestUtils =
      new TennisWorldBookingRequestTestUtils();
  private TennisWorldBookingResponseTestUtils tennisWorldBookingResponseTestUtils =
      new TennisWorldBookingResponseTestUtils();
  private TennisWorldBookingService tennisWorldBookingServiceSpy;

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
    tennisWorldBookingRequestTestUtils.initTestData();
    TennisWorldBookingService tennisWorldBookingService = new TennisWorldBookingService();
    tennisWorldBookingServiceSpy = PowerMockito.spy(tennisWorldBookingService);
    tennisWorldBookingService.setSleepMs(0);
    tennisWorldBookingResponseTestUtils.initTestData();

    MockitoAnnotations.initMocks(this);
    Mockito.reset(httpClientMock);
    Mockito.reset(httpResponseMock);

    PowerMockito.mockStatic(HttpClientUtils.class);
    when(HttpClientUtils.getClient(any(), any())).thenReturn(httpClientMock);
    when(HttpClientUtils.execRequest(any(), any())).thenReturn(httpResponseMock);
    when(HttpClientUtils.httpGet(any())).thenCallRealMethod();
    when(HttpClientUtils.getHeader(any(), any())).thenCallRealMethod();
    when(HttpClientUtils.getHeader(any(), any())).thenReturn(TennisWorldBookingService.ROOT_URL);
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
    TennisWorldBookingRequest request = tennisWorldBookingRequestTestUtils.getSingleTestData();
    TennisWorldBookingResponse expected = tennisWorldBookingResponseTestUtils.getSingleTestData();
    tennisWorldBookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    TennisWorldBookingResponse response = tennisWorldBookingServiceSpy.book(request);

    tennisWorldBookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request dry run flow.
   */
  @Test
  public void bookFacilityOverlayRequestDryRunTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES);
    TennisWorldBookingRequest request = tennisWorldBookingRequestTestUtils.getSingleTestData();
    request.setDryRun(true);
    TennisWorldBookingResponse expected = tennisWorldBookingResponseTestUtils.getSingleTestData();
    expected.setMessage(TennisWorldBookingService.SUCCESSFUL_BOOKING_DRY_RUN);
    tennisWorldBookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    TennisWorldBookingResponse response = tennisWorldBookingServiceSpy.book(request);

    tennisWorldBookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request invalid site flow.
   */
  @Test
  public void bookFacilityOverlayRequestInvalidSiteTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES);
    TennisWorldBookingRequest request = tennisWorldBookingRequestTestUtils.getSingleTestData();
    request.setSite("Melbourne Park - Invalid Site");
    TennisWorldBookingResponse expected =
        tennisWorldBookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Invalid site: Melbourne Park - Invalid Site");
    tennisWorldBookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    TennisWorldBookingResponse response = tennisWorldBookingServiceSpy.book(request);

    tennisWorldBookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request invalid session type flow.
   */
  @Test
  public void bookFacilityOverlayRequestInvalidSessionTypeTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES);
    TennisWorldBookingRequest request = tennisWorldBookingRequestTestUtils.getSingleTestData();
    request.setSessionType("Rod Laver Arena Center Court");
    TennisWorldBookingResponse expected =
        tennisWorldBookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Invalid sessionType: Rod Laver Arena Center Court");
    tennisWorldBookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    TennisWorldBookingResponse response = tennisWorldBookingServiceSpy.book(request);

    tennisWorldBookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request invalid date flow.
   */
  @Test
  public void bookFacilityOverlayRequestInvalidDateTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES);
    TennisWorldBookingRequest request = tennisWorldBookingRequestTestUtils.getSingleTestData();
    request.setDate("1800-12-10");
    TennisWorldBookingResponse expected =
        tennisWorldBookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Error getting the selectedSessionDatePath");
    tennisWorldBookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    TennisWorldBookingResponse response = tennisWorldBookingServiceSpy.book(request);

    tennisWorldBookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request invalid time flow.
   */
  @Test
  public void bookFacilityOverlayRequestInvalidTimeTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES);
    TennisWorldBookingRequest request = tennisWorldBookingRequestTestUtils.getSingleTestData();
    request.setTime("00:00am");
    TennisWorldBookingResponse expected =
        tennisWorldBookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Unable to get the selectedSessionPath");
    tennisWorldBookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    TennisWorldBookingResponse response = tennisWorldBookingServiceSpy.book(request);

    tennisWorldBookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request invalid login flow.
   */
  @Test
  public void bookFacilityOverlayRequestInvalidLoginTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_LOGIN_ERROR_RESPONSES);
    when(HttpClientUtils.getStatusCode(any())).thenReturn(HttpStatus.OK.value());
    TennisWorldBookingRequest request = tennisWorldBookingRequestTestUtils.getSingleTestData();
    TennisWorldBookingResponse expected =
        tennisWorldBookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Invalid login to tennis world");
    tennisWorldBookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    TennisWorldBookingResponse response = tennisWorldBookingServiceSpy.book(request);

    tennisWorldBookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request error confirming pay flow.
   */
  @Test
  public void bookFacilityOverlayRequestErrorConfirmingPayTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_PAYMENT_ERROR_RESPONSES);
    TennisWorldBookingRequest request = tennisWorldBookingRequestTestUtils.getSingleTestData();
    TennisWorldBookingResponse expected =
        tennisWorldBookingResponseTestUtils.getTestDataList().get(2);
    expected.setMessage("Error confirming booking result: [Name on card is required, Credit card "
       + "number is invalid, CVV number is required, Expiry month is required, Expiry year is "
       + "required]");
    tennisWorldBookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    TennisWorldBookingResponse response = tennisWorldBookingServiceSpy.book(request);

    tennisWorldBookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request IOException error.
   */
  @Test
  public void bookFacilityOverlayRequestIOExceptionTest() throws IOException {
    when(HttpClientUtils.execRequest(any(), any())).thenThrow(new IOException("IO Error"));
    TennisWorldBookingRequest request = tennisWorldBookingRequestTestUtils.getSingleTestData();
    TennisWorldBookingResponse expected =
        tennisWorldBookingResponseTestUtils.getTestDataList().get(2);
    expected.setMessage("Error executing booking request to tennis world Message: IO Error");
    tennisWorldBookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    TennisWorldBookingResponse response = tennisWorldBookingServiceSpy.book(request);

    tennisWorldBookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a session overlay request success flow.
   */
  @Test
  public void bookSessionOverlayRequestSuccessTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_SESSION_OVERLAY_STANDARD_RESPONSES);
    TennisWorldBookingRequest request = tennisWorldBookingRequestTestUtils.getSessionRequest();
    TennisWorldBookingResponse expected = tennisWorldBookingResponseTestUtils.getSingleTestData();
    tennisWorldBookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    TennisWorldBookingResponse response = tennisWorldBookingServiceSpy.book(request);

    tennisWorldBookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a session overlay request dry run flow.
   */
  @Test
  public void bookSessionOverlayRequestDryRunTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_SESSION_OVERLAY_STANDARD_RESPONSES);
    TennisWorldBookingRequest request = tennisWorldBookingRequestTestUtils.getSessionRequest();
    request.setDryRun(true);
    TennisWorldBookingResponse expected = tennisWorldBookingResponseTestUtils.getSingleTestData();
    expected.setMessage(TennisWorldBookingService.SUCCESSFUL_BOOKING_DRY_RUN);
    tennisWorldBookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    TennisWorldBookingResponse response = tennisWorldBookingServiceSpy.book(request);

    tennisWorldBookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test unhandled session type error flow.
   */
  @Test
  public void bookSessionOverlayRequestErrorTest() {
    TennisWorldBookingRequest request = tennisWorldBookingRequestTestUtils.getSessionRequest();
    request.setSessionType(TennisWorldSessionType.UNKNOWN.name());
    TennisWorldBookingResponse expected = tennisWorldBookingResponseTestUtils.getSingleTestData();
    expected.setStatus(TennisWorldBookingResponse.Status.INTERNAL_ERROR);
    expected.setMessage("Unhandled sessionType: UNKNOWN");
    tennisWorldBookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    TennisWorldBookingResponse response = tennisWorldBookingServiceSpy.book(request);

    tennisWorldBookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
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
    TennisWorldBookingResponse expected = tennisWorldBookingResponseTestUtils.getSingleTestData();
    tennisWorldBookingResponseTestUtils.updateResponseWithCardioRequestData(expected,
        "12:00pm", "2021-07-25");

    TennisWorldBookingResponse response = tennisWorldBookingServiceSpy.bookScheduledCardioSession();

    tennisWorldBookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
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
    TennisWorldBookingResponse expected = tennisWorldBookingResponseTestUtils.getSingleTestData();
    tennisWorldBookingResponseTestUtils.updateResponseWithCardioRequestData(expected,
        "07:15pm", "2021-07-26");

    TennisWorldBookingResponse response = tennisWorldBookingServiceSpy.bookScheduledCardioSession();

    tennisWorldBookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a scheduled cardio session for unscheduled days.
   */
  @Test
  public void bookScheduledCardioSessionUnscheduledDaysSuccessTest() {
    when(DateUtils.getCurrentDayOfWeek()).thenReturn(Calendar.TUESDAY);
    TennisWorldBookingResponse expected = tennisWorldBookingResponseTestUtils.getSingleTestData();
    expected.setMessage("Today is Tuesday. No cardio booking is scheduled.");
    tennisWorldBookingResponseTestUtils.updateResponseWithCardioRequestData(expected,
        null, null);

    TennisWorldBookingResponse response = tennisWorldBookingServiceSpy.bookScheduledCardioSession();

    expected.setDate(response.getDate());
    tennisWorldBookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a scheduled cardio session from an invalid booking server.
   */
  @Test
  public void bookScheduledCardioSessionFromInvalidBookingServerTest() {
    PowerMockito.when(PropertiesUtils.getProperty("booking.server")).thenReturn("namek-host");
    when(DateUtils.getCurrentDayOfWeek()).thenReturn(Calendar.MONDAY);
    TennisWorldBookingResponse expected = tennisWorldBookingResponseTestUtils.getSingleTestData();
    expected.setStatus(TennisWorldBookingResponse.Status.INTERNAL_ERROR);
    expected.setMessage(TennisWorldBookingService.INVALID_BOOKING_SERVER);

    TennisWorldBookingResponse response = tennisWorldBookingServiceSpy.bookScheduledCardioSession();

    expected.setDate(response.getDate());
    tennisWorldBookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Setup http response input stream mocks from files in test resources.
   */
  private void setupHttpResponseInputStreamMocks(String[] testFilenames) throws Exception {
    OngoingStubbing ongoingStubbing = when(HttpClientUtils.getInputStream(any()));
    for (String testFilename : testFilenames) {
      InputStream testInputStream = tennisWorldBookingRequestTestUtils.getInputStream(testFilename);
      ongoingStubbing = ongoingStubbing.thenReturn(testInputStream);
    }
  }
}
