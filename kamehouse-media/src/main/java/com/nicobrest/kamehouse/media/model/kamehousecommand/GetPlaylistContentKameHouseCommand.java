package com.nicobrest.kamehouse.media.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.commons.validator.InputValidator;
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

  /**
   * Set playlist filename to load the content from.
   */
  public GetPlaylistContentKameHouseCommand(String playlistFilename) {
    super();
    InputValidator.validateForbiddenCharsForShell(playlistFilename);
    this.playlistFilename = playlistFilename.replace("\\", "/");
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
    return "kamehouse/media/get-playlist-content.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    return List.of("-f", playlistFilename);
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "kamehouse/media/get-playlist-content.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    if (StringUtils.isEmpty(playlistFilename)) {
      return null;
    }
    return "-f " + playlistFilename;
  }
}
