package com.nicobrest.kamehouse.systemcommand.model;

import java.util.Arrays;

/**
 * System command to suspend the server.
 * 
 * @author nbrest
 *
 */
public class SuspendSystemCommand extends SystemCommand {

  /**
   * Default constructor.
   */
  public SuspendSystemCommand() {
    linuxCommand.addAll(Arrays.asList("/bin/bash", "-c", "sudo /bin/systemctl suspend -i"));
    windowsCommand.addAll(Arrays.asList("cmd.exe", "/c", "start", "rundll32.exe",
        "powrprof.dll,SetSuspendState", "0,1,0"));
  }
}
