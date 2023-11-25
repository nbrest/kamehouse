package com.nicobrest.kamehouse.tennisworld.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
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
import java.net.URISyntaxException;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;

/**
 * Test class for the PerfectGymBookingService.
 *
 * @author nbrest
 */
class PerfectGymBookingServiceTest {

  private BookingRequestTestUtils bookingRequestTestUtils = new BookingRequestTestUtils();
  private BookingResponseTestUtils bookingResponseTestUtils = new BookingResponseTestUtils();
  private BookingScheduleConfigTestUtils bookingScheduleConfigTestUtils =
      new BookingScheduleConfigTestUtils();
  private static final StatusLine STATUS_LINE_200 = new BasicStatusLine(
      new ProtocolVersion("http", 1, 1), 200, "OK");
  private static final StatusLine STATUS_LINE_499 = new BasicStatusLine(
      new ProtocolVersion("http", 1, 1), 499, "Client closed request");
  private static final StatusLine STATUS_LINE_500 = new BasicStatusLine(
      new ProtocolVersion("http", 1, 1), 500, "Server Error");

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
  private PerfectGymBookingService perfectGymBookingServiceSpy;

  private MockedStatic<HttpClientUtils> httpClientUtilsMock;
  private MockedStatic<DateUtils> dateUtilsMock;
  private MockedStatic<EncryptionUtils> encryptionUtilsMock;

