package com.nicobrest.kamehouse.main.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;

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
}
