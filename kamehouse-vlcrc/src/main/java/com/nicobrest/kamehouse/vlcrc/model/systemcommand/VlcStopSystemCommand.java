package com.nicobrest.kamehouse.vlcrc.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import java.util.List;

/**
 * System command to stop a vlc player.
 *
 * @author nbrest
 */
public class VlcStopSystemCommand extends KameHouseShellSystemCommand {

  /**
   * Sets the command line for each operation required for this SystemCommand.
   */
  public VlcStopSystemCommand(int sleepTime) {
    super();
    setSleepTime(sleepTime);
  }

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "win/vlc/vlc-stop.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    return null;
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/vlc/vlc-stop.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return null;
  }
}
