package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import java.util.Arrays;

/**
 * System command to shutdown the server.
 *
 * @author nbrest
 */
public class ShutdownSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation system required for this SystemCommand.
   */
  public ShutdownSystemCommand(int shutdownDelaySeconds) {
    executeOnDockerHost = true;
    if (shutdownDelaySeconds < 0) {
      throw new KameHouseInvalidCommandException(
          "Invalid time for shutdown command " + shutdownDelaySeconds);
    }
    int shutdownDelayMinutes = 0;
    if (shutdownDelaySeconds >= 60) {
      shutdownDelayMinutes = shutdownDelaySeconds / 60;
    }
    addBashPrefix();
    linuxCommand.addAll(Arrays.asList(
        "sudo /sbin/shutdown -P ", String.valueOf(shutdownDelayMinutes)));
    addWindowsCmdStartPrefix();
    windowsCommand.addAll(Arrays.asList(
        "shutdown", "/s", "/t ", String.valueOf(shutdownDelaySeconds)));
    setOutputCommand();
  }
}
