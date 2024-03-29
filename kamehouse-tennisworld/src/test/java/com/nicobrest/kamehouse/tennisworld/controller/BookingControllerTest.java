package com.nicobrest.kamehouse.tennisworld.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.controller.AbstractControllerTest;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingResponseDto;
import com.nicobrest.kamehouse.tennisworld.service.BookingService;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingRequestTestUtils;
import com.nicobrest.kamehouse.tennisworld.testutils.BookingResponseTestUtils;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for BookingControllerTest class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
class BookingControllerTest
    extends AbstractControllerTest<BookingResponse, BookingResponseDto> {

  private static final String API_V1_TENNISWORLD_BOOKINGS =
      BookingResponseTestUtils.API_V1_TENNISWORLD_BOOKINGS;
  private static final String API_V1_TENNISWORLD_SCHEDULED_BOOKINGS =
      BookingResponseTestUtils.API_V1_TENNISWORLD_SCHEDULED_BOOKINGS;

  private BookingRequestTestUtils bookingRequestTestUtils = new BookingRequestTestUtils();

  @InjectMocks private BookingController bookingController;

  @Mock private BookingService bookingService;

  /**
   * Tests setup.
   */
  @BeforeEach
  void beforeTest() {
    testUtils = new BookingResponseTestUtils();
    testUtils.initTestData();
    bookingRequestTestUtils.initTestData();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(bookingService);
    mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
  }

  /** Tests a successful tennis world booking. */
  @Test
  void bookingsSuccessfulTest() throws Exception {
    when(bookingService.book(any())).thenReturn(testUtils.getSingleTestData());
    BookingRequest requestBody = bookingRequestTestUtils.getSingleTestData();
    byte[] requestPayload = JsonUtils.toJsonByteArray(requestBody);
    MockHttpServletResponse response = doPost(API_V1_TENNISWORLD_BOOKINGS, requestPayload);
    BookingResponse responseBody = getResponseBody(response, BookingResponse.class);

    verifyResponseStatus(response, HttpStatus.CREATED);
    verifyContentType(response, MediaType.APPLICATION_JSON);
    testUtils.assertEqualsAllAttributes(testUtils.getSingleTestData(), responseBody);
    verify(bookingService, times(1)).book(any());
    verifyNoMoreInteractions(bookingService);
  }

  /** Tests a successful tennis world scheduled booking. */
  @Test
  void scheduledBookingsSuccessfulTest() throws Exception {
    when(bookingService.bookScheduledSessions()).thenReturn(testUtils.getTestDataList());
    List<BookingRequest> requestBody = bookingRequestTestUtils.getTestDataList();
    byte[] requestPayload = JsonUtils.toJsonByteArray(requestBody);
    MockHttpServletResponse response =
        doPost(API_V1_TENNISWORLD_SCHEDULED_BOOKINGS, requestPayload);
    List<BookingResponse> responseBody = getResponseBodyList(response, BookingResponse.class);

    verifyResponseStatus(response, HttpStatus.CREATED);
    verifyContentType(response, MediaType.APPLICATION_JSON);
    testUtils.assertEqualsAllAttributesList(testUtils.getTestDataList(), responseBody);
    verify(bookingService, times(1)).bookScheduledSessions();
    verifyNoMoreInteractions(bookingService);
  }

  /** Tests a client error tennis world booking. */
  @Test
  void bookingsClientErrorTest() throws Exception {
    BookingResponse expectedResponse = testUtils.getTestDataList().get(1);
    when(bookingService.book(any())).thenReturn(expectedResponse);
    BookingRequest requestBody = bookingRequestTestUtils.getSingleTestData();
    byte[] requestPayload = JsonUtils.toJsonByteArray(requestBody);
    MockHttpServletResponse response = doPost(API_V1_TENNISWORLD_BOOKINGS, requestPayload);
    BookingResponse responseBody = getResponseBody(response, BookingResponse.class);

    verifyResponseStatus(response, HttpStatus.BAD_REQUEST);
    verifyContentType(response, MediaType.APPLICATION_JSON);
    testUtils.assertEqualsAllAttributes(expectedResponse, responseBody);
    verify(bookingService, times(1)).book(any());
    verifyNoMoreInteractions(bookingService);
  }

  /** Tests a server error tennis world booking. */
  @Test
  void bookingsServerErrorTest() throws Exception {
    BookingResponse expectedResponse = testUtils.getTestDataList().get(2);
    when(bookingService.book(any())).thenReturn(expectedResponse);
    BookingRequest requestBody = bookingRequestTestUtils.getSingleTestData();
    byte[] requestPayload = JsonUtils.toJsonByteArray(requestBody);
    MockHttpServletResponse response = doPost(API_V1_TENNISWORLD_BOOKINGS, requestPayload);
    BookingResponse responseBody = getResponseBody(response, BookingResponse.class);

    verifyResponseStatus(response, HttpStatus.INTERNAL_SERVER_ERROR);
    verifyContentType(response, MediaType.APPLICATION_JSON);
    testUtils.assertEqualsAllAttributes(expectedResponse, responseBody);
    verify(bookingService, times(1)).book(any());
    verifyNoMoreInteractions(bookingService);
  }
}
