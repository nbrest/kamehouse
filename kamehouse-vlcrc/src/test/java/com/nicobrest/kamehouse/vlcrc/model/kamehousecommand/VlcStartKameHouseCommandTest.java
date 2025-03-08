package com.nicobrest.kamehouse.vlcrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class VlcStartKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new VlcStartKameHouseCommand("file.mkv");
  }

  @Override
  protected String getWindowsShellCommand() {
    return "win/vlc/vlc-start.sh -f file.mkv";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "lin/vlc/vlc-start.sh -f file.mkv";
  }
}
