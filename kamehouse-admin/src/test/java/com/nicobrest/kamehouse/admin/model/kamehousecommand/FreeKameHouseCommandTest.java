package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test for the FreeKameHouseCommand.
 */
class FreeKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new FreeKameHouseCommand();
  }

  @Override
  protected String getWindowsShellCommand() {
    return "win/sysadmin/free.sh";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "lin/sysadmin/free.sh";
  }
}
