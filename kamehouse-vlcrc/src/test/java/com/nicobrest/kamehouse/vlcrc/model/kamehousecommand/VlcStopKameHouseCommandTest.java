package com.nicobrest.kamehouse.vlcrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class VlcStopKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new VlcStopKameHouseCommand(0);
  }

  @Override
  protected String getWindowsShellCommand() {
    return "vlc/vlc-stop.sh";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "vlc/vlc-stop.sh";
  }
}
