package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.systemcommand.model.ShutdownStatusSystemCommand;

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
