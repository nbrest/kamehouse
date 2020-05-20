package com.nicobrest.kamehouse.main.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author nbrest
 */
public class StringUtilsTest {

  @Test
  public void sanitizeInputTests() {
    String input = "goku\ngohan\ttrunks\rsanada";
    String output = StringUtils.sanitizeInput(input);
    assertEquals("gokugohantrunkssanada", output);
  }
}
