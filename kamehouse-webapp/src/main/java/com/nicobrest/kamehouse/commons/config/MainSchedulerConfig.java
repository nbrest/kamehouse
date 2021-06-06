package com.nicobrest.kamehouse.commons.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Configuration class to setup the scheduler beans in the main package.
 * 
 * @author nbrest
 *
 */
@Configuration
@EnableScheduling
public class MainSchedulerConfig {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private ApplicationContext applicationContext;

  /**
   * Workaround to add autowiring support to SpringBeanJobFactory.
   */
  @Bean
  public SpringBeanJobFactory springBeanJobFactory() {
    logger.info("Configuring Job factory");
    AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
    jobFactory.setApplicationContext(applicationContext);
    return jobFactory;
  }

  /**
   * schedulerFactoryBean bean.
   */
  @Bean
  public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
    SchedulerFactoryBean factory = new SchedulerFactoryBean();
    factory.setJobFactory(springBeanJobFactory());
    factory.setQuartzProperties(quartzProperties());
    return factory;
  }

  /**
   * Get quartz propeties file.
   */
  public Properties quartzProperties() throws IOException {
    PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
    propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
    propertiesFactoryBean.afterPropertiesSet();
    return propertiesFactoryBean.getObject();
  }

  /**
   * Scheduler bean.
   */
  @Bean
  public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) throws SchedulerException {
    logger.info("Setting up the Scheduler");
    Scheduler scheduler = schedulerFactoryBean.getScheduler();
    logger.info("Starting Scheduler threads");
    scheduler.start();
    return scheduler;
  }
}