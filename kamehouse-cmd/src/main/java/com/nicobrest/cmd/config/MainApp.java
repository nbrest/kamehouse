package com.nicobrest.cmd.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Entry point of the application. 
 * It only creates the application context and then delegates the execution to KameHouseCmd.
 *
 * @author nbrest
 */
public class MainApp {

  private static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class);
  private static ApplicationContext context = getContext();
  
  /**
   * Execute kamehouseCmd.
   */
  public static void main(String[] args) {
    getKameHouseCmd().run(args);
    closeContext();
  }
  
  private static ApplicationContext getContext() {
    ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    LOGGER.info("Application context: Bean definitions:");
    for (String beanName : context.getBeanDefinitionNames()) {
      System.out.println("bean: " + beanName);
    }
    return context;
  }
  
  private static void closeContext() {
    ((AbstractApplicationContext) context).close();
  }
  
  private static KameHouseCmd getKameHouseCmd() {
    return (KameHouseCmd) context.getBean("kameHouseCmd");
  }
}