package com.nicobrest.kamehouse.testmodule.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * Class to configure the security of all websockets in the application.
 * 
 * @author nbrest
 *
 */
@Configuration
public class TestModuleWebSocketSecurityConfig
    extends AbstractSecurityWebSocketMessageBrokerConfigurer {

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