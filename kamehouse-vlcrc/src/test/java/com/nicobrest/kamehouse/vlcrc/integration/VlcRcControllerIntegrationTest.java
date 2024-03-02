package com.nicobrest.kamehouse.vlcrc.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcCommand;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the VlcRcController class.
 *
 * @author nbrest
 */
class VlcRcControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/vlc-rc/players/localhost";

  @Override
  public String getWebapp() {
    return "kame-house-vlcrc";
  }

  @Test
  void vlcRcStatusTest() throws Exception {
    logger.info("Running vlcRcStatusTest");

    HttpResponse response = get(getWebappUrl() + API_URL + "/status");

    verifySuccessfulResponse(response, VlcRcStatus.class);
  }

  @Test
  void vlcRcPlaylistTest() throws Exception {
    logger.info("Running vlcRcPlaylistTest");

    HttpResponse response = get(getWebappUrl() + API_URL + "/playlist");

    verifySuccessfulResponse(response, List.class);
  }

  @Test
  void vlcRcBrowseTest() throws Exception {
    logger.info("Running vlcRcBrowseTest");

    HttpResponse response = get(getWebappUrl() + API_URL + "/browse");

    verifySuccessfulResponse(response, List.class);
  }

  @Test
  void vlcRcCommandsTest() throws Exception {
    logger.info("Running vlcRcCommandsTest");
    VlcRcCommand command = new VlcRcCommand();
    command.setName("pl_next");

    HttpResponse response = post(getWebappUrl() + API_URL + "/commands", command);

    assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
  }
}

