package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

/**
 * KameHouse command to lock the screen.
 *
 * @author nbrest
 */
public class ScreenLockKameHouseCommand extends KameHouseShellScript {

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
    return "win/screen/screen-lock.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/screen/screen-lock.sh";
  }
}
