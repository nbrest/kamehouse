package com.nicobrest.kamehouse.commons.config;

import java.util.Properties;
import org.springframework.core.env.Environment;

/**
 * Builds the Hibernate properties shared by the identity and domain entity manager factories.
 */
final class JpaPropertiesUtils {

  private JpaPropertiesUtils() {
  }

  /**
   * Builds the Hibernate properties from the environment.
   */
  static Properties build(Environment env) {
    Properties jpaProperties = new Properties();
    jpaProperties.setProperty("hibernate.dialect", getProperty(env, "hibernate.dialect"));
    jpaProperties.setProperty("hibernate.show_sql", getProperty(env, "hibernate.show_sql"));
    jpaProperties.setProperty("hibernate.format_sql", getProperty(env, "hibernate.format_sql"));
    jpaProperties.setProperty(
        "hibernate.generate_statistics", getProperty(env, "hibernate.generate_statistics"));
    jpaProperties.setProperty(
        "hibernate.hbm2ddl.auto", getProperty(env, "hibernate.hbm2ddl.auto"));
    jpaProperties.setProperty(
        "connection.provider_class", getProperty(env, "connection.provider_class"));
    jpaProperties.setProperty(
        "hibernate.c3p0.acquire_increment", getProperty(env, "hibernate.c3p0.acquire_increment"));
    jpaProperties.setProperty(
        "hibernate.c3p0.idle_test_period", getProperty(env, "hibernate.c3p0.idle_test_period"));
    jpaProperties.setProperty(
        "hibernate.c3p0.min_size", getProperty(env, "hibernate.c3p0.min_size"));
    jpaProperties.setProperty(
        "hibernate.c3p0.max_size", getProperty(env, "hibernate.c3p0.max_size"));
    jpaProperties.setProperty(
        "hibernate.c3p0.max_statements", getProperty(env, "hibernate.c3p0.max_statements"));
    jpaProperties.setProperty(
        "hibernate.c3p0.timeout", getProperty(env, "hibernate.c3p0.timeout"));
    jpaProperties.setProperty(
        "hibernate.c3p0.acquireRetryAttempts",
        getProperty(env, "hibernate.c3p0.acquireRetryAttempts"));
    jpaProperties.setProperty(
        "hibernate.c3p0.acquireRetryDelay", getProperty(env, "hibernate.c3p0.acquireRetryDelay"));
    return jpaProperties;
  }

  private static String getProperty(Environment env, String key) {
    return env.getProperty(key, "");
  }
}
