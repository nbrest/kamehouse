package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * StringUtils tests.
 *
 * @author nbrest
 */
class StringUtilsTest {

  @Test
  public void sanitizeInputTests() {
    String input = "goku\ngohan\ttrunks\rsanad<>^&`a";
    String output = StringUtils.sanitizeInput(input);
    assertEquals("gokugohantrunkssanada", output);
  }
}
