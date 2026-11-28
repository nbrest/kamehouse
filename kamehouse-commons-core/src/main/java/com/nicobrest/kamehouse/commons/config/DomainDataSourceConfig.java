package com.nicobrest.kamehouse.commons.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * Domain DataSource, entityManagerFactory and transactionManager for modules with domain tables.
 *
 * <p>The beans are created only when the module provides a jdbc-domain.properties file with a
 * jdbc.domain.url value. Modules that only use identity and session data keep the default beans
 * from DataSourceConfig.
 */
@Configuration
@PropertySource("classpath:hibernate.properties")
@PropertySource(value = "classpath:jdbc-domain.properties", ignoreResourceNotFound = true)
public class DomainDataSourceConfig {

  private final Environment env;

  @Autowired
  public DomainDataSourceConfig(Environment env) {
    this.env = env;
  }

  /**
   * Domain DataSource.
   */
  @Bean(name = "domainDataSource")
  @Conditional(DomainDataSourceCondition.class)
  public DriverManagerDataSource domainDataSource() {
    DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
    driverManagerDataSource.setDriverClassName(getProperty("jdbc.domain.driverClassName"));
    driverManagerDataSource.setUrl(getProperty("jdbc.domain.url"));
    driverManagerDataSource.setUsername(getProperty("jdbc.domain.username"));
    driverManagerDataSource.setPassword(getProperty("jdbc.domain.password"));
    return driverManagerDataSource;
  }

  /**
   * Domain entityManagerFactory.
   */
  @Bean(name = "domainEntityManagerFactory")
  @Conditional(DomainDataSourceCondition.class)
  public LocalContainerEntityManagerFactoryBean domainEntityManagerFactory(
      @Qualifier("domainDataSource") DriverManagerDataSource domainDataSource) {
    LocalContainerEntityManagerFactoryBean entityManagerFactory =
        new LocalContainerEntityManagerFactoryBean();
    entityManagerFactory.setDataSource(domainDataSource);
    entityManagerFactory.setPersistenceXmlLocation("classpath:META-INF/persistence-domain.xml");
    entityManagerFactory.setJpaProperties(JpaPropertiesUtils.build(env));
    return entityManagerFactory;
  }

  /**
   * Domain transactionManager.
   */
  @Bean(name = "domainTransactionManager")
  @Conditional(DomainDataSourceCondition.class)
  public JpaTransactionManager domainTransactionManager(
      @Qualifier("domainEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
    jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
    return jpaTransactionManager;
  }

  private String getProperty(String property) {
    return env.getProperty(property, "");
  }
}
