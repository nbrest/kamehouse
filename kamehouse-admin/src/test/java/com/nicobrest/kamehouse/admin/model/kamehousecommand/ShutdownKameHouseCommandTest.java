package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class ShutdownKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new ShutdownKameHouseCommand(55);
  }

  @Override
  protected String getWindowsShellCommand() {
    return "shutdown/shutdown.sh -s -d 55";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "shutdown/shutdown.sh -d 0";
  }
}
