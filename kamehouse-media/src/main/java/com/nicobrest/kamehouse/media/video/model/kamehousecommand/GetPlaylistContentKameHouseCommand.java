package com.nicobrest.kamehouse.media.video.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
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

  /**
   * Set playlist filename to load the content from.
   */
  public GetPlaylistContentKameHouseCommand(String playlistFilename) {
    super();
    validateFilename(playlistFilename);
    this.playlistFilename = playlistFilename.replaceAll("\\\\", "/");
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
    return "-f " + playlistFilename;
  }

  /**
   * Validate filename parameter.
   */
  private void validateFilename(String filename) {
    if (StringUtils.isEmpty(filename)) {
      throw new KameHouseInvalidCommandException("No file specified");
    }
    if (FileUtils.isRemoteFile(filename) || DockerUtils.shouldExecuteOnDockerHost(
        executeOnDockerHost())) {
      validateRemoteFile(filename);
    } else {
      if (!FileUtils.isValidLocalFile(filename)) {
        throw new KameHouseInvalidCommandException(
            "File doesn't exist on the server: " + filename);
      }
    }
  }

  /**
   * Throw an exception if the filename contains unauthorized characters that could allow remote
   * code execution.
   */
  private static void validateRemoteFile(String filename) {
    if (StringUtils.isEmpty(filename)) {
      throw new KameHouseInvalidCommandException("Empty file provided to vlc start");
    }
  }
}
