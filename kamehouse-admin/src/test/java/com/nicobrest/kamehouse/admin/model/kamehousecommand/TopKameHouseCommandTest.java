package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test for the TopKameHouseCommand.
 */
class TopKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new TopKameHouseCommand();
  }

  @Override
  protected String getWindowsShellCommand() {
    return "win/sysadmin/top.sh";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "lin/sysadmin/top.sh";
  }
}
