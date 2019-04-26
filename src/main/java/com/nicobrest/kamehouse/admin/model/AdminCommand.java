package com.nicobrest.kamehouse.admin.model;

public class AdminCommand {
  
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
