package com.nicobrest.kamehouse.tennisworld.testutils;

import static org.junit.Assert.assertEquals;
import com.nicobrest.kamehouse.main.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.main.testutils.TestUtils;
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

  @Override
  public void initTestData() {
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
    singleTestData.setDate("08:00pm");
    singleTestData.setDuration("60");
    singleTestData.setPassword("goku-son");
    singleTestData.setUsername("goku@dbz.com");
    singleTestData.setSessionType("Rod Laver Arena Showcourt");
    singleTestData.setSite("Tennis World - Melbourne Park");
    singleTestData.setCardDetails(null);
  }

  private void initTestDataList() {
    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
  }
}
