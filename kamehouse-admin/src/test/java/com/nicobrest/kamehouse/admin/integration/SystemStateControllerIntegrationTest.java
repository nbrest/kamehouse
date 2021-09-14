package com.nicobrest.kamehouse.admin.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Integration tests for the SystemStateController class.
 *
 * @author nbrest
 */
public class SystemStateControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/admin/system-state";

  @Override
  public String getWebapp() {
    return "kame-house-admin";
  }

  @ParameterizedTest
  @ValueSource(strings = {"uptime", "free", "df", "httpd"})
  public void systemStateTest(String suffix) throws Exception {
    logger.info("Running systemStateTest with parameter {}", suffix);

    HttpResponse response = get(getWebappUrl() + API_URL + "/" + suffix);

    verifySuccessfulResponseList(response, SystemCommand.Output.class);
  }

  @Test
  public void httpdRestartTest() throws Exception {
    logger.info("Running httpdRestartTest");

    HttpResponse response = post(getWebappUrl() + API_URL + "/httpd");

    verifySuccessfulResponseList(response, SystemCommand.Output.class);
  }
}

