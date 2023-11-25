package com.nicobrest.kamehouse.commons.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.Properties;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * Configuration class to setup the scheduler beans in the main package.
 *
 * @author nbrest
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private ApplicationContext applicationContext;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public SchedulerConfig(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

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
    Properties properties = quartzProperties();
    if (properties != null) {
      factory.setQuartzProperties(properties);
    }
    return factory;
  }

  /**
   * Get quartz propeties file.
   */
  private static Properties quartzProperties() throws IOException {
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

  /**
   * Adds auto-wiring support to quartz jobs.
   *
   * @author nbrest
   */
  public static final class AutoWiringSpringBeanJobFactory extends SpringBeanJobFactory
      implements ApplicationContextAware {

    private AutowireCapableBeanFactory beanFactory;

    public AutoWiringSpringBeanJobFactory() {
      super();
      beanFactory = null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
      beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
      final Object job = super.createJobInstance(bundle);
      beanFactory.autowireBean(job);
      return job;
    }
  }
}
