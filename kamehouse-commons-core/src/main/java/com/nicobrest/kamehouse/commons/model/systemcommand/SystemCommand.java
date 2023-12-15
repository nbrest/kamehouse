package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.annotations.Masked;
import com.nicobrest.kamehouse.commons.model.SystemCommandStatus;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.ProcessUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a command to execute as a system process. It's a single operation executed through the
 * command line.
 *
 * @author nbrest
 */
public abstract class SystemCommand {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private static final String EXCEPTION_EXECUTING_PROCESS =
      "Error occurred while executing the process.";
  private static final List<String> BASH_START = Arrays.asList("/bin/bash", "-c");
  private static final List<String> POWERSHELL_START = Arrays.asList("powershell.exe", "-c");
  private static final List<String> WINDOWS_CMD_START = Arrays.asList("cmd.exe", "/c", "start");
  private static final List<String> WINDOWS_CMD_START_MIN =
      Arrays.asList("cmd.exe", "/c", "start", "/min");

  protected boolean logCommand = true;
  protected boolean executeOnDockerHost = false;
  protected boolean isDaemon = false;
  protected boolean windowsCmdStartMinimized = true;
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

  public boolean getWindowsCmdStartMinimized() {
    return windowsCmdStartMinimized;
  }

  public void setWindowsCmdStartMinimized(boolean windowsCmdStartMinimized) {
    this.windowsCmdStartMinimized = windowsCmdStartMinimized;
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
      if (getWindowsCmdStartMinimized()) {
        windowsCommand.addAll(WINDOWS_CMD_START_MIN);
      } else {
        windowsCommand.addAll(WINDOWS_CMD_START);
      }
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

  /**
   * Execute the system command as a local process and return it's output.
   */
  public SystemCommand.Output execute() {
    SystemCommand.Output commandOutput = getOutput();
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command(getCommand());
    logger.debug("execute {}", commandOutput.getCommand());
    Process process;
    try {
      process = ProcessUtils.start(processBuilder);
      if (!isDaemon()) {
        // Not an ongoing process. Wait until the process finishes and then read
        // standard output and error streams.
        ProcessUtils.waitFor(process);
        getStreamsFromProcess(process);
        int exitValue = ProcessUtils.getExitValue(process);
        commandOutput.setExitCode(exitValue);
        if (exitValue > 0) {
          commandOutput.setStatus(SystemCommandStatus.FAILED.getStatus());
        } else {
          commandOutput.setStatus(SystemCommandStatus.COMPLETED.getStatus());
        }
      } else {
        // Ongoing process
        commandOutput.setExitCode(-1); // process is still running.
        commandOutput.setStatus(SystemCommandStatus.RUNNING.getStatus());
      }
    } catch (IOException e) {
      logger.error(EXCEPTION_EXECUTING_PROCESS, e);
      commandOutput.setExitCode(1);
      commandOutput.setStatus(SystemCommandStatus.FAILED.getStatus());
      commandOutput.setStandardError(
          Arrays.asList("An error occurred executing the command. Message: " + e.getMessage()));
    } catch (InterruptedException e) {
      logger.error(EXCEPTION_EXECUTING_PROCESS, e);
      Thread.currentThread().interrupt();
    }
    if (SystemCommandStatus.FAILED.getStatus().equals(commandOutput.getStatus())) {
      logger.error("execute {} response {}", commandOutput.getCommand(), commandOutput);
    } else {
      logger.debug("execute {} response {}", commandOutput.getCommand(), commandOutput);
    }
    try {
      if (sleepTime > 0) {
        logger.debug("Sleeping for {} seconds", sleepTime);
        TimeUnit.SECONDS.sleep(sleepTime);
      }
    } catch (InterruptedException e) {
      logger.warn("Interrupted exception", e);
      Thread.currentThread().interrupt();
    }
    return commandOutput;
  }

  /**
   * Gets input and error streams from process and add them to the system command output.
   */
  private void getStreamsFromProcess(Process process)
      throws IOException {
    try (InputStream processInputStream = ProcessUtils.getInputStream(process);
        BufferedReader processBufferedReader =
            new BufferedReader(new InputStreamReader(processInputStream, StandardCharsets.UTF_8));
        InputStream processErrorStream = ProcessUtils.getErrorStream(process);
        BufferedReader processErrorBufferedReader =
            new BufferedReader(new InputStreamReader(processErrorStream, StandardCharsets.UTF_8))) {
      // Read command standard output stream
      List<String> processStandardOuputList = readStreamIntoList(processBufferedReader);
      getOutput().setStandardOutput(processStandardOuputList);
      // Read command standard error stream
      List<String> processStandardErrorList = readStreamIntoList(processErrorBufferedReader);
      getOutput().setStandardError(processStandardErrorList);
    }
  }

  /**
   * Reads the stream from a buffered reader and store it in a List of Strings.
   */
  private List<String> readStreamIntoList(BufferedReader bufferedReader) throws IOException {
    List<String> streamAsList = new ArrayList<>();
    String streamLine;
    while ((streamLine = bufferedReader.readLine()) != null) {
      if (!StringUtils.isEmpty(streamLine)) {
        streamAsList.add(streamLine);
      }
    }
    return streamAsList;
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
      if (obj instanceof Output other) {
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
