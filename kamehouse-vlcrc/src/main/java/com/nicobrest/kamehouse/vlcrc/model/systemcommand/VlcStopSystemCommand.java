package com.nicobrest.kamehouse.vlcrc.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;

import java.util.Arrays;

/**
 * System command to stop a vlc player.
 * 
 * @author nbrest
 *
 */
public class VlcStopSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation system required for this SystemCommand.
   */
  public VlcStopSystemCommand(int sleepTime) {
    super();
    String killVlcScript = "KILL_VLC_PID=`ps aux | grep vlc | grep -v grep | awk '{print $2}'` ;"
        + " [ ! -z \"$KILL_VLC_PID\" ] && kill -9 ${KILL_VLC_PID}  || echo \"vlc not running\"";
    linuxCommand.addAll(Arrays.asList("/bin/bash", "-c", killVlcScript));
    windowsCommand.addAll(Arrays.asList("cmd.exe", "/c", "start", "taskkill", "/im", "vlc.exe"));
    setOutputCommand();
    this.sleepTime = sleepTime;
  }
}
