package com.nicobrest.kamehouse.commons.integration;

import com.nicobrest.kamehouse.commons.model.KameHouseJob;
import java.util.List;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the SchedulerController class.
 *
 * @author nbrest
 */
class SchedulerControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/commons/scheduler/jobs";

  @Override
  public String getWebapp() {
    return "kame-house-testmodule";
  }

  @Test
  void getAllJobsTest() throws Exception {
    logger.info("Running getAllJobsTest");

    HttpResponse response = get(getWebappUrl() + API_URL);

    verifySuccessfulResponseList(response, KameHouseJob.class);
  }

  @Test
  void cancelJobTest() throws Exception {
    logger.info("Running cancelJobTest");

    HttpResponse response = get(getWebappUrl() + API_URL + "?name=sampleJobDetail&group=DEFAULT");

    verifySuccessfulResponseList(response, KameHouseJob.class);
  }

  @Test
  void scheduleJobTest() throws Exception {
    logger.info("Running scheduleJobTest");
    String url = getWebappUrl() + API_URL + "?name=sampleJobDetail&group=DEFAULT&delay=2";

    HttpResponse response = post(url);

    verifySuccessfulCreatedResponse(response, List.class);
  }
}

