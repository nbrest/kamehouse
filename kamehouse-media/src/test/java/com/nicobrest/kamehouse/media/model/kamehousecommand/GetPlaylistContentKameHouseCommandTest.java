package com.nicobrest.kamehouse.media.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class GetPlaylistContentKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new GetPlaylistContentKameHouseCommand("playlist.m3u");
  }

  @Override
  protected String getWindowsShellCommand() {
    return "kamehouse/media/get-playlist-content.sh -f playlist.m3u";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "kamehouse/media/get-playlist-content.sh -f playlist.m3u";
  }
}
