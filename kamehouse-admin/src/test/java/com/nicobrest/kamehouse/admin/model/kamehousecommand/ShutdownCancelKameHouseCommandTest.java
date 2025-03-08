package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class ShutdownCancelKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new ShutdownCancelKameHouseCommand();
  }

  @Override
  protected String getWindowsShellCommand() {
    return "win/shutdown/shutdown-cancel.sh";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "lin/shutdown/shutdown-cancel.sh";
  }
}
