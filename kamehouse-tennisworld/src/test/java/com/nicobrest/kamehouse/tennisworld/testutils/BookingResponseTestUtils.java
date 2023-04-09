package com.nicobrest.kamehouse.tennisworld.testutils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.SessionType;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingResponseDto;
import com.nicobrest.kamehouse.tennisworld.service.BookingService;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Test data and common test methods to test Tennis World Booking Responses in all layers of the
 * application.
 *
 * @author nbrest
 */
public class BookingResponseTestUtils extends AbstractTestUtils<BookingResponse, BookingResponseDto>
    implements TestUtils<BookingResponse, BookingResponseDto> {

  public static final String API_V1_TENNISWORLD_BOOKING_RESPONSES =
      "/api/v1/tennis-world" + "/booking-responses/";
  public static final String API_V1_TENNISWORLD_BOOKINGS = "/api/v1/tennis-world/bookings";
  public static final String API_V1_TENNISWORLD_SCHEDULED_BOOKINGS =
      "/api/v1/tennis-world" + "/scheduled-bookings";

  private BookingRequestTestUtils bookingRequestTestUtils = new BookingRequestTestUtils();
  private BookingRequest bookingRequest;

  @Override
  public void initTestData() {
    bookingRequestTestUtils.initTestData();
    bookingRequest = bookingRequestTestUtils.getSingleTestData();
    List<BookingRequest> bookingRequestList = bookingRequestTestUtils.getTestDataList();
    for (int i = 0; i <= 2; i++) {
      BookingRequest request = bookingRequestList.get(i);
      request.setId(i + 1L);
      request.setCardDetails(null);
      request.setDryRun(false);
      request.setPassword(null);
    }

    initSingleTestData();
    initTestDataList();
    initTestDataDto();
  }

  @Override
  public void assertEqualsAllAttributes(BookingResponse expected, BookingResponse returned) {
    assertEquals(expected, returned);
    assertEquals(expected.getStatus(), returned.getStatus());
    assertEquals(expected.getMessage(), returned.getMessage());
  }

  public static void updateResponseWithRequestData(
      BookingRequest request, BookingResponse response) {
    response.setRequest(request);
  }

  /**
   * The id and booking time are generated during the booking so it can't be mocked and matched
   * automatically.
   */
  public static void matchDynamicFields(BookingResponse response, BookingResponse expected) {
    expected.setId(response.getId());
    expected.getRequest().setPassword(response.getRequest().getPassword());
    expected.getRequest().setId(response.getRequest().getId());
    expected.getRequest().setCreationDate(response.getRequest().getCreationDate());
  }

  /**
   * Update response with request data.
   */
  public static void updateResponseWithRequestData(
      BookingResponse response, Date date, String time, SessionType sessionType, String duration) {
    response.getRequest().setDate(date);
    response.getRequest().setDuration(duration);
    response.getRequest().setTime(time);
    response.getRequest().setSessionType(sessionType);
    response.getRequest().setDuration(duration);
  }

  private void initSingleTestData() {
    singleTestData = new BookingResponse();
    singleTestData.setStatus(BookingResponse.Status.SUCCESS);
    singleTestData.setMessage(BookingService.SUCCESSFUL_BOOKING);
    singleTestData.setRequest(bookingRequest);
  }

  private void initTestDataDto() {
    testDataDto = new BookingResponseDto();
    testDataDto.setStatus(BookingResponse.Status.SUCCESS);
    testDataDto.setMessage(BookingService.SUCCESSFUL_BOOKING);
    testDataDto.setRequest(bookingRequest);
  }

  private void initTestDataList() {
    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
    BookingResponse error = new BookingResponse();
    error.setStatus(BookingResponse.Status.ERROR);
    error.setMessage("Client error");
    BookingRequest bookingRequest2 = bookingRequestTestUtils.getTestDataList().get(1);
    bookingRequest2.setId(2L);
    error.setRequest(bookingRequest2);
    testDataList.add(error);

    BookingResponse internalError = new BookingResponse();
    internalError.setStatus(BookingResponse.Status.INTERNAL_ERROR);
    internalError.setMessage("Server error");
    BookingRequest bookingRequest3 = bookingRequestTestUtils.getTestDataList().get(2);
    bookingRequest3.setId(3L);
    internalError.setRequest(bookingRequest3);
    testDataList.add(internalError);
  }
}
