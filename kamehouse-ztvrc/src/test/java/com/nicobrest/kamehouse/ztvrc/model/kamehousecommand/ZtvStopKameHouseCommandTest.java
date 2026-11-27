package com.nicobrest.kamehouse.ztvrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class ZtvStopKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new ZtvStopKameHouseCommand(0);
  }

  @Override
  protected String getWindowsShellCommand() {
    return "ztv/ztv-stop.sh";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "ztv/ztv-stop.sh";
  }
}
