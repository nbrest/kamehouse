package com.nicobrest.kamehouse.testmodule.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.testmodule.model.TestWebSocketRequestMessage;
import com.nicobrest.kamehouse.testmodule.model.TestWebSocketResponseMessage;

import org.junit.jupiter.api.Test;

/**
 * Test class for the test module websocket service.
 * 
 * @author nbrest
 *
 */
public class TestWebSocketServiceTest {

  @Test
  public void generateTestWebSocketResponseMessageTest() {
    String expectedMessage = "まだまだだね, Son Goku";
    TestWebSocketRequestMessage testWebSocketRequestMessage = new TestWebSocketRequestMessage();
    testWebSocketRequestMessage.setFirstName("Goku");
    testWebSocketRequestMessage.setLastName("Son");
    TestWebSocketService testWebSocketService = new TestWebSocketService();
    
    TestWebSocketResponseMessage testWebSocketResponseMessage = testWebSocketService
        .generateTestWebSocketResponseMessage(testWebSocketRequestMessage);

    assertEquals(expectedMessage, testWebSocketResponseMessage.getMessage());
  }
}
