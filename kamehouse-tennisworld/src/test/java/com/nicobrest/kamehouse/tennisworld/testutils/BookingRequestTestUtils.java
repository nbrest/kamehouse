package com.nicobrest.kamehouse.tennisworld.testutils;

import static org.junit.Assert.assertEquals;
import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.commons.utils.DateUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.SessionType;
import com.nicobrest.kamehouse.tennisworld.model.Site;

import java.util.Calendar;
import java.util.LinkedList;

/**
 * Test data and common test methods to test Tennis World Booking Requests in all layers of the
 * application.
 *
 * @author nbrest
 *
 */
public class BookingRequestTestUtils extends AbstractTestUtils<BookingRequest, Object>
    implements TestUtils<BookingRequest, Object> {

  private BookingRequest sessionRequest = null;

  public BookingRequest getSessionRequest() {
    return sessionRequest;
  }

  @Override
  public void initTestData() {
    initSessionRequest();
    initSingleTestData();
    initTestDataList();
  }

  @Override
  public void assertEqualsAllAttributes(BookingRequest expected, BookingRequest returned) {
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
    singleTestData = new BookingRequest();
    singleTestData.setDate(DateUtils.getDate(2020, Calendar.JULY, 28));
    singleTestData.setTime("18:45");
    singleTestData.setDuration("60");
    singleTestData.setPassword("goku-son");
    singleTestData.setUsername("goku@dbz.com");
    singleTestData.setSessionType(SessionType.ROD_LAVER_OUTDOOR);
    singleTestData.setSite(Site.MELBOURNE_PARK);
    BookingRequest.CardDetails cardDetails = new BookingRequest.CardDetails();
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
    sessionRequest = new BookingRequest();
    sessionRequest.setDate(DateUtils.getDate(2021, Calendar.JULY, 30));
    sessionRequest.setTime("06:30");
    sessionRequest.setDuration("45");
    sessionRequest.setPassword("goku-son");
    sessionRequest.setUsername("goku@dbz.com");
    sessionRequest.setSessionType(SessionType.CARDIO);
    sessionRequest.setSite(Site.MELBOURNE_PARK);
    BookingRequest.CardDetails cardDetails = new BookingRequest.CardDetails();
    cardDetails.setName("SON GOKU");
    cardDetails.setNumber("1111222233334444");
    cardDetails.setCvv("999");
    cardDetails.setExpiryDate("12/3099");
    sessionRequest.setCardDetails(cardDetails);
  }
}
