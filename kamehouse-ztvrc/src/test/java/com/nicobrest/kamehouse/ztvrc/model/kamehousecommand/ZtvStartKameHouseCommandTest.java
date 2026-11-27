package com.nicobrest.kamehouse.ztvrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class ZtvStartKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new ZtvStartKameHouseCommand("file.mkv");
  }

  @Override
  protected String getWindowsShellCommand() {
    return "ztv/ztv-start.sh -f file.mkv";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "ztv/ztv-start.sh -f file.mkv";
  }
}
