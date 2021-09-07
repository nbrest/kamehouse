package com.nicobrest.kamehouse.commons.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

  protected int messageCount = 0;
  private StompSession stompSession;
  private int sleepMs = 3000;

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
  public AbstractWebSocketIntegrationTest() {
    setProtocol("ws://");
    try {
      stompSession = getStompSession();
    } catch (ExecutionException | InterruptedException e) {
      logger.error("Error getting the stomp session");
    }
  }

  public void setSleepMs(int sleepMs) {
    this.sleepMs = sleepMs;
  }

  /**
   * Send a message to the websocket.
   */
  public void send() {
    stompSession.send(getPollUrl(), getRequestPayload());
  }

  /**
   * Sleep the test to give time for the websockets to respond.
   */
  protected void sleep() {
    try {
      logger.info("Sleeping for {} ms", sleepMs);
      Thread.sleep(sleepMs);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Default test that sends 2 messages to the websocket and counts the responses.
   */
  @Test
  public void defaultWebSocketTest() {
    logger.info("Running defaultWebSocketTest");

    send();
    send();

    sleep();
    assertEquals(2, messageCount);
    logger.info("Finished defaultWebSocketTest successfully");
  }

  /**
   * Get the stomp session and subscripbe to a topic.
   */
  private StompSession getStompSession() throws ExecutionException, InterruptedException {
    WebSocketStompClient stompClient = getStompClient();
    StompSessionHandler sessionHandler = getStompSessionHandler();
    Future<StompSession> stompSessionFuture = stompClient.connect(getWebSocketUrl(),
        sessionHandler);
    StompSession stompSession = stompSessionFuture.get();
    if (getTopicUrl() != null) {
      stompSession.subscribe(getTopicUrl(), sessionHandler);
    }
    return stompSession;
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
    StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
      @Override
      public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        logger.info("New session established : " + session.getSessionId());
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
    return sessionHandler;
  }
}
