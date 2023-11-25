package com.nicobrest.kamehouse.admin.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.model.KameHouseGenericResponse;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Integration tests for the PowerManagementController class.
 *
 * @author nbrest
 */
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class PowerManagementControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/admin/power-management";

  @Override
  public String getWebapp() {
    return "kame-house-admin";
  }

  @Test
  @Order(1)
  void shutdownTest() throws Exception {
    logger.info("Running shutdownTest");

    HttpResponse response = post(getWebappUrl() + API_URL + "/shutdown?delay=999999");

    verifySuccessfulCreatedResponse(response, KameHouseGenericResponse.class);
  }

  @Test
  @Order(2)
  void shutdownStatusTest() throws Exception {
    logger.info("Running shutdownStatusTest");

    HttpResponse response = get(getWebappUrl() + API_URL + "/shutdown");

    verifySuccessfulResponse(response, KameHouseGenericResponse.class);
  }

  @Test
  @Order(3)
  void shutdownCancelTest() throws Exception {
    logger.info("Running shutdownCancelTest");

    HttpResponse response = delete(getWebappUrl() + API_URL + "/shutdown");

    verifySuccessfulResponse(response, KameHouseGenericResponse.class);
  }

  @Test
  @Order(4)
  void suspendTest() throws Exception {
    logger.info("Running suspendTest");

    HttpResponse response = post(getWebappUrl() + API_URL + "/suspend?delay=999999");

    verifySuccessfulCreatedResponse(response, KameHouseGenericResponse.class);
  }

  @Test
  @Order(5)
  void suspendStatusTest() throws Exception {
    logger.info("Running suspendStatusTest");

    HttpResponse response = get(getWebappUrl() + API_URL + "/suspend");

    verifySuccessfulResponse(response, KameHouseGenericResponse.class);
  }

  @Test
  @Order(6)
  void suspendCancelTest() throws Exception {
    logger.info("Running suspendCancelTest");

    HttpResponse response = delete(getWebappUrl() + API_URL + "/suspend");

    verifySuccessfulResponse(response, KameHouseGenericResponse.class);
  }

  @Test
  @Order(7)
  @Disabled("Enabling this will reboot the system running the test!")
  void rebootTest() throws Exception {
    logger.info("Running rebootTest");

    HttpResponse response = post(getWebappUrl() + API_URL + "/reboot");

    verifySuccessfulCreatedResponse(response, KameHouseGenericResponse.class);
  }

  @Test
  @Order(8)
  void wolTest() throws Exception {
    logger.info("Running suspendTest");

    HttpResponse response = post(getWebappUrl() + API_URL + "/wol?server=media.server");

    verifySuccessfulCreatedResponse(response, KameHouseGenericResponse.class);
  }
}

