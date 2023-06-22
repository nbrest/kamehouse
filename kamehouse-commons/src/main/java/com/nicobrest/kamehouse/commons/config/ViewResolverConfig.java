package com.nicobrest.kamehouse.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * View Resolver Configuration.
 *
 * @author nbrest
 *
 */
@Configuration
public class ViewResolverConfig {

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
