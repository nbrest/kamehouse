package com.nicobrest.kamehouse.testmodule.service;

import com.nicobrest.kamehouse.testmodule.model.TestWebSocketRequestMessage;
import com.nicobrest.kamehouse.testmodule.model.TestWebSocketResponseMessage;

import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Service layer for the test WebSocket module.
 * 
 * @author nbrest
 *
 */
@Service
public class TestWebSocketService {

  /**
   * Generates a websocket response message for the specified request input.
   */
  public TestWebSocketResponseMessage generateTestWebSocketResponseMessage(
      TestWebSocketRequestMessage inputMessage) {
    TestWebSocketResponseMessage testWebSocketResponseMessage = new TestWebSocketResponseMessage();
    testWebSocketResponseMessage.setDate(new Date());
    testWebSocketResponseMessage.setMessage("まだまだだね, " + inputMessage.getLastName() + " "
        + inputMessage.getFirstName());
    return testWebSocketResponseMessage;
  }
}
