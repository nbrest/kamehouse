package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;

import java.util.Arrays;

/**
 * System command to reboot the server.
 * 
 * @author nbrest
 *
 */
public class RebootSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation system required for this SystemCommand.
   */
  public RebootSystemCommand() {
    linuxCommand.addAll(Arrays.asList("/bin/bash", "-c", "sudo reboot"));
    windowsCommand.addAll(Arrays.asList("cmd.exe", "/c", "start", "shutdown", "/r", "/f", "/t",
        "0"));
    setOutputCommand();
  }
}
