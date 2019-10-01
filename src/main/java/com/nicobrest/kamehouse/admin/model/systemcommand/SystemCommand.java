package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.main.utils.JsonUtils;
import com.nicobrest.kamehouse.main.utils.PropertiesUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a command to execute as a system process. It's a unique operation
 * executed through the command line.
 * 
 * @author nbrest
 *
 */
public abstract class SystemCommand {

  protected boolean isDaemon = false;
  protected List<String> linuxCommand = new ArrayList<>();
  protected List<String> windowsCommand = new ArrayList<>();
  private Output output = new Output();

  public boolean isDaemon() {
    return isDaemon;
  }

  public Output getOutput() {
    return output;
  }

  public void setOutput(Output output) {
    this.output = output;
  }

  /**
   * Get the specified system command for the correct operating system.
   */
  public List<String> getCommand() {
    if (PropertiesUtils.isWindowsHost()) {
      return windowsCommand;
    } else {
      return linuxCommand;
    }
  }

  /**
   * Set the Output command, hiding it if its is a vncdo command, as it contains
   * passwords. Call this method in the constructor of <b>EVERY</b> concrete subclass,
   * after initializing the command lists.
   */
  protected void setOutputCommand() {
    if (isVncdoCommand(getCommand())) {
      output.setCommand("[vncdo (hidden from logs as it contains passwords)]");
    } else {
      output.setCommand(getCommand().toString());
    }
  }
  
  /**
   * Returns true if the command is a vncdo command.
   */
  private boolean isVncdoCommand(List<String> command) {
    return command.contains("vncdo") || (command.size() >= 3 && command.get(2).contains("vncdo"));
  }
  
  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
  
  /**
   * Represents the output and status of an executed system command.
   */
  public static class Output {

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
      if (obj instanceof Output) {
        final Output other = (Output) obj;
        return new EqualsBuilder().append(command, other.getCommand()).append(exitCode, other
            .getExitCode()).append(pid, other.getPid()).append(status, other.getStatus())
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
}
