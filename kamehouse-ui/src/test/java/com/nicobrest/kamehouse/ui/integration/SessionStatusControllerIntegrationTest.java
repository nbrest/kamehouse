package com.nicobrest.kamehouse.ui.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the TennisWorldUserController class.
 *
 * @author nbrest
 */
public class SessionStatusControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String WEBAPP = "/kame-house";
  private static final String STATUS_API = "/api/v1/ui/session/status";

  @Test
  public void sessionStatusTest() throws Exception {
    logger.info("Running sessionStatusTest");
    HttpGet get = HttpClientUtils.httpGet(getBaseUrl() + WEBAPP + STATUS_API);

    HttpResponse response = getHttpClient().execute(get);

    verifySuccessfulOkResponse(response);
  }
}

