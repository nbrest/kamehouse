package com.nicobrest.kamehouse.commons.config;

import com.nicobrest.kamehouse.commons.web.filter.logger.CustomRequestLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Config to create the CustomRequestLoggingFilter bean.
 */
@Configuration
public class RequestLoggingConfig {

  @Bean
  CustomRequestLoggingFilter customRequestLoggingFilter() {
    return new CustomRequestLoggingFilter();
  }
}
