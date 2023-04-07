package com.nicobrest.kamehouse.tennisworld.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.utils.DateUtils;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse.Status;
import com.nicobrest.kamehouse.tennisworld.model.SessionType;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingRequestTestUtils;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingResponseTestUtils;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingScheduleConfigTestUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
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
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.HttpStatus;

/**
 * Test class for the ActiveCarrotBookingService.
 *
 * @author nbrest`
 */
@Deprecated(since = "v8.01")
public class ActiveCarrotBookingServiceTest {

  private BookingRequestTestUtils bookingRequestTestUtils = new BookingRequestTestUtils();
  private BookingResponseTestUtils bookingResponseTestUtils = new BookingResponseTestUtils();
  private BookingScheduleConfigTestUtils bookingScheduleConfigTestUtils =
      new BookingScheduleConfigTestUtils();

  @InjectMocks
  private ActiveCarrotBookingService activeCarrotBookingServiceSpy;

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

  private static final String[] BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES = {
      "activecarrot/facility-booking-responses/step-1.1.html",
      "activecarrot/facility-booking-responses/step-1.2.html",
      "activecarrot/facility-booking-responses/step-1.3.html",
      "activecarrot/facility-booking-responses/step-2.html",
      "activecarrot/facility-booking-responses/step-3.html",
      "activecarrot/facility-booking-responses/step-4.html",
      "activecarrot/facility-booking-responses/step-5.html",
      "activecarrot/facility-booking-responses/step-6.html",
      "activecarrot/facility-booking-responses/step-7.html",
      "activecarrot/facility-booking-responses/step-8.html"
  };

  private static final String[] BOOK_FACILITY_OVERLAY_STEP_1_1_ERROR_RESPONSES = {
      "activecarrot/facility-booking-responses/step-1.1-error.html"
  };

  private static final String[] BOOK_FACILITY_OVERLAY_STEP_1_2_ERROR_RESPONSES = {
      "activecarrot/facility-booking-responses/step-1.1.html",
      "activecarrot/facility-booking-responses/step-1.2-error.html"
  };

  private static final String[] BOOK_FACILITY_OVERLAY_STEP_1_3_ERROR_RESPONSES = {
      "activecarrot/facility-booking-responses/step-1.1.html",
      "activecarrot/facility-booking-responses/step-1.2.html",
      "activecarrot/facility-booking-responses/step-1.3-error.html"
  };

  private static final String[] BOOK_FACILITY_OVERLAY_STEP_4_ERROR_RESPONSES = {
      "activecarrot/facility-booking-responses/step-1.1.html",
      "activecarrot/facility-booking-responses/step-1.2.html",
      "activecarrot/facility-booking-responses/step-1.3.html",
      "activecarrot/facility-booking-responses/step-2.html",
      "activecarrot/facility-booking-responses/step-3.html",
      "activecarrot/facility-booking-responses/step-4-error.html"
  };

  private static final String[] BOOK_FACILITY_OVERLAY_STEP_5_ERROR_RESPONSES = {
      "activecarrot/facility-booking-responses/step-1.1.html",
      "activecarrot/facility-booking-responses/step-1.2.html",
      "activecarrot/facility-booking-responses/step-1.3.html",
      "activecarrot/facility-booking-responses/step-2.html",
      "activecarrot/facility-booking-responses/step-3.html",
      "activecarrot/facility-booking-responses/step-4.html",
      "activecarrot/facility-booking-responses/step-5-error.html"
  };

  private static final String[] BOOK_FACILITY_OVERLAY_STEP_6_ERROR_RESPONSES = {
      "activecarrot/facility-booking-responses/step-1.1.html",
      "activecarrot/facility-booking-responses/step-1.2.html",
      "activecarrot/facility-booking-responses/step-1.3.html",
      "activecarrot/facility-booking-responses/step-2.html",
      "activecarrot/facility-booking-responses/step-3.html",
      "activecarrot/facility-booking-responses/step-4.html",
      "activecarrot/facility-booking-responses/step-5.html",
      "activecarrot/facility-booking-responses/step-6-error.html"
  };

