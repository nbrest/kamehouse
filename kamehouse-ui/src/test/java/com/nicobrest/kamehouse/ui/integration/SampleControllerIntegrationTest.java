package com.nicobrest.kamehouse.ui.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.ui.model.SessionStatus;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the SampleController class.
 *
 * @author nbrest
 */
class SampleControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/ui/sample/dragonball/model-and-view";

  @Override
  public String getWebapp() {
    return "kame-house";
  }

  @Test
  void sampleTest() throws Exception {
    logger.info("Running sampleTest");

    HttpResponse response = get(getWebappUrl() + API_URL);

    verifySuccessfulResponse(response, String.class);
  }
}

