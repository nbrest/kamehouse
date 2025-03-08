package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class RebootKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new RebootKameHouseCommand();
  }

  @Override
  protected String getWindowsShellCommand() {
    return "win/shutdown/reboot.sh";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "lin/shutdown/reboot.sh";
  }
}
