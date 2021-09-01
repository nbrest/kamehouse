package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import org.apache.commons.codec.Charsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JsonUtils tests.
 *
 * @author nbrest
 */
public class JsonUtilsTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private JsonNode jsonNode;
  private JsonNode jsonNodeWithSubNode;
  private JsonNode emptyJsonNode;
  private ArrayNode jsonArray;
  private ArrayNode emptyJsonArray;

  @BeforeEach
  public void init() throws IOException {
    populateTestData();
  }

  /** Tests getting the byte array from an object and compares it's string representation. */
  @Test
  public void toJsonByteArraySuccessTest() throws IOException {
    byte[] output = JsonUtils.toJsonByteArray(jsonNode);
    String expectedOutput = JsonUtils.toJsonString(jsonNode);
    assertEquals(expectedOutput, new String(output, Charsets.UTF_8));
  }

  /** Tests toJsonString with masked fields. */
  @Test
  public void toJsonStringWithMaskedFieldsTest() {
    String[] maskedFields = {"textField", "doubleField"};
    String output = JsonUtils.toJsonString(jsonNode, null, maskedFields);
    String expectedOutput =
        "{\"intField\":128,\"booleanField\":true,\"textField\":\"****\","
            + "\"doubleField\":\"****\"}";
    assertEquals(expectedOutput, output);
  }

  /** Tests toJsonString with masked fields in subnode. */
  @Test
  public void toJsonStringWithMaskedFieldsInSubNodeTest() {
    String[] maskedFields = {"textField", "doubleField", "user.password"};
    String output = JsonUtils.toJsonString(jsonNodeWithSubNode, null, maskedFields);
    String expectedOutput =
        "{\"intField\":128,\"booleanField\":true,\"user\":{\"username\":"
            + "\"goku@dbz.com\",\"password\":\"****\"},\"textField\":\"****\""
            + ",\"doubleField\":\"****\"}";
    assertEquals(expectedOutput, output);
  }

  /** Tests toJsonString returning a custom default value. */
  @Test
  public void toJsonStringWithDefaultValueTest() {
    String defaultValue = "{\"msg\":\"my custom default\"}";
    String output = JsonUtils.toJsonString(new Object(), defaultValue);
    assertEquals(defaultValue, output);
  }

  /** Tests toJsonString returning the standard default value. */
  @Test
  public void toJsonStringWithoutDefaultValueTest() {
    String output = JsonUtils.toJsonString(new Object());
    assertEquals(JsonUtils.DEFAULT_VALUE, output);
  }

  /** Tests the getText method from a jsonNode. */
  @Test
  public void getTextTest() {
    String validKeyOutput = JsonUtils.getText(jsonNode, "textField");
    assertEquals("goku", validKeyOutput);

    String invalidKeyOutput = JsonUtils.getText(jsonNode, "invalidField");
    assertNull(invalidKeyOutput, "Expecting null for invalid key");

    String emptyJsonOutput = JsonUtils.getText(emptyJsonNode, "textField");
    assertNull(emptyJsonOutput, "Expecting null for empty json");
  }

  /** Tests the getInt method from a jsonNode. */
  @Test
  public void getIntTest() {
    Integer validKeyOutput = JsonUtils.getInt(jsonNode, "intField");
    assertEquals(Integer.valueOf(128), validKeyOutput);

    Integer invalidKeyOutput = JsonUtils.getInt(jsonNode, "invalidField");
    assertEquals(Integer.valueOf(0), invalidKeyOutput);

    Integer emptyJsonOutput = JsonUtils.getInt(emptyJsonNode, "intField");
    assertEquals(Integer.valueOf(0), emptyJsonOutput);
  }

  /** Tests the getDouble method from a jsonNode. */
  @Test
  public void getDoubleTest() {
    Double validKeyOutput = JsonUtils.getDouble(jsonNode, "doubleField");
    assertEquals(Double.valueOf(255), validKeyOutput);

    Double invalidKeyOutput = JsonUtils.getDouble(jsonNode, "invalidField");
    assertEquals(Double.valueOf(0), invalidKeyOutput);

    Double emptyJsonOutput = JsonUtils.getDouble(emptyJsonNode, "doubleField");
    assertEquals(Double.valueOf(0), emptyJsonOutput);
  }

  /** Tests the getBoolean method from a jsonNode. */
  @Test
  public void getBooleanTest() {
    Boolean validKeyOutput = JsonUtils.getBoolean(jsonNode, "booleanField");
    assertEquals(true, validKeyOutput);

    Boolean invalidKeyOutput = JsonUtils.getBoolean(jsonNode, "invalidField");
    assertEquals(false, invalidKeyOutput);

    Boolean emptyJsonOutput = JsonUtils.getBoolean(emptyJsonNode, "booleanField");
    assertEquals(false, emptyJsonOutput);
  }

  /** Tests an empty array check. */
  @Test
  public void isJsonNodeArrayEmptyTest() {
    boolean output = JsonUtils.isJsonNodeArrayEmpty(emptyJsonArray);
    assertEquals(true, output);
  }

  /** Tests a on empty array check. */
  @Test
  public void isJsonNodeArrayEmptyNonEmptyTest() {
    boolean output = JsonUtils.isJsonNodeArrayEmpty(jsonArray);
    assertEquals(false, output);
  }

  /** Tests a non array check. */
  @Test
  public void isJsonNodeArrayEmptyNonArrayTest() {
    boolean output = JsonUtils.isJsonNodeArrayEmpty(jsonNode);
    assertEquals(true, output);
  }

  /** Tests toJson. */
  @Test
  public void toJsonTest() {
    String jsonString = "{}";
    JsonNode output = JsonUtils.toJson(jsonString);
    assertNotNull(output);

    output = JsonUtils.toJson(null);
    assertNull(output);

    String invalidJson = "{invalid]";
    output = JsonUtils.toJson(invalidJson);
    assertNull(output);
  }

  /** Set mock objects. */
  private void populateTestData() throws IOException {
    emptyJsonNode = MAPPER.readTree(MAPPER.createObjectNode().toString());
    ObjectNode objectNode = MAPPER.createObjectNode();
    objectNode.put("textField", "goku");
    objectNode.put("intField", 128);
    objectNode.put("doubleField", Double.valueOf(255));
    objectNode.put("booleanField", true);
    jsonNode = MAPPER.readTree(objectNode.toString());
    ObjectNode subNode = MAPPER.createObjectNode();
    subNode.put("password", "mada mada dane");
    subNode.put("username", "goku@dbz.com");
    objectNode.set("user", subNode);
    jsonNodeWithSubNode = MAPPER.readTree(objectNode.toString());
    jsonArray = MAPPER.createArrayNode();
    int i = 0;
    while (i < 6) {
      jsonArray.add(MAPPER.createArrayNode().add("" + i++).add("" + i++));
    }
    emptyJsonArray = MAPPER.createArrayNode();
  }
}
