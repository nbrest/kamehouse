package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

/**
 * KameHouse command to get the available disk space of the server.
 *
 * @author nbrest
 */
public class DfKameHouseCommand extends KameHouseShellScript {

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
    return "sysadmin/df.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "sysadmin/df.sh";
  }
}
