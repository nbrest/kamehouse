package com.nicobrest.kamehouse.tennisworld.testutils;

import static org.junit.Assert.assertEquals;
import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingResponse;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldSessionType;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldSite;
import com.nicobrest.kamehouse.tennisworld.service.TennisWorldBookingService;

import java.util.LinkedList;

/**
 * Test data and common test methods to test Tennis World Booking Responses in all layers of the
 * application.
 *
 * @author nbrest
 *
 */
public class TennisWorldBookingResponseTestUtils extends
    AbstractTestUtils<TennisWorldBookingResponse, Object>
    implements TestUtils<TennisWorldBookingResponse, Object> {

  public static final String API_V1_TENNISWORLD_BOOKINGS = "/api/v1/tennis-world/bookings";

  @Override
  public void initTestData() {
    initSingleTestData();
    initTestDataList();
  }

  @Override
  public void assertEqualsAllAttributes(TennisWorldBookingResponse expected,
                                        TennisWorldBookingResponse returned) {
    assertEquals(expected, returned);
    assertEquals(expected.getStatus(), returned.getStatus());
    assertEquals(expected.getMessage(), returned.getMessage());
  }

  public static void updateResponseWithRequestData(TennisWorldBookingRequest request,
                                            TennisWorldBookingResponse response) {
    response.setUsername(request.getUsername());
    response.setDate(request.getDate());
    response.setTime(request.getTime());
    response.setSessionType(request.getSessionType());
    response.setSite(request.getSite());
  }

  /**
   * The id is generated during the booking so it can't be mocked and matched automatically.
   */
  public static void matchIds(TennisWorldBookingResponse response,
                              TennisWorldBookingResponse expected) {
    expected.setId(response.getId());
  }

  public static void updateResponseWithCardioRequestData(TennisWorldBookingResponse response,
                                                  String time, String date) {
    response.setDate(date);
    response.setTime(time);
    response.setSessionType(TennisWorldSessionType.CARDIO.name());
    response.setSite(TennisWorldSite.MELBOURNE_PARK.name());
  }

  private void initSingleTestData() {
    singleTestData = new TennisWorldBookingResponse();
    singleTestData.setStatus(TennisWorldBookingResponse.Status.SUCCESS);
    singleTestData.setMessage(TennisWorldBookingService.SUCCESSFUL_BOOKING);
  }

  private void initTestDataList() {
    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
    TennisWorldBookingResponse error = new TennisWorldBookingResponse();
    error.setStatus(TennisWorldBookingResponse.Status.ERROR);
    error.setMessage("Client error");
    testDataList.add(error);
    TennisWorldBookingResponse internalError = new TennisWorldBookingResponse();
    internalError.setStatus(TennisWorldBookingResponse.Status.INTERNAL_ERROR);
    internalError.setMessage("Server error");
    testDataList.add(internalError);
  }
}
