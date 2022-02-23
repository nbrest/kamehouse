package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import java.util.Arrays;

/**
 * System command to cancel a scheduled shutdown of the server.
 *
 * @author nbrest
 */
public class ShutdownCancelSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation system required for this SystemCommand.
   */
  public ShutdownCancelSystemCommand() {
    executeOnDockerHost = true;
    addBashPrefix();
    linuxCommand.add("sudo /sbin/shutdown -c");
    addWindowsCmdStartPrefix();
    windowsCommand.addAll(Arrays.asList("shutdown", "/a"));
    setOutputCommand();
  }
}
