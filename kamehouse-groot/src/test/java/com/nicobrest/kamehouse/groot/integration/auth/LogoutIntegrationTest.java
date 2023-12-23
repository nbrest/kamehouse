package com.nicobrest.kamehouse.groot.integration.auth;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nicobrest.kamehouse.groot.integration.AbstractGrootIntegrationTest;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for GRoot logout endpoint.
 *
 * @author nbrest
 */
class LogoutIntegrationTest extends AbstractGrootIntegrationTest {

  private static final String API_URL = "/api/v1/auth/logout.php";

  /**
   * Test for groot logout success response.
   */
  @Test
  void logoutSuccessTest() throws IOException {
    logger.info("Running test for {}", getWebappUrl() + API_URL);

    HttpResponse response = get(getWebappUrl() + API_URL);

    String responseBody = verifySuccessfulResponse(response, String.class);
    String expected = "<title>GRoot - Login</title>";
    assertTrue(responseBody.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
  }
}
