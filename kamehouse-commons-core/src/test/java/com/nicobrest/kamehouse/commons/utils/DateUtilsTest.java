package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.utils.DateUtils.Day;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DateUtils tests.
 *
 * @author nbrest
 */
class DateUtilsTest {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Tests the several methods to get a cron expression.
   */
  @Test
  void toCronExpressionTest() {
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
  void addSecondsTest() {
    Date date = new GregorianCalendar(2020, Calendar.OCTOBER, 15).getTime();
    Date output = DateUtils.addSeconds(date, 60);
    // format: Xxx Xxx 99 99:99:99 XXXX 9999
    String expectedDateRegex =
        "[A-Za-z]{3} [A-Za-z]{3} \\d{1,2} \\d{2}:\\d{2}:\\d{2} " + "[A-Za-z]+ \\d{4}";
    Assertions.assertTrue(
        output.toString().matches(expectedDateRegex), "Date doesn't match expected format");
  }

  /**
   * Test getCurrentDate.
   */
  @Test
  void getCurrentDateTest() {
    assertNotNull(DateUtils.getCurrentDate());
  }

  /**
   * Test getTwoWeeksFrom.
   */
  @Test
  void getTwoWeeksFromTest() {
    assertNotNull(DateUtils.getTwoWeeksFrom(new Date()));
  }

  /**
   * Test getDateFromToday.
   */
  @Test
  void getDateFromTodayTest() {
    assertNotNull(DateUtils.getDateFromToday(2));
  }

  /**
   * Test getDate.
   */
  @Test
  void getDateTest() {
    Date date = DateUtils.getDate(1984, Calendar.OCTOBER, 15);
    assertTrue(
        date.toString().startsWith("Mon Oct 15 00:00:00"), "Date doesn't match the expected value");

    date = DateUtils.getDate(1984, Calendar.OCTOBER, 15, 9, 10, 11);
    assertTrue(
        date.toString().startsWith("Mon Oct 15 09:10:11"), "Date doesn't match the expected value");
  }

  /**
   * Test getFormattedDate.
   */
  @Test
  void getFormattedDateTest() {
    String expectedDateRegex = "[0-9]{4}-[0-9]{2}-[0-9]{2}";

    String formattedDate = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD);
    assertTrue(formattedDate.matches(expectedDateRegex), "Date doesn't match expected format");

    formattedDate = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD, new Date());
    assertTrue(formattedDate.matches(expectedDateRegex), "Date doesn't match expected format");

