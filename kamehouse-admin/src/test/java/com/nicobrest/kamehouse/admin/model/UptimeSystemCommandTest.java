package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.admin.model.systemcommand.UptimeSystemCommand;
import com.nicobrest.kamehouse.commons.model.AbstractSystemCommandTest;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;

/**
 * Test for the UptimeSystemCommand.
 */
class UptimeSystemCommandTest extends AbstractSystemCommandTest {

  @Override
  protected SystemCommand getSystemCommand() {
    return new UptimeSystemCommand();
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
