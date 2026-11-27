package com.nicobrest.kamehouse.ztvrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

/**
 * KameHouse command to stop a ZTV Player.
 *
 * @author nbrest
 */
public class ZtvStopKameHouseCommand extends KameHouseShellScript {

  /**
   * Sets the command line for each operation required for this KameHouseCommand.
   */
  public ZtvStopKameHouseCommand(int sleepTime) {
    super();
    setSleepTime(sleepTime);
  }

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
    return "ztv/ztv-stop.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "ztv/ztv-stop.sh";
  }
}
