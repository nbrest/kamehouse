package com.nicobrest.kamehouse.ztvrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class ZtvStatsFullReportKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new ZtvStatsFullReportKameHouseCommand(false);
  }

  @Override
  protected String getWindowsShellCommand() {
    return "ztv/ztv-stats-full-report.sh --show-history-file-only";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "ztv/ztv-stats-full-report.sh --show-history-file-only";
  }
}
