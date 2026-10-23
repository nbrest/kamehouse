package com.nicobrest.kamehouse.commons.utils;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to manipulate dates.
 *
 * @author nbrest
 */
public class DateUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);

  public static final String YYYY_MM_DD = "yyyy-MM-dd";
  public static final String HH_MM_24HS = "HH:mm";
  public static final String HH_MM_AM_PM = "hh:mm a";
  public static final String HH_MMAM_PM = "hh:mma";

  private DateUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Add the specified amount of seconds to the date.
   */
  public static LocalDateTime addSeconds(LocalDateTime date, int seconds) {
    return date.plusSeconds(seconds);
  }

  /**
   * Generate a cron expression from a date.
   */
  public static String toCronExpression(LocalDateTime date) {
    return toCronExpression(date.getSecond(), date.getMinute(), date.getHour(),
        date.getDayOfMonth(), date.getMonthValue(), "?", date.getYear());
  }

  /**
   * Generate a CRON expression is a string comprising 6 or 7 fields separated by white space.
   *
   * @param seconds    mandatory = yes. allowed values = {@code 0-59 * / , -}
   * @param minutes    mandatory = yes. allowed values = {@code 0-59 * / , -}
   * @param hours      mandatory = yes. allowed values = {@code 0-23 * / , -}
   * @param dayOfMonth mandatory = yes. allowed values = {@code 1-31 * / , - ? L W}
   * @param month      mandatory = yes. allowed values = {@code 1-12 or JAN-DEC * / , -}
   * @param dayOfWeek  mandatory = yes. allowed values = {@code 0-6 or SUN-SAT * / , - ? L #}
   * @param year       mandatory = no. allowed values = {@code 1970–2099 * / , -}
   * @return a CRON Formatted String.
   */
  public static String toCronExpression(
      int seconds, int minutes, int hours, int dayOfMonth, int month, String dayOfWeek, int year) {
    return String.format(
        "%1$s %2$s %3$s %4$s %5$s %6$s %7$s",
        seconds, minutes, hours, dayOfMonth, month, dayOfWeek, year);
  }

  /**
   * Get the current date and time.
   */
  public static LocalDateTime getCurrentDate() {
    return LocalDateTime.now(ZoneId.systemDefault());
  }

  /**
   * Get the current local date.
   */
  public static LocalDate getCurrentLocalDate() {
    return LocalDate.now(ZoneId.systemDefault());
  }

  /**
   * Get a date in the future with the specified days from today.
   */
  public static LocalDate getDateFromToday(Integer daysFromToday) {
    return LocalDate.now(ZoneId.systemDefault()).plusDays(daysFromToday);
  }

  /**
   * Get the date for two weeks from the specified date. Used for scheduled cardio bookings.
   */
  public static LocalDate getTwoWeeksFrom(LocalDate date) {
    return date.plusDays(14);
  }

  /**
   * Get the date from the specified parameters.
   */
  public static LocalDate getDate(Integer year, Month month, Integer day) {
    return LocalDate.of(year, month, day);
  }

  /**
   * Get the date and time from the specified parameters.
   */
  public static LocalDateTime getDate(
      Integer year, Month month, Integer day, Integer hours, Integer minutes, Integer seconds) {
    return LocalDateTime.of(year, month, day, hours, minutes, seconds);
  }

  /**
   * Get the current date in the specified format pattern. Ej yyyy-MM-dd.
   */
  public static String getFormattedDate(String pattern) {
    return getFormattedDate(pattern, getCurrentDate());
  }

  /**
   * Get the specified date in the specified format pattern. Ej yyyy-MM-dd.
   */
  public static String getFormattedDate(String pattern, TemporalAccessor date) {
    if (date == null) {
      return null;
    }
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
    return dateTimeFormatter.format(date);
  }

  /**
   * Get the current day of the week. SUNDAY = 1 MONDAY = 2 TUESDAY = 3 WEDNESDAY = 4 THURSDAY = 5
   * FRIDAY = 6 SATURDAY = 7
   */
  public static int getCurrentDayOfWeek() {
    return toCalendarDayOfWeek(LocalDate.now(ZoneId.systemDefault()).getDayOfWeek());
  }

  private static int toCalendarDayOfWeek(DayOfWeek dayOfWeek) {
    return DayOfWeek.SUNDAY.equals(dayOfWeek) ? 1 : dayOfWeek.getValue() + 1;
  }

  /**
   * Get the day of the week as a string from an int. Expects values 1 to 7.
   */
  public static String getDayOfWeek(Integer dayOfWeek) {
    return getDay(dayOfWeek).getValue();
  }

  /**
   * Returns the difference in days between the dates, in absolute value. Returns 0 if the dates are
   * on the same day. If the difference is 1 week, then it returns 6 days in between dates. (Ej.
   * Monday this week to Monday next weeks). If the difference is 2 weeks, then it returns 13 days
   * in between dates. (Ej Monday this week to monday 2 weeks from now)
   */
  public static long getDaysBetweenDates(LocalDate date1, LocalDate date2) {
    if (date1 == null || date2 == null) {
      throw new KameHouseInvalidDataException("Dates to compare can't be null");
    }
    long daysBetween = ChronoUnit.DAYS.between(date1, date2);
    return Math.abs(daysBetween);
  }

  /**
   * Returns true if afterDate is equal or after to beforeDate (ignoring timestamps).
   */
  public static boolean isOnOrAfter(LocalDate beforeDate, LocalDate afterDate) {
    if (beforeDate == null || afterDate == null) {
      throw new KameHouseInvalidDataException("beforeDate and afterDate can't be null");
    }
    return !beforeDate.isAfter(afterDate);
  }

  /**
   * Converts time from one format to another. To convert from 24hs to hh:mm [AM|PM] use
   * inFormat:'HH:mm' and outFormat:'hh:mm a'
   */
  public static String convertTime(String input, String inFormat, String outFormat) {
    return convertTime(input, inFormat, outFormat, false);
  }

  /**
   * Converts time from one format to another. To convert from 24hs to hh:mm [AM|PM] use
   * inFormat:'HH:mm' and outFormat:'hh:mm a'
   */
  public static String convertTime(
      String input, String inFormat, String outFormat, boolean lowerCaseOut) {
    if (input == null) {
      return null;
    }
    try {
      input = input.toUpperCase(Locale.getDefault());
      LocalTime localTime = getLocalTime(input, inFormat);
      DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(outFormat);
      String result = localTime.format(dateTimeFormatter);
      if (lowerCaseOut) {
        result = result.toLowerCase(Locale.getDefault());
      } else {
        result = result.toUpperCase(Locale.getDefault());
      }
      return result;
    } catch (DateTimeParseException e) {
      LOGGER.error(
          "Error parsing time for input {}, inFormat {}, outFormat {} and lowerCaseOut {},",
          input, inFormat, outFormat, lowerCaseOut, e);
      throw new KameHouseInvalidDataException("Unable to parse input time " + input);
    }
  }

  /**
   * Convert the build date of the modules to YYYY-MM-DD HH:MM:SS format. Sample input date format
   * 'Sat Mar 26 19:47:48 AEDT 2022'.
   */
  public static String getFormattedBuildDate(String inputDate) {
    if (StringUtils.isEmpty(inputDate)) {
      LOGGER.error("Unexpected empty input date");
      return inputDate;
    }
    String buildDateRegex =
        "^[A-Za-z]{3} [A-Za-z]{3} \\d{2} \\d{2}:\\d{2}:\\d{2} .{3,5} \\d{4}";
    Pattern buildDatePattern = Pattern.compile(buildDateRegex);
    Matcher matcher = buildDatePattern.matcher(inputDate);
    if (!matcher.matches()) {
      LOGGER.warn("Unexpected build date pattern found with input: {}", inputDate);
      return inputDate;
    }
    String month;
    try {
      String monthField = inputDate.substring(4, 7).toUpperCase(Locale.getDefault());
      month = MonthValue.valueOf(monthField).getNumber();
    } catch (IllegalArgumentException e) {
      LOGGER.error("Error parsing month from input: {}", inputDate, e);
      return inputDate;
    }
    String day = inputDate.substring(8, 10);
    String year = inputDate.substring(inputDate.length() - 4);
    String time = inputDate.substring(11, 19);
    return year + "-" + month + "-" + day + " " + time;
  }

  /**
   * Get local time for the specified input and inFormat.
   */
  public static LocalTime getLocalTime(String input, String inFormat) {
    try {
      return LocalTime.parse(input, DateTimeFormatter.ofPattern(inFormat));
    } catch (DateTimeParseException _) {
      return LocalTime.parse(input.toLowerCase(Locale.getDefault()),
          DateTimeFormatter.ofPattern(inFormat));
    }
  }

  /**
   * Return the day of the week as {@link Day} for the specified date.
   */
  public static Day getDay(LocalDate date) {
    if (date == null) {
      return null;
    }
    return getDay(toCalendarDayOfWeek(date.getDayOfWeek()));
  }

  /**
   * Get the day of the week as a string from an int. Expects values 1 to 7.
   */
  public static Day getDay(Integer dayOfWeek) {
    if (dayOfWeek == null || dayOfWeek < 1 || dayOfWeek > 7) {
      throw new IllegalArgumentException("Invalid dayOfWeek int parameter passed. Expected 1 to 7");
    }
    return Day.values()[dayOfWeek - 1];
  }

  /**
   * Day of the week enum.
   */
  public enum Day {
    SUNDAY("Sunday", 1),
    MONDAY("Monday", 2),
    TUESDAY("Tuesday", 3),
    WEDNESDAY("Wednesday", 4),
    THURSDAY("Thursday", 5),
    FRIDAY("Friday", 6),
    SATURDAY("Saturday", 7);

    private final String value;
    private final int number;

    Day(String value, int number) {
      this.value = value;
      this.number = number;
    }

    public String getValue() {
      return value;
    }

    public int getNumber() {
      return number;
    }
  }

  /**
   * Month translated from MMM to its numeric 2 digit value.
   */
  public enum MonthValue {
    JAN("01"),
    FEB("02"),
    MAR("03"),
    APR("04"),
    MAY("05"),
    JUN("06"),
    JUL("07"),
    AUG("08"),
    SEP("09"),
    OCT("10"),
    NOV("11"),
    DEC("12");

    private final String number;

    MonthValue(String value) {
      this.number = value;
    }

    public String getNumber() {
      return number;
    }
  }
}
