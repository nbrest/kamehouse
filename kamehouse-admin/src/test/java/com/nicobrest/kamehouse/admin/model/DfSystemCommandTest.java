package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.admin.model.systemcommand.DfSystemCommand;
import com.nicobrest.kamehouse.commons.model.AbstractSystemCommandTest;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;

/**
 * Test for the DfSystemCommand.
 */
class DfSystemCommandTest extends AbstractSystemCommandTest {

  @Override
  protected SystemCommand getSystemCommand() {
    return new DfSystemCommand();
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
