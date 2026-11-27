package com.nicobrest.kamehouse.ztvrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class ZtvStatusKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new ZtvStatusKameHouseCommand();
  }

  @Override
  protected String getWindowsShellCommand() {
    return "ztv/ztv-status.sh";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "ztv/ztv-status.sh";
  }
}
