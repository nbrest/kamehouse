package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

/**
 * KameHouse command to stop kamehouse desktop.
 *
 * @author nbrest
 */
public class KameHouseDesktopStopKameHouseCommand extends KameHouseShellScript {

  /**
   * Sets the command line for each operation required for this KameHouseCommand.
   */
  public KameHouseDesktopStopKameHouseCommand() {
    super();
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
    return "desktop/kamehouse-desktop-stop.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "desktop/kamehouse-desktop-stop.sh";
  }
}
