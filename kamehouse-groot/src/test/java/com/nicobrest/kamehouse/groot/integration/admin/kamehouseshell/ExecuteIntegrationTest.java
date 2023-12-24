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
import org.junit.jupiter.api.Test;

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
  @Test
  void executeHelpParameterSuccessTest() throws IOException {
    String urlParams = "?script=is-linux-host.sh&args=-h";
    logger.info("Running test for {}", getWebappUrl() + API_URL + urlParams);

    HttpResponse response = get(getWebappUrl() + API_URL + urlParams);

    JsonNode responseBody = verifySuccessfulResponse(response, JsonNode.class);
    assertEquals(2, responseBody.size());
    assertNotNull(responseBody.get("htmlConsoleOutput"), "htmlConsoleOutput is null");
    assertNotNull(responseBody.get("bashConsoleOutput"), "bashConsoleOutput is null");
    ArrayNode htmlConsoleOutput = (ArrayNode) responseBody.get("htmlConsoleOutput");
    String expected = "[<span style=\"color:#3996ff\">INFO<span style=\"color:gray\">]";
    assertStringInArray(htmlConsoleOutput, expected);
    expected = "Started executing <span style=\"color:purple\">exec-script.sh<span style=\"c"
        + "olor:green\"> with command line arguments <span style=\"color:purple\">\"-s is-linux-ho"
        + "st.sh -a -h\"<span style=\"color:green\"><span style=\"color:gray\">";
    assertStringInArray(htmlConsoleOutput, expected);
    expected = "Usage: <span style=\"color:purple\">exec-script.sh<span style=\"color:gray\"> "
        + "[options]";
    assertStringInArray(htmlConsoleOutput, expected);
    expected = "<span style=\"color:#3996ff\">-h<span style=\"color:gray\"> display help";
    assertStringInArray(htmlConsoleOutput, expected);

    String bashConsoleOutput = responseBody.get("bashConsoleOutput").asText();
    expected = "Started executing ";
    assertTrue(bashConsoleOutput.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
    expected = "-s is-linux-host.sh -a -h";
    assertTrue(bashConsoleOutput.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
    expected = "exec-script.sh";
    assertTrue(bashConsoleOutput.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
    expected = "display help";
    assertTrue(bashConsoleOutput.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
  }

  /**
   * Test for groot kamehouse-shell execute success response.
   */
  @Test
  void executeSuccessTest() throws IOException {
    String urlParams = "?script=is-linux-host.sh";
    logger.info("Running test for {}", getWebappUrl() + API_URL + urlParams);

    HttpResponse response = get(getWebappUrl() + API_URL + urlParams);

    JsonNode responseBody = verifySuccessfulResponse(response, JsonNode.class);
    assertEquals(2, responseBody.size());
    assertNotNull(responseBody.get("htmlConsoleOutput"), "htmlConsoleOutput is null");
    assertNotNull(responseBody.get("bashConsoleOutput"), "bashConsoleOutput is null");
    ArrayNode htmlConsoleOutput = (ArrayNode) responseBody.get("htmlConsoleOutput");
    String expected = "[<span style=\"color:#3996ff\">INFO<span style=\"color:gray\">]";
    assertStringInArray(htmlConsoleOutput, expected);
    expected = "Started executing <span style=\"color:purple\">exec-script.sh<span style=\"c"
        + "olor:green\"> with command line arguments <span style=\"color:purple\">\"-s is-linux-ho"
        + "st.sh -a \"<span style=\"color:green\"><span style=\"color:gray\">";
    assertStringInArray(htmlConsoleOutput, expected);
    expected = "<span style=\"color:green\">Finished executing <span style=\"color:purple\">exec-s"
        + "cript.sh<span style=\"color:green\"> with command line arguments <span style=\"color:pu"
        + "rple\">\"-s is-linux-host.sh -a \"<span style=\"color:green\"> successfully<span style"
        + "=\"color:gray\">";
    assertStringInArray(htmlConsoleOutput, expected);

    String bashConsoleOutput = responseBody.get("bashConsoleOutput").asText();
    expected = "Started executing ";
    assertTrue(bashConsoleOutput.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
    expected = "-s is-linux-host.sh -a ";
    assertTrue(bashConsoleOutput.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
    expected = "exec-script.sh";
    assertTrue(bashConsoleOutput.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
    expected = "Finished executing ";
    assertTrue(bashConsoleOutput.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
    expected = " successfully";
    assertTrue(bashConsoleOutput.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
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
