package com.nicobrest.kamehouse.admin.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the SystemStateController class.
 *
 * @author nbrest
 */
public class SystemStateControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String SYSTEM_STATE_API = "/api/v1/admin/system-state";

  @Override
  public String getWebapp() {
    return "kame-house-admin";
  }

  @Test
  public void uptimeTest() throws Exception {
    logger.info("Running uptimeTest");

    HttpResponse response = get(getWebappUrl() + SYSTEM_STATE_API + "/uptime");

    verifySuccessfulResponseList(response, SystemCommand.Output.class);
  }

  @Test
  public void freeTest() throws Exception {
    logger.info("Running freeTest");

    HttpResponse response = get(getWebappUrl() + SYSTEM_STATE_API + "/free");

    verifySuccessfulResponseList(response, SystemCommand.Output.class);
  }

  @Test
  public void dfTest() throws Exception {
    logger.info("Running dfTest");

    HttpResponse response = get(getWebappUrl() + SYSTEM_STATE_API + "/df");

    verifySuccessfulResponseList(response, SystemCommand.Output.class);
  }

  @Test
  public void httpdStatusTest() throws Exception {
    logger.info("Running httpdStatusTest");

    HttpResponse response = get(getWebappUrl() + SYSTEM_STATE_API + "/httpd");

    verifySuccessfulResponseList(response, SystemCommand.Output.class);
  }

  @Test
  public void httpdRestartTest() throws Exception {
    logger.info("Running httpdRestartTest");

    HttpResponse response = post(getWebappUrl() + SYSTEM_STATE_API + "/httpd");

    verifySuccessfulResponseList(response, SystemCommand.Output.class);
  }
}

