package com.nicobrest.kamehouse.vlcrc.model.systemcommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import java.util.Arrays;

/**
 * System command to start a vlc player with an optional file (or playlist) to play.
 *
 * @author nbrest
 */
public class VlcStartSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation required for this SystemCommand.
   */
  public VlcStartSystemCommand(String filename) {
    isDaemon = true;
    linuxCommand.addAll(Arrays.asList("vlc"));
    windowsCommand.addAll(Arrays.asList("cmd.exe", "/c", "start", "vlc"));
    if (filename != null) {
      if (FileUtils.isRemoteFile(filename)) {
        validateRemoteFile(filename);
      } else {
        if (!FileUtils.isValidLocalFile(filename)) {
          throw new KameHouseInvalidCommandException(
              "File to play doesn't exist on the server: " + filename);
        }
      }
      linuxCommand.add(filename);
      windowsCommand.add(filename);
    }
    setOutputCommand();
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
