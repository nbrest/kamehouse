package com.nicobrest.kamehouse.main.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Utility class to process jsons.
 *
 * @author nbrest
 */
public class JsonUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);
  private static final String HIDDEN_FROM_LOGS = "Field content hidden from logs.";

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
   * Converts an object to a JSON string filtering the specified hidden fields.
   * Returns the specified default value if the conversion to JSON fails.
   */
  public static String toJsonString(Object object, String defaultValue, String[] hiddenFields) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode objectNode = objectMapper.valueToTree(object);
      for (String hiddenField : hiddenFields) {
        if (objectNode.has(hiddenField)) {
          objectNode.remove(hiddenField);
          objectNode.put(hiddenField, HIDDEN_FROM_LOGS);
        }
      }
      return objectMapper.writer().writeValueAsString(objectNode);
    } catch (IOException e) {
      LOGGER.error("Error formatting object as json", e);
      return defaultValue;
    }
  }

  /**
   * Converts an object to a JSON string. Returns the specified default value if
   * the conversion to JSON fails.
   */
  public static String toJsonString(Object object, String defaultValue) {
    try {
      /*
        If I ever need output with pretty print, create a new method
        toJsonStringPrettyPrint that uses the pretty print writer;
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
      */
      return new ObjectMapper().writer().writeValueAsString(object);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      LOGGER.error("Error formatting object as json", e);
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
