package com.nicobrest.kamehouse.admin.service;

import com.nicobrest.kamehouse.admin.model.AdminCommand;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommand;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;
import com.nicobrest.kamehouse.systemcommand.service.SystemCommandService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to execute admin commands.
 * 
 * @author nbrest
 *
 */
@Service
public class AdminCommandService {

  @Autowired
  private SystemCommandService systemCommandService;

  public SystemCommandService getSystemCommandService() {
    return systemCommandService;
  }

  public void setSystemCommandService(SystemCommandService systemCommandService) {
    this.systemCommandService = systemCommandService;
  }

  /**
   * Execute AdminCommand. Translate it to system commands and execute them.
   */
  public List<SystemCommandOutput> execute(AdminCommand adminCommand) {
    List<SystemCommand> systemCommands = systemCommandService.getSystemCommands(adminCommand);
    return systemCommandService.execute(systemCommands);
  }
}
