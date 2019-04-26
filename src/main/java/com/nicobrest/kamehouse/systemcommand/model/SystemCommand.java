package com.nicobrest.kamehouse.systemcommand.model;

public class SystemCommand {
  
  private String name;
  private String[] arguments = null;
 
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String[] getArguments() {
    return arguments.clone();
  }

  public void setArguments(String[] arguments) {
    this.arguments = arguments.clone();
  }
}
