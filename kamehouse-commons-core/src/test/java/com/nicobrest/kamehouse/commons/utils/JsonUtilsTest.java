package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nicobrest.kamehouse.commons.annotations.Masked;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

  /**
   * Tests getting the byte array from an object and compares it's string representation.
   */
  @Test
  public void toJsonByteArraySuccessTest() throws IOException {
    byte[] output = JsonUtils.toJsonByteArray(jsonNode);
    String expectedOutput = JsonUtils.toJsonString(jsonNode);
    assertEquals(expectedOutput, new String(output, Charsets.UTF_8));
  }

  /**
   * Tests toJsonString with masked fields.
   */
  @Test
  public void toJsonStringWithMaskedFieldsTest() {
    String[] maskedFields = {"textField", "doubleField"};
    String output = JsonUtils.toJsonString(jsonNode, null, maskedFields);
    String expectedOutput = "{"
        + "  \"intField\":128,"
        + "  \"booleanField\":true,"
        + "  \"textField\":\"****\","
        + "  \"doubleField\":\"****\""
        + "}";
    expectedOutput = expectedOutput.replace(" ", "");
    assertEquals(expectedOutput, output);
  }

  /**
   * Tests toJsonString with masked annotated fields.
   */
  @Test
  public void toJsonStringMaskedAnnotatedFieldsTest() {
    TestClass testClass = new TestClass();
    testClass.setId(1L);
    testClass.setName("goku");
    TestSubClass testSubClass = new TestSubClass();
    testSubClass.setId(2L);
    testSubClass.setPasswordSub("pegasus");
    testClass.setTestSubClass(testSubClass);

    String output = JsonUtils.toJsonString(testClass, null, true);

    String expectedOutput = "{"
        + "  \"id\":1,"
        + "  \"testSubClass\":{"
        + "      \"id\":2,"
        + "      \"testSubSubClass\":null,"
        + "      \"passwordSub\":\"****\""
        + "  },"
        + "  \"testSubClassList\":null,"
        + "  \"testSubClassMap\":null,"
        + "  \"name\":\"****\""
        + "}";
    expectedOutput = expectedOutput.replace(" ", "");
    assertEquals(expectedOutput, output);
  }

  /**
   * Tests toJsonString with masked annotated fields with nested fields and lists.
   */
  //TODO Fix JsonUtils.toJsonString() to handle Masked fields on lists and maps. I expect name,
  // passwordSub and passwordSubSub to be hidden at all levels. The current fix is to annotate the
  // list or map with Masked and mask the entire list, when some property in an object of the list
  // needs to be hidden
  @Test
  public void toJsonStringMaskedAnnotatedNestedFieldsTest() {
    TestClass testClass = new TestClass();
    testClass.setId(1L);
    testClass.setName("goku");
    TestSubClass testSubClass = new TestSubClass();
    testSubClass.setId(2L);
    testSubClass.setPasswordSub("pegasus");
    TestSubSubClass testSubSubClass = new TestSubSubClass();
    testSubSubClass.setId(3L);
    testSubSubClass.setPasswordSubSub("seiya");
    testSubClass.setTestSubSubClass(testSubSubClass);
    testClass.setTestSubClass(testSubClass);
    List<TestSubClass> testSubClassList = new ArrayList<>();
    testSubClassList.add(testSubClass);
    testClass.setTestSubClassList(testSubClassList);
    Map<Long, TestSubClass> testSubClassMap = new HashMap<>();
    testSubClassMap.put(2L, testSubClass);
    testClass.setTestSubClassMap(testSubClassMap);

    String output = JsonUtils.toJsonString(testClass, null, true);

    String expectedOutput = "{"
        + "  \"id\":1,"
        + "  \"testSubClass\":{"
        + "      \"id\":2,"
        + "      \"testSubSubClass\":{"
        + "          \"id\":3,"
        + "          \"passwordSubSub\":\"****\""
        + "      },"
        + "      \"passwordSub\":\"****\""
        + "  },"
        + "  \"testSubClassList\":[{"
        + "      \"id\":2,"
        + "      \"passwordSub\":\"pegasus\","
        + "      \"testSubSubClass\":{"
        + "          \"id\":3,"
        + "          \"passwordSubSub\":\"seiya\""
        + "      }"
        + "  }],"
        + "  \"testSubClassMap\":{"
        + "      \"2\":{"
        + "          \"id\":2,"
        + "          \"passwordSub\":\"pegasus\","
        + "          \"testSubSubClass\":{"
        + "              \"id\":3,"
        + "              \"passwordSubSub\":\"seiya\""
        + "          }"
        + "    }"
        + "  },"
        + "  \"name\":\"****\""
        + "}";
    expectedOutput = expectedOutput.replace(" ", "");
    assertEquals(expectedOutput, output);
  }

  /**
   * Tests toJsonString with masked fields in subnode.
   */
  @Test
  public void toJsonStringWithMaskedFieldsInSubNodeTest() {
    String[] maskedFields = {"textField", "doubleField", "user.password"};
    String output = JsonUtils.toJsonString(jsonNodeWithSubNode, null, maskedFields);
    String expectedOutput = "{"
        + "  \"intField\":128,"
        + "  \"booleanField\":true,"
        + "  \"user\":{"
        + "      \"username\":\"goku@dbz.com\","
        + "      \"password\":\"****\""
        + "  },"
        + "  \"textField\":\"****\","
        + "  \"doubleField\":\"****\""
        + "}";
    expectedOutput = expectedOutput.replace(" ", "");
    assertEquals(expectedOutput, output);
  }

  /**
   * Tests toJsonString without any masked fields.
   */
  @Test
  public void toJsonStringWithoutMaskedFieldsTest() {
    String output = JsonUtils.toJsonString(jsonNodeWithSubNode, null, true);
    String expectedOutput = "{"
        + "  \"textField\":\"goku\","
        + "  \"intField\":128,"
        + "  \"doubleField\":255.0,"
        + "  \"booleanField\":true,"
        + "  \"user\":{"
        + "    \"password\":\"madamadadane\","
        + "    \"username\":\"goku@dbz.com\""
        + "  }"
        + "}";
    expectedOutput = expectedOutput.replace(" ", "")
        .replace("madamadadane", "mada mada dane");
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
    assertNull(invalidKeyOutput, "Expecting null for invalid key");

    String emptyJsonOutput = JsonUtils.getText(emptyJsonNode, "textField");
    assertNull(emptyJsonOutput, "Expecting null for empty json");
  }

  /**
   * Tests the getInt method from a jsonNode.
   */
  @Test
  public void getIntTest() {
    Integer validKeyOutput = JsonUtils.getInt(jsonNode, "intField");
    assertEquals(Integer.valueOf(128), validKeyOutput);

    Integer invalidKeyOutput = JsonUtils.getInt(jsonNode, "invalidField");
    assertEquals(Integer.valueOf(0), invalidKeyOutput);

    Integer emptyJsonOutput = JsonUtils.getInt(emptyJsonNode, "intField");
    assertEquals(Integer.valueOf(0), emptyJsonOutput);
  }

  /**
   * Tests the getDouble method from a jsonNode.
   */
  @Test
  public void getDoubleTest() {
    Double validKeyOutput = JsonUtils.getDouble(jsonNode, "doubleField");
    assertEquals(Double.valueOf(255), validKeyOutput);

    Double invalidKeyOutput = JsonUtils.getDouble(jsonNode, "invalidField");
    assertEquals(Double.valueOf(0), invalidKeyOutput);

    Double emptyJsonOutput = JsonUtils.getDouble(emptyJsonNode, "doubleField");
    assertEquals(Double.valueOf(0), emptyJsonOutput);
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
   * Tests toJson.
   */
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

  /**
   * Set mock objects.
   */
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

  /**
   * Test class to test Masked annotation.
   */
  public static class TestClass {

    private Long id;

    @Masked
    private String name;

    private TestSubClass testSubClass;

    private List<TestSubClass> testSubClassList;

    private Map<Long, TestSubClass> testSubClassMap;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public TestSubClass getTestSubClass() {
      return testSubClass;
    }

    public void setTestSubClass(
        TestSubClass testSubClass) {
      this.testSubClass = testSubClass;
    }

    public List<TestSubClass> getTestSubClassList() {
      return testSubClassList;
    }

    public void setTestSubClassList(List<TestSubClass> testSubClassList) {
      this.testSubClassList = testSubClassList;
    }

    public Map<Long, TestSubClass> getTestSubClassMap() {
      return testSubClassMap;
    }

    public void setTestSubClassMap(Map<Long, TestSubClass> testSubClassMap) {
      this.testSubClassMap = testSubClassMap;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      TestClass that = (TestClass) o;
      return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, name);
    }

    @Override
    public String toString() {
      return JsonUtils.toJsonString(this, super.toString());
    }
  }

  /**
   * Test class to test Masked annotation.
   */
  public static class TestSubClass {

    private Long id;

    @Masked
    private String passwordSub;

    private TestSubSubClass testSubSubClass;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getPasswordSub() {
      return passwordSub;
    }

    public void setPasswordSub(String passwordSub) {
      this.passwordSub = passwordSub;
    }

    public TestSubSubClass getTestSubSubClass() {
      return testSubSubClass;
    }

    public void setTestSubSubClass(TestSubSubClass testSubSubClass) {
      this.testSubSubClass = testSubSubClass;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      TestSubClass that = (TestSubClass) o;
      return Objects.equals(id, that.id) && Objects.equals(passwordSub, that.passwordSub);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, passwordSub);
    }

    @Override
    public String toString() {
      return JsonUtils.toJsonString(this, super.toString());
    }
  }

  /**
   * Test class to test Masked annotation.
   */
  public static class TestSubSubClass {

    private Long id;

    @Masked
    private String passwordSubSub;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getPasswordSubSub() {
      return passwordSubSub;
    }

    public void setPasswordSubSub(String passwordSubSub) {
      this.passwordSubSub = passwordSubSub;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      TestSubSubClass that = (TestSubSubClass) o;
      return Objects.equals(id, that.id) && Objects.equals(passwordSubSub, that.passwordSubSub);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, passwordSubSub);
    }

    @Override
    public String toString() {
      return JsonUtils.toJsonString(this, super.toString());
    }
  }
}
