package com.nicobrest.kamehouse.vlcrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.vlcrc.model.systemcommand.VlcStatusSystemCommand;

/**
 * KameHouseSystemCommand to get the status of vlc player.
 *
 * @author nbrest
 */
public class VlcStatusKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Sets the required SystemCommands to achieve this KameHouseSystemCommand.
   */
  public VlcStatusKameHouseSystemCommand() {
    systemCommands.add(new VlcStatusSystemCommand());
  }
}
