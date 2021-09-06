package com.nicobrest.kamehouse.vlcrc.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Integration tests for the VlcProcessController class.
 *
 * @author nbrest
 */
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class VlcProcessControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/vlc-rc/vlc-process";

  @Override
  public String getWebapp() {
    return "kame-house-vlcrc";
  }

  @Test
  @Order(1)
  public void vlcProcessStartTest() throws Exception {
    logger.info("Running vlcProcessStartTest");

    HttpResponse response = post(getWebappUrl() + API_URL);

    verifySuccessfulResponseList(response, SystemCommand.Output.class);
  }

  @Test
  @Order(2)
  public void vlcProcessStatusTest() throws Exception {
    logger.info("Running vlcProcessStatusTest");

    HttpResponse response = get(getWebappUrl() + API_URL);

    verifySuccessfulResponseList(response, SystemCommand.Output.class);
  }

  @Test
  @Order(3)
  public void vlcProcessStopTest() throws Exception {
    logger.info("Running vlcProcessStopTest");

    HttpResponse response = delete(getWebappUrl() + API_URL);

    verifySuccessfulResponseList(response, SystemCommand.Output.class);
  }
}

