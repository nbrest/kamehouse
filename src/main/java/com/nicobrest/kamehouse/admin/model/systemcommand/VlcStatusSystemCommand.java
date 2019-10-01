package com.nicobrest.kamehouse.admin.model.systemcommand;

import java.util.Arrays;

/**
 * System command to stop a vlc player.
 * 
 * @author nbrest
 *
 */
public class VlcStatusSystemCommand extends SystemCommand {

  /**
   * Set the command line for each operation system required for this SystemCommand.
   */
  public VlcStatusSystemCommand() {
    linuxCommand.addAll(Arrays.asList("/bin/bash", "-c",
        "ps aux | grep -e \"vlc\\|COMMAND\" | grep -v grep"));
    windowsCommand.addAll(Arrays.asList("tasklist", "/FI", "IMAGENAME eq vlc.exe"));
  }
}
