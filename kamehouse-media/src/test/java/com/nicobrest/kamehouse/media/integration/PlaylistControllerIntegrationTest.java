package com.nicobrest.kamehouse.media.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import com.nicobrest.kamehouse.media.model.Playlist;
import java.util.List;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Integration tests for the PlaylistController class.
 *
 * @author nbrest
 */
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class PlaylistControllerIntegrationTest extends AbstractControllerIntegrationTest {

  private static final String API_URL = "/api/v1/media";
  private Playlist playlist = null;

  @Override
  public String getWebapp() {
    return "kame-house-media";
  }

  @Test
  @Order(1)
  void playlistsTest() throws Exception {
    logger.info("Running playlistsTest");

    HttpResponse response = get(getWebappUrl() + API_URL + "/playlists");

    List<Playlist> responseBody = verifySuccessfulResponseList(response, Playlist.class);
    playlist = responseBody.get(0);
  }

  @Test
  @Order(2)
  void playlistTest() throws Exception {
    logger.info("Running playlistTest");
    String path = HttpClientUtils.urlEncode(playlist.getPath());

    HttpResponse response = get(getWebappUrl() + API_URL + "/playlist?path=" + path);

    verifySuccessfulResponse(response, Playlist.class);
  }
}

