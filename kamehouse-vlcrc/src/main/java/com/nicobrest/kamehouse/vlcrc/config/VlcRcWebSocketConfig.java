package com.nicobrest.kamehouse.vlcrc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * Configuration class for all web sockets in the application.
 *
 * @author nbrest
 */
@Configuration
@EnableWebSocketMessageBroker
public class VlcRcWebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/api/ws/vlc-player/status")
        .setAllowedOriginPatterns("*")
        .withSockJS();
    registry.addEndpoint("/api/ws/vlc-player/playlist")
        .setAllowedOriginPatterns("*")
        .withSockJS();
  }

  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
    registry.setMessageSizeLimit(10000 * 1024);
    registry.setSendBufferSizeLimit(10000 * 1024);
    registry.setSendTimeLimit(20000);
    registry.setTimeToFirstMessage(20000);
  }
}
