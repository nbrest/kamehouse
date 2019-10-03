package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.main.exception.KameHouseInvalidCommandException;

import java.util.Arrays;

/**
 * System command to shutdown the server.
 * 
 * @author nbrest
 *
 */
public class ShutdownSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation system required for this SystemCommand.
   */
  public ShutdownSystemCommand(int shutdownDelaySeconds) {
    if (shutdownDelaySeconds <= 0) {
      throw new KameHouseInvalidCommandException("Invalid time for shutdown command "
          + shutdownDelaySeconds);
    }
    linuxCommand.addAll(Arrays.asList("/bin/bash", "-c", "sudo /sbin/shutdown -P ", String
        .valueOf(shutdownDelaySeconds / 60)));
    windowsCommand.addAll(Arrays.asList("cmd.exe", "/c", "start", "shutdown", "/s", "/t ", String
        .valueOf(shutdownDelaySeconds)));
    setOutputCommand();
  }
}
