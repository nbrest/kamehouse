package com.nicobrest.kamehouse.commons.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.model.ApplicationCache;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the EhCacheController class.
 *
 * @author nbrest
 */
public class EhCacheControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/commons/ehcache";

  @Override
  public String getWebapp() {
    return "kame-house-testmodule";
  }

  @Test
  public void ehcacheStatusTest() throws Exception {
    logger.info("Running ehcacheStatusTest");

    HttpResponse response = get(getWebappUrl() + API_URL);

    verifySuccessfulResponseList(response, ApplicationCache.class);
  }

  @Test
  public void ehcacheDeleteTest() throws Exception {
    logger.info("Running ehcacheDeleteTest");

    HttpResponse response = get(getWebappUrl() + API_URL);

    assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
  }
}

