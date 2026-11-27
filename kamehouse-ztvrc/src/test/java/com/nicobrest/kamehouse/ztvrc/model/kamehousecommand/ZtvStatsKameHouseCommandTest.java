package com.nicobrest.kamehouse.ztvrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class ZtvStatsKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new ZtvStatsKameHouseCommand(false);
  }

  @Override
  protected String getWindowsShellCommand() {
    return "ztv/ztv-stats.sh -n 2 --show-history-file-only";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "ztv/ztv-stats.sh -n 8 --show-history-file-only";
  }
}
