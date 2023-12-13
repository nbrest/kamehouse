package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import java.util.Arrays;
import java.util.List;

/**
 * System command to shutdown the server.
 *
 * @author nbrest
 */
public class ShutdownSystemCommand extends KameHouseShellSystemCommand {

  /**
   * Sets the command line for each operation required for this SystemCommand.
   */
  public ShutdownSystemCommand(int shutdownDelaySeconds) {
    super();
    if (shutdownDelaySeconds < 0) {
      throw new KameHouseInvalidCommandException(
          "Invalid time for shutdown command " + shutdownDelaySeconds);
    }
    buildLinuxCommand(shutdownDelaySeconds);
    windowsCommand.add(String.valueOf(shutdownDelaySeconds));
    setOutputCommand();
  }

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected List<String> getWindowsCommand() {
    return Arrays.asList("shutdown", "/s", "/t ");
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/shutdown/shutdown.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return null;
  }

  private void buildLinuxCommand(int shutdownDelaySeconds) {
    int shutdownDelayMinutes = 0;
    if (shutdownDelaySeconds >= 60) {
      shutdownDelayMinutes = shutdownDelaySeconds / 60;
    }
    String command = linuxCommand.get(linuxCommand.size() - 1);
    command = command + " -d " + shutdownDelayMinutes;
    linuxCommand.remove(linuxCommand.size() - 1);
    linuxCommand.add(command);
  }
}
