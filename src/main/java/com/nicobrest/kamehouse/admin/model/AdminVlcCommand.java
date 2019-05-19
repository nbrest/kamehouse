package com.nicobrest.kamehouse.admin.model;

public class AdminVlcCommand {

  public static final String START = "start";
  public static final String STATUS = "status";
  public static final String STOP = "stop";
  
  private String command;
  private String file;
  
  public String getCommand() {
    return command;
  }
  
  public void setCommand(String command) {
    this.command = command;
  } 
  
  public String getFile() {
    return file;
  }
  
  public void setFile(String file) {
    this.file = file;
  }
}
