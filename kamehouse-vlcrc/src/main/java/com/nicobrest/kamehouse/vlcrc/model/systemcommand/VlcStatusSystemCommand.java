package com.nicobrest.kamehouse.vlcrc.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import java.util.Arrays;

/**
 * System command to stop a vlc player.
 *
 * @author nbrest
 */
public class VlcStatusSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation system required for this SystemCommand.
   */
  public VlcStatusSystemCommand() {
    executeOnDockerHost = true;
    addBashPrefix();
    linuxCommand.add("ps aux | grep -e \"vlc\\|COMMAND\" | grep -v grep");
    windowsCommand.addAll(Arrays.asList("tasklist", "/FI", "IMAGENAME eq vlc.exe"));
    setOutputCommand();
  }
}
