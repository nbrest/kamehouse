package com.nicobrest.kamehouse.vlcrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.vlcrc.model.systemcommand.VlcStopSystemCommand;

/**
 * KameHouseSystemCommand to stop a vlc player.
 * 
 * @author nbrest
 *
 */
public class VlcStopKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public VlcStopKameHouseSystemCommand() {
    systemCommands.add(new VlcStopSystemCommand(2));
  }
}
