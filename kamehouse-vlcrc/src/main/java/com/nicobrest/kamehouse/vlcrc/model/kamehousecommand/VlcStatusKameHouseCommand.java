package com.nicobrest.kamehouse.vlcrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

/**
 * KameHouse command to check the status of vlc player process.
 *
 * @author nbrest
 */
public class VlcStatusKameHouseCommand extends KameHouseShellScript {

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "win/vlc/vlc-status.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/vlc/vlc-status.sh";
  }
}
