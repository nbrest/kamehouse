package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test for the DfKameHouseCommand.
 */
class DfKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new DfKameHouseCommand();
  }

  @Override
  protected String getWindowsShellCommand() {
    return "win/sysadmin/df.sh";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "lin/sysadmin/df.sh";
  }
}
