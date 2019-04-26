package com.nicobrest.kamehouse.systemcommand.model;

public class SystemCommand {
  
  private String[] command = null;
  private Boolean isDaemon = false;

  public String[] getCommand() {
    return command.clone();
  }

  public void setCommand(String[] command) {
    this.command = command.clone();
  }
  
  public Boolean isDaemon() {
    return isDaemon;
  }
  
  public void setIsDaemon(Boolean isDaemon) {
    this.isDaemon = isDaemon;
  }
}
