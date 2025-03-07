package com.nicobrest.kamehouse.groot.integration.admin.kamehouseshell;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import com.nicobrest.kamehouse.groot.integration.AbstractGrootIntegrationTest;
import java.io.IOException;
import java.util.List;
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

    var result = verifySuccessfulResponse(response, KameHouseCommandResult.class);
    var standardOutputHtml = result.getStandardOutputHtml();
    var standardOutput = result.getStandardOutput();
    assertNotNull(standardOutputHtml, "standardOutputHtml is null");
    assertNotNull(standardOutput, "standardOutput is null");
    assertStringInOutput(standardOutputHtml, "Started executing script");
    assertStringInOutput(standardOutputHtml, "Finished executing script");
    assertStringInOutput(standardOutput, "Started executing script");
    assertStringInOutput(standardOutput, "Finished executing script");
    assertStringInOutput(standardOutput, "status: ");
    assertStringInOutput(standardOutput, "display help");
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

    var result = verifySuccessfulResponse(response, KameHouseCommandResult.class);
    var standardOutputHtml = result.getStandardOutputHtml();
    var standardOutput = result.getStandardOutput();
    assertNotNull(standardOutputHtml, "standardOutputHtml is null");
    assertNotNull(standardOutput, "standardOutput is null");
    assertStringInOutput(standardOutputHtml, "Started executing script");
    assertStringInOutput(standardOutputHtml, "Finished executing script");
    assertStringInOutput(standardOutput, "Started executing script");
    assertStringInOutput(standardOutput, "Finished executing script");
    assertStringInOutput(standardOutput, "status: ");
  }

  /**
   * Validate that the expected string is contained in the specified output.
   */
  private void assertStringInOutput(List<String> output, String expected) {
    boolean isStringInArray = false;
    for (String line : output) {
      if (line.contains(expected)) {
        isStringInArray = true;
      }
    }
    assertTrue(isStringInArray, "'" + expected + "' is not in output");
  }
}
