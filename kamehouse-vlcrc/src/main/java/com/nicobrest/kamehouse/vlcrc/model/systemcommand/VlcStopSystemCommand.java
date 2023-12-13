package com.nicobrest.kamehouse.vlcrc.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import java.util.Arrays;
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
  protected boolean isSudo() {
    return false;
  }

  @Override
  protected List<String> getWindowsCommand() {
    return Arrays.asList("taskkill", "/im", "vlc.exe", "/f");
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/kamehouse/vlc-stop.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return null;
  }
}
