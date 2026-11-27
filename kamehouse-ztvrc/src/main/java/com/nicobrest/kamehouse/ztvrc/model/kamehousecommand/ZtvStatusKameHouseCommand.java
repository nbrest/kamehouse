package com.nicobrest.kamehouse.ztvrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

/**
 * KameHouse command to check the status of ZTV Player process.
 *
 * @author nbrest
 */
public class ZtvStatusKameHouseCommand extends KameHouseShellScript {

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "ztv/ztv-status.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "ztv/ztv-status.sh";
  }
}
