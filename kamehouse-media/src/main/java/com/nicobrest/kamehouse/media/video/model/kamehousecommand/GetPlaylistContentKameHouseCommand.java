package com.nicobrest.kamehouse.media.video.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import java.util.List;

/**
 * KameHouse command to get a playlist content. This is used when running on a docker container to
 * get the playlist contents from the host. When running natively, it loads the files directly on
 * the webapp from the filesystem without going through a kamehouse command.
 *
 * @author nbrest
 */
public class GetPlaylistContentKameHouseCommand extends KameHouseShellScript {

  private String playlistFilename = null;

  public GetPlaylistContentKameHouseCommand(String playlistFilename) {
    super();
    this.playlistFilename = playlistFilename;
  }

  @Override
  public boolean hasSensitiveInformation() {
    return false;
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
    return List.of("-f", playlistFilename);
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
    return "-f " + playlistFilename.replace("\\", "/");
  }
}
