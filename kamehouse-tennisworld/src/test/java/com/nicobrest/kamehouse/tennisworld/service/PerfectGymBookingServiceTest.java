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
import java.io.InputStream;
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
public class PerfectGymBookingServiceTest {

  private BookingRequestTestUtils bookingRequestTestUtils = new BookingRequestTestUtils();
  private BookingResponseTestUtils bookingResponseTestUtils = new BookingResponseTestUtils();
  private BookingScheduleConfigTestUtils bookingScheduleConfigTestUtils =
      new BookingScheduleConfigTestUtils();
  private static final StatusLine STATUS_LINE = new BasicStatusLine(
      new ProtocolVersion("http", 1, 1), 200, "OK");

  @InjectMocks
  private PerfectGymBookingService perfectGymBookingServiceSpy;

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

  private static final String[] BOOK_CLASS_RESPONSES = {
      "perfectgym/book-class-responses/step-1-login.json",
      "perfectgym/book-class-responses/step-2-classes-clubs.json",
      "perfectgym/book-class-responses/step-3-calendar-filters.json",
      "perfectgym/book-class-responses/step-4-daily-classes.json",
      "perfectgym/book-class-responses/step-5-book-class.json"
  };

  private static final String[] BOOK_COURT_RESPONSES = {
      "perfectgym/book-court-responses/step-1-login.json",
      "perfectgym/book-court-responses/step-2-clubs.json",
      "perfectgym/book-court-responses/step-3-club-zone-types.json"
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
    PerfectGymBookingService perfectGymBookingService = new PerfectGymBookingService();
    perfectGymBookingServiceSpy = Mockito.spy(perfectGymBookingService);
    PerfectGymBookingService.setSleepMs(0);
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
    when(HttpClientUtils.getStatusLine(any())).thenReturn(STATUS_LINE);
    when(HttpClientUtils.hasResponseBody(any())).thenReturn(true);

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
  public void bookClassSuccessTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_CLASS_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a class dry run flow.
   */
  @Test
  public void bookClassDryRunTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_CLASS_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCardioTennisBookingRequest();
    request.setDryRun(true);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setMessage(PerfectGymBookingService.SUCCESSFUL_BOOKING_DRY_RUN);
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking a court success flow.
   */
  @Test
  public void bookCourtSuccessTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_COURT_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking court dry run flow.
   */
  @Test
  public void bookCourtDryRunTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_COURT_RESPONSES);
    BookingRequest request = bookingRequestTestUtils.getCourtBookingRequest();
    request.setSessionType(SessionType.NTC_CLAY_COURTS);
    request.setDryRun(true);
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.setMessage(PerfectGymBookingService.SUCCESSFUL_BOOKING_DRY_RUN);
    bookingResponseTestUtils.updateResponseWithRequestData(request, expected);

    BookingResponse response = perfectGymBookingServiceSpy.book(request);
    bookingResponseTestUtils.matchDynamicFields(response, expected);

    bookingResponseTestUtils.assertEqualsAllAttributes(expected, response);
  }

  /**
   * Test booking an unknown session type flow.
   */
  @Test
  public void bookUnknownSessionTypeTest() {
    BookingResponse expected = bookingResponseTestUtils.getSingleTestData();
    expected.getRequest().setSessionType(SessionType.UNKNOWN);
    BookingRequest request = bookingRequestTestUtils.getSingleTestData();
    request.setSessionType(SessionType.UNKNOWN);

    BookingResponse response = perfectGymBookingServiceSpy.executeBookingRequestOnTennisWorld(
        request);
    expected.setStatus(Status.INTERNAL_ERROR);
    expected.setMessage("Unhandled sessionType: UNKNOWN");
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
