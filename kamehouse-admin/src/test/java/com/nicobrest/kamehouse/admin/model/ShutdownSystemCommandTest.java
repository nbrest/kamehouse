package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.admin.model.systemcommand.ShutdownSystemCommand;
import com.nicobrest.kamehouse.commons.model.AbstractSystemCommandTest;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;

/**
 * Test for the ShutdownSystemCommand.
 */
class ShutdownSystemCommandTest extends AbstractSystemCommandTest {

  @Override
  protected SystemCommand getSystemCommand() {
    return new ShutdownSystemCommand(55);
  }

  @Override
  protected String getWindowsShellCommand() {
    return "win/shutdown/shutdown.sh -s -t 55";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "lin/shutdown/shutdown.sh -d 0";
  }
}
