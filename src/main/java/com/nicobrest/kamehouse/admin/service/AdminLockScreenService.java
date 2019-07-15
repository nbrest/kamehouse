package com.nicobrest.kamehouse.admin.service;

import com.nicobrest.kamehouse.admin.model.AdminLockScreenCommand;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommand;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;
import com.nicobrest.kamehouse.systemcommand.service.SystemCommandService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminLockScreenService {

  @Autowired
  private SystemCommandService systemCommandService;

  public SystemCommandService getSystemCommandService() {
    return systemCommandService;
  }

  public void setSystemCommandService(SystemCommandService systemCommandService) {
    this.systemCommandService = systemCommandService;
  }

  /**
   * Lock the screen.
   */
  public List<SystemCommandOutput> lockScreen() {

    AdminLockScreenCommand adminLockScreenCommand = new AdminLockScreenCommand();
    adminLockScreenCommand.setCommand(AdminLockScreenCommand.LOCK);
    List<SystemCommand> systemCommands = systemCommandService.getSystemCommands(
        adminLockScreenCommand);
    List<SystemCommandOutput> systemCommandOutputs = systemCommandService.execute(systemCommands);
    return systemCommandOutputs;
  }  
  
  /**
   * Unlock the screen.
   */
  public List<SystemCommandOutput> unlockScreen() {

    AdminLockScreenCommand adminLockScreenCommand = new AdminLockScreenCommand();
    adminLockScreenCommand.setCommand(AdminLockScreenCommand.UNLOCK);
    List<SystemCommand> systemCommands = systemCommandService.getSystemCommands(
        adminLockScreenCommand);
    List<SystemCommandOutput> systemCommandOutputs = systemCommandService.execute(systemCommands);
    return systemCommandOutputs;
  }
}
