package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * PropertiesUtils tests. NOTE: I wouldn't test this class with it's current version (2019/05/19).
 * But * adding the tests for practice.
 *
 * @author nbrest
 */
public class PropertiesUtilsTest {

  @Test
  public void getPropertyTest() {
    String expectedPropertyValue = "mada mada dane";

    String returnedPropertyValue = PropertiesUtils.getProperty("my.test.property");

    assertEquals(expectedPropertyValue, returnedPropertyValue);
  }
}
