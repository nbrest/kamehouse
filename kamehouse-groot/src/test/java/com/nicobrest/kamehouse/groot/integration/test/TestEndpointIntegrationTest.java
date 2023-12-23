package com.nicobrest.kamehouse.groot.integration.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nicobrest.kamehouse.groot.integration.AbstractGrootIntegrationTest;
import java.io.IOException;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for GRoot Test endpoint.
 *
 * @author nbrest
 */
class TestEndpointIntegrationTest extends AbstractGrootIntegrationTest {

  private static final String API_URL = "/api/v1/test/test.php";
  private static final String RESPONSE_DOESNT_CONTAIN = "Response doesn't contain: ";

  /**
   * Test for groot test endpoint.
   */
  @Test
  void testEndpointTest() throws IOException {
    logger.info("Running test for {}", getWebappUrl() + API_URL);

    HttpResponse response = get(getWebappUrl() + API_URL);

    assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    String responseBody = new String(response.getEntity().getContent().readAllBytes(),
        Charsets.UTF_8);
    assertNotNull(responseBody);
    logger.info("responseBody: {}", responseBody);
    String expected = "<h1>print server info</h1><h1>print all headers</h1>";
    assertTrue(responseBody.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
    expected = "<br>testFunction: mada mada dane:<br>";
    assertTrue(responseBody.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
    expected = "<br>isLinuxHostExample():<br>";
    assertTrue(responseBody.contains(expected), RESPONSE_DOESNT_CONTAIN + expected);
  }
}

