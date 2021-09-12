package com.nicobrest.kamehouse.commons.integration;

import com.nicobrest.kamehouse.commons.model.KameHouseGenericResponse;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Integration tests for the LogLevelManagerController class.
 *
 * @author nbrest
 */
public class LogLevelManagerControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/commons/log-level";

  @Override
  public String getWebapp() {
    return "kame-house-testmodule";
  }

  @Test
  public void logLevelStatusTest() throws Exception {
    logger.info("Running logLevelStatusTest");

    HttpResponse response = get(getWebappUrl() + API_URL);

    verifySuccessfulResponseList(response, String.class);
  }

  @Test
  public void logLevelDeleteTest() throws Exception {
    logger.info("Running logLevelDeleteTest");

    HttpResponse response = delete(getWebappUrl() + API_URL);

    verifySuccessfulResponseList(response, String.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"/debug", "?level=TRACE", "/trace"})
  public void logLevelTest(String suffix) throws Exception {
    logger.info("Running logLevelTest with parameter {}", suffix);

    HttpResponse response = put(getWebappUrl() + API_URL + suffix);

    verifySuccessfulResponseList(response, String.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "payload?logPayload=true",
      "headers?logHeaders=true",
      "client-info?logClientInfo=true",
      "query-string?logQueryString=true"
  })
  public void requestLoggerTest(String suffix) throws Exception {
    logger.info("Running requestLoggerTest with parameter {}", suffix);
    String url = getWebappUrl() + API_URL + "/request-logger/" + suffix;

    HttpResponse response = put(url);

    verifySuccessfulResponse(response, KameHouseGenericResponse.class);
  }
}

