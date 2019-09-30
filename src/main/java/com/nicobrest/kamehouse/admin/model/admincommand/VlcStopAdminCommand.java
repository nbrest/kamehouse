package com.nicobrest.kamehouse.admin.model.admincommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.VlcStopSystemCommand;

/**
 * AdminCommand to stop a vlc player.
 * 
 * @author nbrest
 *
 */
public class VlcStopAdminCommand extends AdminCommand {

  public VlcStopAdminCommand() {
    systemCommands.add(new VlcStopSystemCommand());
  }
}
