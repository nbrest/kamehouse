package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

/**
 * KameHouse command to check the uptime of the server.
 *
 * @author nbrest
 */
public class UptimeKameHouseCommand extends KameHouseShellScript {

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
    return "win/sysadmin/uptime.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/sysadmin/uptime.sh";
  }
}
