package com.nicobrest.kamehouse.vlcrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AdminCommand;
import com.nicobrest.kamehouse.vlcrc.model.systemcommand.VlcStartSystemCommand;
import com.nicobrest.kamehouse.vlcrc.model.systemcommand.VlcStopSystemCommand;

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
    systemCommands.add(new VlcStopSystemCommand(2));
    systemCommands.add(new VlcStartSystemCommand(fileToPlay));
  }
}
