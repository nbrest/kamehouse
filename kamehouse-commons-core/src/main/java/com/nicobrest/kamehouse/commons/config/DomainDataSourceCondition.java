package com.nicobrest.kamehouse.commons.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * Enables the domain datasource beans only when the module defines a domain JDBC url.
 */
public class DomainDataSourceCondition implements Condition {

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    return StringUtils.hasText(context.getEnvironment().getProperty("jdbc.domain.url"));
  }
}
