package com.nicobrest.kamehouse.commons.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nicobrest.kamehouse.commons.annotations.Masked.MaskedUtils;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to process jsons.
 *
 * @author nbrest
 */
public class JsonUtils {

  public static final String DEFAULT_VALUE =
      "{\"message\": \"Error: Unable to convert object to json string.\"}";
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
   * Converts an object to a JSON string filtering the specified masked fields. Returns the
   * specified default value if the conversion to JSON fails. A masked field can be at the root
   * object or in the any of the sub objects. If it's at the root, it would just be the field name.
   * For example [ "password" ]. If it's in a sub object, the field is prepended by the sub object
   * and separated by a dot. For example [ "tennisWorldUser.password" ] would mask the password of
   * the tennisWorldUser. And tennisWorldUser is an element of the root node. It doesn't mask fields
   * in Maps or Lists.
   */
  public static String toJsonString(Object object, String defaultValue, String[] maskedFields) {
    try {
      JsonNode jsonNode = MAPPER.valueToTree(object);
      if (!(jsonNode instanceof ObjectNode)) {
        return MAPPER.writer().writeValueAsString(jsonNode);
      }
      ObjectNode objectNode = (ObjectNode) jsonNode;
      for (String maskedField : maskedFields) {
        maskField(objectNode, maskedField);
      }
      /*
        If I ever need output with pretty print, create a new method
        toJsonStringPrettyPrint that uses the pretty print writer;
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
      */
      return MAPPER.writer().writeValueAsString(objectNode);
    } catch (IllegalArgumentException | IOException e) {
      LOGGER.error("Error formatting object as json", e);
      return defaultValue;
    }
  }

  /**
   * Converts an object to a JSON string.
   */
  public static String toJsonString(Object object, String defaultValue, boolean maskFields) {
    if (maskFields) {
      return toJsonString(object, defaultValue, MaskedUtils.getMaskedFields(object));
    } else {
      return toJsonString(object, defaultValue, new String[0]);
    }
  }

  /**
   * Converts an object to a JSON string. Returns the specified default value if the conversion to
   * JSON fails. By default, mask fields.
   */
  public static String toJsonString(Object object, String defaultValue) {
    return toJsonString(object, defaultValue, true);
  }

  /**
   * Converts an object to a JSON string. Returns a standard error message if the conversion to JSON
   * fails.
   */
  public static String toJsonString(Object object) {
    return toJsonString(object, DEFAULT_VALUE);
  }

  /**
   * Converts the specified string to an array of JSON objects.
   */
  public static JsonNode[] toJsonArray(String jsonArrayString) {
    if (jsonArrayString == null) {
      return null;
    }
    try {
      return MAPPER.readValue(jsonArrayString, JsonNode[].class);
    } catch (JsonProcessingException e) {
      LOGGER.error("Error converting string to json array. {}", e.getMessage());
      return null;
    }
  }

  /**
   * Returns the text value of the specified key and node. Returns null if not found.
   */
  public static String getText(JsonNode jsonNode, String key) {
    if (jsonNode != null && jsonNode.has(key)) {
      return jsonNode.get(key).asText();
    } else {
      return null;
    }
  }

  /**
   * Returns the int value of the specified key and node. Returns 0 if not found.
   */
  public static Integer getInt(JsonNode jsonNode, String key) {
    if (jsonNode != null && jsonNode.has(key)) {
      return jsonNode.get(key).asInt();
    } else {
      return 0;
    }
  }

  /**
   * Returns the long value of the specified key and node. Returns 0 if not found.
   */
  public static Long getLong(JsonNode jsonNode, String key) {
    if (jsonNode != null && jsonNode.has(key)) {
      return jsonNode.get(key).asLong();
    } else {
      return 0L;
    }
  }

  /**
   * Returns the double value of the specified key and node. Returns 0 if not found.
   */
  public static Double getDouble(JsonNode jsonNode, String key) {
    if (jsonNode != null && jsonNode.has(key)) {
      return jsonNode.get(key).asDouble();
    } else {
      return 0D;
    }
  }

  /**
   * Returns the boolean value of the specified key and node. Returns null if not found.
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

  /**
   * Checks if the specified string is a JSON object.
   */
  public static boolean isJsonObject(String text) {
    if (text == null) {
      return false;
    }
    try {
      new JSONObject(text);
    } catch (JSONException e) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the specified string is a JSON array.
   */
  public static boolean isJsonArray(String text) {
    if (text == null) {
      return false;
    }
    try {
      new JSONArray(text);
    } catch (JSONException e) {
      return false;
    }
    return true;
  }

  /**
   * Mask the specified field.
   */
  private static void maskField(ObjectNode objectNode, String maskedField) {
    String[] maskFieldPath = maskedField.split("\\.");
    int maskedFieldPathDepth = maskFieldPath.length;
    if (maskedFieldPathDepth == 1 && objectNode.has(maskedField)) {
      objectNode.remove(maskedField);
      objectNode.put(maskedField, FIELD_MASK);
    }
    if (maskedFieldPathDepth > 1) {
      JsonNode childNode = objectNode;
      for (int i = 0; i < maskedFieldPathDepth - 1; i++) {
        if (childNode != null && childNode.has(maskFieldPath[i])) {
          childNode = childNode.get(maskFieldPath[i]);
        } else {
          childNode = null;
          break;
        }
      }
      String finalMaskedField = maskFieldPath[maskedFieldPathDepth - 1];
      if (childNode != null && childNode.has(finalMaskedField)) {
        ObjectNode childObjectNode = (ObjectNode) childNode;
        childObjectNode.remove(finalMaskedField);
        childObjectNode.put(finalMaskedField, FIELD_MASK);
      }
    }
  }
}
