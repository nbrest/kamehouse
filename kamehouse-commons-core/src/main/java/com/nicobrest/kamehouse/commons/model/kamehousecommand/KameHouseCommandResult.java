package com.nicobrest.kamehouse.commons.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the result of an executed kamehouse command.
 */
public class KameHouseCommandResult {

  private String command;
  private int exitCode = -1;
  private int pid = -1;
  private String status = null;
  private List<String> standardOutput = new ArrayList<>();
  private List<String> standardError = new ArrayList<>();

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
    return List.copyOf(standardOutput);
  }

  public void setStandardOutput(List<String> standardOutput) {
    this.standardOutput = List.copyOf(standardOutput);
  }

  public List<String> getStandardError() {
    return List.copyOf(standardError);
  }

  public void setStandardError(List<String> standardError) {
    this.standardError = List.copyOf(standardError);
  }

  /**
   * Initialize the result of a kamehouse command.
   */
  public KameHouseCommandResult() {

  }

  /**
   * Initialize the result of a kamehouse command.
   */
  public KameHouseCommandResult(KameHouseCommand kameHouseCommand) {
    if (kameHouseCommand.hasSensitiveInformation()) {
      setCommand("Command executed has sensitive information");
      return;
    }
    setCommand(kameHouseCommand.getCommand());
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(command)
        .append(exitCode)
        .append(pid)
        .append(status)
        .toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof KameHouseCommandResult other) {
      return new EqualsBuilder()
          .append(command, other.getCommand())
          .append(exitCode, other.getExitCode())
          .append(pid, other.getPid())
          .append(status, other.getStatus())
          .isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
