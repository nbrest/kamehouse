package com.nicobrest.kamehouse.systemcommand.model;

import java.util.Arrays;

/**
 * System command to stop a vlc player.
 * 
 * @author nbrest
 *
 */
public class VlcStopSystemCommand extends SystemCommand {

  public VlcStopSystemCommand() {
    linuxCommand.addAll(Arrays.asList("skill", "-9", "vlc"));
    windowsCommand.addAll(Arrays.asList("cmd.exe", "/c", "start", "taskkill", "/im", "vlc.exe"));
  }
}
