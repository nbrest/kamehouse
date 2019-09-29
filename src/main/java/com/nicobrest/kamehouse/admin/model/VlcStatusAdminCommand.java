package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.systemcommand.model.VlcStatusSystemCommand;

/**
 * AdminCommand to get the status of vlc player.
 * 
 * @author nbrest
 *
 */
public class VlcStatusAdminCommand extends AdminCommand {

  public VlcStatusAdminCommand() {
    systemCommands.add(new VlcStatusSystemCommand());
  }
}
