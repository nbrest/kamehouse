package com.nicobrest.kamehouse.commons.config;

import com.nicobrest.kamehouse.commons.security.CustomAuthenticationSuccessHandler;
import com.nicobrest.kamehouse.commons.security.CustomEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration specific to spring security.
 */
@Configuration
public class SpringSecurityConfig {

  /**
   * Custom entry point to handle unauthorized requests.
   */
  @Bean
  public CustomEntryPoint customEntryPoint() {
    CustomEntryPoint customEntryPoint = new CustomEntryPoint("/login");
    return customEntryPoint;
  }

  /**
   * Custom implementation of AuthenticationSuccessHandler.
   * Not sure if it's still relevant to keep this. It's referenced in spring-security.xml.
   */
  @Bean
  public CustomAuthenticationSuccessHandler authenticationSuccessHandler() {
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler =
        new CustomAuthenticationSuccessHandler();
    // Set to false, as with js redirects, the last referer will be /login
    customAuthenticationSuccessHandler.setUseReferer(false);
    return customAuthenticationSuccessHandler;
  }
}
