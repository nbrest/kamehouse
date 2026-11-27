package com.nicobrest.kamehouse.ztvrc.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Integration tests for the ZtvProcessController class.
 *
 * @author nbrest
 */
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class ZtvProcessControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/ztv-rc/ztv-process";

  @Override
  public String getWebapp() {
    return "kame-house-ztvrc";
  }

  @Test
  @Order(1)
  void ztvProcessStartTest() throws Exception {
    logger.info("Running ztvProcessStartTest");

    HttpResponse response = post(getWebappUrl() + API_URL + "?file=sftp://localhost/path/file.mkv");

    verifySuccessfulResponseList(response, KameHouseCommandResult.class);
  }

  @Test
  @Order(2)
  void ztvProcessStatusTest() throws Exception {
    logger.info("Running ztvProcessStatusTest");

    HttpResponse response = get(getWebappUrl() + API_URL);

    verifySuccessfulResponseList(response, KameHouseCommandResult.class);
  }

  @Test
  @Order(3)
  void ztvProcessStopTest() throws Exception {
    logger.info("Running ztvProcessStopTest");

    HttpResponse response = delete(getWebappUrl() + API_URL);

    verifySuccessfulResponseList(response, KameHouseCommandResult.class);
  }
}

