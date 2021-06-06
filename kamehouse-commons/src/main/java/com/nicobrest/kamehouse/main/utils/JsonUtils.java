package com.nicobrest.kamehouse.main.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
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

  public static final String DEFAULT_VALUE = "{\"message\": \"Unable to convert object to json "
      + "string.\"}";
  private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);
  private static final String FIELD_MASK = "****";
  private static final ObjectMapper MAPPER = new ObjectMapper();

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
   * Converts a string to a JSON object. Returns null if it can't do the mapping.
   */
  public static JsonNode toJson(String objectString) {
    if (objectString == null) {
      return null;
    }
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_NULL);
    try {
      return mapper.readTree(objectString);
    } catch (IOException e) {
      LOGGER.warn("Unable to map '{}' to a json object", objectString);
      return null;
    }
  }

  /**
   * Converts an object to a JSON string filtering the specified masked fields.
   * Returns the specified default value if the conversion to JSON fails.
   */
  public static String toJsonString(Object object, String defaultValue, String[] maskedFields) {
    try {
      ObjectNode objectNode = MAPPER.valueToTree(object);
      for (String maskedField : maskedFields) {
        if (objectNode.has(maskedField)) {
          objectNode.remove(maskedField);
          objectNode.put(maskedField, FIELD_MASK);
        }
      }
      return MAPPER.writer().writeValueAsString(objectNode);
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
      return MAPPER.writer().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      LOGGER.error("Error formatting object as json", e);
      return defaultValue;
    }
  }

  /**
   * Converts an object to a JSON string. Returns a standard error message if the
   * conversion to JSON fails.
   */
  public static String toJsonString(Object object) {
    return toJsonString(object, DEFAULT_VALUE);
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
  public static boolean getBoolean(JsonNode jsonNode, String key) {
    if (jsonNode != null && jsonNode.has(key)) {
      return jsonNode.get(key).asBoolean();
    } else {
      return false;
    }
  }

  /**
   * Checks if the specified JsonNode is an array and is empty.
   */
  public static boolean isJsonNodeArrayEmpty(JsonNode jsonNodeArray) {
    return !(jsonNodeArray != null && jsonNodeArray.isArray() && jsonNodeArray.size() > 0);
  }
}
