package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class ScreenLockKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new ScreenLockKameHouseCommand();
  }

  @Override
  protected String getWindowsShellCommand() {
    return "screen/screen-lock.sh";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "screen/screen-lock.sh";
  }
}
