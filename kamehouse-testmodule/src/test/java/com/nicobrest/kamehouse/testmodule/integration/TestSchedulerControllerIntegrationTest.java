package com.nicobrest.kamehouse.testmodule.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.model.KameHouseGenericResponse;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the TestSchedulerController class.
 *
 * @author nbrest
 */
class TestSchedulerControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/test-module/test-scheduler/sample-job";

  @Override
  public String getWebapp() {
    return "kame-house-testmodule";
  }

  @Test
  void sampleJobScheduleTest() throws Exception {
    logger.info("Running sampleJobScheduleTest");

    HttpResponse response = post(getWebappUrl() + API_URL + "?delay=2");

    verifySuccessfulCreatedResponse(response, KameHouseGenericResponse.class);
  }

  @Test
  void sampleJobStatusTest() throws Exception {
    logger.info("Running sampleJobStatusTest");

    HttpResponse response = get(getWebappUrl() + API_URL);

    verifySuccessfulResponse(response, KameHouseGenericResponse.class);
  }

  @Test
  void sampleJobCancelTest() throws Exception {
    logger.info("Running sampleJobCancelTest");

    HttpResponse response = delete(getWebappUrl() + API_URL);

    verifySuccessfulResponse(response, KameHouseGenericResponse.class);
  }
}

