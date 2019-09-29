package com.nicobrest.kamehouse.systemcommand.model;

import java.util.Arrays;

/**
 * System command to lock the screen.
 * 
 * @author nbrest
 *
 */
public class ScreenLockSystemCommand extends SystemCommand {

  /**
   * Default constructor.
   */
  public ScreenLockSystemCommand() {
    linuxCommand.addAll(Arrays.asList("/bin/bash", "-c", "gnome-screensaver-command -l"));
    windowsCommand.addAll(Arrays.asList("cmd.exe", "/c", "start", "rundll32.exe",
        "user32.dll,LockWorkStation"));
  }
}
