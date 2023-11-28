package com.nicobrest.kamehouse.commons.utils;

/**
 * Utility class to manipulate strings.
 *
 * @author nbrest
 */
public class StringUtils {

  private static final String DEFAULT_SANITIZE_REGEX = "[\n\r\t\"\'<>`^&]";

  private StringUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Removes potentially dangerous characters from external input.
   */
  public static String sanitize(String input) {
    return sanitize(input, DEFAULT_SANITIZE_REGEX);
  }

  /**
   * Removes potentially dangerous characters from external input for the specified regex.
   */
  public static String sanitize(String input, String regex) {
    if (input == null) {
      return null;
    }
    return input.replaceAll(regex, "");
  }

  /**
   * Removes potentially dangerous characters from external input for the specified regex.
   */
  public static <T> String sanitize(T entity) {
    return sanitize(entity, DEFAULT_SANITIZE_REGEX);
  }

  /**
   * Removes potentially dangerous characters from external input for the specified regex.
   */
  public static <T> String sanitize(T entity, String regex) {
    if (entity == null) {
      return null;
    }
    return sanitize(entity.toString(), regex);
  }

  /**
   * Wrapper for apache commmons StringUtils so I don't need to import both in the same file.
   */
  public static boolean isEmpty(String value) {
    return org.apache.commons.lang3.StringUtils.isEmpty(value);
  }

  /**
   * Wrapper for apache commmons StringUtils so I don't need to import both in the same file.
   */
  public static String substringAfter(String source, String pattern) {
    return org.apache.commons.lang3.StringUtils.substringAfter(source, pattern);
  }

  /**
   * Wrapper for apache commmons StringUtils so I don't need to import both in the same file.
   */
  public static String substringAfterLast(String source, String pattern) {
    return org.apache.commons.lang3.StringUtils.substringAfterLast(source, pattern);
  }

  /**
   * Wrapper for apache commmons StringUtils so I don't need to import both in the same file.
   */
  public static String substringBeforeLast(String source, String pattern) {
    return org.apache.commons.lang3.StringUtils.substringBeforeLast(source, pattern);
  }

  /**
   * Wrapper for apache commmons StringUtils so I don't need to import both in the same file.
   */
  public static String substring(String source, int start, int end) {
    return org.apache.commons.lang3.StringUtils.substring(source, start, end);
  }

  /**
   * Wrapper for apache commmons StringUtils so I don't need to import both in the same file.
   */
  public static int lastIndexOf(CharSequence source, CharSequence seq) {
    return org.apache.commons.lang3.StringUtils.lastIndexOf(source, seq);
  }
}
