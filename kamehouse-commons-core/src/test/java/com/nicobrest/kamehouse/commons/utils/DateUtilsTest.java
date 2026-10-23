package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.utils.DateUtils.Day;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * DateUtils tests.
 *
 * @author nbrest
 */
class DateUtilsTest {

  /**
   * Tests the several methods to get a cron expression.
   */
  @Test
  void toCronExpressionTest() {
    LocalDateTime date = LocalDateTime.of(2020, Month.OCTOBER, 15, 0, 0);
    String output = DateUtils.toCronExpression(date);
    assertEquals("0 0 0 15 10 ? 2020", output);

    output = DateUtils.toCronExpression(20, 30, 10, 15, 10, "?", 1984);
    assertEquals("20 30 10 15 10 ? 1984", output);
  }

  /**
   * Tests adding seconds to a date.
   */
  @Test
  void addSecondsTest() {
    LocalDateTime date = LocalDateTime.of(2020, Month.OCTOBER, 15, 0, 0);
    LocalDateTime output = DateUtils.addSeconds(date, 60);
    assertEquals(date.plusMinutes(1), output);
  }

  /**
   * Test getCurrentDate.
   */
  @Test
  void getCurrentDateTest() {
    assertNotNull(DateUtils.getCurrentDate());
  }

  /**
   * Test getCurrentLocalDate.
   */
  @Test
  void getCurrentLocalDateTest() {
    assertNotNull(DateUtils.getCurrentLocalDate());
  }

  /**
   * Test getTwoWeeksFrom.
   */
  @Test
  void getTwoWeeksFromTest() {
    LocalDate date = LocalDate.now();
    assertEquals(date.plusDays(14), DateUtils.getTwoWeeksFrom(date));
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
    LocalDate date = DateUtils.getDate(1984, Month.OCTOBER, 15);
    assertEquals(LocalDate.of(1984, Month.OCTOBER, 15), date);

    LocalDateTime dateTime = DateUtils.getDate(1984, Month.OCTOBER, 15, 9, 10, 11);
    assertEquals(LocalDateTime.of(1984, Month.OCTOBER, 15, 9, 10, 11), dateTime);
  }

  /**
   * Test getFormattedDate.
   */
  @Test
  void getFormattedDateTest() {
    String expectedDateRegex = "[0-9]{4}-[0-9]{2}-[0-9]{2}";

    String formattedDate = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD);
    assertTrue(formattedDate.matches(expectedDateRegex), "Date doesn't match expected format");

    formattedDate = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD, LocalDate.now());
    assertTrue(formattedDate.matches(expectedDateRegex), "Date doesn't match expected format");

    formattedDate = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD, LocalDateTime.now());
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
    assertEquals("Sunday", DateUtils.getDayOfWeek(1));
    assertEquals("Monday", DateUtils.getDayOfWeek(2));
    assertEquals("Tuesday", DateUtils.getDayOfWeek(3));
    assertEquals("Wednesday", DateUtils.getDayOfWeek(4));
    assertEquals("Thursday", DateUtils.getDayOfWeek(5));
    assertEquals("Friday", DateUtils.getDayOfWeek(6));
    assertEquals("Saturday", DateUtils.getDayOfWeek(7));
  }

  /**
   * Test getDaysBetweenDates.
   */
  @Test
  void getDaysBetweenDatesTest() {
    LocalDate date = LocalDate.now();
    assertEquals(0, DateUtils.getDaysBetweenDates(date, date));
  }

  /**
   * Test isOnOrAfter.
   */
  @Test
  void isOnOrAfterTest() {
    LocalDate date = LocalDate.now();
    assertEquals(true, DateUtils.isOnOrAfter(date, date));
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
    assertDoesNotThrow(() -> {
      boolean lowerCaseOut = Boolean.parseBoolean(lowerCaseOutStr);
      assertEquals(expected, DateUtils.convertTime(input, inFormat, outFormat, lowerCaseOut));
    });
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
    LocalDate date = LocalDate.now();
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          DateUtils.isOnOrAfter(null, date);
        });
  }

  /**
   * Test getDay from LocalDate.
   */
  @Test
  void getDayFromLocalDateTest() {
    assertNotNull(DateUtils.getDay(LocalDate.now()));
    assertNull(DateUtils.getDay((LocalDate) null));
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
