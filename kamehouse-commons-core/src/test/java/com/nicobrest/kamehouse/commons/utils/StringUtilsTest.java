package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

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
