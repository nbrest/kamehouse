package com.nicobrest.kamehouse.vlcrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AdminCommand;
import com.nicobrest.kamehouse.vlcrc.model.systemcommand.VlcStopSystemCommand;

/**
 * AdminCommand to stop a vlc player.
 * 
 * @author nbrest
 *
 */
public class VlcStopAdminCommand extends AdminCommand {

  /**
   * Sets the required SystemCommands to achieve this AdminCommand.
   */
  public VlcStopAdminCommand() {
    systemCommands.add(new VlcStopSystemCommand(2));
  }
}
