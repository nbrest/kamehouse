package com.nicobrest.kamehouse.cmd.config;

import com.nicobrest.kamehouse.cmd.model.CmdArgumentHandler;

/**
 * Entry point of the application.
 * It only calls CmdArgumentHandler to parse the arguments
 * and then delegates the execution to KameHouseCmd.
 *
 * @author nbrest
 */
public class MainApp {

  /**
   * Execute kamehouseCmd.
   */
  public static void main(String[] args) {
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    KameHouseCmd.execute(cmdArgumentHandler);
  }
}