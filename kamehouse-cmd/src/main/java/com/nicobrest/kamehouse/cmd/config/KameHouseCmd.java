package com.nicobrest.kamehouse.cmd.config;

import com.nicobrest.kamehouse.cmd.executor.KameHouseCmdExecutor;
import com.nicobrest.kamehouse.cmd.model.CmdArgumentHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class loads the application context and the bean to execute the command.
 *
 * @author nbrest
 */
public class KameHouseCmd {

  private static ApplicationContext context;

  /**
   * Class only with static methods. No need for instances.
   */
  private KameHouseCmd() {

  }

  /** Start kamehouse cmd. */
  public static void execute(CmdArgumentHandler cmdArgumentHandler) {
    loadContext();
    getKameHouseCmdExecutor().execute(cmdArgumentHandler);
    closeContext();
  }

  /** Load the application context. */
  private static void loadContext() {
    context = new ClassPathXmlApplicationContext("applicationContext.xml");
  }

  /** Close the application context. */
  private static void closeContext() {
    ((AbstractApplicationContext) context).close();
  }

  /** Get the main executor to process kamehouse commands. */
  private static KameHouseCmdExecutor getKameHouseCmdExecutor() {
    return (KameHouseCmdExecutor) context.getBean("kameHouseCmdExecutor");
  }
}