  private static final String[] BOOK_FACILITY_OVERLAY_STEP_7_ERROR_RESPONSES = {
      "activecarrot/facility-booking-responses/step-1.1.html",
      "activecarrot/facility-booking-responses/step-1.2.html",
      "activecarrot/facility-booking-responses/step-1.3.html",
      "activecarrot/facility-booking-responses/step-2.html",
      "activecarrot/facility-booking-responses/step-3.html",
      "activecarrot/facility-booking-responses/step-4.html",
      "activecarrot/facility-booking-responses/step-5.html",
      "activecarrot/facility-booking-responses/step-6.html",
      "activecarrot/facility-booking-responses/step-7-error.html"
  };

  private static final String[] BOOK_FACILITY_OVERLAY_STEP_8_ERROR_RESPONSES = {
      "activecarrot/facility-booking-responses/step-1.1.html",
      "activecarrot/facility-booking-responses/step-1.2.html",
      "activecarrot/facility-booking-responses/step-1.3.html",
      "activecarrot/facility-booking-responses/step-2.html",
      "activecarrot/facility-booking-responses/step-3.html",
      "activecarrot/facility-booking-responses/step-4.html",
      "activecarrot/facility-booking-responses/step-5.html",
      "activecarrot/facility-booking-responses/step-6.html",
      "activecarrot/facility-booking-responses/step-7.html",
      "activecarrot/facility-booking-responses/step-8-error.html"
  };

  private static final String[] BOOK_SESSION_OVERLAY_STANDARD_RESPONSES = {
      "activecarrot/session-booking-responses/step-1.1.html",
      "activecarrot/session-booking-responses/step-1.2.html",
      "activecarrot/session-booking-responses/step-1.3.html",
      "activecarrot/session-booking-responses/step-2.html",
      "activecarrot/session-booking-responses/step-3.html",
      "activecarrot/session-booking-responses/step-4.html",
      "activecarrot/session-booking-responses/step-5.html",
      "activecarrot/session-booking-responses/step-6.html",
      "activecarrot/session-booking-responses/step-7.html",
  };

  private static final String[] BOOK_SESSION_OVERLAY_STEP_1_1_ERROR_RESPONSES = {
      "activecarrot/session-booking-responses/step-1.1-error.html"
  };

  private static final String[] BOOK_SESSION_OVERLAY_STEP_1_2_ERROR_RESPONSES = {
      "activecarrot/session-booking-responses/step-1.1.html",
      "activecarrot/session-booking-responses/step-1.2-error.html"
  };

  private static final String[] BOOK_SESSION_OVERLAY_STEP_1_3_ERROR_RESPONSES = {
      "activecarrot/session-booking-responses/step-1.1.html",
      "activecarrot/session-booking-responses/step-1.2.html",
      "activecarrot/session-booking-responses/step-1.3-error.html"
  };

  private static final String[] BOOK_SESSION_OVERLAY_STEP_3_ERROR_RESPONSES = {
      "activecarrot/session-booking-responses/step-1.1.html",
      "activecarrot/session-booking-responses/step-1.2.html",
      "activecarrot/session-booking-responses/step-1.3.html",
      "activecarrot/session-booking-responses/step-2.html",
      "activecarrot/session-booking-responses/step-3-error.html"
  };

  private static final String[] BOOK_SESSION_OVERLAY_STEP_4_ERROR_RESPONSES = {
      "activecarrot/session-booking-responses/step-1.1.html",
      "activecarrot/session-booking-responses/step-1.2.html",
      "activecarrot/session-booking-responses/step-1.3.html",
      "activecarrot/session-booking-responses/step-2.html",
      "activecarrot/session-booking-responses/step-3.html",
      "activecarrot/session-booking-responses/step-4-error.html"
  };

