package com.nicobrest.kamehouse.commons.config;

import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/** Configuration of exception handling in the application. */
@Configuration
public class ExceptionHandlerConfig {

  /** Mapping of the exceptions to status codes. */
  @Bean
  public SimpleMappingExceptionResolver exceptionResolver() {
    Properties exceptionMappings = new Properties();
    exceptionMappings.setProperty(
        "com.nicobrest.kamehouse.commons.exception" + ".KameHouseBadRequestException",
        "/error/400");
    exceptionMappings.setProperty(
        "com.nicobrest.kamehouse.commons.exception" + ".KameHouseConflictException", "/error/409");
    exceptionMappings.setProperty(
        "com.nicobrest.kamehouse.commons.exception" + ".KameHouseForbiddenException", "/error/403");
    exceptionMappings.setProperty(
        "com.nicobrest.kamehouse.commons.exception" + ".KameHouseInvalidCommandException",
        "/error/400");
    exceptionMappings.setProperty(
        "com.nicobrest.kamehouse.commons.exception" + ".KameHouseNotFoundException", "/error/404");
    exceptionMappings.setProperty(
        "com.nicobrest.kamehouse.commons.exception" + ".KameHouseServerErrorException",
        "/error/500");
    exceptionMappings.setProperty(
        "org.springframework.security.core.userdetails" + ".UsernameNotFoundException",
        "/error/404");
    Properties statusCodes = new Properties();
    statusCodes.setProperty("/error/400", "400");
    statusCodes.setProperty("/error/403", "403");
    statusCodes.setProperty("/error/404", "404");
    statusCodes.setProperty("/error/405", "405");
    statusCodes.setProperty("/error/409", "409");
    statusCodes.setProperty("/error/500", "500");
    SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
    exceptionResolver.setExceptionMappings(exceptionMappings);
    exceptionResolver.setStatusCodes(statusCodes);
    return exceptionResolver;
  }
}
