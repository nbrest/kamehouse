package com.nicobrest.kamehouse.vlcrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class VlcStatusKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new VlcStatusKameHouseCommand();
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
