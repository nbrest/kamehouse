package com.nicobrest.kamehouse.systemcommand.model;

import java.util.List;

public class SystemCommandOutput {

  private String command;
  private int exitCode = -1;
  private int pid = -1;
  private String status = null;
  private List<String> standardOutput = null;
  private List<String> standardError = null;
  
  public String getCommand() {
    return command;
  }
  
  public void setCommand(String command) {
    this.command = command;
  }
  
  public int getExitCode() {
    return exitCode;
  }

  public void setExitCode(int exitCode) {
    this.exitCode = exitCode;
  }
  
  public int getPid() {
    return pid;
  }
  
  public void setPid(int pid) {
    this.pid = pid;
  }
  
  public String getStatus() {
    return status;
  }
  
  public void setStatus(String status) {
    this.status = status;
  }
  
  public List<String> getStandardOutput() {
    return standardOutput;
  }
  
  public void setStandardOutput(List<String> standardOutput) {
    this.standardOutput = standardOutput;
  }
  
  public List<String> getStandardError() {
    return standardError;
  }
  
  public void setStandardError(List<String> standardError) {
    this.standardError = standardError;
  }
}