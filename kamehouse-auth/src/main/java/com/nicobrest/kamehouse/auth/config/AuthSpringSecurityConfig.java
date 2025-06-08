package com.nicobrest.kamehouse.auth.config;

import com.nicobrest.kamehouse.auth.security.CustomAuthenticationFailureHandler;
import com.nicobrest.kamehouse.auth.security.CustomAuthenticationSuccessHandler;
import com.nicobrest.kamehouse.auth.security.CustomLogoutSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * Configuration specific to spring security for Auth module.
 */
@Configuration
public class AuthSpringSecurityConfig {

  /**
   * Custom implementation of AuthenticationSuccessHandler.
   */
  @Bean
  public CustomAuthenticationSuccessHandler authenticationSuccessHandler() {
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler =
        new CustomAuthenticationSuccessHandler();
    // Set to false, as with js redirects, the last referer will be /login
    customAuthenticationSuccessHandler.setUseReferer(false);
    return customAuthenticationSuccessHandler;
  }

  /**
   * Custom implementation of AuthenticationFailureHandler.
   */
  @Bean
  public AuthenticationFailureHandler authenticationFailureHandler() {
    CustomAuthenticationFailureHandler customAuthenticationFailureHandler =
        new CustomAuthenticationFailureHandler();
    return customAuthenticationFailureHandler;
  }

  /**
   * Custom implementation of LogoutSuccessHandler.
   */
  @Bean
  public LogoutSuccessHandler logoutSuccessHandler() {
    CustomLogoutSuccessHandler logoutSuccessHandler = new CustomLogoutSuccessHandler();
    return logoutSuccessHandler;
  }
}
