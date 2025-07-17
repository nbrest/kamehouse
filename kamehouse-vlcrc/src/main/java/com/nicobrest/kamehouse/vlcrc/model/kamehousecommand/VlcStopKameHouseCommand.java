package com.nicobrest.kamehouse.vlcrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

/**
 * KameHouse command to stop a vlc player.
 *
 * @author nbrest
 */
public class VlcStopKameHouseCommand extends KameHouseShellScript {

  /**
   * Sets the command line for each operation required for this KameHouseCommand.
   */
  public VlcStopKameHouseCommand(int sleepTime) {
    super();
    setSleepTime(sleepTime);
  }

  @Override
  public boolean hasSensitiveInformation() {
    return false;
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
  protected String getLinuxKameHouseShellScript() {
    return "lin/vlc/vlc-stop.sh";
  }
}
