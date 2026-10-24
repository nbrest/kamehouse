package com.nicobrest.kamehouse.admin.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the KameHouseDesktopController class.
 *
 * @author nbrest
 */
class KameHouseDesktopControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/admin/kamehouse-desktop";

  @Override
  public String getWebapp() {
    return "kame-house-admin";
  }

  @Test
  void kameHouseDesktopStartupTest() throws Exception {
    HttpResponse response = post(getWebappUrl() + API_URL);
    verifySuccessfulResponseList(response, KameHouseCommandResult.class);
  }

  @Test
  void kameHouseDesktopStopTest() throws IOException {
    HttpResponse response = delete(getWebappUrl() + API_URL);
    verifySuccessfulResponseList(response, KameHouseCommandResult.class);
  }
}

