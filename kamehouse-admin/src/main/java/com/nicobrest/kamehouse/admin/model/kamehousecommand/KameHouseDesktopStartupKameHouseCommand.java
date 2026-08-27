package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

/**
 * KameHouse command to start kamehouse desktop.
 *
 * @author nbrest
 */
public class KameHouseDesktopStartupKameHouseCommand extends KameHouseShellScript {

  /**
   * Sets the command line for each operation required for this KameHouseCommand.
   */
  public KameHouseDesktopStartupKameHouseCommand() {
    super();
  }

  @Override
  public boolean isDaemon() {
    return true;
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
    return "desktop/kamehouse-desktop-startup.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "desktop/kamehouse-desktop-startup.sh";
  }
}
