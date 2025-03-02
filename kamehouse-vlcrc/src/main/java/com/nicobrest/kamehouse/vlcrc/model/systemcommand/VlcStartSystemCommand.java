package com.nicobrest.kamehouse.vlcrc.model.systemcommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import java.util.List;

/**
 * System command to start a vlc player with an optional file (or playlist) to play.
 *
 * @author nbrest
 */
public class VlcStartSystemCommand extends KameHouseShellSystemCommand {

  private String filename = null;

  /**
   * Sets the command line for each operation required for this SystemCommand.
   */
  public VlcStartSystemCommand(String filename) {
    super();
    sshTimeout = 3000L;
    this.filename = filename;
    validateFilename(filename);
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
    List<String> args = List.of("-f", "'" + filename + "'");
    if (DockerUtils.shouldExecuteOnDockerHost(executeOnDockerHost)) {
      args.add("--start-from-ssh");
    }
    return args;
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/vlc/vlc-start.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return "-f " + "'" + filename + "'";
  }

  /**
   * Validate filename parameter.
   */
  private void validateFilename(String filename) {
    if (FileUtils.isRemoteFile(filename) || DockerUtils.shouldExecuteOnDockerHost(
        executeOnDockerHost)) {
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
    if (filename == null) {
      throw new KameHouseInvalidCommandException("Empty file");
    }
  }
}
