package com.nicobrest.kamehouse.admin.integration;

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
 * Integration tests for the ScreenController class.
 *
 * @author nbrest
 */
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ScreenControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/admin/screen";

  @Override
  public String getWebapp() {
    return "kame-house-admin";
  }

  @Test
  @Order(1)
  public void lockTest() throws Exception {
    logger.info("Running lockTest");

    HttpResponse response = post(getWebappUrl() + API_URL + "/lock");

    verifySuccessfulResponseList(response, SystemCommand.Output.class);
  }

  @Test
  @Order(2)
  public void wakeUpTest() throws Exception {
    logger.info("Running wakeUpTest");

    HttpResponse response = post(getWebappUrl() + API_URL + "/wake-up");

    verifySuccessfulResponseList(response, SystemCommand.Output.class);
  }

  @Test
  @Order(3)
  public void unlockTest() throws Exception {
    logger.info("Running unlockTest");

    HttpResponse response = post(getWebappUrl() + API_URL + "/unlock");

    verifySuccessfulResponseList(response, SystemCommand.Output.class);
  }
}

