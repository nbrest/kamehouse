package com.nicobrest.kamehouse.commons.utils;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to manipulate dates.
 *
 * @author nbrest
 */
public class DateUtils {

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
  public static Date addSeconds(Date date, int seconds) {
    return org.apache.commons.lang3.time.DateUtils.addSeconds(date, seconds);
  }

  /**
   * Generate a cron expression from a Date.
   */
  public static String toCronExpression(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return toCronExpression(calendar);
  }

  /**
   * Generate a cron expression from a Calendar.
   */
  public static String toCronExpression(Calendar calendar) {
    int seconds = calendar.get(Calendar.SECOND);
    int minutes = calendar.get(Calendar.MINUTE);
    int hours = calendar.get(Calendar.HOUR_OF_DAY);
    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    int month = calendar.get(Calendar.MONTH) + 1;
    int year = calendar.get(Calendar.YEAR);
    return toCronExpression(seconds, minutes, hours, dayOfMonth, month, "?", year);
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
   * Get the current date with hours set to 00:00.
   */
  public static Date getCurrentDate() {
    return new Date();
  }

  /**
   * Get a Date in the future with the specified days from today.
   */
  public static Date getDateFromToday(Integer daysFromToday) {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, daysFromToday);
    return calendar.getTime();
  }

  /**
   * Get the date for two weeks from the specified date. Used for scheduled cardio bookings.
   */
  public static Date getTwoWeeksFrom(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, 14);
    return calendar.getTime();
  }

  /**
   * Get the date from the specified parameters. Pass the month as Calendar.OCTOBER for October for
   * example. The months start from 0 in the Calendar class, so october would be 9 instead of 10.
   */
  public static Date getDate(Integer year, Integer month, Integer day) {
    return getDate(year, month, day, 0, 0, 0);
  }

  /**
   * Get the date from the specified parameters. Pass the month as Calendar.OCTOBER for October for
   * example. The months start from 0 in the Calendar class, so october would be 9 instead of 10.
   */
  public static Date getDate(
      Integer year, Integer month, Integer day, Integer hours, Integer minutes, Integer seconds) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month, day, hours, minutes, seconds);
    return calendar.getTime();
  }

  /**
   * Get the current date in the specified format pattern. Ej yyyy-MM-dd.
   */
  public static String getFormattedDate(String pattern) {
    return getFormattedDate(pattern, new Date());
  }

  /**
   * Get the specified date in the specified format pattern. Ej yyyy-MM-dd.
   */
  public static String getFormattedDate(String pattern, Date date) {
    if (date == null) {
      return null;
    }
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String formattedDate = simpleDateFormat.format(date);
    return formattedDate;
  }

  /**
   * Get the current day of the week. Compare the int response with Calendar.SUNDAY to check if it's
   * a sunday. SUNDAY = 1 MONDAY = 2 TUESDAY = 3 WEDNESDAY = 4 THURSDAY = 5 FRIDAY = 6 SATURDAY = 7
   */
  public static int getCurrentDayOfWeek() {
    Calendar calendar = Calendar.getInstance();
    return calendar.get(Calendar.DAY_OF_WEEK);
  }

  /**
   * Get the day of the week as a string from an int. Pass the value as Calendar.SUNDAY for example
   * to get the string Sunday. Expects values 1 to 7.
   */
  public static String getDayOfWeek(Integer dayOfWeek) {
    switch (dayOfWeek) {
      case Calendar.SUNDAY:
        return Day.SUNDAY.getValue();
      case Calendar.MONDAY:
        return Day.MONDAY.getValue();
      case Calendar.TUESDAY:
        return Day.TUESDAY.getValue();
      case Calendar.WEDNESDAY:
        return Day.WEDNESDAY.getValue();
      case Calendar.THURSDAY:
        return Day.THURSDAY.getValue();
      case Calendar.FRIDAY:
        return Day.FRIDAY.getValue();
      case Calendar.SATURDAY:
        return Day.SATURDAY.getValue();
      default:
        break;
    }
    throw new IllegalArgumentException("Invalid dayOfWeek int parameter passed. Expected 1 to 7");
  }

  /**
   * Returns the difference in days between the dates, in absolute value. Returns 0 if the dates are
   * on the same day. If the difference is 1 week, then it returns 6 days in between dates. (Ej.
   * Monday this week to Monday next weeks). If the difference is 2 weeks, then it returns 13 days
   * in between dates. (Ej Monday this week to monday 2 weeks from now)
   */
  public static long getDaysBetweenDates(Date date1, Date date2) {
    if (date1 == null || date2 == null) {
      throw new KameHouseInvalidDataException("Dates to compare can't be null");
    }
    LocalDateTime dateTime1 =
        getLocalDateTime(date1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    LocalDateTime dateTime2 =
        getLocalDateTime(date2).withHour(0).withMinute(0).withSecond(0).withNano(0);
    long daysBetween = Duration.between(dateTime1, dateTime2).toDays();
    return Math.abs(daysBetween);
  }

  /**
   * Returns true if afterDate is equal or after to beforeDate (ignoring timestamps).
   */
  public static boolean isOnOrAfter(Date beforeDate, Date afterDate) {
    if (beforeDate == null || afterDate == null) {
      throw new KameHouseInvalidDataException("beforeDate and afterDate can't be null");
    }
    long daysBetween =
        Duration.between(getLocalDateTime(beforeDate), getLocalDateTime(afterDate)).toDays();
    return daysBetween >= 0;
  }

  /**
   * Convert a Date to LocalDateTime.
   */
  public static LocalDateTime getLocalDateTime(Date date) {
    return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
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
      String input, String inFormat, String outFormat, Boolean lowerCaseOut) {
    if (input == null) {
      return null;
    }
    try {
      if (PropertiesUtils.isWindowsHost()) {
        input = input.toUpperCase(Locale.getDefault());
      } else {
        input = input.toLowerCase(Locale.getDefault());
      }
      String result =
          LocalTime.parse(input, DateTimeFormatter.ofPattern(inFormat))
              .format(DateTimeFormatter.ofPattern(outFormat));
      if (lowerCaseOut) {
        result = result.toLowerCase(Locale.getDefault());
      } else {
        result = result.toUpperCase(Locale.getDefault());
      }
      return result;
    } catch (DateTimeParseException e) {
      throw new KameHouseInvalidDataException("Unable to parse input time " + input);
    }
  }

  /**
   * Return the day of the week as {@link Day} for the specified date.
   */
  public static Day getDay(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    return getDay(dayOfWeek);
  }

  /**
   * Get the day of the week as a string from an int. Pass the value as Calendar.SUNDAY for example
   * to get the string Sunday. Expects values 1 to 7.
   */
  public static Day getDay(Integer dayOfWeek) {
    switch (dayOfWeek) {
      case Calendar.SUNDAY:
        return Day.SUNDAY;
      case Calendar.MONDAY:
        return Day.MONDAY;
      case Calendar.TUESDAY:
        return Day.TUESDAY;
      case Calendar.WEDNESDAY:
        return Day.WEDNESDAY;
      case Calendar.THURSDAY:
        return Day.THURSDAY;
      case Calendar.FRIDAY:
        return Day.FRIDAY;
      case Calendar.SATURDAY:
        return Day.SATURDAY;
      default:
        break;
    }
    throw new IllegalArgumentException("Invalid dayOfWeek int parameter passed. Expected 1 to 7");
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

    private String value;
    private int number;

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
}
