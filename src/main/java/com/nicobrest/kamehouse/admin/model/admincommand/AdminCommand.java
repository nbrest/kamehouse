package com.nicobrest.kamehouse.admin.model.admincommand;

import com.nicobrest.kamehouse.admin.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.main.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for Admin Commands in the application. Admin commands are
 * translated to one or more System Commands specific to the operating system
 * running the application to be executed through the command line. These System
 * Commands together, executed one after the other, achieve the goal of the
 * Admin Command.
 * 
 * @author nbrest
 *
 */
public abstract class AdminCommand {

  protected List<SystemCommand> systemCommands = new ArrayList<>();

  /**
   * Gets the list of system commands required to execute to perform this admin
   * command.
   */
  public List<SystemCommand> getSystemCommands() {
    return systemCommands;
  }
  
  @Override
  public String toString() {
    String[] hiddenFields = { "systemCommands" };
    return JsonUtils.toJsonString(this, super.toString(), hiddenFields);
  }
}
