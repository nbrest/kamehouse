package com.nicobrest.kamehouse.main.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
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
   * Converts an object to a JSON string. Returns a standard error message if the
   * conversion to JSON fails.
   */
  public static String toJsonString(Object object) {
    String defaultValue = "{\"message\": \"Unable to convert object to json string.\"}";
    return toJsonString(object, defaultValue);
  }

  /**
   * Returns the text value of the specified key and node. Returns null if not
   * found.
   */
  public static String getText(JsonNode jsonNode, String key) {
    if (jsonNode != null && jsonNode.has(key)) {
      return jsonNode.get(key).asText();
    } else {
      return null;
    }
  }

  /**
   * Returns the int value of the specified key and node. Returns 0 if not
   * found.
   */
  public static Integer getInt(JsonNode jsonNode, String key) {
    if (jsonNode != null && jsonNode.has(key)) {
      return jsonNode.get(key).asInt();
    } else {
      return 0;
    }
  }

  /**
   * Returns the double value of the specified key and node. Returns 0 if not
   * found.
   */
  public static Double getDouble(JsonNode jsonNode, String key) {
    if (jsonNode != null && jsonNode.has(key)) {
      return jsonNode.get(key).asDouble();
    } else {
      return 0D;
    }
  }

  /**
   * Returns the boolean value of the specified key and node. Returns null if not
   * found.
   */
  public static Boolean getBoolean(JsonNode jsonNode, String key) {
    if (jsonNode != null && jsonNode.has(key)) {
      return jsonNode.get(key).asBoolean();
    } else {
      return false;
    }
  }

  /**
   * Checks if the specified JsonNode is an array and is not empty.
   */
  public static boolean isJsonNodeArrayEmpty(JsonNode jsonNodeArray) {
    return !(jsonNodeArray != null && jsonNodeArray.isArray() && jsonNodeArray.size() > 0);
  }
}
