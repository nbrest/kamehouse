package com.nicobrest.kamehouse.commons.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * DateUtils tests.
 *
 * @author nbrest
 */
public class DateUtilsTest {

  /**
   * Tests the several methods to get a cron expression.
   */
  @Test
  public void toCronExpressionTest() {
    Date date = new GregorianCalendar(2020, Calendar.OCTOBER, 15).getTime();
    String output = DateUtils.toCronExpression(date);
    assertEquals("0 0 0 15 10 ? 2020", output);

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    output = DateUtils.toCronExpression(calendar);
    assertEquals("0 0 0 15 10 ? 2020", output);

    output = DateUtils.toCronExpression(20, 30, 10, 15, 10, "?", 1984);
    assertEquals("20 30 10 15 10 ? 1984", output);
  }

  /**
   * Tests adding seconds to a date.
   */
  @Test
  public void addSecondsTest() {
    Date date = new GregorianCalendar(2020, Calendar.OCTOBER, 15).getTime();
    Date output = DateUtils.addSeconds(date, 60);
    // format: Xxx Xxx 99 99:99:99 XXXX 9999
    String expectedDateRegex = "[A-Za-z]{3} [A-Za-z]{3} [0-9]{1,2} [0-9]{2}:[0-9]{2}:[0-9]{2} " +
        "[A-Za-z]+ [0-9]{4}";
    assertTrue("Date doesn't match expected format", output.toString().matches(expectedDateRegex));
  }

  /**
   * Test getCurrentDate.
   */
  @Test
  public void getCurrentDateTest() {
    assertNotNull(DateUtils.getCurrentDate());
  }

  /**
   * Test getTwoWeeksFromToday.
   */
  @Test
  public void getTwoWeeksFromTodayTest() {
    assertNotNull(DateUtils.getTwoWeeksFromToday());
  }

  /**
   * Test getTwoWeeksFrom.
   */
  @Test
  public void getTwoWeeksFromTest() {
    assertNotNull(DateUtils.getTwoWeeksFrom(new Date()));
  }

  /**
   * Test getDate.
   */
  @Test
  public void getDateTest() {
    Date date = DateUtils.getDate(1984, Calendar.OCTOBER, 15);
    assertTrue("Date doesn't match the expected value",
        date.toString().startsWith("Mon Oct 15 00:00:00"));

    date = DateUtils.getDate(1984, Calendar.OCTOBER, 15, 9, 10, 11);
    assertTrue("Date doesn't match the expected value",
        date.toString().startsWith("Mon Oct 15 09:10:11"));
  }

  /**
   * Test getFormattedDate.
   */
  @Test
  public void getFormattedDateTest() {
    String expectedDateRegex = "[0-9]{4}-[0-9]{2}-[0-9]{2}";

    String formattedDate = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD);
    assertTrue("Date doesn't match expected format",
        formattedDate.matches(expectedDateRegex));

    formattedDate = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD, new Date());
    assertTrue("Date doesn't match expected format",
        formattedDate.matches(expectedDateRegex));
  }

  /**
   * Test getCurrentDayOfWeek.
   */
  @Test
  public void getCurrentDayOfWeekTest() {
    int currentDayOfWeek = DateUtils.getCurrentDayOfWeek();
    assertTrue("currentDayOfWeek has an invalid value",
        currentDayOfWeek >= 1 && currentDayOfWeek <= 7);
  }

  /**
   * Test getDayOfWeek.
   */
  @Test
  public void getDayOfWeekTest() {
    String dayOfWeek = DateUtils.getDayOfWeek(Calendar.SUNDAY);
    assertEquals("Sunday", dayOfWeek);
    dayOfWeek = DateUtils.getDayOfWeek(Calendar.MONDAY);
    assertEquals("Monday", dayOfWeek);
    dayOfWeek = DateUtils.getDayOfWeek(Calendar.TUESDAY);
    assertEquals("Tuesday", dayOfWeek);
    dayOfWeek = DateUtils.getDayOfWeek(Calendar.WEDNESDAY);
    assertEquals("Wednesday", dayOfWeek);
    dayOfWeek = DateUtils.getDayOfWeek(Calendar.THURSDAY);
    assertEquals("Thursday", dayOfWeek);
    dayOfWeek = DateUtils.getDayOfWeek(Calendar.FRIDAY);
    assertEquals("Friday", dayOfWeek);
    dayOfWeek = DateUtils.getDayOfWeek(Calendar.SATURDAY);
    assertEquals("Saturday", dayOfWeek);
  }
}
