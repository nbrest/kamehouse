package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;

/**
 * System command to get the available memory of the server.
 *
 * @author nbrest
 */
public class FreeSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation required for this SystemCommand.
   */
  public FreeSystemCommand() {
    executeOnDockerHost = true;
    addBashPrefix();
    linuxCommand.add("free -h");
    addPowerShellPrefix();
    windowsCommand.add("\"systeminfo | Select-String -Pattern Memory\"");
    setOutputCommand();
  }
}
