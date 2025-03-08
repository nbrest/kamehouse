package com.nicobrest.kamehouse.media.video.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class ListPlaylistsKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new ListPlaylistsKameHouseCommand();
  }

  @Override
  protected String getWindowsShellCommand() {
    return "kamehouse/list-video-playlists.sh";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "kamehouse/list-video-playlists.sh";
  }
}
