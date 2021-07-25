package com.nicobrest.kamehouse.cmd.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Entry point of the application. 
 * It only loads the application context and then delegates the execution to KameHouseCmd.
 *
 * @author nbrest
 */
public class MainApp {

  private static ApplicationContext context = getContext();
  
  /**
   * Execute kamehouseCmd.
   */
  public static void main(String[] args) {
    getKameHouseCmd().execute(args);
    closeContext();
  }
  
  private static ApplicationContext getContext() {
    return new ClassPathXmlApplicationContext("applicationContext.xml");
  }
  
  private static void closeContext() {
    ((AbstractApplicationContext) context).close();
  }
  
  private static KameHouseCmd getKameHouseCmd() {
    return (KameHouseCmd) context.getBean("kameHouseCmd");
  }
}