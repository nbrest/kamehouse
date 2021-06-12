package com.nicobrest.kamehouse.admin.model.admincommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.VlcStatusSystemCommand;

/**
 * AdminCommand to get the status of vlc player.
 * 
 * @author nbrest
 *
 */
public class VlcStatusAdminCommand extends AdminCommand {

  /**
   * Sets the required SystemCommands to achieve this AdminCommand.
   */
  public VlcStatusAdminCommand() {
    systemCommands.add(new VlcStatusSystemCommand());
  }
}
