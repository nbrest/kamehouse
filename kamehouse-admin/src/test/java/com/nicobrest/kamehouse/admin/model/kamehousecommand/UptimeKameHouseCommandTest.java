package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test for the UptimeKameHouseCommand.
 */
class UptimeKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new UptimeKameHouseCommand();
  }

  @Override
  protected String getWindowsShellCommand() {
    return "win/sysadmin/uptime.sh";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "lin/sysadmin/uptime.sh";
  }
}
