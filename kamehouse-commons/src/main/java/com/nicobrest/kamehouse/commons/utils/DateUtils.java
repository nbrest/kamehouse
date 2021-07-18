package com.nicobrest.kamehouse.commons.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility class to manipulate dates.
 *
 * @author nbrest
 */
public class DateUtils {

  public static final String YYYY_MM_DD = "yyyy-MM-dd";

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
   * @param seconds    mandatory = yes. allowed values = {@code  0-59    * / , -}
   * @param minutes    mandatory = yes. allowed values = {@code  0-59    * / , -}
   * @param hours      mandatory = yes. allowed values = {@code 0-23   * / , -}
   * @param dayOfMonth mandatory = yes. allowed values = {@code 1-31  * / , - ? L W}
   * @param month      mandatory = yes. allowed values = {@code 1-12 or JAN-DEC    * / , -}
   * @param dayOfWeek  mandatory = yes. allowed values = {@code 0-6 or SUN-SAT * / , - ? L #}
   * @param year       mandatory = no. allowed values = {@code 1970–2099    * / , -}
   * @return a CRON Formatted String.
   */
  public static String toCronExpression(int seconds, int minutes, int hours,
                                        int dayOfMonth, int month, String dayOfWeek,
                                        int year) {
    return String.format("%1$s %2$s %3$s %4$s %5$s %6$s %7$s", seconds, minutes, hours, dayOfMonth,
        month, dayOfWeek, year);
  }

  /**
   * Get the current date.
   */
  public static Date getCurrentDate() {
    return new Date();
  }

  /**
   * Get the date for two weeks from today.
   * Used for scheduled cardio bookings.
   */
  public static Date getTwoWeeksFromToday() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, 14);
    return calendar.getTime();
  }

  /**
   * Get the date for two weeks from the specified date.
   * Used for scheduled cardio bookings.
   */
  public static Date getTwoWeeksFrom(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, 14);
    return calendar.getTime();
  }

  /**
   * Get the date from the specified parameters.
   * Pass the month as Calendar.OCTOBER for October for example. The months start from 0 in the
   * Calendar class, so october would be 9 instead of 10.
   */
  public static Date getDate(Integer year, Integer month, Integer day) {
    return getDate(year, month, day, 0, 0, 0);
  }

  /**
   * Get the date from the specified parameters.
   * Pass the month as Calendar.OCTOBER for October for example. The months start from 0 in the
   * Calendar class, so october would be 9 instead of 10.
   */
  public static Date getDate(Integer year, Integer month, Integer day, Integer hours,
                             Integer minutes, Integer seconds) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month, day, hours, minutes, seconds);
    return calendar.getTime();
  }

  /**
   * Get the current date in the specified format pattern.
   * Ej yyyy-MM-dd.
   */
  public static String getFormattedDate(String pattern) {
    return getFormattedDate(pattern, new Date());
  }

  /**
   * Get the specified date in the specified format pattern.
   * Ej yyyy-MM-dd.
   */
  public static String getFormattedDate(String pattern, Date date) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String formattedDate = simpleDateFormat.format(date);
    return formattedDate;
  }

  /**
   * Get the current day of the week.
   * Compare the int response with Calendar.SUNDAY to check if it's a sunday.
   * SUNDAY = 1
   * MONDAY = 2
   * TUESDAY = 3
   * WEDNESDAY = 4
   * THURSDAY = 5
   * FRIDAY = 6
   * SATURDAY = 7
   */
  public static int getCurrentDayOfWeek() {
    Calendar calendar = Calendar.getInstance();
    return calendar.get(Calendar.DAY_OF_WEEK);
  }

  /**
   * Get the day of the week as a string from an int.
   * Pass the value as Calendar.SUNDAY for example to get the string Sunday.
   * Expects values 1 to 7.
   */
  public static String getDayOfWeek(Integer dayOfWeek) {
    switch (dayOfWeek) {
      case Calendar.SUNDAY:
        return "Sunday";
      case Calendar.MONDAY:
        return "Monday";
      case Calendar.TUESDAY:
        return "Tuesday";
      case Calendar.WEDNESDAY:
        return "Wednesday";
      case Calendar.THURSDAY:
        return "Thursday";
      case Calendar.FRIDAY:
        return "Friday";
      case Calendar.SATURDAY:
        return "Saturday";
      default:
        break;
    }
    throw new IllegalArgumentException("Invalid dayOfWeek int parameter passed. Expected 1 to 7");
  }
}
