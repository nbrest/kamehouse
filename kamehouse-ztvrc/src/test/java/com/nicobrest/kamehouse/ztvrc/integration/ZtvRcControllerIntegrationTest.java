package com.nicobrest.kamehouse.ztvrc.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcCommand;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcStatus;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Integration tests for the ZtvRcController class.
 *
 * @author nbrest
 */
class ZtvRcControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/ztv-rc/players/localhost";

  @Override
  public String getWebapp() {
    return "kame-house-ztvrc";
  }

  @Test
  void ztvRcStatusTest() throws Exception {
    logger.info("Running ztvRcStatusTest");

    HttpResponse response = get(getWebappUrl() + API_URL + "/status");

    verifySuccessfulResponse(response, ZtvRcStatus.class);
  }

  @Test
  void ztvRcCommandsTest() throws Exception {
    logger.info("Running ztvRcCommandsTest");
    ZtvRcCommand command = new ZtvRcCommand();
    command.setName("pl_next");

    HttpResponse response = post(getWebappUrl() + API_URL + "/commands", command);

    assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
  }

  @ParameterizedTest
  @CsvSource({
      "ztvRcStatsTest,/stats?fullReport=false&updateStats=false",
      "ztvRcPlaylistTest,/playlist",
      "ztvRcBrowseTest,/browse",
  })
  void ztvRcEndpointsTest(String testCase, String urlSuffix) throws Exception {
    logger.info("Running {}", testCase);

    HttpResponse response = get(getWebappUrl() + API_URL + urlSuffix);

    verifySuccessfulResponse(response, List.class);
  }
}

