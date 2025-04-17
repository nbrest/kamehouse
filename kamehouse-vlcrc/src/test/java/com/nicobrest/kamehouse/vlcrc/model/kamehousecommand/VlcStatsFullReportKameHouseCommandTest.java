package com.nicobrest.kamehouse.vlcrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class VlcStatsFullReportKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new VlcStatsFullReportKameHouseCommand(false);
  }

  @Override
  protected String getWindowsShellCommand() {
    return "common/vlc/vlc-stats-full-report.sh --show-history-file-only";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "common/vlc/vlc-stats-full-report.sh --show-history-file-only";
  }
}
