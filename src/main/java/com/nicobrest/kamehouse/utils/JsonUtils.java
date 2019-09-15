package com.nicobrest.kamehouse.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Utility class to process jsons used in the test classes. This code is not
 * necessary in the application. Only in the test classes. Had to move it to the
 * main package because eclipse randomly stops finding it in /src/test/java
 * 
 * @author nbrest
 */
public class JsonUtils {

  private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

  private JsonUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Converts an object to a JSON byte array.
   */
  public static byte[] toJsonByteArray(Object object) throws IOException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_NULL);
    return mapper.writeValueAsBytes(object);
  }

  /**
   * Converts an object to a JSON string. Returns the specified default value if
   * the conversion to JSON fails.
   */
  public static String toJsonString(Object object, String defaultValue) {
    try {
      return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      logger.error("Error formatting object as json", e);
      return defaultValue;
    }
  }
  
  /**
   * Converts an object to a JSON string. Returns a standard error message if
   * the conversion to JSON fails.
   */
  public static String toJsonString(Object object) {
    String defaultValue = "{\"message\": \"Unable to convert object to json string.\"}";
    return toJsonString(object, defaultValue);
  }
}
