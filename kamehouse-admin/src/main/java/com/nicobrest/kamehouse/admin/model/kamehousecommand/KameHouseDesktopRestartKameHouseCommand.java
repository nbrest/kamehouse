package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

/**
 * KameHouse command to restart kamehouse desktop.
 *
 * @author nbrest
 */
public class KameHouseDesktopRestartKameHouseCommand extends KameHouseShellScript {

  /**
   * Sets the command line for each operation required for this KameHouseCommand.
   */
  public KameHouseDesktopRestartKameHouseCommand() {
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
    return "desktop/kamehouse-desktop-restart.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "desktop/kamehouse-desktop-restart.sh";
  }
}
