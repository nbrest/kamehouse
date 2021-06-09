package com.nicobrest.kamehouse.vlcrc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * Configuration class for all web sockets in the application.
 * 
 * @author nbrest
 *
 */
@Configuration
@EnableWebSocketMessageBroker
public class VlcRcWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer  {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/api/ws/vlc-player/status").setAllowedOrigins("*").withSockJS();
    registry.addEndpoint("/api/ws/vlc-player/playlist").setAllowedOrigins("*").withSockJS();
  }
}