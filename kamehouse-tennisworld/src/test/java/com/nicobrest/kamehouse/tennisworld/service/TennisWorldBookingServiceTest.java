package com.nicobrest.kamehouse.tennisworld.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingResponse;
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

/**
 * Test class for the TennisWorldBookingService.
 * 
 * @author nbrest
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpClientUtils.class })
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
  }

  /**
   * Test booking a facility overlay request success flow.
   */
  @Test
  public void bookFacilityOverlayRequestSuccessTest() throws Exception {
    setupHttpResponseInputStreamMocks(BOOK_FACILITY_OVERLAY_STANDARD_RESPONSES);
    TennisWorldBookingRequest request = tennisWorldBookingRequestTestUtils.getSingleTestData();
    TennisWorldBookingResponse expected = tennisWorldBookingResponseTestUtils.getSingleTestData();

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
    expected.setMessage("Completed the booking request DRY-RUN successfully");

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

    TennisWorldBookingResponse response = tennisWorldBookingServiceSpy.book(request);

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
