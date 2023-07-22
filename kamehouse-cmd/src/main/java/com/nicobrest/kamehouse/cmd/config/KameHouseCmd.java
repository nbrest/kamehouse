package com.nicobrest.kamehouse.cmd.config;

import com.nicobrest.kamehouse.cmd.executor.KameHouseCmdExecutor;
import com.nicobrest.kamehouse.cmd.model.CmdArgumentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class loads the application context and the bean to execute the command.
 *
 * @author nbrest
 */
public class KameHouseCmd {

  private static final Logger logger = LoggerFactory.getLogger(KameHouseCmd.class);

  private static ApplicationContext context;

  /**
   * Class only with static methods. No need for instances.
   */
  private KameHouseCmd() {

  }

  /** Start kamehouse cmd. */
  public static void execute(CmdArgumentHandler cmdArgumentHandler) {
    logger.info("Started executing kamehouse-cmd execute");
    loadContext();
    getKameHouseCmdExecutor().execute(cmdArgumentHandler);
    closeContext();
    logger.info("Finished executing kamehouse-cmd execute");
  }

  /** Load the application context. */
  private static void loadContext() {
    logger.info("Loading application context");
    context = new ClassPathXmlApplicationContext("applicationContext.xml");
  }

  /** Close the application context. */
  private static void closeContext() {
    ((AbstractApplicationContext) context).close();
  }

  /** Get the main executor to process kamehouse commands. */
  private static KameHouseCmdExecutor getKameHouseCmdExecutor() {
    logger.info("Getting KameHouseCmdExecutor bean");
    return (KameHouseCmdExecutor) context.getBean("kameHouseCmdExecutor");
  }
}
