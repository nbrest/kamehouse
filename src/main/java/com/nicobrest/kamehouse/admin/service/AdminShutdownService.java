package com.nicobrest.kamehouse.admin.service;

import com.nicobrest.kamehouse.admin.model.AdminShutdownCommand;
import com.nicobrest.kamehouse.main.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommand;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;
import com.nicobrest.kamehouse.systemcommand.service.SystemCommandService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class AdminShutdownService {

  @Autowired
  private SystemCommandService systemCommandService;

  public SystemCommandService getSystemCommandService() {
    return systemCommandService;
  }

  public void setSystemCommandService(SystemCommandService systemCommandService) {
    this.systemCommandService = systemCommandService;
  }

  /**
   * Set the system to shutdown at the specified time.
   */
  public List<SystemCommandOutput> setShutdown(AdminShutdownCommand adminShutdownCommand) {
    if (!"set".equals(adminShutdownCommand.getCommand())) {
      throw new KameHouseInvalidCommandException("Invalid AdminShutdownCommand "
          + adminShutdownCommand.getCommand());
    }
    List<SystemCommand> systemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);
    List<SystemCommandOutput> systemCommandOutputs = new ArrayList<SystemCommandOutput>();
    for (SystemCommand systemCommand : systemCommands) {
      SystemCommandOutput systemCommandOutput = systemCommandService.execute(systemCommand);
      systemCommandOutputs.add(systemCommandOutput);
    }
    return systemCommandOutputs;
  }

  /**
   * Cancel an active shutdown.
   */
  public List<SystemCommandOutput> cancelShutdown() {
    AdminShutdownCommand adminShutdownCommand = new AdminShutdownCommand();
    adminShutdownCommand.setCommand("cancel");
    List<SystemCommand> systemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);
    List<SystemCommandOutput> systemCommandOutputs = new ArrayList<SystemCommandOutput>();
    for (SystemCommand systemCommand : systemCommands) {
      SystemCommandOutput systemCommandOutput = systemCommandService.execute(systemCommand);
      systemCommandOutputs.add(systemCommandOutput);
    }
    return systemCommandOutputs;
  }

  /**
   * Get the status of an active shutdown.
   */
  public List<SystemCommandOutput> statusShutdown() {
    AdminShutdownCommand adminShutdownCommand = new AdminShutdownCommand();
    adminShutdownCommand.setCommand("status");
    List<SystemCommand> systemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);
    List<SystemCommandOutput> systemCommandOutputs = new ArrayList<SystemCommandOutput>();
    for (SystemCommand systemCommand : systemCommands) {
      SystemCommandOutput systemCommandOutput = systemCommandService.execute(systemCommand);
      systemCommandOutputs.add(systemCommandOutput);
    }
    return systemCommandOutputs;
  }
}
