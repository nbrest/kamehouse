package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

/**
 * KameHouse command to cancel a scheduled shutdown of the server.
 *
 * @author nbrest
 */
public class ShutdownCancelKameHouseCommand extends KameHouseShellScript {

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
    return "shutdown/shutdown-cancel.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "shutdown/shutdown-cancel.sh";
  }
}
