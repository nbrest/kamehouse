package com.nicobrest.kamehouse.tennisworld.testutils;

import static org.junit.Assert.assertEquals;
import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldBookingRequest;

import java.util.LinkedList;

/**
 * Test data and common test methods to test Tennis World Booking Requests in all layers of the
 * application.
 *
 * @author nbrest
 *
 */
public class TennisWorldBookingRequestTestUtils extends
    AbstractTestUtils<TennisWorldBookingRequest, Object>
    implements TestUtils<TennisWorldBookingRequest, Object> {

  private TennisWorldBookingRequest sessionRequest = null;

  public TennisWorldBookingRequest getSessionRequest() {
    return sessionRequest;
  }

  @Override
  public void initTestData() {
    initSessionRequest();
    initSingleTestData();
    initTestDataList();
  }

  @Override
  public void assertEqualsAllAttributes(TennisWorldBookingRequest expected,
                                        TennisWorldBookingRequest returned) {
    assertEquals(expected, returned);
    assertEquals(expected.getSite(), returned.getSite());
    assertEquals(expected.getDate(), returned.getDate());
    assertEquals(expected.getPassword(), returned.getPassword());
    assertEquals(expected.getSessionType(), returned.getSessionType());
    assertEquals(expected.getTime(), returned.getTime());
    assertEquals(expected.getUsername(), returned.getUsername());
    assertEquals(expected.getCardDetails(), returned.getCardDetails());
    assertEquals(expected.getDuration(), returned.getDuration());
  }

  private void initSingleTestData() {
    singleTestData = new TennisWorldBookingRequest();
    singleTestData.setDate("2020-07-28");
    singleTestData.setTime("06:45pm");
    singleTestData.setDuration("60");
    singleTestData.setPassword("goku-son");
    singleTestData.setUsername("goku@dbz.com");
    singleTestData.setSessionType("ROD_LAVER_OUTDOOR");
    singleTestData.setSite("MELBOURNE_PARK");
    TennisWorldBookingRequest.CardDetails cardDetails = new TennisWorldBookingRequest.CardDetails();
    cardDetails.setName("SON GOKU");
    cardDetails.setNumber("1111222233334444");
    cardDetails.setCvv("999");
    cardDetails.setExpiryDate("12/3099");
    singleTestData.setCardDetails(cardDetails);
  }

  private void initTestDataList() {
    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
    testDataList.add(sessionRequest);
  }

  private void initSessionRequest() {
    sessionRequest = new TennisWorldBookingRequest();
    sessionRequest.setDate("2021-07-30");
    sessionRequest.setTime("06:30am");
    sessionRequest.setDuration("45");
    sessionRequest.setPassword("goku-son");
    sessionRequest.setUsername("goku@dbz.com");
    sessionRequest.setSessionType("CARDIO");
    sessionRequest.setSite("MELBOURNE_PARK");
    TennisWorldBookingRequest.CardDetails cardDetails = new TennisWorldBookingRequest.CardDetails();
    cardDetails.setName("SON GOKU");
    cardDetails.setNumber("1111222233334444");
    cardDetails.setCvv("999");
    cardDetails.setExpiryDate("12/3099");
    sessionRequest.setCardDetails(cardDetails);
  }
}
