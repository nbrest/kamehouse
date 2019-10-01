package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.main.exception.KameHouseInvalidCommandException;

import java.io.File;
import java.util.Arrays;

/**
 * System command to start a vlc player with an optional file to play.
 * 
 * @author nbrest
 *
 */
public class VlcStartSystemCommand extends SystemCommand {

  /**
   * Set the command line for each operation system required for this SystemCommand.
   */
  public VlcStartSystemCommand(String filename) {
    isDaemon = true;
    linuxCommand.addAll(Arrays.asList("vlc"));
    windowsCommand.addAll(Arrays.asList("cmd.exe", "/c", "start", "vlc"));    
    if (filename != null) {
      File fileToPlay = new File(filename);
      if (!fileToPlay.exists()) {
        throw new KameHouseInvalidCommandException("File to play doesn't exist on the server: "
            + filename);
      }
      linuxCommand.add(filename);
      windowsCommand.add(filename);
    }
  }
}
