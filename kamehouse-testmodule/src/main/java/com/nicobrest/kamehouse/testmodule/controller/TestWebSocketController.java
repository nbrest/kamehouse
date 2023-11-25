package com.nicobrest.kamehouse.testmodule.controller;

import com.nicobrest.kamehouse.testmodule.model.TestWebSocketRequestMessage;
import com.nicobrest.kamehouse.testmodule.model.TestWebSocketResponseMessage;
import com.nicobrest.kamehouse.testmodule.service.TestWebSocketService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * Controller for the test WebSocket in the test module.
 *
 * @author nbrest
 */
@Controller
public class TestWebSocketController {

  private TestWebSocketService testWebSocketService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public TestWebSocketController(TestWebSocketService testWebSocketService) {
    this.testWebSocketService = testWebSocketService;
  }

  /**
   * Processes the websocket input request.
   */
  @MessageMapping("/test-module/websocket-in")
  @SendTo("/topic/test-module/websocket-out")
  public TestWebSocketResponseMessage testWebSocketProcess(
      TestWebSocketRequestMessage inputMessage) {
    return testWebSocketService.generateTestWebSocketResponseMessage(inputMessage);
  }
}
