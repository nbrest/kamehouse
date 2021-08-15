package com.nicobrest.kamehouse.tennisworld.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.commons.utils.DateUtils;
import com.nicobrest.kamehouse.tennisworld.dao.BookingScheduleConfigDaoJpa;
import com.nicobrest.kamehouse.tennisworld.model.BookingScheduleConfig;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldSessionType;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldSite;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingScheduleConfigDto;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

/**
 * Test data and common test methods to test BookingScheduleConfig in all layers of
 * the application.
 * 
 * @author nbrest
 *
 */
public class BookingScheduleConfigTestUtils
    extends AbstractTestUtils<BookingScheduleConfig, BookingScheduleConfigDto>
    implements TestUtils<BookingScheduleConfig, BookingScheduleConfigDto> {

  public static final String API_V1_TENNISWORLD_BOOKING_SCHEDULE_CONFIG = "/api/v1/tennis-world"
      + "/booking-schedule-config/";

  private TennisWorldUserTestUtils tennisWorldUserTestUtils = new TennisWorldUserTestUtils();
  private TennisWorldUser tennisWorldUser;

  @Override
  public void initTestData() {
    tennisWorldUserTestUtils.initTestData();
    tennisWorldUser = tennisWorldUserTestUtils.getSingleTestData();
    tennisWorldUser.setId(1L);
    initSingleTestData();
    initTestDataList();
    initTestDataDto();
  }

  @Override
  public void assertEqualsAllAttributes(BookingScheduleConfig expectedEntity,
      BookingScheduleConfig returnedEntity) {
    assertEquals(expectedEntity.getId(), returnedEntity.getId());
    assertEquals(expectedEntity.getTennisWorldUser(), returnedEntity.getTennisWorldUser());
    assertEquals(expectedEntity.getSessionType(), returnedEntity.getSessionType());
    assertEquals(expectedEntity.getSite(), returnedEntity.getSite());
    assertEquals(expectedEntity.getDay(), returnedEntity.getDay());
    assertEquals(expectedEntity.getTime(), returnedEntity.getTime());
    assertEquals(expectedEntity.getBookAheadDays(), returnedEntity.getBookAheadDays());
    assertEquals(expectedEntity.getEnabled(), returnedEntity.getEnabled());
    assertEquals(expectedEntity.getDuration(), returnedEntity.getDuration());
    if (expectedEntity.getBookingDate() != null && returnedEntity.getBookingDate() != null) {
      String expectedDate = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD,
          expectedEntity.getBookingDate());
      String returnedDate = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD,
          returnedEntity.getBookingDate());
      assertEquals(expectedDate, returnedDate);
    }
  }

  private void initSingleTestData() {
    singleTestData = new BookingScheduleConfig();
    singleTestData.setTennisWorldUser(tennisWorldUser);
    singleTestData.setBookingDate(BookingScheduleConfigDaoJpa.DEFAULT_BOOKING_DATE);
    singleTestData.setBookAheadDays(1);
    singleTestData.setDay(DateUtils.Day.FRIDAY);
    singleTestData.setDuration("45");
    singleTestData.setEnabled(true);
    singleTestData.setSessionType(TennisWorldSessionType.CARDIO);
    singleTestData.setSite(TennisWorldSite.MELBOURNE_PARK);
    singleTestData.setTime("12:00pm");
  }

  private void initTestDataDto() {
    testDataDto = new BookingScheduleConfigDto();
    testDataDto.setTennisWorldUser(tennisWorldUser);
    testDataDto.setBookingDate(BookingScheduleConfigDaoJpa.DEFAULT_BOOKING_DATE);
    testDataDto.setBookAheadDays(1);
    testDataDto.setDay(DateUtils.Day.FRIDAY);
    testDataDto.setDuration("45");
    testDataDto.setEnabled(true);
    testDataDto.setSessionType(TennisWorldSessionType.CARDIO);
    testDataDto.setSite(TennisWorldSite.MELBOURNE_PARK);
    testDataDto.setTime("12:00pm");
  }

  private void initTestDataList() {
    BookingScheduleConfig user2 = new BookingScheduleConfig();
    user2.setTennisWorldUser(tennisWorldUser);
    user2.setBookingDate(BookingScheduleConfigDaoJpa.DEFAULT_BOOKING_DATE);
    user2.setBookAheadDays(2);
    user2.setDay(DateUtils.Day.FRIDAY);
    user2.setDuration("45");
    user2.setEnabled(true);
    user2.setSessionType(TennisWorldSessionType.CARDIO);
    user2.setSite(TennisWorldSite.MELBOURNE_PARK);
    user2.setTime("12:00pm");

    BookingScheduleConfig user3 = new BookingScheduleConfig();
    user3.setTennisWorldUser(tennisWorldUser);
    user3.setBookingDate(BookingScheduleConfigDaoJpa.DEFAULT_BOOKING_DATE);
    user3.setBookAheadDays(3);
    user3.setDay(DateUtils.Day.FRIDAY);
    user3.setDuration("45");
    user3.setEnabled(true);
    user3.setSessionType(TennisWorldSessionType.CARDIO);
    user3.setSite(TennisWorldSite.MELBOURNE_PARK);
    user3.setTime("12:00pm");

    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
    testDataList.add(user2);
    testDataList.add(user3);
  }
}
