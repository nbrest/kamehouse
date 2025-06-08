package com.nicobrest.kamehouse.commons.config;

import com.nicobrest.kamehouse.commons.security.CustomEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration specific to spring security for all modules.
 */
@Configuration
public class SpringSecurityConfig {

  /**
   * Custom entry point to handle unauthorized requests.
   */
  @Bean
  public CustomEntryPoint customEntryPoint() {
    return new CustomEntryPoint("/login");
  }
}
