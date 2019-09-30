package com.nicobrest.kamehouse.admin.model.admincommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.ShutdownStatusSystemCommand;

/**
 * AdminCommand to get the status of a scheduled shutdown of the server.
 * 
 * @author nbrest
 *
 */
public class ShutdownStatusAdminCommand extends AdminCommand {

  public ShutdownStatusAdminCommand() {
    systemCommands.add(new ShutdownStatusSystemCommand());
  }
}
