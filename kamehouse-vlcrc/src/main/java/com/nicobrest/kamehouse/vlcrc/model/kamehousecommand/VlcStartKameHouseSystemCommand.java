package com.nicobrest.kamehouse.vlcrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.vlcrc.model.systemcommand.VlcStartSystemCommand;
import com.nicobrest.kamehouse.vlcrc.model.systemcommand.VlcStopSystemCommand;

/**
 * KameHouseSystemCommand to start a vlc player with an optional file to play.
 *
 * @author nbrest
 */
public class VlcStartKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public VlcStartKameHouseSystemCommand(String fileToPlay) {
    systemCommands.add(new VlcStopSystemCommand(2));
    systemCommands.add(new VlcStartSystemCommand(fileToPlay));
  }
}
