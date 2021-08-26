package com.nicobrest.kamehouse.tennisworld.testutils;

import static org.junit.Assert.assertEquals;
import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.SessionType;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingResponseDto;
import com.nicobrest.kamehouse.tennisworld.service.BookingService;

import java.util.Date;
import java.util.LinkedList;

/**
 * Test data and common test methods to test Tennis World Booking Responses in all layers of the
 * application.
 *
 * @author nbrest
 *
 */
public class BookingResponseTestUtils extends AbstractTestUtils<BookingResponse, BookingResponseDto>
    implements TestUtils<BookingResponse, BookingResponseDto> {

  public static final String API_V1_TENNISWORLD_BOOKING_RESPONSES = "/api/v1/tennis-world"
      + "/booking-responses/";
  public static final String API_V1_TENNISWORLD_BOOKINGS = "/api/v1/tennis-world/bookings";
  public static final String API_V1_TENNISWORLD_SCHEDULED_BOOKINGS = "/api/v1/tennis-world" +
      "/scheduled-bookings";

  private BookingRequestTestUtils bookingRequestTestUtils = new BookingRequestTestUtils();

  @Override
  public void initTestData() {
    bookingRequestTestUtils.initTestData();
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

  public static void updateResponseWithRequestData(BookingRequest request,
                                                   BookingResponse response) {
    response.setRequest(request);
  }

  /**
   * The id and booking time are generated during the booking so it can't be mocked and matched
   * automatically.
   */
  public static void matchDynamicFields(BookingResponse response, BookingResponse expected) {
    expected.setId(response.getId());
    expected.getRequest().setPassword(response.getRequest().getPassword());
  }

  public static void updateResponseWithCardioRequestData(BookingResponse response, Date date,
                                                         String time, SessionType sessionType,
                                                         String duration) {
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
    singleTestData.setRequest(bookingRequestTestUtils.getSingleTestData());
  }

  private void initTestDataDto() {
    testDataDto = new BookingResponseDto();
    testDataDto.setStatus(BookingResponse.Status.SUCCESS);
    testDataDto.setMessage(BookingService.SUCCESSFUL_BOOKING);
    testDataDto.setRequest(bookingRequestTestUtils.getSingleTestData());
  }

  private void initTestDataList() {
    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
    BookingResponse error = new BookingResponse();
    error.setStatus(BookingResponse.Status.ERROR);
    error.setMessage("Client error");
    error.setRequest(bookingRequestTestUtils.getSingleTestData());
    testDataList.add(error);
    BookingResponse internalError = new BookingResponse();
    internalError.setStatus(BookingResponse.Status.INTERNAL_ERROR);
    internalError.setMessage("Server error");
    internalError.setRequest(bookingRequestTestUtils.getSingleTestData());
    testDataList.add(internalError);
  }
}
