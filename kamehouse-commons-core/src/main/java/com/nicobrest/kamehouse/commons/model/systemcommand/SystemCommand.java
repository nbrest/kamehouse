package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.annotations.Masked;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Arrays;
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

  private static final List<String> BASH_START = Arrays.asList("/bin/bash", "-c");
  private static final List<String> POWERSHELL_START = Arrays.asList("powershell.exe", "-c");
  private static final List<String> WINDOWS_CMD_START = Arrays.asList("cmd.exe", "/c", "start");

  protected boolean logCommand = true;
  protected boolean executeOnDockerHost = false;
  protected boolean isDaemon = false;
  @Masked
  protected List<String> linuxCommand = new ArrayList<>();
  @Masked
  protected List<String> windowsCommand = new ArrayList<>();
  protected int sleepTime = 0;
  protected Output output = new Output();

  public boolean logCommand() {
    return logCommand;
  }

  public boolean executeOnDockerHost() {
    return executeOnDockerHost;
  }

  public boolean isDaemon() {
    return isDaemon;
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public Output getOutput() {
    return output;
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public void setOutput(Output output) {
    this.output = output;
  }

  /**
   * Gets the specified system command for the correct operating system.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public List<String> getCommand() {
    if (PropertiesUtils.isWindowsHost()
        || (DockerUtils.shouldExecuteOnDockerHost(executeOnDockerHost)
        && DockerUtils.isWindowsDockerHost())) {
      return windowsCommand;
    } else {
      return linuxCommand;
    }
  }

  /**
   * Get the command as a string to execute over ssh.
   */
  public String getCommandForSsh() {
    List<String> commandList = getCommand();
    StringBuilder sb = new StringBuilder();
    for (String command : commandList) {
      sb.append(command).append(" ");
    }
    return sb.toString();
  }

  /**
   * Add cmd.exe start command prefix when not executing the windows command on docker host from
   * docker.
   */
  protected void addWindowsCmdStartPrefix() {
    if (!DockerUtils.shouldExecuteOnDockerHost(executeOnDockerHost)) {
      windowsCommand.addAll(WINDOWS_CMD_START);
    }
  }

  /**
   * Add powershell.exe command prefix to windows commands.
   */
  protected void addPowerShellPrefix() {
    windowsCommand.addAll(POWERSHELL_START);
  }

  /**
   * Add bash -c prefix to linux commands.
   */
  protected void addBashPrefix() {
    if (!DockerUtils.shouldExecuteOnDockerHost(executeOnDockerHost)) {
      linuxCommand.addAll(BASH_START);
    }
  }

  /**
   * Sets the Output command. Call this method in the constructor of <b>EVERY</b> concrete subclass,
   * after initializing the command lists.
   */
  protected void setOutputCommand() {
    output.setCommand(getCommand().toString());
  }

  /**
   * Get sleep time (in seconds) to sleep AFTER the command executes.
   */
  public int getSleepTime() {
    return sleepTime;
  }

  /**
   * Get sleep time (in seconds) to sleep AFTER the command executes.
   */
  public void setSleepTime(int sleepTime) {
    this.sleepTime = sleepTime;
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

    @SuppressFBWarnings(value = "EI_EXPOSE_REP")
    public List<String> getStandardOutput() {
      return standardOutput;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
    public void setStandardOutput(List<String> standardOutput) {
      this.standardOutput = standardOutput;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP")
    public List<String> getStandardError() {
      return standardError;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
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
