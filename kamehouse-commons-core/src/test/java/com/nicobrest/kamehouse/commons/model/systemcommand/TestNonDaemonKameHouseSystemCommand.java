package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;

/**
 * Test KameHouseSystemCommand to test the SystemCommandService.
 *
 * @author nbrest
 */
public class TestNonDaemonKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Test KameHouseSystemCommand to test the SystemCommandService.
   */
  public TestNonDaemonKameHouseSystemCommand() {
    systemCommands.add(new TestNonDaemonCommand());
  }
}
