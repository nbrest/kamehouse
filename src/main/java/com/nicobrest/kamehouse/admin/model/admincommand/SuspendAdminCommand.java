package com.nicobrest.kamehouse.admin.model.admincommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.SuspendSystemCommand;

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
