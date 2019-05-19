package com.nicobrest.kamehouse.admin.service;

import com.nicobrest.kamehouse.admin.model.AdminVlcCommand;
import com.nicobrest.kamehouse.main.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommand;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;
import com.nicobrest.kamehouse.systemcommand.service.SystemCommandService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminVlcService {

  @Autowired
  private SystemCommandService systemCommandService;

  public SystemCommandService getSystemCommandService() {
    return systemCommandService;
  }

  public void setSystemCommandService(SystemCommandService systemCommandService) {
    this.systemCommandService = systemCommandService;
  }
  
  /**
   * Start vlc player with the specified file or playlist in the AdminVlcCommand.
   */
  public List<SystemCommandOutput> startVlcPlayer(AdminVlcCommand adminVlcCommand) {
    if (!AdminVlcCommand.START.equals(adminVlcCommand.getCommand())) {
      throw new KameHouseInvalidCommandException("Invalid AdminVlcCommand " + adminVlcCommand
          .getCommand());
    }
    List<SystemCommand> systemCommands = systemCommandService.getSystemCommands(adminVlcCommand);
    List<SystemCommandOutput> systemCommandOutputs = new ArrayList<SystemCommandOutput>();
    for (SystemCommand systemCommand : systemCommands) {
      SystemCommandOutput systemCommandOutput = systemCommandService.execute(systemCommand);
      systemCommandOutputs.add(systemCommandOutput);
    }
    return systemCommandOutputs;
  }

  /**
   * Stop the active vlc player.
   */
  public List<SystemCommandOutput> stopVlcPlayer() {
    AdminVlcCommand adminVlcCommand = new AdminVlcCommand();
    adminVlcCommand.setCommand(AdminVlcCommand.STOP);
    List<SystemCommand> systemCommands = systemCommandService.getSystemCommands(adminVlcCommand);
    List<SystemCommandOutput> systemCommandOutputs = new ArrayList<SystemCommandOutput>();
    for (SystemCommand systemCommand : systemCommands) {
      SystemCommandOutput systemCommandOutput = systemCommandService.execute(systemCommand);
      systemCommandOutputs.add(systemCommandOutput);
    }
    return systemCommandOutputs;
  }

  /**
   * Get the status of the vlc player.
   */
  public List<SystemCommandOutput> statusVlcPlayer() {
    AdminVlcCommand adminVlcCommand = new AdminVlcCommand();
    adminVlcCommand.setCommand(AdminVlcCommand.STATUS);
    List<SystemCommand> systemCommands = systemCommandService.getSystemCommands(adminVlcCommand);
    List<SystemCommandOutput> systemCommandOutputs = new ArrayList<SystemCommandOutput>();
    for (SystemCommand systemCommand : systemCommands) {
      SystemCommandOutput systemCommandOutput = systemCommandService.execute(systemCommand);
      systemCommandOutputs.add(systemCommandOutput);
    }
    return systemCommandOutputs;
  }
}
