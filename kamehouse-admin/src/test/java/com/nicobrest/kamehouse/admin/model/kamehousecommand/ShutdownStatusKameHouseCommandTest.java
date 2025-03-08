package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class ShutdownStatusKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new ShutdownStatusKameHouseCommand();
  }

  @Override
  protected String getWindowsShellCommand() {
    return "win/shutdown/shutdown-status.sh";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "lin/shutdown/shutdown-status.sh";
  }
}
