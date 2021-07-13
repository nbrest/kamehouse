package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;

import java.util.Arrays;

/**
 * System command to check the uptime of the server.
 * 
 * @author nbrest
 *
 */
public class UptimeSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation system required for this SystemCommand.
   */
  public UptimeSystemCommand() {
    linuxCommand.addAll(Arrays.asList("/bin/bash", "-c", "uptime"));
    windowsCommand.addAll(Arrays.asList("powershell.exe", "-c", "systeminfo | Select-String "
        + "-Pattern Time"));
    setOutputCommand();
  }
}
