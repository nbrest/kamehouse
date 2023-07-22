package com.nicobrest.kamehouse.cmd.config;

import com.nicobrest.kamehouse.cmd.model.CmdArgumentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point of the application.
 * It only calls CmdArgumentHandler to parse the arguments
 * and then delegates the execution to KameHouseCmd.
 *
 * @author nbrest
 */
public class MainApp {

  private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

  /**
   * Execute kamehouseCmd.
   */
  public static void main(String[] args) {
    logger.info("Started executing kamehouse-cmd main");
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    KameHouseCmd.execute(cmdArgumentHandler);
    logger.info("Finished executing kamehouse-cmd main");
  }
}