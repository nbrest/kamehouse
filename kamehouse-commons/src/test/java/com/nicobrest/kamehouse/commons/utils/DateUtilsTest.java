package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * DateUtils tests.
 *
 * @author nbrest
 */
public class DateUtilsTest {

  /** Tests the several methods to get a cron expression. */
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

  /** Tests adding seconds to a date. */
  @Test
  public void addSecondsTest() {
    Date date = new GregorianCalendar(2020, Calendar.OCTOBER, 15).getTime();
    Date output = DateUtils.addSeconds(date, 60);
    // format: Xxx Xxx 99 99:99:99 XXXX 9999
    String expectedDateRegex =
        "[A-Za-z]{3} [A-Za-z]{3} [0-9]{1,2} [0-9]{2}:[0-9]{2}:[0-9]{2} " + "[A-Za-z]+ [0-9]{4}";
    Assertions.assertTrue(
        output.toString().matches(expectedDateRegex), "Date doesn't match expected format");
  }

  /** Test getCurrentDate. */
  @Test
  public void getCurrentDateTest() {
    assertNotNull(DateUtils.getCurrentDate());
  }

  /** Test getTwoWeeksFrom. */
  @Test
  public void getTwoWeeksFromTest() {
    assertNotNull(DateUtils.getTwoWeeksFrom(new Date()));
  }

  /** Test getDateFromToday. */
  @Test
  public void getDateFromTodayTest() {
    assertNotNull(DateUtils.getDateFromToday(2));
  }

  /** Test getDate. */
  @Test
  public void getDateTest() {
    Date date = DateUtils.getDate(1984, Calendar.OCTOBER, 15);
    assertTrue(
        date.toString().startsWith("Mon Oct 15 00:00:00"), "Date doesn't match the expected value");

    date = DateUtils.getDate(1984, Calendar.OCTOBER, 15, 9, 10, 11);
    assertTrue(
        date.toString().startsWith("Mon Oct 15 09:10:11"), "Date doesn't match the expected value");
  }

  /** Test getFormattedDate. */
  @Test
  public void getFormattedDateTest() {
    String expectedDateRegex = "[0-9]{4}-[0-9]{2}-[0-9]{2}";

    String formattedDate = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD);
    assertTrue(formattedDate.matches(expectedDateRegex), "Date doesn't match expected format");

    formattedDate = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD, new Date());
    assertTrue(formattedDate.matches(expectedDateRegex), "Date doesn't match expected format");
  }

  /** Test getCurrentDayOfWeek. */
  @Test
  public void getCurrentDayOfWeekTest() {
    int currentDayOfWeek = DateUtils.getCurrentDayOfWeek();
    assertTrue(
        currentDayOfWeek >= 1 && currentDayOfWeek <= 7, "currentDayOfWeek has an invalid value");
  }

  /** Test getDayOfWeek. */
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

  /** Test getDaysBetweenDates. */
  @Test
  public void getDaysBetweenDatesTest() {
    assertEquals(0, DateUtils.getDaysBetweenDates(new Date(), new Date()));
  }

  /** Test isOnOrAfter. */
  @Test
  public void isOnOrAfterTest() {
    assertEquals(true, DateUtils.isOnOrAfter(new Date(), new Date()));
  }

  /** Test convertTime. */
  @Test
  public void convertTimeTest() {
    assertEquals(
        "08:15 PM", DateUtils.convertTime("20:15", DateUtils.HH_MM_24HS, DateUtils.HH_MM_AM_PM));

    assertEquals(
        "08:15 AM", DateUtils.convertTime("08:15", DateUtils.HH_MM_24HS, DateUtils.HH_MM_AM_PM));

    assertEquals(
        "20:15", DateUtils.convertTime("08:15 PM", DateUtils.HH_MM_AM_PM, DateUtils.HH_MM_24HS));

    assertEquals(
        "08:15", DateUtils.convertTime("08:15 AM", DateUtils.HH_MM_AM_PM, DateUtils.HH_MM_24HS));

    assertEquals(
        "08:15 pm",
        DateUtils.convertTime("20:15", DateUtils.HH_MM_24HS, DateUtils.HH_MM_AM_PM, true));

    assertEquals(
        "08:15pm",
        DateUtils.convertTime("20:15", DateUtils.HH_MM_24HS, DateUtils.HH_MMAM_PM, true));

    assertEquals(
        "20:15",
        DateUtils.convertTime("08:15pm", DateUtils.HH_MMAM_PM, DateUtils.HH_MM_24HS, false));
  }

  /** Test convertTime exception. */
  @Test
  public void convertTimeExceptionTest() {
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          DateUtils.convertTime("20:1 5", DateUtils.HH_MM_24HS, DateUtils.HH_MM_AM_PM);
        });
  }

  /** Test isOnOrAfter exception test. */
  @Test
  public void isOnOrAfterExceptionTest() {
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          DateUtils.isOnOrAfter(null, new Date());
        });
  }

  /** Test getLocalDateTime. */
  @Test
  public void getLocalDateTimeTest() {
    assertNotNull(DateUtils.getLocalDateTime(new Date()));
  }

  /** Test getDay. */
  @Test
  public void getDayTest() {
    assertNotNull(DateUtils.getDay(new Date()));
  }
}