  private static final String[] BOOK_SESSION_OVERLAY_STEP_5_ERROR_RESPONSES = {
      "activecarrot/session-booking-responses/step-1.1.html",
      "activecarrot/session-booking-responses/step-1.2.html",
      "activecarrot/session-booking-responses/step-1.3.html",
      "activecarrot/session-booking-responses/step-2.html",
      "activecarrot/session-booking-responses/step-3.html",
      "activecarrot/session-booking-responses/step-4.html",
      "activecarrot/session-booking-responses/step-5-error.html"
  };

  private static final String[] BOOK_SESSION_OVERLAY_STEP_6_ERROR_RESPONSES = {
      "activecarrot/session-booking-responses/step-1.1.html",
      "activecarrot/session-booking-responses/step-1.2.html",
      "activecarrot/session-booking-responses/step-1.3.html",
      "activecarrot/session-booking-responses/step-2.html",
      "activecarrot/session-booking-responses/step-3.html",
      "activecarrot/session-booking-responses/step-4.html",
      "activecarrot/session-booking-responses/step-5.html",
      "activecarrot/session-booking-responses/step-6-error.html"
  };

  private static final String[] BOOK_SESSION_OVERLAY_STEP_7_ERROR_RESPONSES = {
      "activecarrot/session-booking-responses/step-1.1.html",
      "activecarrot/session-booking-responses/step-1.2.html",
      "activecarrot/session-booking-responses/step-1.3.html",
      "activecarrot/session-booking-responses/step-2.html",
      "activecarrot/session-booking-responses/step-3.html",
      "activecarrot/session-booking-responses/step-4.html",
      "activecarrot/session-booking-responses/step-5.html",
      "activecarrot/session-booking-responses/step-6.html",
      "activecarrot/session-booking-responses/step-7-error.html"
  };

  private MockedStatic<HttpClientUtils> httpClientUtilsMock;
  private MockedStatic<DateUtils> dateUtilsMock;
  private MockedStatic<EncryptionUtils> encryptionUtilsMock;

