package com.nicobrest.kamehouse.main.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * JsonUtils tests.
 *
 * @author nbrest
 */
public class JsonUtilsTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private JsonNode jsonNode;
  private JsonNode emptyJsonNode;
  private ArrayNode jsonArray;
  private ArrayNode emptyJsonArray;

  @Before
  public void init() throws IOException {
    populateTestData();
  }

  /**
   * Tests getting the byte array from an object and compares it's string representation.
   */
  @Test
  public void toJsonByteArraySuccessTest() throws IOException {
    byte[] output = JsonUtils.toJsonByteArray(jsonNode);
    String expectedOutput = JsonUtils.toJsonString(jsonNode);
    assertEquals(expectedOutput, new String(output));
  }

  /**
   * Tests toJsonString with hidden fields.
   */
  @Test
  public void toJsonStringWithHiddenFieldsTest() {
    String[] hiddenFields = { "textField", "doubleField" };
    String output = JsonUtils.toJsonString(jsonNode, null, hiddenFields);
    String expectedOutput = "{\"intField\":128,\"booleanField\":true,\"textField\":\"Field " +
        "content hidden from logs.\",\"doubleField\":\"Field content hidden from logs.\"}";
    assertEquals(expectedOutput, output);
  }

  /**
   * Tests toJsonString returning a custom default value.
   */
  @Test
  public void toJsonStringWithDefaultValueTest() {
    String defaultValue = "{\"msg\":\"my custom default\"}";
    String output = JsonUtils.toJsonString(new Object(), defaultValue);
    assertEquals(defaultValue, output);
  }

  /**
   * Tests toJsonString returning the standard default value.
   */
  @Test
  public void toJsonStringWithoutDefaultValueTest() {
    String output = JsonUtils.toJsonString(new Object());
    assertEquals(JsonUtils.DEFAULT_VALUE, output);
  }

  /**
   * Tests the getText method from a jsonNode.
   */
  @Test
  public void getTextTest() {
    String validKeyOutput = JsonUtils.getText(jsonNode, "textField");
    assertEquals("goku", validKeyOutput);

    String invalidKeyOutput = JsonUtils.getText(jsonNode, "invalidField");
    assertNull("Expecting null for invalid key", invalidKeyOutput);

    String emptyJsonOutput = JsonUtils.getText(emptyJsonNode, "textField");
    assertNull("Expecting null for empty json", emptyJsonOutput);
  }

  /**
   * Tests the getInt method from a jsonNode.
   */
  @Test
  public void getIntTest() {
    Integer validKeyOutput = JsonUtils.getInt(jsonNode, "intField");
    assertEquals(new Integer(128), validKeyOutput);

    Integer invalidKeyOutput = JsonUtils.getInt(jsonNode, "invalidField");
    assertEquals(new Integer(0), invalidKeyOutput);

    Integer emptyJsonOutput = JsonUtils.getInt(emptyJsonNode, "intField");
    assertEquals(new Integer(0), emptyJsonOutput);
  }

  /**
   * Tests the getDouble method from a jsonNode.
   */
  @Test
  public void getDoubleTest() {
    Double validKeyOutput = JsonUtils.getDouble(jsonNode, "doubleField");
    assertEquals(new Double(255), validKeyOutput);

    Double invalidKeyOutput = JsonUtils.getDouble(jsonNode, "invalidField");
    assertEquals(new Double(0), invalidKeyOutput);

    Double emptyJsonOutput = JsonUtils.getDouble(emptyJsonNode, "doubleField");
    assertEquals(new Double(0), emptyJsonOutput);
  }

  /**
   * Tests the getBoolean method from a jsonNode.
   */
  @Test
  public void getBooleanTest() {
    Boolean validKeyOutput = JsonUtils.getBoolean(jsonNode, "booleanField");
    assertEquals(true, validKeyOutput);

    Boolean invalidKeyOutput = JsonUtils.getBoolean(jsonNode, "invalidField");
    assertEquals(false, invalidKeyOutput);

    Boolean emptyJsonOutput = JsonUtils.getBoolean(emptyJsonNode, "booleanField");
    assertEquals(false, emptyJsonOutput);
  }

  /**
   * Tests an empty array check.
   */
  @Test
  public void isJsonNodeArrayEmptyTest() {
    boolean output = JsonUtils.isJsonNodeArrayEmpty(emptyJsonArray);
    assertEquals(true, output);
  }

  /**
   * Tests a on empty array check.
   */
  @Test
  public void isJsonNodeArrayEmptyNonEmptyTest() {
    boolean output = JsonUtils.isJsonNodeArrayEmpty(jsonArray);
    assertEquals(false, output);
  }

  /**
   * Tests a non array check.
   */
  @Test
  public void isJsonNodeArrayEmptyNonArrayTest() {
    boolean output = JsonUtils.isJsonNodeArrayEmpty(jsonNode);
    assertEquals(true, output);
  }

  /**
   * Set mock objects.
   */
  private void populateTestData() throws IOException {
    emptyJsonNode = MAPPER.readTree(MAPPER.createObjectNode().toString());
    ObjectNode objectNode = MAPPER.createObjectNode();
    objectNode.put("textField", "goku");
    objectNode.put("intField", 128);
    objectNode.put("doubleField", new Double(255));
    objectNode.put("booleanField", true);
    jsonNode = MAPPER.readTree(objectNode.toString());
    jsonArray = MAPPER.createArrayNode();
    int i = 0;
    while (i < 6) {
      jsonArray.add(MAPPER.createArrayNode().add("" + i++).add("" + i++));
    }
    emptyJsonArray = MAPPER.createArrayNode();
  }
}
