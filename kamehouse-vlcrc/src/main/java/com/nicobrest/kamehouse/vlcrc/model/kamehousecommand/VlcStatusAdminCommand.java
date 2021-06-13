package com.nicobrest.kamehouse.vlcrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AdminCommand;
import com.nicobrest.kamehouse.vlcrc.model.systemcommand.VlcStatusSystemCommand;

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
