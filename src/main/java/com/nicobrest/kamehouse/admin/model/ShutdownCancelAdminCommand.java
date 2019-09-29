package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.systemcommand.model.ShutdownCancelSystemCommand;

/**
 * AdminCommand to cancel a scheduled shutdown of the server.
 * 
 * @author nbrest
 *
 */
public class ShutdownCancelAdminCommand extends AdminCommand {

  public ShutdownCancelAdminCommand() {
    systemCommands.add(new ShutdownCancelSystemCommand());
  }
}
