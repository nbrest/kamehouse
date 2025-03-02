package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import java.util.List;

/**
 * Test Daemon command that should execute on the docker host to test the SystemCommandService.
 *
 * @author nbrest
 */
public class TestDaemonCommand extends KameHouseShellSystemCommand {

  /**
   * Test Daemon command.
   */
  public TestDaemonCommand() {
    super();
    isDaemon = true;
    executeOnDockerHost = true;
    sshTimeout = 20000L;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "test-script.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    return null;
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "test-script.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return null;
  }
}
