package com.nicobrest.kamehouse.systemcommand.model;

import java.util.List;

/**
 * Represents a command to execute as a system process.
 * 
 * @author nbrest
 *
 */
public class SystemCommand {
  
  private List<String> command = null;
  private Boolean isDaemon = false;

  public List<String> getCommand() {
    return command;
  }

  public void setCommand(List<String> command) {
    this.command = command;
  }
  
  public Boolean isDaemon() {
    return isDaemon;
  }
  
  public void setIsDaemon(Boolean isDaemon) {
    this.isDaemon = isDaemon;
  }
}
