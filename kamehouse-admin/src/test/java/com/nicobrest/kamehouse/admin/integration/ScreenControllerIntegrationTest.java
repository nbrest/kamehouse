package com.nicobrest.kamehouse.admin.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.model.KameHouseGenericResponse;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Integration tests for the ScreenController class.
 *
 * @author nbrest
 */
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class ScreenControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/admin/screen";

  @Override
  public String getWebapp() {
    return "kame-house-admin";
  }

  @ParameterizedTest
  @CsvSource({
      "lockTest, /lock",
      "wakeUpTest, /wake-up",
      "unlockTest, /unlock",
      "keyPressTest, /key-press?key=ESC&keyPresses=1",
      "mouseClickTest, /mouse-click?mouseButton=LEFT&positionX=100&positionY=1000&clickCount=1",
      "mouseClickTest, /mouse-click?mouseButton=RIGHT&positionX=100&positionY=1000&clickCount=1"
  })
  void screenControllerTest(String testName, String apiEndpoint) throws Exception {
    logger.info("Running {} : {}", testName, apiEndpoint);

    HttpResponse response = post(getWebappUrl() + API_URL + apiEndpoint);

    verifyResponseList(response, List.of(HttpStatus.SC_OK, HttpStatus.SC_INTERNAL_SERVER_ERROR),
        List.of(KameHouseCommandResult.class, KameHouseGenericResponse.class));
  }
}
