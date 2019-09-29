package com.nicobrest.kamehouse.systemcommand.model;

import java.util.Arrays;

/**
 * System command to stop a vlc player.
 * 
 * @author nbrest
 *
 */
public class VlcStatusSystemCommand extends SystemCommand {

  /**
   * Default constructor.
   */
  public VlcStatusSystemCommand() {
    linuxCommand.addAll(Arrays.asList("/bin/bash", "-c",
        "ps aux | grep -e \"vlc\\|COMMAND\" | grep -v grep"));
    windowsCommand.addAll(Arrays.asList("tasklist", "/FI", "IMAGENAME eq vlc.exe"));
  }
}
