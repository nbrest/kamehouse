package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class KameHouseDesktopStartupKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new KameHouseDesktopStartupKameHouseCommand();
  }

  @Override
  protected String getWindowsShellCommand() {
    return "desktop/kamehouse-desktop-startup.sh";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "desktop/kamehouse-desktop-startup.sh";
  }
}
