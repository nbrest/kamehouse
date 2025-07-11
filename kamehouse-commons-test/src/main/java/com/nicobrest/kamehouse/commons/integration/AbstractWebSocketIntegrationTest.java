package com.nicobrest.kamehouse.commons.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
 * Abstract class to group common integration tests functionality for websockets.
 *
 * @author nbrest
 */
public abstract class AbstractWebSocketIntegrationTest extends AbstractIntegrationTest {

  private int timeoutMs = 10000;
  private int expectedMessageCount = 5;
  private int messageCount = 0;
  private StompSession stompSession;

  /**
   * Get the class of the response of the websocket.
   */
  public abstract Class<?> getResponseClass();

  /**
   * Get the url to connect the websocket to.
   */
  public abstract String getWebSocketUrl();

  /**
   * Get the topic url.
   */
  public abstract String getTopicUrl();

  /**
   * Get the poll url to send messages to.
   */
  public abstract String getPollUrl();

  /**
   * Get the request payload to send a message through the websocket.
   */
  public abstract Object getRequestPayload();

  /**
   * Init integration tests.
   */
  protected AbstractWebSocketIntegrationTest() {
    setProtocol("ws://");
    try {
      stompSession = getStompSession();
    } catch (ExecutionException e) {
      logger.error("Error getting the stomp session");
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public void setExpectedMessageCount(int expectedMessageCount) {
    this.expectedMessageCount = expectedMessageCount;
  }

  public void setTimeoutMs(int timeoutMs) {
    this.timeoutMs = timeoutMs;
  }

  /**
   * Send a message to the websocket.
   */
  public void send() {
    logger.info("Sending message to websocket");
    stompSession.send(getPollUrl(), getRequestPayload());
  }

  /**
   * Sleep the test to give time for the websockets to respond.
   */
  public void waitForMessages() {
    try {
      logger.info("Waiting for messages to be received from the websocket");
      int sleepMs = timeoutMs;
      while (sleepMs > 0) {
        if (messageCount >= expectedMessageCount) {
          break;
        }
        sleepMs = sleepMs - 1000;
        Thread.sleep(1000);
      }
      if (sleepMs <= 0) {
        logger.warn("Timed out before receiving all messages");
      } else {
        logger.info("Received all expected messages from the websocket");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Default test that sends 2 messages to the websocket and counts the responses.
   */
  @Test
  void defaultWebSocketTest() {
    logger.info("Running defaultWebSocketTest");
    for (int i = 0; i < expectedMessageCount; i++) {
      send();
    }
    waitForMessages();
    assertTrue(messageCount >= expectedMessageCount);
    logger.info("Finished defaultWebSocketTest successfully");
  }

  /**
   * Get the stomp session and subscribe to a topic.
   */
  private StompSession getStompSession() throws ExecutionException, InterruptedException {
    WebSocketStompClient stompClient = getStompClient();
    StompSessionHandler sessionHandler = getStompSessionHandler();
    Future<StompSession> stompSessionFuture = stompClient.connectAsync(
        getWebappUrl() + getWebSocketUrl(), sessionHandler);
    StompSession stompSessionInstance = stompSessionFuture.get();
    if (getTopicUrl() != null) {
      stompSessionInstance.subscribe(getTopicUrl(), sessionHandler);
    }
    return stompSessionInstance;
  }

  /**
   * Get the stomp client.
   */
  private WebSocketStompClient getStompClient() {
    List<Transport> transports = new ArrayList<>();
    transports.add(new RestTemplateXhrTransport());
    SockJsClient client = new SockJsClient(transports);
    WebSocketStompClient stompClient = new WebSocketStompClient(client);
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    return stompClient;
  }

  /**
   * Get the stomp session handler.
   */
  private StompSessionHandler getStompSessionHandler() {
    return new StompSessionHandlerAdapter() {
      @Override
      public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        logger.info("New session established : {}", session.getSessionId());
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
        messageCount++;
        logger.info("Message count: {}. Payload: {}", messageCount, payload);
      }

      @Override
      public Type getPayloadType(StompHeaders headers) {
        return getResponseClass();
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
  }
}
