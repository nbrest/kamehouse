package com.nicobrest.kamehouse.admin.model.admincommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.VlcStopSystemCommand;

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
