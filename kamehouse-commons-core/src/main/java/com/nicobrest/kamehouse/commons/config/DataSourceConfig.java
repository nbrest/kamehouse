package com.nicobrest.kamehouse.commons.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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

  private final Environment env;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public DataSourceConfig(Environment env) {
    this.env = env;
  }

  /**
   * Default identity DataSource. Spring Session binds to this bean.
   */
  @Bean
  @Primary
  public DriverManagerDataSource dataSource() {
    DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
    driverManagerDataSource.setDriverClassName(getProperty("jdbc.driverClassName"));
    driverManagerDataSource.setUrl(getProperty("jdbc.url"));
    driverManagerDataSource.setUsername(getProperty("jdbc.username"));
    driverManagerDataSource.setPassword(getProperty("jdbc.password"));
    return driverManagerDataSource;
  }

  /**
   * Default identity entityManagerFactory.
   */
  @Bean
  @Primary
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      @Qualifier("dataSource") DriverManagerDataSource dataSource) {
    LocalContainerEntityManagerFactoryBean entityManagerFactory =
        new LocalContainerEntityManagerFactoryBean();
    entityManagerFactory.setDataSource(dataSource);
    entityManagerFactory.setJpaProperties(JpaPropertiesUtils.build(env));
    return entityManagerFactory;
  }

  /**
   * Get property from the environment.
   */
  private String getProperty(String property) {
    return env.getProperty(property, "");
  }

  /**
   * Default identity transactionManager.
   */
  @Bean
  @Primary
  public JpaTransactionManager transactionManager(
      @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
    jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
    return jpaTransactionManager;
  }
}
