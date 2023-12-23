package com.nicobrest.kamehouse.groot.integration.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nicobrest.kamehouse.groot.integration.AbstractGrootIntegrationTest;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for GRoot Test endpoint.
 *
 * @author nbrest
 */
class TestEndpointIntegrationTest extends AbstractGrootIntegrationTest {

  private static final String API_URL = "/api/v1/test/test.php";

  /**
   * Test for groot test endpoint successful response.
   */
  @Test
  void testEndpointSuccessTest() throws IOException {
    logger.info("Running test for {}", getWebappUrl() + API_URL);

    HttpResponse response = get(getWebappUrl() + API_URL);

    String responseBody = verifySuccessfulResponse(response, String.class);
    String expected = "<h1>print server info</h1><h1>print all headers</h1>";
    assertTrue(responseBody.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
    expected = "<br>testFunction: mada mada dane:<br>";
    assertTrue(responseBody.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
    expected = "<br>isLinuxHostExample():<br>";
    assertTrue(responseBody.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
  }
}
