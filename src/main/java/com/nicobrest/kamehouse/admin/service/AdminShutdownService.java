package com.nicobrest.kamehouse.admin.service;

import com.nicobrest.kamehouse.admin.model.AdminShutdownCommand;
import com.nicobrest.kamehouse.main.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommand;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;
import com.nicobrest.kamehouse.systemcommand.service.SystemCommandService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to execute shutdown commands.
 * 
 * @author nbrest
 *
 */
@Service
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
    if (!AdminShutdownCommand.SET.equals(adminShutdownCommand.getCommand())) {
      throw new KameHouseInvalidCommandException("Invalid AdminShutdownCommand "
          + adminShutdownCommand.getCommand());
    }
    List<SystemCommand> systemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);
    List<SystemCommandOutput> systemCommandOutputs = systemCommandService.execute(systemCommands);
    return systemCommandOutputs;
  }

  /**
   * Cancel an active shutdown.
   */
  public List<SystemCommandOutput> cancelShutdown() {
    AdminShutdownCommand adminShutdownCommand = new AdminShutdownCommand();
    adminShutdownCommand.setCommand(AdminShutdownCommand.CANCEL);
    List<SystemCommand> systemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);
    List<SystemCommandOutput> systemCommandOutputs = systemCommandService.execute(systemCommands);
    return systemCommandOutputs;
  }

  /**
   * Get the status of an active shutdown.
   */
  public List<SystemCommandOutput> statusShutdown() {
    AdminShutdownCommand adminShutdownCommand = new AdminShutdownCommand();
    adminShutdownCommand.setCommand(AdminShutdownCommand.STATUS);
    List<SystemCommand> systemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);
    List<SystemCommandOutput> systemCommandOutputs = systemCommandService.execute(systemCommands);
    return systemCommandOutputs;
  }
}
