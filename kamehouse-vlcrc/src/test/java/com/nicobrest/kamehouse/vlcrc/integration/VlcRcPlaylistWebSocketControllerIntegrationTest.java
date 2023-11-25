package com.nicobrest.kamehouse.vlcrc.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractWebSocketIntegrationTest;
import java.util.List;

/**
 * Integration tests for the VlcRcPlaylistWebSocketController class.
 *
 * @author nbrest
 */
class VlcRcPlaylistWebSocketControllerIntegrationTest extends
    AbstractWebSocketIntegrationTest {

  @Override
  public Class<?> getResponseClass() {
    return List.class;
  }

  @Override
  public String getWebSocketUrl() {
    return "/api/ws/vlc-player/playlist/";
  }

  @Override
  public String getTopicUrl() {
    return "/topic/vlc-player/playlist-out";
  }

  @Override
  public String getPollUrl() {
    return "/app/vlc-player/playlist-in";
  }

  @Override
  public Object getRequestPayload() {
    return null;
  }

  @Override
  public String getWebapp() {
    return "kame-house-vlcrc";
  }
}

