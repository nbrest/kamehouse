package com.nicobrest.kamehouse.ui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * UI module bean configuration.
 */
@Configuration
public class UiAppConfig {

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
