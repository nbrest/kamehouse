package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.admin.model.systemcommand.FreeSystemCommand;
import com.nicobrest.kamehouse.commons.model.AbstractSystemCommandTest;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;

/**
 * Test for the FreeSystemCommand.
 */
class FreeSystemCommandTest extends AbstractSystemCommandTest {

  @Override
  protected SystemCommand getSystemCommand() {
    return new FreeSystemCommand();
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
