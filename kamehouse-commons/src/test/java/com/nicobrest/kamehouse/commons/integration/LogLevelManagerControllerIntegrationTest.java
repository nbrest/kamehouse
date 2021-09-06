package com.nicobrest.kamehouse.commons.integration;

import com.nicobrest.kamehouse.commons.model.KameHouseGenericResponse;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;

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

  @Test
  public void logLevelDebugTest() throws Exception {
    logger.info("Running logLevelDebugTest");

    HttpResponse response = put(getWebappUrl() + API_URL + "/debug");

    verifySuccessfulResponseList(response, String.class);
  }

  @Test
  public void logLevelSetTest() throws Exception {
    logger.info("Running logLevelSetTest");

    HttpResponse response = put(getWebappUrl() + API_URL + "?level=TRACE");

    verifySuccessfulResponseList(response, String.class);
  }

  @Test
  public void logLevelTraceTest() throws Exception {
    logger.info("Running logLevelTraceTest");

    HttpResponse response = put(getWebappUrl() + API_URL + "/trace");

    verifySuccessfulResponseList(response, String.class);
  }

  @Test
  public void requestLoggerPayloadTest() throws Exception {
    logger.info("Running requestLoggerPayloadTest");
    String url = getWebappUrl() + API_URL + "/request-logger/payload?logPayload=true";

    HttpResponse response = put(url);

    verifySuccessfulResponse(response, KameHouseGenericResponse.class);
  }

  @Test
  public void requestLoggerHeadersTest() throws Exception {
    logger.info("Running requestLoggerHeadersTest");
    String url = getWebappUrl() + API_URL + "/request-logger/headers?logHeaders=true";

    HttpResponse response = put(url);

    verifySuccessfulResponse(response, KameHouseGenericResponse.class);
  }

  @Test
  public void requestLoggerClientInfoTest() throws Exception {
    logger.info("Running requestLoggerClientInfoTest");
    String url = getWebappUrl() + API_URL + "/request-logger/client-info?logClientInfo=true";

    HttpResponse response = put(url);

    verifySuccessfulResponse(response, KameHouseGenericResponse.class);
  }

  @Test
  public void requestLoggerQueryStringTest() throws Exception {
    logger.info("Running requestLoggerQueryStringTest");
    String url = getWebappUrl() + API_URL + "/request-logger/query-string?logQueryString=true";

    HttpResponse response = put(url);

    verifySuccessfulResponse(response, KameHouseGenericResponse.class);
  }
}

