package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.systemcommand.model.VlcStartSystemCommand;
import com.nicobrest.kamehouse.systemcommand.model.VlcStopSystemCommand;

/**
 * AdminCommand to start a vlc player with an optional file to play.
 * 
 * @author nbrest
 *
 */
public class VlcStartAdminCommand extends AdminCommand {

  public VlcStartAdminCommand(String fileToPlay) {
    systemCommands.add(new VlcStopSystemCommand());
    systemCommands.add(new VlcStartSystemCommand(fileToPlay));
  }
}
