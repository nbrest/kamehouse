package com.nicobrest.kamehouse.vlcrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import java.util.List;

/**
 * KameHouse command to start a vlc player with an optional file (or playlist) to play.
 *
 * @author nbrest
 */
public class VlcStartKameHouseCommand extends KameHouseShellScript {

  private String filename = null;

  /**
   * Sets the command line for each operation required for this KameHouseCommand.
   */
  public VlcStartKameHouseCommand(String filename) {
    super();
    validateFilename(filename);
    this.filename = filename.replaceAll("\\\\", "/");
  }

  @Override
  public long getSshTimeout() {
    return 20000L;
  }

  @Override
  public boolean hasSensitiveInformation() {
    return false;
  }

  @Override
  public boolean isDaemon() {
    return true;
  }

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "win/vlc/vlc-start.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    return List.of("-f", filename);
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/vlc/vlc-start.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return "-f " + filename;
  }

  /**
   * Validate filename parameter.
   */
  private void validateFilename(String filename) {
    if (StringUtils.isEmpty(filename)) {
      throw new KameHouseInvalidCommandException("No file specified to play on vlc");
    }
    if (FileUtils.isRemoteFile(filename) || DockerUtils.shouldExecuteOnDockerHost(
        executeOnDockerHost())) {
      validateRemoteFile(filename);
    } else {
      if (!FileUtils.isValidLocalFile(filename)) {
        throw new KameHouseInvalidCommandException(
            "File to play doesn't exist on the server: " + filename);
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
