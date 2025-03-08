package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class SuspendKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new SuspendKameHouseCommand();
  }

  @Override
  protected String getWindowsShellCommand() {
    return "win/shutdown/suspend.sh";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "lin/shutdown/suspend.sh";
  }
}
