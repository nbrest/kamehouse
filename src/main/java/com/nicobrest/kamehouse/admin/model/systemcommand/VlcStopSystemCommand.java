package com.nicobrest.kamehouse.admin.model.systemcommand;

import java.util.Arrays;

/**
 * System command to stop a vlc player.
 * 
 * @author nbrest
 *
 */
public class VlcStopSystemCommand extends SystemCommand {

  /**
   * Set the command line for each operation system required for this SystemCommand.
   */
  public VlcStopSystemCommand() {
    linuxCommand.addAll(Arrays.asList("skill", "-9", "vlc"));
    windowsCommand.addAll(Arrays.asList("cmd.exe", "/c", "start", "taskkill", "/im", "vlc.exe"));
  }
}
