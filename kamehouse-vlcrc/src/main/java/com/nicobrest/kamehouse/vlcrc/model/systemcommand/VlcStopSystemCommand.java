package com.nicobrest.kamehouse.vlcrc.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import java.util.Arrays;

/**
 * System command to stop a vlc player.
 *
 * @author nbrest
 */
public class VlcStopSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation system required for this SystemCommand.
   */
  public VlcStopSystemCommand(int sleepTime) {
    super();
    this.sleepTime = sleepTime;
    executeOnDockerHost = true;
    //TODO move this script to kamehouse-shell and call it
    //Get kamehouse-shell scripts home from properties
    // add a property in PropertiesUtils KAMEHOUSE_SHELL_BASE_PATH = ${HOME}/my.scripts
    // then this would be KAMEHOUSE_SHELL_BASE_PATH + "/lin/xxx/vlc-stop.sh"
    String killVlcScript =
        "KILL_VLC_PID=`ps aux | grep vlc "
            + "| grep -v grep "
            + "| grep -v VlcProcessController "
            + "| grep -v surefire "
            + "| grep -v failsafe\\:integration-test "
            + "| grep -v build-java-web-kamehouse\\.sh "
            + "| awk '{print $2}'` ; "
            + "[ ! -z \"$KILL_VLC_PID\" ] && kill -9 ${KILL_VLC_PID}  "
            + "|| echo \"vlc not running\"";
    linuxCommand.addAll(Arrays.asList("/bin/bash", "-c", killVlcScript));
    if (DockerUtils.shouldExecuteOnDockerHost(executeOnDockerHost)) {
      windowsCommand.addAll(Arrays.asList("taskkill", "/im", "vlc.exe", "/f"));
    } else {
      windowsCommand.addAll(Arrays.asList("cmd.exe", "/c", "start", "taskkill", "/im", "vlc.exe"));
    }
    setOutputCommand();
  }
}
