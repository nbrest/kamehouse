package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.admin.model.systemcommand.TopSystemCommand;
import com.nicobrest.kamehouse.commons.model.AbstractSystemCommandTest;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;

/**
 * Test for the TopSystemCommand.
 */
class TopSystemCommandTest extends AbstractSystemCommandTest {

  @Override
  protected SystemCommand getSystemCommand() {
    return new TopSystemCommand();
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
