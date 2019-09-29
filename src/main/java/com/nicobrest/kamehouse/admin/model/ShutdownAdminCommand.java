package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.systemcommand.model.ShutdownSystemCommand;

/**
 * AdminCommand to shutdown the server.
 * 
 * @author nbrest
 *
 */
public class ShutdownAdminCommand extends AdminCommand {

  public ShutdownAdminCommand(int shutdownDelaySeconds) {
    systemCommands.add(new ShutdownSystemCommand(shutdownDelaySeconds));
  }
}
