package com.nicobrest.kamehouse.commons.model;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;

/**
 * Test KameHouseSystemCommand to test the SystemCommandService.
 *
 * @author nbrest
 */
public class TestDaemonKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Test KameHouseSystemCommand to test the SystemCommandService.
   */
  public TestDaemonKameHouseSystemCommand() {
    systemCommands.add(new TestDaemonCommand());
  }
}
