package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.systemcommand.model.SuspendSystemCommand;

/**
 * AdminCommand to suspend the server.
 * 
 * @author nbrest
 *
 */
public class SuspendAdminCommand extends AdminCommand {

  public SuspendAdminCommand() {
    systemCommands.add(new SuspendSystemCommand());
  }
}
