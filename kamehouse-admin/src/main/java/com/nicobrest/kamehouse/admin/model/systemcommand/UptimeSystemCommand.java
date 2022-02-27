package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;

/**
 * System command to check the uptime of the server.
 *
 * @author nbrest
 */
public class UptimeSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation system required for this SystemCommand.
   */
  public UptimeSystemCommand() {
    executeOnDockerHost = true;
    addBashPrefix();
    linuxCommand.add("uptime");
    addPowerShellPrefix();
    windowsCommand.add("\"systeminfo | Select-String -Pattern Time\"");
    setOutputCommand();
  }
}
