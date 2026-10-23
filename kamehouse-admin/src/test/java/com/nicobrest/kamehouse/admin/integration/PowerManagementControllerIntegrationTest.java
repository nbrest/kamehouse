package com.nicobrest.kamehouse.admin.integration;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.model.KameHouseGenericResponse;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

  @ParameterizedTest
  @Order(1)
  @CsvSource({
      "/shutdown?delay=999999, true",
      "/suspend?delay=999999, true",
      "/wol?server=kamehouse-server, true",
      "/reboot, false"
  })
  void postTest(String endpoint, boolean enabled) throws Exception {
    assumeTrue(enabled, "Enabling this will reboot the system running the test!");
    logger.info("Running postTest for {}", endpoint);

    HttpResponse response = post(getWebappUrl() + API_URL + endpoint);

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

}

