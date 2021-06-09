package com.nicobrest.kamehouse.webapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * WebApp module bean configuration.
 */
@Configuration
public class WebAppConfig {

  /**
   * View resolver for jsps.
   */
  @Bean
  public InternalResourceViewResolver viewResolver() {
    InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
    internalResourceViewResolver.setPrefix("/WEB-INF/jsp");
    internalResourceViewResolver.setSuffix(".jsp");
    return internalResourceViewResolver;
  }
}
