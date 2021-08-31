package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a command to execute as a system process. It's a single operation executed through the
 * command line.
 *
 * @author nbrest
 */
public abstract class SystemCommand {

  protected boolean isDaemon = false;
  protected List<String> linuxCommand = new ArrayList<>();
  protected List<String> windowsCommand = new ArrayList<>();
  protected int sleepTime = 0;
  protected Output output = new Output();

  public boolean isDaemon() {
    return isDaemon;
  }

  public Output getOutput() {
    return output;
  }

  public void setOutput(Output output) {
    this.output = output;
  }

  /** Gets the specified system command for the correct operating system. */
  public List<String> getCommand() {
    if (PropertiesUtils.isWindowsHost()) {
      return windowsCommand;
    } else {
      return linuxCommand;
    }
  }

  /**
   * Sets the Output command. Call this method in the constructor of <b>EVERY</b> concrete subclass,
   * after initializing the command lists.
   */
  protected void setOutputCommand() {
    output.setCommand(getCommand().toString());
  }

  /** Get sleep time (in seconds) to sleep AFTER the command executes. */
  public int getSleepTime() {
    return sleepTime;
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }

  /** Represents the output and status of an executed system command. */
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
      return new HashCodeBuilder()
          .append(command)
          .append(exitCode)
          .append(pid)
          .append(status)
          .toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof Output) {
        final Output other = (Output) obj;
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
}
