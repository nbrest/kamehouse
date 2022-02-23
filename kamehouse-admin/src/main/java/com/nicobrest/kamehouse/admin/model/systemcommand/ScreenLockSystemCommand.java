package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import java.util.Arrays;

/**
 * System command to lock the screen.
 *
 * @author nbrest
 */
public class ScreenLockSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation system required for this SystemCommand.
   */
  public ScreenLockSystemCommand() {
    executeOnDockerHost = true;
    addBashPrefix();
    linuxCommand.add("DISPLAY=:0.0 gnome-screensaver-command -l");
    addWindowsCmdStartPrefix();
    windowsCommand.addAll(Arrays.asList("rundll32.exe", "user32.dll,LockWorkStation"));
    setOutputCommand();
  }
}