  /**
   * Tests setup.
   */
  @BeforeEach
  public void init() throws Exception {
    bookingRequestTestUtils.initTestData();
    PerfectGymBookingService perfectGymBookingService = new PerfectGymBookingService(
        bookingScheduleConfigService, bookingRequestService, bookingResponseService);
    perfectGymBookingServiceSpy = Mockito.spy(perfectGymBookingService);
    PerfectGymBookingService.setSleepMs(0);
    PerfectGymBookingService.setRetrySleepMs(0);
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
    when(HttpClientUtils.getHeader(httpResponseMock,
        PerfectGymBookingService.CP_BOOK_FACILITY_SESSION_ID_HEADER)).thenReturn(
        "3131b9de-204f-45d7-81df-4583e200f23f");

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
   * Test booking a class success flow.
   */
  @Test
  void bookClassSuccessTest() throws Exception {
    setupHttpResponseInputStreamMocks(PerfectGymResponses.BOOK_CLASS_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a class dry run flow.
   */
  @Test
  void bookClassDryRunTest() throws Exception {
    setupHttpResponseInputStreamMocks(PerfectGymResponses.BOOK_CLASS_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    request.setDryRun(true);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setMessage(PerfectGymBookingService.SUCCESSFUL_BOOKING_DRY_RUN);
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a class step 1 error invalid user/pass flow.
   */
  @Test
  void bookClassStep1ErrorInvalidUserPassTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_CLASS_STEP_1_ERROR_INVALID_USER_PASS_RESPONSES);
    when(HttpClientUtils.getStatusLine(any())).thenReturn(STATUS_LINE_499);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.ERROR);
    expected.setMessage("Invalid http client error response code: 499 for request to "
        + PerfectGymBookingService.LOGIN_URL);
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a class step 1 perfectgym server error flow.
   */
  @Test
  void bookClassStep1PerfectGymServerErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_CLASS_STEP_1_ERROR_INVALID_USER_PASS_RESPONSES);
    when(HttpClientUtils.getStatusLine(any())).thenReturn(STATUS_LINE_500);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Invalid http server error response code: 500 for request to "
        + PerfectGymBookingService.LOGIN_URL);
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a class step 1 error invalid json flow.
   */
  @Test
  void bookClassStep1ErrorInvalidJsonTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_CLASS_STEP_1_ERROR_INVALID_JSON_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.ERROR);
    expected.setMessage("Invalid login to tennis world.");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a class step 1 error empty response flow.
   */
  @Test
  void bookClassStep1ErrorEmptyTest() throws Exception {
    setupHttpResponseInputStreamMocks(PerfectGymResponses.BOOK_CLASS_STEP_1_ERROR_EMPTY_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.ERROR);
    expected.setMessage("Invalid login to tennis world.");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a class step 1 error no user response flow.
   */
  @Test
  void bookClassStep1ErrorNoUserTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_CLASS_STEP_1_ERROR_NO_USER_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.ERROR);
    expected.setMessage("Invalid login to tennis world.");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a class step 2 error invalid json array flow.
   */
  @Test
  void bookClassStep2ErrorInvalidJsonArrayTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_CLASS_STEP_2_ERROR_INVALID_JSON_ARRAY_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Invalid classes clubs response from PerfectGym");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a class step 2 error no club id flow.
   */
  @Test
  void bookClassStep2ErrorNoClubIdTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_CLASS_STEP_2_ERROR_NO_CLUB_ID_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage(
        "Unable to find club id for Tennis World Melbourne from PerfectGym response");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a class step 3 error invalid json flow.
   */
  @Test
  void bookClassStep3ErrorInvalidJsonTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_CLASS_STEP_3_ERROR_INVALID_JSON_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Invalid calendar filters response from PerfectGym");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a class step 4 error invalid json flow.
   */
  @Test
  void bookClassStep4ErrorInvalidJsonTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_CLASS_STEP_4_ERROR_INVALID_JSON_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Invalid daily classes response from PerfectGym");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a class step 4 error no class id found flow.
   */
  @Test
  void bookClassStep4ErrorNoClassIdFoundTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_CLASS_STEP_4_ERROR_NO_CLASS_ID_FOUND_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.ERROR);
    expected.setMessage(PerfectGymBookingService.NO_BOOKABLE_CLASS_FOUND);
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a class step 5 error invalid json flow.
   */
  @Test
  void bookClassStep5ErrorInvalidJsonTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_CLASS_STEP_5_ERROR_INVALID_JSON_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Invalid book class response from PerfectGym");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a class step 5 error no tickets found flow.
   */
  @Test
  void bookClassStep5ErrorNoTicketsFoundTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_CLASS_STEP_5_ERROR_NO_TICKETS_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Book class response from PerfectGym doesnt contain a Tickets entry");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a court success flow.
   */
  @Test
  void bookCourtSuccessTest() throws Exception {
    setupHttpResponseInputStreamMocks(PerfectGymResponses.BOOK_COURT_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking court dry run flow.
   */
  @Test
  void bookCourtDryRunTest() throws Exception {
    setupHttpResponseInputStreamMocks(PerfectGymResponses.BOOK_COURT_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    request.setDryRun(true);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setMessage(PerfectGymBookingService.SUCCESSFUL_BOOKING_DRY_RUN);
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a court with a custom court number success flow.
   */
  @Test
  void bookCourtCustomCourtNumberSuccessTest() throws Exception {
    setupHttpResponseInputStreamMocks(PerfectGymResponses.BOOK_COURT_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    request.setCourtNumber(3);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a court step 1 error flow.
   */
  @Test
  void bookCourtStep1ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(PerfectGymResponses.BOOK_COURT_STEP_1_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.ERROR);
    expected.setMessage("Invalid login to tennis world.");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a court step 2 error flow.
   */
  @Test
  void bookCourtStep2ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(PerfectGymResponses.BOOK_COURT_STEP_2_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Invalid clubs response from PerfectGym");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a court step 3 error flow.
   */
  @Test
  void bookCourtStep3ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(PerfectGymResponses.BOOK_COURT_STEP_3_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Invalid club zone types response from PerfectGym");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a court step 3 no zoneTypeId error flow.
   */
  @Test
  void bookCourtStep3ErrorNoZoneTypeIdTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_COURT_STEP_3_ERROR_NO_ZONETYPEID_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Unable to determine zoneTypeId");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a court step 4 error flow.
   */
  @Test
  void bookCourtStep4ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(PerfectGymResponses.BOOK_COURT_STEP_4_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Invalid court weekly schedule response from PerfectGym");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a court step 5 error flow.
   */
  @Test
  void bookCourtStep5ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(PerfectGymResponses.BOOK_COURT_STEP_5_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Invalid start booking modal response from PerfectGym");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a court step 5 no zoneId error flow.
   */
  @Test
  void bookCourtStep5ErrorNoZoneIdTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_COURT_STEP_5_ERROR_NO_ZONEID_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Unable to determine zoneId");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a court step 5 no userId error flow.
   */
  @Test
  void bookCourtStep5ErrorNoUserIdTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_COURT_STEP_5_ERROR_NO_USERID_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Unable to determine userId");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a court step 5 no session id error flow.
   */
  @Test
  void bookCourtStep5ErrorNoSessionIdTest() throws Exception {
    when(HttpClientUtils.getHeader(httpResponseMock,
        PerfectGymBookingService.CP_BOOK_FACILITY_SESSION_ID_HEADER)).thenReturn(null);
    setupHttpResponseInputStreamMocks(PerfectGymResponses.BOOK_COURT_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Unable to get session id from start booking modal request to PerfectGym");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a court step 5 build request url error flow.
   */
  @Test
  void bookCourtStep5ErrorBuildUrlErrorTest() throws Exception {
    when(HttpClientUtils.addUrlParameters(any(), anyMap())).thenThrow(URISyntaxException.class);
    setupHttpResponseInputStreamMocks(PerfectGymResponses.BOOK_COURT_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Unable to build start booking modal request");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a court step 6 error flow.
   */
  @Test
  void bookCourtStep6ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(PerfectGymResponses.BOOK_COURT_STEP_6_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Invalid select court booking response from PerfectGym");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a court step 6 no ruleId error flow.
   */
  @Test
  void bookCourtStep6ErrorNoRuleIdTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_COURT_STEP_6_ERROR_NO_RULEID_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Unable to determine ruleId");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a court step 7 error flow.
   */
  @Test
  void bookCourtStep7ErrorTest() throws Exception {
    setupHttpResponseInputStreamMocks(PerfectGymResponses.BOOK_COURT_STEP_7_ERROR_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Invalid finalize court booking response from PerfectGym");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a court step 7 no facilityBooking error flow.
   */
  @Test
  void bookCourtStep7ErrorNoFacilityBookingTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_COURT_STEP_7_ERROR_NO_FACILITYBOOKING_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage(
        "Error finalizing court booking. No FacilityBooking element in the response");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking an unknown session type flow.
   */
  @Test
  void bookUnknownSessionTypeTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_CLASS_STEP_2_ERROR_INVALID_JSON_ARRAY_RESPONSES);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.getRequest().setSessionType(SessionType.UNKNOWN);
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    request.setSessionType(SessionType.UNKNOWN);

    BookingResponse response = perfectGymBookingServiceSpy.executeBookingRequest(request);
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Unhandled sessionType: UNKNOWN");
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test IOException error in login.
   */
  @Test
  void bookIoExceptionInLoginTest() throws IOException {
    when(HttpClientUtils.execRequest(any(), any())).thenThrow(new IOException("IO Error"));
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(1);
    expected.setMessage("Invalid login to tennis world.");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test IOException error in booking steps.
   */
  @Test
  void bookIoExceptionInBookingTest() throws Exception {
    setupHttpResponseInputStreamMocks(
        PerfectGymResponses.BOOK_CLASS_STEP_2_ERROR_INVALID_JSON_ARRAY_RESPONSES);
    OngoingStubbing<HttpResponse> ongoingStubbing = when(HttpClientUtils.execRequest(any(), any()));
    ongoingStubbing.thenReturn(httpResponseMock).thenThrow(new IOException("IO Error"));
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    request.setRetries(2);
    BookingResponse expected = bookingResponseTestUtils.getTestDataList().get(2);
    expected.setMessage("Error executing booking request to tennis world Message: IO Error");
    BookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    BookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Setup http response input stream mocks from files in test resources.
   */
  private void setupHttpResponseInputStreamMocks(PerfectGymResponses perfectGymResponses)
      throws Exception {
    OngoingStubbing<InputStream> ongoingStubbing = when(HttpClientUtils.getInputStream(any()));
    for (int i = 0; i < BookingService.MAX_BOOKING_RETRIES; i++) {
      String[] responses = perfectGymResponses.getValue();
      InputStream testInputStream = BookingRequestTestUtils.getInputStream(responses[0]);
      ongoingStubbing = ongoingStubbing.thenReturn(testInputStream);
      for (int j = 1; j < responses.length; j++) {
        testInputStream = BookingRequestTestUtils.getInputStream(responses[j]);
        ongoingStubbing = ongoingStubbing.thenReturn(testInputStream);
      }
    }
  }

  /**
   * Mocked responses from PerfectGym for each test case.
   */
  public enum PerfectGymResponses {
    BOOK_CLASS_RESPONSES(new String[]{
        "perfectgym/book-class-responses/step-1-login.json",
        "perfectgym/book-class-responses/step-2-classes-clubs.json",
        "perfectgym/book-class-responses/step-3-calendar-filters.json",
        "perfectgym/book-class-responses/step-4-daily-classes.json",
        "perfectgym/book-class-responses/step-5-book-class.json"
    }),
    BOOK_CLASS_STEP_1_ERROR_INVALID_USER_PASS_RESPONSES(new String[]{
        "perfectgym/book-class-responses/step-1-login-error.txt"
    }),
    BOOK_CLASS_STEP_1_ERROR_INVALID_JSON_RESPONSES(new String[]{
        "perfectgym/book-class-responses/step-1-login-error-invalid-json.json"
    }),
    BOOK_CLASS_STEP_1_ERROR_EMPTY_RESPONSES(new String[]{
        "perfectgym/book-class-responses/step-1-login-error-empty.json"
    }),
    BOOK_CLASS_STEP_1_ERROR_NO_USER_RESPONSES(new String[]{
        "perfectgym/book-class-responses/step-1-login-error-no-user.json"
    }),
    BOOK_CLASS_STEP_2_ERROR_INVALID_JSON_ARRAY_RESPONSES(new String[]{
        "perfectgym/book-class-responses/step-1-login.json",
        "perfectgym/book-class-responses/step-2-classes-clubs-invalid-json-array.json"
    }),
    BOOK_CLASS_STEP_2_ERROR_NO_CLUB_ID_RESPONSES(new String[]{
        "perfectgym/book-class-responses/step-1-login.json",
        "perfectgym/book-class-responses/step-2-classes-clubs-no-club-id.json"
    }),
    BOOK_CLASS_STEP_3_ERROR_INVALID_JSON_RESPONSES(new String[]{
        "perfectgym/book-class-responses/step-1-login.json",
        "perfectgym/book-class-responses/step-2-classes-clubs.json",
        "perfectgym/book-class-responses/step-3-calendar-filters-invalid-json.json"
    }),
    BOOK_CLASS_STEP_4_ERROR_INVALID_JSON_RESPONSES(new String[]{
        "perfectgym/book-class-responses/step-1-login.json",
        "perfectgym/book-class-responses/step-2-classes-clubs.json",
        "perfectgym/book-class-responses/step-3-calendar-filters.json",
        "perfectgym/book-class-responses/step-4-daily-classes-invalid-json.json"
    }),
    BOOK_CLASS_STEP_4_ERROR_NO_CLASS_ID_FOUND_RESPONSES(new String[]{
        "perfectgym/book-class-responses/step-1-login.json",
        "perfectgym/book-class-responses/step-2-classes-clubs.json",
        "perfectgym/book-class-responses/step-3-calendar-filters.json",
        "perfectgym/book-class-responses/step-4-daily-classes-no-class-id-found.json"
    }),
    BOOK_CLASS_STEP_5_ERROR_INVALID_JSON_RESPONSES(new String[]{
        "perfectgym/book-class-responses/step-1-login.json",
        "perfectgym/book-class-responses/step-2-classes-clubs.json",
        "perfectgym/book-class-responses/step-3-calendar-filters.json",
        "perfectgym/book-class-responses/step-4-daily-classes.json",
        "perfectgym/book-class-responses/step-5-book-class-invalid-json.json"
    }),
    BOOK_CLASS_STEP_5_ERROR_NO_TICKETS_RESPONSES(new String[]{
        "perfectgym/book-class-responses/step-1-login.json",
        "perfectgym/book-class-responses/step-2-classes-clubs.json",
        "perfectgym/book-class-responses/step-3-calendar-filters.json",
        "perfectgym/book-class-responses/step-4-daily-classes.json",
        "perfectgym/book-class-responses/step-5-book-class-no-tickets.json"
    }),
    BOOK_COURT_RESPONSES(new String[]{
        "perfectgym/book-court-responses/step-1-login.json",
        "perfectgym/book-court-responses/step-2-clubs.json",
        "perfectgym/book-court-responses/step-3-club-zone-types.json",
        "perfectgym/book-court-responses/step-4-weekly-schedule.json",
        "perfectgym/book-court-responses/step-5-start-booking-modal.json",
        "perfectgym/book-court-responses/step-6-select-court.json",
        "perfectgym/book-court-responses/step-7-finalize-court-booking.json"
    }),
    BOOK_COURT_STEP_1_ERROR_RESPONSES(new String[]{
        "perfectgym/book-court-responses/step-1-login-error.txt"
    }),
    BOOK_COURT_STEP_2_ERROR_RESPONSES(new String[]{
        "perfectgym/book-court-responses/step-1-login.json",
        "perfectgym/book-court-responses/step-2-clubs-error.json"
    }),
    BOOK_COURT_STEP_3_ERROR_RESPONSES(new String[]{
        "perfectgym/book-court-responses/step-1-login.json",
        "perfectgym/book-court-responses/step-2-clubs.json",
        "perfectgym/book-court-responses/step-3-club-zone-types-error.json"
    }),
    BOOK_COURT_STEP_3_ERROR_NO_ZONETYPEID_RESPONSES(new String[]{
        "perfectgym/book-court-responses/step-1-login.json",
        "perfectgym/book-court-responses/step-2-clubs.json",
        "perfectgym/book-court-responses/step-3-club-zone-types-error-no-zonetypeid.json"
    }),
    BOOK_COURT_STEP_4_ERROR_RESPONSES(new String[]{
        "perfectgym/book-court-responses/step-1-login.json",
        "perfectgym/book-court-responses/step-2-clubs.json",
        "perfectgym/book-court-responses/step-3-club-zone-types.json",
        "perfectgym/book-court-responses/step-4-weekly-schedule-error.json"
    }),
    BOOK_COURT_STEP_5_ERROR_RESPONSES(new String[]{
        "perfectgym/book-court-responses/step-1-login.json",
        "perfectgym/book-court-responses/step-2-clubs.json",
        "perfectgym/book-court-responses/step-3-club-zone-types.json",
        "perfectgym/book-court-responses/step-4-weekly-schedule.json",
        "perfectgym/book-court-responses/step-5-start-booking-modal-error.json"
    }),
    BOOK_COURT_STEP_5_ERROR_NO_ZONEID_RESPONSES(new String[]{
        "perfectgym/book-court-responses/step-1-login.json",
        "perfectgym/book-court-responses/step-2-clubs.json",
        "perfectgym/book-court-responses/step-3-club-zone-types.json",
        "perfectgym/book-court-responses/step-4-weekly-schedule.json",
        "perfectgym/book-court-responses/step-5-start-booking-modal-error-no-zoneid.json"
    }),
    BOOK_COURT_STEP_5_ERROR_NO_USERID_RESPONSES(new String[]{
        "perfectgym/book-court-responses/step-1-login.json",
        "perfectgym/book-court-responses/step-2-clubs.json",
        "perfectgym/book-court-responses/step-3-club-zone-types.json",
        "perfectgym/book-court-responses/step-4-weekly-schedule.json",
        "perfectgym/book-court-responses/step-5-start-booking-modal-error-no-userid.json"
    }),
    BOOK_COURT_STEP_6_ERROR_RESPONSES(new String[]{
        "perfectgym/book-court-responses/step-1-login.json",
        "perfectgym/book-court-responses/step-2-clubs.json",
        "perfectgym/book-court-responses/step-3-club-zone-types.json",
        "perfectgym/book-court-responses/step-4-weekly-schedule.json",
        "perfectgym/book-court-responses/step-5-start-booking-modal.json",
        "perfectgym/book-court-responses/step-6-select-court-error.json"
    }),
    BOOK_COURT_STEP_6_ERROR_NO_RULEID_RESPONSES(new String[]{
        "perfectgym/book-court-responses/step-1-login.json",
        "perfectgym/book-court-responses/step-2-clubs.json",
        "perfectgym/book-court-responses/step-3-club-zone-types.json",
        "perfectgym/book-court-responses/step-4-weekly-schedule.json",
        "perfectgym/book-court-responses/step-5-start-booking-modal.json",
        "perfectgym/book-court-responses/step-6-select-court-error-no-ruleid.json"
    }),
    BOOK_COURT_STEP_7_ERROR_RESPONSES(new String[]{
        "perfectgym/book-court-responses/step-1-login.json",
        "perfectgym/book-court-responses/step-2-clubs.json",
        "perfectgym/book-court-responses/step-3-club-zone-types.json",
        "perfectgym/book-court-responses/step-4-weekly-schedule.json",
        "perfectgym/book-court-responses/step-5-start-booking-modal.json",
        "perfectgym/book-court-responses/step-6-select-court.json",
        "perfectgym/book-court-responses/step-7-finalize-court-booking-error.json"
    }),
    BOOK_COURT_STEP_7_ERROR_NO_FACILITYBOOKING_RESPONSES(new String[]{
        "perfectgym/book-court-responses/step-1-login.json",
        "perfectgym/book-court-responses/step-2-clubs.json",
        "perfectgym/book-court-responses/step-3-club-zone-types.json",
        "perfectgym/book-court-responses/step-4-weekly-schedule.json",
        "perfectgym/book-court-responses/step-5-start-booking-modal.json",
        "perfectgym/book-court-responses/step-6-select-court.json",
        "perfectgym/book-court-responses/step-7-finalize-court-booking-error-"
            + "no-facilitybooking.json"
    });

    private String[] value;

    PerfectGymResponses(String[] value) {
      this.value = value;
    }

    public String[] getValue() {
      return value;
    }

    public void setValue(String[] value) {
      this.value = value;
    }
  }
}
