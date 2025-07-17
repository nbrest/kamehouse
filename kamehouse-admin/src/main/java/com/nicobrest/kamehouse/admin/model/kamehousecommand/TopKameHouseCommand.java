package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

/**
 * KameHouse commandto get the current state of the processes of the server.
 *
 * @author nbrest
 */
public class TopKameHouseCommand extends KameHouseShellScript {

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
    return "win/sysadmin/top.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/sysadmin/top.sh";
  }
}
