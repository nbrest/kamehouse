package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import java.util.List;

/**
 * Test Non Daemon command that should execute on the docker host to test the SystemCommandService.
 *
 * @author nbrest
 */
public class TestNonDaemonCommand extends KameHouseShellSystemCommand {

  /**
   * Test Daemon command.
   */
  public TestNonDaemonCommand() {
    super();
    isDaemon = false;
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
