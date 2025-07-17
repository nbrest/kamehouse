package com.nicobrest.kamehouse.media.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

/**
 * KameHouse command to list all the playlists in the server. This is used when running on a docker
 * container to get the playlists from the host.
 *
 * @author nbrest
 */
public class ListPlaylistsKameHouseCommand extends KameHouseShellScript {

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "kamehouse/media/list-playlists.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "kamehouse/media/list-playlists.sh";
  }
}
