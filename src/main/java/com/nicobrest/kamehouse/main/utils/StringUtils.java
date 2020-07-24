package com.nicobrest.kamehouse.main.utils;

/**
 * Utility class to manipulate strings.
 *
 * @author nbrest
 */
public class StringUtils {

  private StringUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Removes potentially dangerous characters from external input.
   * This method will need to be updated constantly.
   */
  public static String sanitizeInput(String input) {
    if (input == null) {
      return null;
    }
    return input.replaceAll("[\n|\r|\t]", "");
  }

  /**
   * Wrapper for apache commmons StringUtils so I don't need to import both in the same file.
   */
  public static boolean isEmpty(String value) {
    return org.apache.commons.lang3.StringUtils.isEmpty(value);
  }
}
