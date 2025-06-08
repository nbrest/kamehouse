package com.nicobrest.kamehouse.auth.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.auth.model.SessionStatus;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the SessionStatusController class.
 *
 * @author nbrest
 */
class SessionStatusControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/auth/session/status";

  @Override
  public String getWebapp() {
    return "kame-house-auth";
  }

  @Test
  void sessionStatusTest() throws Exception {
    logger.info("Running sessionStatusTest");

    HttpResponse response = get(getWebappUrl() + API_URL);

    verifySuccessfulResponse(response, SessionStatus.class);
  }
}

