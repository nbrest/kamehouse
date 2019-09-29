package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.systemcommand.model.VlcStopSystemCommand;

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
