package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import java.util.Arrays;

/**
 * System command to suspend the server.
 *
 * @author nbrest
 */
public class SuspendSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation system required for this SystemCommand.
   */
  public SuspendSystemCommand() {
    executeOnDockerHost = true;
    addBashPrefix();
    linuxCommand.add("sudo /bin/systemctl suspend -i");
    addWindowsCmdStartPrefix();
    windowsCommand.addAll(Arrays.asList("rundll32.exe", "powrprof.dll,SetSuspendState", "0,1,0"));
    setOutputCommand();
  }
}
