package com.nicobrest.kamehouse.admin.service;

import com.nicobrest.kamehouse.admin.model.CommandOutput;
import com.nicobrest.kamehouse.systemcommand.service.SystemCommandService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminVlcService {

  private static final Logger logger = LoggerFactory.getLogger(AdminVlcService.class);
  
  @Autowired
  private SystemCommandService systemCommandService;
  
  public SystemCommandService getSystemCommandService() {
    return systemCommandService;
  }
  
  public void setSystemCommandService(SystemCommandService systemCommandService) {
    this.systemCommandService = systemCommandService;
  }
  
  public CommandOutput startVlcPlayer() {
    return systemCommandService.startVlcPlayer();
  }
  
  public CommandOutput stopVlcPlayer() {
    return systemCommandService.stopVlcPlayer();
  }
  
  public CommandOutput statusVlcPlayer() {
    return systemCommandService.statusVlcPlayer();
  }
}
