package com.nicobrest.kamehouse.systemcommand.model;

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
   * Default constructor.
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
  }
}
