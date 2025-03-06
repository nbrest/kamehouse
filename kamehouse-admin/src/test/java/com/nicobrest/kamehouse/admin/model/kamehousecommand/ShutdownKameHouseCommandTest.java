package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test for the ShutdownKameHouseCommand.
 */
class ShutdownKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new ShutdownKameHouseCommand(55);
  }

  @Override
  protected String getWindowsShellCommand() {
    return "win/shutdown/shutdown.sh -s -t 55";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "lin/shutdown/shutdown.sh -d 0";
  }
}
