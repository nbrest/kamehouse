package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;

/**
 * System commandto get the current state of the processes of the server.
 *
 * @author nbrest
 */
public class TopSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation required for this SystemCommand.
   */
  public TopSystemCommand() {
    executeOnDockerHost = true;
    addBashPrefix();
    linuxCommand.add("top -n 1");
    addPowerShellPrefix();
    windowsCommand.add("\"ps | sort -desc cpu | select -first 20\"");
    setOutputCommand();
  }
}
