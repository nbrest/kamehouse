package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

/**
 * KameHouse command to suspend the server.
 *
 * @author nbrest
 */
public class SuspendKameHouseCommand extends KameHouseShellScript {

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
    return "win/shutdown/suspend.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/shutdown/suspend.sh";
  }
}
