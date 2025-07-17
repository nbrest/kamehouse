package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

/**
 * KameHouse command to get the available memory of the server.
 *
 * @author nbrest
 */
public class FreeKameHouseCommand extends KameHouseShellScript {

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
    return "win/sysadmin/free.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/sysadmin/free.sh";
  }
}
