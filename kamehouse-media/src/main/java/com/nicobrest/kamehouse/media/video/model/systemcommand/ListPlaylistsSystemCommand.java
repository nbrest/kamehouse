package com.nicobrest.kamehouse.media.video.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import java.util.List;

/**
 * System command to list all the playlists in the server. This is used when running on a docker
 * container to get the playlists from the host.
 *
 * @author nbrest
 */
public class ListPlaylistsSystemCommand extends SystemCommand {

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "kamehouse/list-video-playlists.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    return null;
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "kamehouse/list-video-playlists.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return null;
  }
}
