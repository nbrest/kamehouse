package com.nicobrest.kamehouse.commons.utils;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

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
