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
    return input.replaceAll("[\n|\r|\t]", "_");
  }
}
