package com.nicobrest.kamehouse.commons.integration;

import java.util.Map;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the ModuleStatusController class.
 *
 * @author nbrest
 */
public class ModuleStatusControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/commons/module/status";

  @Override
  public String getWebapp() {
    return "kame-house-testmodule";
  }

  @Test
  public void moduleStatusTest() throws Exception {
    logger.info("Running moduleStatusTest");

    HttpResponse response = get(getWebappUrl() + API_URL);

    verifySuccessfulResponse(response, Map.class);
  }
}

