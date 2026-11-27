package com.nicobrest.kamehouse.ztvrc.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractWebSocketIntegrationTest;
import java.util.List;

/**
 * Integration tests for the ZtvRcPlaylistWebSocketController class.
 *
 * @author nbrest
 */
class ZtvRcPlaylistWebSocketControllerIntegrationTest extends
    AbstractWebSocketIntegrationTest {

  @Override
  public Class<?> getResponseClass() {
    return List.class;
  }

  @Override
  public String getWebSocketUrl() {
    return "/api/ws/ztvrc/sockjs/";
  }

  @Override
  public String getTopicUrl() {
    return "/topic/ztv-player/playlist-out";
  }

  @Override
  public String getPollUrl() {
    return "/app/ztv-player/playlist-in";
  }

  @Override
  public Object getRequestPayload() {
    return null;
  }

  @Override
  public String getWebapp() {
    return "kame-house-ztvrc";
  }
}

