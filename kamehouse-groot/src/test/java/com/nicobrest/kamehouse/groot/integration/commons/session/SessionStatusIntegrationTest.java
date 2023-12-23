package com.nicobrest.kamehouse.groot.integration.commons.session;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.nicobrest.kamehouse.groot.integration.AbstractGrootIntegrationTest;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for GRoot session status endpoint.
 *
 * @author nbrest
 */
class SessionStatusIntegrationTest extends AbstractGrootIntegrationTest {

  private static final String API_URL = "/api/v1/commons/session/status.php";

  /**
   * Test for groot session status success response.
   */
  @Test
  void testEndpointSuccessTest() throws IOException {
    logger.info("Running test for {}", getWebappUrl() + API_URL);

    HttpResponse response = get(getWebappUrl() + API_URL);

    JsonNode responseBody = verifySuccessfulResponse(response, JsonNode.class);
    assertNotNull(responseBody, "response body is null");
    assertNotNull(responseBody.get("server"), "server is null");
    assertNotNull(responseBody.get("username"), "username is null");
    assertNotNull(responseBody.get("isLinuxHost"), "isLinuxHost is null");
    assertNotNull(responseBody.get("isLinuxDockerHost"), "isLinuxDockerHost is null");
    assertNotNull(responseBody.get("isDockerContainer"), "isDockerContainer is null");
    assertNotNull(responseBody.get("dockerControlHost"), "dockerControlHost is null");
    assertNotNull(responseBody.get("roles"), "roles is null");
  }
}
