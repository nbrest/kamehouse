package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import java.util.List;

/**
 * System command to shut down the server.
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
    addShutdownDelayToCommand(shutdownDelaySeconds);
    setOutputCommand();
  }

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "win/shutdown/shutdown.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    return List.of("-s", "-t");
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/shutdown/shutdown.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return null;
  }

  private void addShutdownDelayToCommand(int shutdownDelaySeconds) {
    int shutdownDelayMinutes = 0;
    if (shutdownDelaySeconds >= 60) {
      shutdownDelayMinutes = shutdownDelaySeconds / 60;
    }
    String linuxCmd = linuxCommand.get(linuxCommand.size() - 1);
    linuxCmd = linuxCmd + " -d " + shutdownDelayMinutes;
    linuxCommand.remove(linuxCommand.size() - 1);
    linuxCommand.add(linuxCmd);

    String windowsCmd = windowsCommand.get(windowsCommand.size() - 1);
    windowsCmd = windowsCmd + " " + shutdownDelaySeconds;
    windowsCommand.remove(windowsCommand.size() - 1);
    windowsCommand.add(windowsCmd);
  }
}
