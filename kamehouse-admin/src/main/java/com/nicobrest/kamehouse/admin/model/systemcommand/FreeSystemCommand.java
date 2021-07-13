package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;

import java.util.Arrays;

/**
 * System command to get the available memory of the server.
 * 
 * @author nbrest
 *
 */
public class FreeSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation system required for this SystemCommand.
   */
  public FreeSystemCommand() {
    linuxCommand.addAll(Arrays.asList("/bin/bash", "-c", "free -h"));
    windowsCommand.addAll(Arrays.asList("powershell.exe", "-c", "systeminfo | Select-String "
        + "-Pattern Memory"));
    setOutputCommand();
  }
}
