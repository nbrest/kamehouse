package com.nicobrest.kamehouse.admin.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

  @Override
  protected boolean sameOriginDisabled() {
    return true;
  }

  @Override
  protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
    messages.simpDestMatchers("/test-module/websocket-in").permitAll();
    messages.simpDestMatchers("/app/test-module/websocket-in").permitAll();
  }
}