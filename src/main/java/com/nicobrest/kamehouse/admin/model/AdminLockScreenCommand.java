package com.nicobrest.kamehouse.admin.model;

/**
 * Admin Lock Screen command such as lock or unlock.
 * 
 * @author nbrest
 *
 */
public class AdminLockScreenCommand {

  public static final String LOCK = "lock";
  public static final String UNLOCK = "unlock";
  
  private String command; 
  
  public String getCommand() {
    return command;
  }
  
  public void setCommand(String command) {
    this.command = command;
  } 
}
