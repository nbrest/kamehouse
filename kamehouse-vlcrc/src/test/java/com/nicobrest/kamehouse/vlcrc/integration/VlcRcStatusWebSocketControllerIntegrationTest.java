package com.nicobrest.kamehouse.vlcrc.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractWebSocketIntegrationTest;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;

/**
 * Integration tests for the VlcRcStatusWebSocketController class.
 *
 * @author nbrest
 */
class VlcRcStatusWebSocketControllerIntegrationTest extends
    AbstractWebSocketIntegrationTest {

  @Override
  public Class<?> getResponseClass() {
    return VlcRcStatus.class;
  }

  @Override
  public String getWebSocketUrl() {
    return "/api/ws/vlcrc/sockjs/";
  }

  @Override
  public String getTopicUrl() {
    return "/topic/vlc-player/status-out";
  }

  @Override
  public String getPollUrl() {
    return "/app/vlc-player/status-in";
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
