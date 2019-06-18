package com.nicobrest.kamehouse.admin.model;

/**
 * Admin VLC command such as start, stop, or get the status of a VLC player.
 * 
 * @author nbrest
 *
 */
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
