package com.nicobrest.kamehouse.ui.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.ui.model.SessionStatus;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the SessionStatusController class.
 *
 * @author nbrest
 */
public class SessionStatusControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String STATUS_API = "/api/v1/ui/session/status";

  @Override
  public String getWebapp() {
    return "kame-house";
  }

  @Test
  public void sessionStatusTest() throws Exception {
    logger.info("Running sessionStatusTest");

    HttpResponse response = get(getWebappUrl() + STATUS_API);

    verifySuccessfulResponse(response, SessionStatus.class);
  }
}

