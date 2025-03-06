package com.nicobrest.kamehouse.groot.integration.admin.kamehouseshell;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.nicobrest.kamehouse.groot.integration.AbstractGrootIntegrationTest;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Integration tests for GRoot kamehouse-shell execute endpoint.
 *
 * @author nbrest
 */
class ExecuteIntegrationTest extends AbstractGrootIntegrationTest {

  private static final String API_URL = "/api/v1/admin/kamehouse-shell/execute.php";

  /**
   * Test for groot kamehouse-shell execute with help parameter success response.
   */
  @ParameterizedTest
  @ValueSource(strings = {"base-script.sh"})
  void executeHelpParameterSuccessTest(String script) throws IOException {
    String urlParams = "?script=" + script + "&args=-h";
    logger.info("Running test for {}", getWebappUrl() + API_URL + urlParams);

    HttpResponse response = get(getWebappUrl() + API_URL + urlParams);

    JsonNode responseBody = verifySuccessfulResponse(response, JsonNode.class);
    assertEquals(8, responseBody.size());
    assertNotNull(responseBody.get("standardOutputHtml"), "standardOutputHtml is null");
    assertNotNull(responseBody.get("standardOuput"), "standardOuput is null");
    ArrayNode standardOutputHtml = (ArrayNode) responseBody.get("standardOutputHtml");
    String expected = "Started executing script without args";
    assertStringInArray(standardOutputHtml, expected);

    String standardOuput = responseBody.get("standardOuput").asText();
    expected = "Started executing ";
    assertTrue(standardOuput.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
    expected = "display help";
    assertTrue(standardOuput.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
  }

  /**
   * Test for groot kamehouse-shell execute success response.
   */
  @ParameterizedTest
  @ValueSource(strings = {"base-script.sh"})
  void executeSuccessTest(String script) throws IOException {
    String urlParams = "?script=" + script;
    logger.info("Running test for {}", getWebappUrl() + API_URL + urlParams);

    HttpResponse response = get(getWebappUrl() + API_URL + urlParams);

    JsonNode responseBody = verifySuccessfulResponse(response, JsonNode.class);
    assertEquals(8, responseBody.size());
    assertNotNull(responseBody.get("standardOutputHtml"), "standardOutputHtml is null");
    assertNotNull(responseBody.get("standardOuput"), "standardOuput is null");
    ArrayNode standardOutputHtml = (ArrayNode) responseBody.get("standardOutputHtml");
    String expected = "Started executing script without args";
    assertStringInArray(standardOutputHtml, expected);
    expected = "Finished executing script ";
    assertStringInArray(standardOutputHtml, expected);

    String standardOuput = responseBody.get("standardOuput").asText();
    expected = "Started executing ";
    assertTrue(standardOuput.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
    expected = "Finished executing";
    assertTrue(standardOuput.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
    expected = "status: ";
    assertTrue(standardOuput.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
  }

  /**
   * Validate that the expected string is contained in the array.
   */
  private void assertStringInArray(ArrayNode arrayNode, String expected) {
    boolean isStringInArray = false;
    for (JsonNode jsonNode : arrayNode) {
      TextNode textNode = (TextNode) jsonNode;
      if (textNode.textValue().contains(expected)) {
        isStringInArray = true;
      }
    }
    assertTrue(isStringInArray, expected + " is not in array");
  }
}
