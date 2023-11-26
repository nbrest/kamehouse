package com.nicobrest.kamehouse.commons.utils;

import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to manipulate strings.
 *
 * @author nbrest
 */
public class StringUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(StringUtils.class);

  private StringUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Removes potentially dangerous characters from external input. This method will need to be
   * updated constantly.
   */
  public static String sanitizeInput(String input) {
    if (input == null) {
      return null;
    }
    return input.replaceAll("[\n\r\t\"\'<>`^&]", "");
  }


  /**
   * Sanitize String fields in input entity.
   */
  public static <T> void sanitizeEntity(T entity) {
    LOGGER.trace("Sanitizing entity");
    if (entity == null) {
      return;
    }
    Field[] fields = entity.getClass().getDeclaredFields();
    for (Field field : fields) {
      if (String.class.equals(field.getType())) {
        try {
          boolean currentAccessibility = field.trySetAccessible();
          field.setAccessible(true);
          field.set(entity, StringUtils.sanitizeInput((String) field.get(entity)));
          field.setAccessible(currentAccessibility);
        } catch (IllegalAccessException e) {
          LOGGER.trace("Error sanitizing. Field: {}", field.getName());
        }
      }
      if (hasSubFields(field)) {
        try {
          field.setAccessible(true);
          Object fieldValue = field.get(entity);
          sanitizeEntity(fieldValue);
        } catch (IllegalAccessException e) {
          LOGGER.trace("Error accessing object field to sanitize. Field: {}",
              field.getName());
        }
      }
    }
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

  /**
   * Check if the field has subfields.
   */
  private static boolean hasSubFields(Field field) {
    if (!field.getType().getCanonicalName().contains("com.nicobrest.kamehouse")) {
      return false;
    }
    if (field.getType().isEnum()) {
      return false;
    }
    Field[] subfields = field.getType().getDeclaredFields();
    return subfields != null && subfields.length > 0;
  }
}
