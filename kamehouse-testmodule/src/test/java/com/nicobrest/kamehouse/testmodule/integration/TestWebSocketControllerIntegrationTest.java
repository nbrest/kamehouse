package com.nicobrest.kamehouse.testmodule.integration;

import com.nicobrest.kamehouse.commons.integration.AbstractWebSocketIntegrationTest;
import com.nicobrest.kamehouse.testmodule.model.TestWebSocketRequestMessage;
import com.nicobrest.kamehouse.testmodule.model.TestWebSocketResponseMessage;

/**
 * Integration tests for the TestWebSocketController class.
 *
 * @author nbrest
 */
public class TestWebSocketControllerIntegrationTest extends AbstractWebSocketIntegrationTest {

  @Override
  public Class<?> getResponseClass() {
    return TestWebSocketResponseMessage.class;
  }

  @Override
  public String getWebSocketUrl() {
    return getWebappUrl() + "/api/ws/test-module/websocket/";
  }

  @Override
  public String getTopicUrl() {
    return "/topic/test-module/websocket-out";
  }

  @Override
  public String getPollUrl() {
    return "/app/test-module/websocket-in";
  }

  @Override
  public Object getRequestPayload() {
    TestWebSocketRequestMessage message = new TestWebSocketRequestMessage();
    message.setFirstName("Goku");
    message.setLastName("Son");
    return message;
  }

  @Override
  public String getWebapp() {
    return "kame-house-testmodule";
  }
}

