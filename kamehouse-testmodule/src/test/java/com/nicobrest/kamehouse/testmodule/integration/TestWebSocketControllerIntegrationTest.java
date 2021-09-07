package com.nicobrest.kamehouse.testmodule.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.integration.AbstractWebSocketIntegrationTest;
import com.nicobrest.kamehouse.testmodule.model.TestWebSocketRequestMessage;
import com.nicobrest.kamehouse.testmodule.model.TestWebSocketResponseMessage;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;

/**
 * Integration tests for the TestWebSocketController class.
 *
 * @author nbrest
 */
public class TestWebSocketControllerIntegrationTest extends AbstractWebSocketIntegrationTest {

  private static final String WS_URL = "ws://localhost:9980/kame-house-testmodule/api/ws/test-module/websocket/";
  private int messageCount = 0;

  @Test
  public void testWebSocketTest() throws Exception {
    logger.info("Running testWebSocketTest");

    StompSession stompSession = getStompSession();

    stompSession.send("/app/test-module/websocket-in", getSampleRequestMessage());
    stompSession.send("/app/test-module/websocket-in", getSampleRequestMessage());
    stompSession.send("/app/test-module/websocket-in", getSampleRequestMessage());
    stompSession.send("/app/test-module/websocket-in", getSampleRequestMessage());

    try {
      int sleepMs = 3000;
      logger.info("Sleeping for {} ms", sleepMs);
      Thread.sleep(sleepMs);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    assertEquals(4, messageCount);
    logger.info("Finished testWebSocketTest successfully");
  }

  private WebSocketStompClient getStompClient() {
    List<Transport> transports = new ArrayList<>();
    transports.add(new RestTemplateXhrTransport());
    SockJsClient client = new SockJsClient(transports);
    WebSocketStompClient stompClient = new WebSocketStompClient(client);
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    return stompClient;
  }

  private StompSession getStompSession() throws ExecutionException, InterruptedException {
    WebSocketStompClient stompClient = getStompClient();
    StompSessionHandler sessionHandler = getSessionHandler();
    Future<StompSession> stompSessionFuture = stompClient.connect(WS_URL, sessionHandler);
    StompSession stompSession = stompSessionFuture.get();
    stompSession.subscribe("/topic/test-module/websocket-out", sessionHandler);
    return stompSession;
  }

  private StompSessionHandler getSessionHandler() {

    StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {

      @Override
      public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        logger.info("New session established : " + session.getSessionId());
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
        TestWebSocketResponseMessage response = (TestWebSocketResponseMessage) payload;
        messageCount++;
        logger.info("MessageCount: {} Received : date: {} message: {}", messageCount,
            response.getDate(), response.getMessage());
      }

      @Override
      public Type getPayloadType(StompHeaders headers) {
        return TestWebSocketResponseMessage.class;
      }

      @Override
      public void handleException(StompSession session, StompCommand command, StompHeaders headers,
          byte[] payload, Throwable exception) {
        logger.error("handleException: Got an exception", exception);
      }

      @Override
      public void handleTransportError(StompSession session, Throwable exception) {
        logger.error("handleTransportError: Got an exception", exception);
      }
    };
    return sessionHandler;
  }

  private TestWebSocketRequestMessage getSampleRequestMessage() {
    TestWebSocketRequestMessage message = new TestWebSocketRequestMessage();
    message.setFirstName("Goku");
    message.setLastName("Son");
    return message;
  }
}

