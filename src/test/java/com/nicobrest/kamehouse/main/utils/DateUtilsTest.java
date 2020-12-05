package com.nicobrest.kamehouse.main.utils;

import static org.junit.Assert.assertEquals;
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
    assertEquals("Thu Oct 15 00:01:00 AEDT 2020", output.toString());
  }
}
