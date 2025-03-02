package com.nicobrest.kamehouse.media.video.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import java.util.List;

/**
 * System command to get a playlist content. This is used when running on a docker container to get
 * the playlist contents from the host.
 *
 * @author nbrest
 */
public class GetPlaylistContentSystemCommand extends KameHouseShellSystemCommand {

  String playlistFilename = null;

  public GetPlaylistContentSystemCommand(String playlistFilename) {
    this.playlistFilename = playlistFilename;
  }

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "kamehouse/get-video-playlist-content.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    return List.of("-f", "'" + playlistFilename + "'");
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "kamehouse/get-video-playlist-content.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    if (StringUtils.isEmpty(playlistFilename)) {
      return null;
    }
    return "-f '" + playlistFilename.replace("\\", "/") + "'";
  }
}
