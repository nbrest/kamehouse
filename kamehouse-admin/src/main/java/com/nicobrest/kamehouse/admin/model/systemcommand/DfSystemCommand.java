package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;

/**
 * System command to get the available disk space of the server.
 *
 * @author nbrest
 */
public class DfSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation required for this SystemCommand.
   */
  public DfSystemCommand() {
    executeOnDockerHost = true;
    addBashPrefix();
    linuxCommand.add("df -h");
    addPowerShellPrefix();
    windowsCommand.add("gdr");
    setOutputCommand();
  }
}
