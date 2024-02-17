package com.nicobrest.kamehouse.admin.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.model.KameHouseGenericResponse;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
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
      "keyPress, /key-press?key=ESC&keyPresses=1",
      "mouseClick, /mouse-click?xPosition=100&yPosition=1000&clickCount=1"
  })
  void screenControllerTest(String testName, String apiEndpoint) throws Exception {
    logger.info("Running {}", testName);

    HttpResponse response = post(getWebappUrl() + API_URL + apiEndpoint);

    verifyResponseList(response, List.of(HttpStatus.SC_OK, HttpStatus.SC_INTERNAL_SERVER_ERROR),
        List.of(SystemCommand.Output.class, KameHouseGenericResponse.class));
  }
}
