package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

/**
 * KameHouse command to reboot the server.
 *
 * @author nbrest
 */
public class RebootKameHouseCommand extends KameHouseShellScript {

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "win/shutdown/reboot.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/shutdown/reboot.sh";
  }
}
