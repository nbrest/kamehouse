package com.nicobrest.kamehouse.tennisworld.testutils;

import static org.junit.Assert.assertEquals;
import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.SessionType;
import com.nicobrest.kamehouse.tennisworld.model.Site;
import com.nicobrest.kamehouse.tennisworld.service.BookingService;

import java.util.LinkedList;

/**
 * Test data and common test methods to test Tennis World Booking Responses in all layers of the
 * application.
 *
 * @author nbrest
 *
 */
public class BookingResponseTestUtils extends AbstractTestUtils<BookingResponse, Object>
    implements TestUtils<BookingResponse, Object> {

  public static final String API_V1_TENNISWORLD_BOOKINGS = "/api/v1/tennis-world/bookings";
  public static final String API_V1_TENNISWORLD_SCHEDULED_BOOKINGS = "/api/v1/tennis-world" +
      "/scheduled-bookings";

  @Override
  public void initTestData() {
    initSingleTestData();
    initTestDataList();
  }

  @Override
  public void assertEqualsAllAttributes(BookingResponse expected, BookingResponse returned) {
    assertEquals(expected, returned);
    assertEquals(expected.getStatus(), returned.getStatus());
    assertEquals(expected.getMessage(), returned.getMessage());
  }

  public static void updateResponseWithRequestData(BookingRequest request,
                                                   BookingResponse response) {
    response.setUsername(request.getUsername());
    response.setDate(request.getDate());
    response.setTime(request.getTime());
    response.setSessionType(request.getSessionType());
    response.setSite(request.getSite());
  }

  /**
   * The id is generated during the booking so it can't be mocked and matched automatically.
   */
  public static void matchIds(BookingResponse response, BookingResponse expected) {
    expected.setId(response.getId());
  }

  public static void updateResponseWithCardioRequestData(BookingResponse response,
                                                         String time, String date) {
    response.setDate(date);
    response.setTime(time);
    response.setSessionType(SessionType.CARDIO.name());
    response.setSite(Site.MELBOURNE_PARK.name());
  }

  private void initSingleTestData() {
    singleTestData = new BookingResponse();
    singleTestData.setStatus(BookingResponse.Status.SUCCESS);
    singleTestData.setMessage(BookingService.SUCCESSFUL_BOOKING);
  }

  private void initTestDataList() {
    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
    BookingResponse error = new BookingResponse();
    error.setStatus(BookingResponse.Status.ERROR);
    error.setMessage("Client error");
    testDataList.add(error);
    BookingResponse internalError = new BookingResponse();
    internalError.setStatus(BookingResponse.Status.INTERNAL_ERROR);
    internalError.setMessage("Server error");
    testDataList.add(internalError);
  }
}
