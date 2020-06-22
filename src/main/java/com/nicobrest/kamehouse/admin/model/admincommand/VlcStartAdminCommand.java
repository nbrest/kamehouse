package com.nicobrest.kamehouse.admin.model.admincommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.VlcStartSystemCommand;
import com.nicobrest.kamehouse.admin.model.systemcommand.VlcStopSystemCommand;

/**
 * AdminCommand to start a vlc player with an optional file to play.
 * 
 * @author nbrest
 *
 */
public class VlcStartAdminCommand extends AdminCommand {

  /**
   * Sets the required SystemCommands to achieve this AdminCommand.
   */
  public VlcStartAdminCommand(String fileToPlay) {
    systemCommands.add(new VlcStopSystemCommand(1));
    systemCommands.add(new VlcStartSystemCommand(fileToPlay));
  }
}
