package com.nicobrest.kamehouse.admin.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Integration tests for the SystemStateController class.
 *
 * @author nbrest
 */
class SystemStateControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/admin/system-state";

  @Override
  public String getWebapp() {
    return "kame-house-admin";
  }

  @ParameterizedTest
  @ValueSource(strings = {"uptime", "free", "df", "top", "httpd"})
  void systemStateTest(String suffix) throws Exception {
    logger.info("Running systemStateTest with parameter {}", suffix);

    HttpResponse response = get(getWebappUrl() + API_URL + "/" + suffix);

    verifySuccessfulResponseList(response, KameHouseCommandResult.class);
  }

  @Test
  @Disabled("It breaks the connection when triggering the integration tests on docker through http")
  void httpdRestartTest() {
    Assertions.assertDoesNotThrow(() -> {
      logger.info("Disabled:httpdRestartTest");
    });
  }
}

