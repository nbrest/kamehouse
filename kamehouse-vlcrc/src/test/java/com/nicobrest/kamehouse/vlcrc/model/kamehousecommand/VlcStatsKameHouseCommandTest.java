package com.nicobrest.kamehouse.vlcrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class VlcStatsKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new VlcStatsKameHouseCommand(false);
  }

  @Override
  protected String getWindowsShellCommand() {
    return "common/vlc/vlc-stats.sh -n 2 --show-history-file-only";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "common/vlc/vlc-stats.sh -n 8 --show-history-file-only";
  }
}
