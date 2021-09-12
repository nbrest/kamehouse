package com.nicobrest.kamehouse.commons.config;

import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * JDBC DataSource bean configuration for all modules.
 */
@Configuration
@PropertySource("classpath:hibernate.properties")
@PropertySource("classpath:jdbc.properties")
public class DataSourceConfig {

  @Autowired
  private Environment env;

  /**
   * Default DataSource.
   */
  @Bean
  public DriverManagerDataSource dataSource() {
    DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
    driverManagerDataSource.setDriverClassName(getProperty("jdbc.driverClassName"));
    driverManagerDataSource.setUrl(getProperty("jdbc.url"));
    driverManagerDataSource.setUsername(getProperty("jdbc.username"));
    driverManagerDataSource.setPassword(getProperty("jdbc.password"));
    return driverManagerDataSource;
  }

  /**
   * Default entityManagerFactory.
   */
  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      DriverManagerDataSource dataSource) {
    LocalContainerEntityManagerFactoryBean entityManagerFactory =
        new LocalContainerEntityManagerFactoryBean();
    entityManagerFactory.setDataSource(dataSource);
    Properties jpaProperties = new Properties();
    jpaProperties.setProperty("hibernate.dialect", getProperty("hibernate.dialect"));
    jpaProperties.setProperty("hibernate.show_sql", getProperty("hibernate.show_sql"));
    jpaProperties.setProperty("hibernate.format_sql", getProperty("hibernate.format_sql"));
    jpaProperties.setProperty(
        "hibernate.generate_statistics", getProperty("hibernate.generate_statistics"));
    jpaProperties.setProperty("hibernate.hbm2ddl.auto", getProperty("hibernate.hbm2ddl.auto"));
    jpaProperties.setProperty("connection.provider_class",
        getProperty("connection.provider_class"));
    jpaProperties.setProperty("hibernate.c3p0.acquire_increment",
        getProperty("hibernate.c3p0.acquire_increment"));
    jpaProperties.setProperty(
        "hibernate.c3p0.idle_test_period", getProperty("hibernate.c3p0.idle_test_period"));
    jpaProperties.setProperty(
        "hibernate.c3p0.min_size", getProperty("hibernate.c3p0.min_size"));
    jpaProperties.setProperty(
        "hibernate.c3p0.max_size", getProperty("hibernate.c3p0.max_size"));
    jpaProperties.setProperty(
        "hibernate.c3p0.max_statements", getProperty("hibernate.c3p0.max_statements"));
    jpaProperties.setProperty("hibernate.c3p0.timeout", getProperty("hibernate.c3p0.timeout"));
    jpaProperties.setProperty("hibernate.c3p0.acquireRetryAttempts",
        getProperty("hibernate.c3p0.acquireRetryAttempts"));
    jpaProperties.setProperty("hibernate.c3p0.acquireRetryDelay",
        getProperty("hibernate.c3p0.acquireRetryDelay"));
    entityManagerFactory.setJpaProperties(jpaProperties);
    return entityManagerFactory;
  }

  /**
   * Get property from the environment.
   */
  private String getProperty(String property) {
    return env.getProperty(property, "");
  }

  /**
   * Default transactionManager.
   */
  @Bean
  public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
    jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
    return jpaTransactionManager;
  }
}
