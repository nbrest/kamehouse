package com.nicobrest.kamehouse.media.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;
import java.util.List;

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
    return "kamehouse/list-playlists.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    return null;
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "kamehouse/list-playlists.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return null;
  }
}