  /**
   * Tests setup.
   */
  @BeforeEach
  public void init() throws Exception {
    bookingRequestTestUtils.initTestData();
    ActiveCarrotBookingService activeCarrotBookingService = new ActiveCarrotBookingService();
    activeCarrotBookingServiceSpy = Mockito.spy(activeCarrotBookingService);
    ActiveCarrotBookingService.setSleepMs(0);
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
   * Test booking a facility overlay request success flow.
   */
  @Test
  public void bookFacilityOverlayRequestSuccessTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request dry run flow.
   */
  @Test
  public void bookFacilityOverlayRequestDryRunTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setDryRun(true);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setMessage(ActiveCarrotBookingService.SUCCESSFUL_BOOKING_DRY_RUN);
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request invalid login step 1.1 error flow.
   */
  @Test
  public void bookFacilityOverlayRequestInvalidLoginStep1p1ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STEP_1_1_ERROR_RESPONSES);
    when(HttpClientUtils.getStatusCode(any())).thenReturn(HttpStatus.OK.value());
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Invalid login to tennis world");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request invalid login step 1.2 error flow.
   */
  @Test
  public void bookFacilityOverlayRequestStep1p2ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STEP_1_2_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Unable to determine the site id for MELBOURNE_PARK");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request invalid login step 1.3 error flow.
   */
  @Test
  public void bookFacilityOverlayRequestStep1p3ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STEP_1_3_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(1);
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Unable to complete login to siteId 52998021");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request invalid date Step 2 error flow.
   */
  @Test
  public void bookFacilityOverlayRequestInvalidDateStep2ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setDate(DateUtils.getDate(1800, Calendar.DECEMBER, 10));
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Error getting the selectedSessionDatePath");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request invalid time Step 3 error flow.
   */
  @Test
  public void bookFacilityOverlayRequestInvalidTimeStep3ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setTime("00:00");
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Unable to get the selectedSessionPath");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request Step 4 error flow.
   */
  @Test
  public void bookFacilityOverlayRequestInvalidTimeStep4ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STEP_4_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(1);
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Unable to get the session page");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request Step 5 error flow.
   */
  @Test
  public void bookFacilityOverlayRequestInvalidTimeStep5ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STEP_5_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(1);
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Error posting book overlay ajax");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request Step 6 error flow.
   */
  @Test
  public void bookFacilityOverlayRequestInvalidTimeStep6ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STEP_6_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(1);
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Error getting the confirm booking page");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request Step 7 error flow.
   */
  @Test
  public void bookFacilityOverlayRequestInvalidTimeStep7ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STEP_7_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(1);
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Error posting booking request: [Error processing request]");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a facility overlay request error confirming pay step 8 error flow.
   */
  @Test
  public void bookFacilityOverlayRequestErrorConfirmingPayStep8ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STEP_8_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(2);
    expected.setMessage(
        "Error confirming booking result: [Name on card is required, Credit card "
            + "number is invalid, CVV number is required, Expiry month is required, Expiry year is "
            + "required]");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a session overlay request success flow.
   */
  @Test
  public void bookSessionOverlayRequestSuccessTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_SESSION_OVERLAY_STANDARD_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a session overlay request dry run flow.
   */
  @Test
  public void bookSessionOverlayRequestDryRunTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_SESSION_OVERLAY_STANDARD_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    request.setDryRun(true);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setMessage(ActiveCarrotBookingService.SUCCESSFUL_BOOKING_DRY_RUN);
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a session overlay request invalid login step 1.1 error flow.
   */
  @Test
  public void bookSessionOverlayRequestInvalidLoginStep1p1ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_SESSION_OVERLAY_STEP_1_1_ERROR_RESPONSES);
    when(HttpClientUtils.getStatusCode(any())).thenReturn(HttpStatus.OK.value());
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Invalid login to tennis world");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a session overlay request invalid login step 1.2 error flow.
   */
  @Test
  public void bookSessionOverlayRequestStep1p2ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_SESSION_OVERLAY_STEP_1_2_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Unable to determine the site id for MELBOURNE_PARK");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a session overlay request invalid login step 1.3 error flow.
   */
  @Test
  public void bookSessionOverlayRequestStep1p3ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_SESSION_OVERLAY_STEP_1_3_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(1);
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Unable to complete login to siteId 52998021");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a session overlay request invalid date Step 2 error flow.
   */
  @Test
  public void bookSessionOverlayRequestInvalidDateStep2ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_SESSION_OVERLAY_STANDARD_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    request.setDate(DateUtils.getDate(1800, Calendar.DECEMBER, 10));
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Error getting the selectedSessionDatePath");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a session overlay request step 3 error flow.
   */
  @Test
  public void bookSessionOverlayRequestStep3ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_SESSION_OVERLAY_STEP_3_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.ERROR);
    expected.setMessage("Error getting the sessionId");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a session overlay with step 4 error flow.
   */
  @Test
  public void bookSessionOverlayRequestStep4ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_SESSION_OVERLAY_STEP_4_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Error posting book overlay ajax");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a session overlay with step 5 error flow.
   */
  @Test
  public void bookSessionOverlayRequestStep5ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_SESSION_OVERLAY_STEP_5_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Error getting the confirm booking page");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a session overlay with step 6 error flow.
   */
  @Test
  public void bookSessionOverlayRequestStep6ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_SESSION_OVERLAY_STEP_6_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Error posting booking request: [Error processing request]");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a session overlay with step 7 error flow.
   */
  @Test
  public void bookSessionOverlayRequestStep7ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_SESSION_OVERLAY_STEP_7_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage(
        "Error confirming booking result: [Name on card is required, Credit card number is invalid,"
            + " CVV number is required, Expiry month is required, Expiry year is required]");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test unhandled session type error flow.
   */
  @Test
  public void bookUnknownSessionTypeErrorTest() {
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    request.setSessionType(SessionType.UNKNOWN);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(BookingResponse.Status.INTERNAL_ERROR);
    expected.setMessage("Unhandled sessionType: UNKNOWN");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test IOException error.
   */
  @Test
  public void bookIoExceptionTest() throws IOException {
    when(HttpClientUtils.execRequest(any(), any())).thenThrow(new IOException("IO Error"));
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(2);
    expected.setMessage("Error executing booking request to tennis world Message: IO Error");
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = activeCarrotBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

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
