package com.nicobrest.kamehouse.commons.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Utility class to manipulate dates.
 *
 * @author nbrest
 */
public class DateUtils {

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
}
