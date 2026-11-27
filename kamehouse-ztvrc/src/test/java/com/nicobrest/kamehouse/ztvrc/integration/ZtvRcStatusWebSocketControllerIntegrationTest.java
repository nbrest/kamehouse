package com.nicobrest.kamehouse.ztvrc.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractWebSocketIntegrationTest;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcStatus;

/**
 * Integration tests for the ZtvRcStatusWebSocketController class.
 *
 * @author nbrest
 */
class ZtvRcStatusWebSocketControllerIntegrationTest extends
    AbstractWebSocketIntegrationTest {

  @Override
  public Class<?> getResponseClass() {
    return ZtvRcStatus.class;
  }

  @Override
  public String getWebSocketUrl() {
    return "/api/ws/ztvrc/sockjs/";
  }

  @Override
  public String getTopicUrl() {
    return "/topic/ztv-player/status-out";
  }

  @Override
  public String getPollUrl() {
    return "/app/ztv-player/status-in";
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