    assertTrue(DateUtils.getFormattedDate(DateUtils.HH_MM_24HS).matches("[0-9]{2}:[0-9]{2}"));
  }

  /**
   * Test getCurrentDayOfWeek.
   */
  @Test
  void getCurrentDayOfWeekTest() {
    int currentDayOfWeek = DateUtils.getCurrentDayOfWeek();
    assertTrue(
        currentDayOfWeek >= 1 && currentDayOfWeek <= 7, "currentDayOfWeek has an invalid value");
  }

  /**
   * Test getDayOfWeek.
   */
  @Test
  void getDayOfWeekTest() {
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

  /**
   * Test getDaysBetweenDates.
   */
  @Test
  void getDaysBetweenDatesTest() {
    assertEquals(0, DateUtils.getDaysBetweenDates(new Date(), new Date()));
  }

  /**
   * Test isOnOrAfter.
   */
  @Test
  void isOnOrAfterTest() {
    assertEquals(true, DateUtils.isOnOrAfter(new Date(), new Date()));
  }

  /**
   * Test convertTime.
   */
  @ParameterizedTest
  @CsvSource({
      "20:15,08:15 PM," + DateUtils.HH_MM_24HS + "," + DateUtils.HH_MM_AM_PM + ",false",
      "08:15,08:15 AM," + DateUtils.HH_MM_24HS + "," + DateUtils.HH_MM_AM_PM + ",false",
      "08:15 PM,20:15," + DateUtils.HH_MM_AM_PM + "," + DateUtils.HH_MM_24HS + ",false",
      "08:15 AM,08:15," + DateUtils.HH_MM_AM_PM + "," + DateUtils.HH_MM_24HS + ",false",
      "20:15,08:15 pm," + DateUtils.HH_MM_24HS + "," + DateUtils.HH_MM_AM_PM + ",true",
      "20:15,08:15pm," + DateUtils.HH_MM_24HS + "," + DateUtils.HH_MMAM_PM + ",true",
      "08:15pm,20:15," + DateUtils.HH_MMAM_PM + "," + DateUtils.HH_MM_24HS + ",false"
  })
  void convertTimeTest(String input, String expected, String inFormat, String outFormat,
      String lowerCaseOutStr) {
    try {
      boolean lowerCaseOut = Boolean.parseBoolean(lowerCaseOutStr);
      assertEquals(expected, DateUtils.convertTime(input, inFormat, outFormat, lowerCaseOut));
    } catch (KameHouseInvalidDataException e) {
      logger.error(
          "Error executing convertTimeTest for input {}, expected {}, inFormat {}, outFormat {}, lowerCaseOutStr {}",
          input, expected, inFormat, outFormat, lowerCaseOutStr, e);
      fail("Error executing convertTimeTest");
    }
  }

  /**
   * Test convertTime exception.
   */
  @Test
  void convertTimeExceptionTest() {
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          DateUtils.convertTime("20:1 5", DateUtils.HH_MM_24HS, DateUtils.HH_MM_AM_PM);
        });
  }

  /**
   * Test isOnOrAfter exception test.
   */
  @Test
  void isOnOrAfterExceptionTest() {
    Date date = new Date();
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          DateUtils.isOnOrAfter(null, date);
        });
  }

  /**
   * Test getLocalDateTime.
   */
  @Test
  void getLocalDateTimeTest() {
    assertNotNull(DateUtils.getLocalDateTime(new Date()));
  }

  /**
   * Test getDay from Date.
   */
  @Test
  void getDayFromDateTest() {
    assertNotNull(DateUtils.getDay(new Date()));
  }

  /**
   * Test getFormattedBuildDate.
   */
  @ParameterizedTest
  @CsvSource({
      "Sat Jan 26 19:47:48 AEDT 2022,2022-01-26 19:47:48",
      "Sat Feb 26 19:47:48 AEDT 2022,2022-02-26 19:47:48",
      "Sat Mar 26 19:47:48 AEDT 2022,2022-03-26 19:47:48",
      "Sat Apr 26 19:47:48 AEDT 2022,2022-04-26 19:47:48",
      "Sat May 26 19:47:48 AEDT 2022,2022-05-26 19:47:48",
      "Sat Jun 26 19:47:48 AEDT 2022,2022-06-26 19:47:48",
      "Sat Jul 26 19:47:48 AEDT 2022,2022-07-26 19:47:48",
      "Sat Aug 26 19:47:48 AEDT 2022,2022-08-26 19:47:48",
      "Sat Sep 26 19:47:48 AEDT 2022,2022-09-26 19:47:48",
      "Sat Oct 26 19:47:48 AEDT 2022,2022-10-26 19:47:48",
      "Sat Nov 26 19:47:48 AEDT 2022,2022-11-26 19:47:48",
      "Sat Dec 26 19:47:48 AEDT 2022,2022-12-26 19:47:48"
  })
  void getFormattedBuildDateTest(String input, String expectedOutput) {
    assertEquals(expectedOutput, DateUtils.getFormattedBuildDate(input));
  }

  /**
   * Test getFormattedBuildDate unmatched regex.
   */
  @Test
  void getFormattedBuildDateUnmatchedRegexTest() {
    String input = "Sat Mar 26 19:47:48 AEDTAA 2022";
    assertEquals(input, DateUtils.getFormattedBuildDate(input));
  }

  /**
   * Test getFormattedBuildDate invalid month.
   */
  @Test
  void getFormattedBuildDateInvalidMonthTest() {
    String input = "Sat Xxx 26 19:47:48 AEDT 2022";
    assertEquals(input, DateUtils.getFormattedBuildDate(input));
  }

  /**
   * Test getFormattedBuildDate null input.
   */
  @Test
  void getFormattedBuildDateNullInputTest() {
    assertNull(DateUtils.getFormattedBuildDate(null));
  }

  /**
   * Test getDay from Integer.
   */
  @Test
  void getDayFromIntegerTest() {
    assertEquals(Day.SUNDAY, DateUtils.getDay(1));
    assertEquals(Day.MONDAY, DateUtils.getDay(2));
    assertEquals(Day.TUESDAY, DateUtils.getDay(3));
    assertEquals(Day.WEDNESDAY, DateUtils.getDay(4));
    assertEquals(Day.THURSDAY, DateUtils.getDay(5));
    assertEquals(Day.FRIDAY, DateUtils.getDay(6));
    assertEquals(Day.SATURDAY, DateUtils.getDay(7));
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> DateUtils.getDay(8)
    );
  }
}
