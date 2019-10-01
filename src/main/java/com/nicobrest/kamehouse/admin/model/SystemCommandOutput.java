package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.main.utils.JsonUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.List;

/**
 * Represents the output and status of an executed system command.
 * 
 * @author nbrest
 *
 */
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

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(command).append(exitCode).append(pid).append(status)
        .toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof SystemCommandOutput) {
      final SystemCommandOutput other = (SystemCommandOutput) obj;
      return new EqualsBuilder().append(command, other.getCommand()).append(exitCode, other
          .getExitCode()).append(pid, other.getPid()).append(status, other.getStatus()).isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}