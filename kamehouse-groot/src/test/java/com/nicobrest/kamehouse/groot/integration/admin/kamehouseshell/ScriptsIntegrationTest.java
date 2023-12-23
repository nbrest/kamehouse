package com.nicobrest.kamehouse.groot.integration.admin.kamehouseshell;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.nicobrest.kamehouse.groot.integration.AbstractGrootIntegrationTest;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for GRoot kamehouse-shell scripts endpoint.
 *
 * @author nbrest
 */
class ScriptsIntegrationTest extends AbstractGrootIntegrationTest {

  private static final String API_URL = "/api/v1/admin/kamehouse-shell/scripts.php";

  /**
   * Test for groot kamehouse-shell scripts success response.
   */
  @Test
  void scriptsSuccessTest() throws IOException {
    logger.info("Running test for {}", getWebappUrl() + API_URL);

    HttpResponse response = get(getWebappUrl() + API_URL);

    JsonNode responseBody = verifySuccessfulResponse(response, JsonNode.class);
    assertTrue(responseBody.isArray(), "response body is not an array");
    assertTrue(!responseBody.isEmpty(), "response body array is empty");
    assertNotNull(responseBody.toString().contains("is-linux-host.sh"),
        "response body is missing the script is-linux-host.sh");
  }
}
