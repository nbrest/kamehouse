package com.nicobrest.kamehouse.tennisworld.testutils;

import static org.junit.Assert.assertEquals;
import com.nicobrest.kamehouse.main.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.main.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingResponse;

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

  private void initSingleTestData() {
    singleTestData = new TennisWorldBookingResponse();
    singleTestData.setStatus(TennisWorldBookingResponse.Status.SUCCESS);
    singleTestData.setMessage("Completed booking request successfully");
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
