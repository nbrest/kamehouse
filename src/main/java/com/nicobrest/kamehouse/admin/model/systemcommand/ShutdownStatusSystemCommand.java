package com.nicobrest.kamehouse.admin.model.systemcommand;

import java.util.Arrays;

/**
 * System command to get the status of a scheduled shutdown of the server.
 * 
 * @author nbrest
 *
 */
public class ShutdownStatusSystemCommand extends SystemCommand {

  /**
   * Default constructor.
   */
  public ShutdownStatusSystemCommand() {
    linuxCommand.addAll(Arrays.asList("/bin/bash", "-c",
        "ps aux | grep -e \"shutdown\\|COMMAND\" | grep -v grep"));
    windowsCommand.addAll(Arrays.asList("tasklist", "/FI", "IMAGENAME eq shutdown.exe"));
  }
}
