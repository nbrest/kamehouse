package com.nicobrest.kamehouse.tennisworld.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import com.nicobrest.kamehouse.main.controller.AbstractControllerTest;
import com.nicobrest.kamehouse.main.utils.JsonUtils;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingResponse;
import com.nicobrest.kamehouse.tennisworld.service.TennisWorldBookingService;
import com.nicobrest.kamehouse.tennisworld.testutils.TennisWorldBookingRequestTestUtils;
import com.nicobrest.kamehouse.tennisworld.testutils.TennisWorldBookingResponseTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for TennisWorldBookingControllerTest class.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class TennisWorldBookingControllerTest
    extends AbstractControllerTest<TennisWorldBookingResponse, Object> {

  private static final String API_V1_TENNISWORLD_BOOKINGS =
      TennisWorldBookingResponseTestUtils.API_V1_TENNISWORLD_BOOKINGS;

  private TennisWorldBookingRequestTestUtils tennisWorldBookingRequestTestUtils =
      new TennisWorldBookingRequestTestUtils();

  @InjectMocks
  private TennisWorldBookingController tennisWorldBookingController;

  @Mock
  private TennisWorldBookingService tennisWorldBookingService;

  @Before
  public void beforeTest() {
    testUtils = new TennisWorldBookingResponseTestUtils();
    testUtils.initTestData();
    tennisWorldBookingRequestTestUtils.initTestData();

    MockitoAnnotations.initMocks(this);
    Mockito.reset(tennisWorldBookingService);
    mockMvc = MockMvcBuilders.standaloneSetup(tennisWorldBookingController).build();
  }

  /**
   * Tests a successful tennis world booking.
   */
  @Test
  public void bookingsSuccessfulTest() throws Exception {
    when(tennisWorldBookingService.book(any())).thenReturn(testUtils.getSingleTestData());
    TennisWorldBookingRequest requestBody = tennisWorldBookingRequestTestUtils.getSingleTestData();
    byte[] requestPayload = JsonUtils.toJsonByteArray(requestBody);
    MockHttpServletResponse response = doPost(API_V1_TENNISWORLD_BOOKINGS, requestPayload);
    TennisWorldBookingResponse responseBody = getResponseBody(response,
        TennisWorldBookingResponse.class);

    verifyResponseStatus(response, HttpStatus.CREATED);
    verifyContentType(response, MediaType.APPLICATION_JSON_UTF8);
    testUtils.assertEqualsAllAttributes(testUtils.getSingleTestData(), responseBody);
    verify(tennisWorldBookingService, times(1)).book(any());
    verifyNoMoreInteractions(tennisWorldBookingService);
  }

  /**
   * Tests a client error tennis world booking.
   */
  @Test
  public void bookingsClientErrorTest() throws Exception {
    TennisWorldBookingResponse expectedResponse = testUtils.getTestDataList().get(1);
    when(tennisWorldBookingService.book(any())).thenReturn(expectedResponse);
    TennisWorldBookingRequest requestBody = tennisWorldBookingRequestTestUtils.getSingleTestData();
    byte[] requestPayload = JsonUtils.toJsonByteArray(requestBody);
    MockHttpServletResponse response = doPost(API_V1_TENNISWORLD_BOOKINGS, requestPayload);
    TennisWorldBookingResponse responseBody = getResponseBody(response,
        TennisWorldBookingResponse.class);

    verifyResponseStatus(response, HttpStatus.BAD_REQUEST);
    verifyContentType(response, MediaType.APPLICATION_JSON_UTF8);
    testUtils.assertEqualsAllAttributes(expectedResponse, responseBody);
    verify(tennisWorldBookingService, times(1)).book(any());
    verifyNoMoreInteractions(tennisWorldBookingService);
  }

  /**
   * Tests a server error tennis world booking.
   */
  @Test
  public void bookingsServerErrorTest() throws Exception {
    TennisWorldBookingResponse expectedResponse = testUtils.getTestDataList().get(2);
    when(tennisWorldBookingService.book(any())).thenReturn(expectedResponse);
    TennisWorldBookingRequest requestBody = tennisWorldBookingRequestTestUtils.getSingleTestData();
    byte[] requestPayload = JsonUtils.toJsonByteArray(requestBody);
    MockHttpServletResponse response = doPost(API_V1_TENNISWORLD_BOOKINGS, requestPayload);
    TennisWorldBookingResponse responseBody = getResponseBody(response,
        TennisWorldBookingResponse.class);

    verifyResponseStatus(response, HttpStatus.INTERNAL_SERVER_ERROR);
    verifyContentType(response, MediaType.APPLICATION_JSON_UTF8);
    testUtils.assertEqualsAllAttributes(expectedResponse, responseBody);
    verify(tennisWorldBookingService, times(1)).book(any());
    verifyNoMoreInteractions(tennisWorldBookingService);
  }
}