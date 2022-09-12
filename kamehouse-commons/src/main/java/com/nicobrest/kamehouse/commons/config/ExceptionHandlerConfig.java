package com.nicobrest.kamehouse.commons.config;

import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * Configuration of exception handling in the application.
 */
@Configuration
public class ExceptionHandlerConfig {

  private static final String ERROR_400 = "/error/400";
  private static final String ERROR_404 = "/error/404";

  /**
   * Mapping of the exceptions to status codes.
   */
  @Bean
  public SimpleMappingExceptionResolver exceptionResolver() {
    Properties exceptionMappings = new Properties();
    exceptionMappings.setProperty(
        "com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException", ERROR_400);
    exceptionMappings.setProperty(
        "com.nicobrest.kamehouse.commons.exception.KameHouseConflictException", "/error/409");
    exceptionMappings.setProperty(
        "com.nicobrest.kamehouse.commons.exception.KameHouseForbiddenException", "/error/403");
    exceptionMappings.setProperty(
        "com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException", ERROR_400);
    exceptionMappings.setProperty(
        "com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException", ERROR_400);
    exceptionMappings.setProperty(
        "com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException", ERROR_404);
    exceptionMappings.setProperty(
        "com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException", "/error/500");
    exceptionMappings.setProperty(
        "org.springframework.security.core.userdetails.UsernameNotFoundException", ERROR_404);
    Properties statusCodes = new Properties();
    statusCodes.setProperty(ERROR_400, "400");
    statusCodes.setProperty("/error/401", "401");
    statusCodes.setProperty("/error/403", "403");
    statusCodes.setProperty(ERROR_404, "404");
    statusCodes.setProperty("/error/405", "405");
    statusCodes.setProperty("/error/409", "409");
    statusCodes.setProperty("/error/500", "500");
    SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
    exceptionResolver.setExceptionMappings(exceptionMappings);
    exceptionResolver.setStatusCodes(statusCodes);
    return exceptionResolver;
  }
}
