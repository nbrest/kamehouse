package com.nicobrest.kamehouse.main.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * Adds auto-wiring support to quartz jobs.
 *
 * @author nbrest
 *
 */
public final class AutoWiringSpringBeanJobFactory extends SpringBeanJobFactory
    implements ApplicationContextAware {

  private AutowireCapableBeanFactory beanFactory;

  public AutoWiringSpringBeanJobFactory() {
    super();
    beanFactory = null;
  }

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