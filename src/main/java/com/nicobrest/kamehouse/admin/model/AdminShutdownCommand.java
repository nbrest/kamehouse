package com.nicobrest.kamehouse.admin.model;

/**
 * Shutdown command.
 * 
 * @author nbrest
 *
 */
public class AdminShutdownCommand {
  
  public static final String CANCEL = "cancel";
  public static final String SET = "set";
  public static final String STATUS = "status";
  
  private String command;
  
  // Time in seconds (delay to shutdown)
  private int time;
  
  public String getCommand() {
    return command;
  }
  
  public void setCommand(String command) {
    this.command = command;
  } 
  
  public int getTime() {
    return time;
  }
  
  public void setTime(int time) {
    this.time = time;
  }
}
