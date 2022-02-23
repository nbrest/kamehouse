package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import java.util.Arrays;

/**
 * System command to reboot the server.
 *
 * @author nbrest
 */
public class RebootSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation system required for this SystemCommand.
   */
  public RebootSystemCommand() {
    executeOnDockerHost = true;
    addBashPrefix();
    linuxCommand.add("sudo reboot");
    addWindowsCmdStartPrefix();
    windowsCommand.addAll(Arrays.asList("shutdown", "/r", "/f", "/t", "0"));
    setOutputCommand();
  }
}
