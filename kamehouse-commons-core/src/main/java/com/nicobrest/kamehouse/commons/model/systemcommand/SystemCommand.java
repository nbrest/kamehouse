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
 * command line via kamehouse-shell. This is the base class for KameHouse Shell system commands that
 * need to be executed from the exec-script.sh on linux to handle sudo calls. By default, linux
 * kamehouse-shell commands here are executed with sudo. But it can be overriden to execute without
 * it.
 *
 * @author nbrest
 */
public abstract class SystemCommand {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private static final String KAMEHOUSE_SHELL_BASE = "/programs/kamehouse-shell/bin/";
  private static final String GIT_BASH_BAT = "win/bat/git-bash.bat";
  private static final String GIT_BASH_SHELL_BASE = "${HOME}/programs/kamehouse-shell/bin/";

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
  protected long sshTimeout = 0;
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
   * Get ssh timeout.
   */
  public long getSshTimeout() {
    return sshTimeout;
  }

  /**
   * Set ssh timeout.
   */
  public void setSshTimeout(long sshTimeout) {
    this.sshTimeout = sshTimeout;
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
   * Build the kamehouse-shell system command.
   */
  public SystemCommand() {
    executeOnDockerHost = executeOnDockerHost();
    sleepTime = getSleepTime();
    isDaemon = isDaemon();
  }

  /**
   * Get the kamehouse-shell script to execute relative to/programs/kamehouse-shell/bin.
   */
  protected abstract String getWindowsKameHouseShellScript();

  /**
   * Get the arguments to pass to the kamehouse-shell script.
   */
  protected abstract List<String> getWindowsKameHouseShellScriptArguments();

  /**
   * Get the kamehouse-shell script to execute relative to/programs/kamehouse-shell/bin.
   */
  protected abstract String getLinuxKameHouseShellScript();

  /**
   * Get the arguments to pass to the kamehouse-shell script.
   */
  protected abstract String getLinuxKameHouseShellScriptArguments();

  /**
   * Override in subclasses to add the cmd start prefix. This might be needed in some daemon
   * processes like starting vlc so that it starts in the UI and not in the background. However,
   * when adding the prefix, I won't get the kamehouse-shell scripts output returned in the
   * SystemCommand Output.
   */
  protected boolean addCmdWindowsStartPrefix() {
    return false;
  }

  /**
   * Override in classes that need to hide the output command.
   */
  protected boolean hideOutputCommand() {
    return false;
  }

  /**
   * Execute the system command as a local process and return it's output.
   */
  public SystemCommand.Output execute() {
    SystemCommand.Output commandOutput = getOutput();
    List<String> command = getCommand();
    initOutputCommand();
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command(command);
    logger.debug("execute {}", commandOutput.getCommand());
    Process process;
    try {
      process = ProcessUtils.start(processBuilder);
      if (!isDaemon()) {
        // Not an ongoing process. Wait until the process finishes and then read
        // standard output and error streams.
        boolean finished = ProcessUtils.waitFor(process, 300L);
        if (finished) {
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
          commandOutput.setExitCode(-1);
          commandOutput.setStatus(SystemCommandStatus.RUNNING.getStatus());
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
   * Gets the specified system command for the correct operating system.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public List<String> getCommand() {
    if (isCommandExecutedOnWindows()) {
      return buildWindowsCommand();
    } else {
      return buildLinuxCommand();
    }
  }

  /**
   * Get the kamehouse shell script to execute.
   */
  public String getShellScriptScript() {
    if (isCommandExecutedOnWindows()) {
      return getWindowsKameHouseShellScript();
    } else {
      return getLinuxKameHouseShellScript();
    }
  }

  /**
   * Get the kamehouse shell script args to execute.
   */
  public String getShellScriptScriptArgs() {
    if (isCommandExecutedOnWindows()) {
      List<String> scriptArgs = getWindowsKameHouseShellScriptArguments();
      if (scriptArgs == null || scriptArgs.isEmpty()) {
        return null;
      }
      StringBuilder sb = new StringBuilder();
      scriptArgs.forEach(arg -> {
        sb.append(arg).append(" ");
      });
      return sb.toString().trim();
    } else {
      return getLinuxKameHouseShellScriptArguments();
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
    return sb.toString().trim();
  }

  /**
   * Set the command in the output returned through the apis. This is just for display, it's not
   * used to get the actual command to execute.
   */
  public void initOutputCommand() {
    if (hideOutputCommand()) {
      output.setCommand("Command executed is hidden");
      return;
    }
    output.setCommand(getCommand().toString().trim());
  }

  /**
   * Build system command to run on linux.
   */
  protected List<String> buildLinuxCommand() {
    addBashPrefix();
    StringBuilder sb = new StringBuilder();
    sb.append(getKameHouseShellBasePath());
    sb.append(getLinuxKameHouseShellScript());
    if (getLinuxKameHouseShellScriptArguments() != null) {
      sb.append(" ");
      sb.append(getLinuxKameHouseShellScriptArguments());
    }
    linuxCommand.add(sb.toString().trim());
    return linuxCommand;
  }

  /**
   * Build system command to run on windows.
   */
  protected List<String> buildWindowsCommand() {
    if (addCmdWindowsStartPrefix()) {
      addWindowsCmdStartPrefix();
    }
    windowsCommand.add(getGitBashBatScript());
    windowsCommand.add("-c");
    String script = GIT_BASH_SHELL_BASE + getWindowsKameHouseShellScript();
    List<String> scriptArgs = getWindowsKameHouseShellScriptArguments();
    if (scriptArgs == null || scriptArgs.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      sb.append("\"");
      sb.append(script);
      sb.append("\"");
      windowsCommand.add(sb.toString().trim());
      return windowsCommand;
    }
    StringBuilder sb = new StringBuilder();
    sb.append("\"");
    sb.append(script);
    scriptArgs.forEach(arg -> {
      sb.append(" ").append(arg);
    });
    sb.append("\"");
    windowsCommand.add(sb.toString().trim());
    return windowsCommand;
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
   * Check if the command should be executed on a windows host.
   */
  private boolean isCommandExecutedOnWindows() {
    return PropertiesUtils.isWindowsHost()
        || (DockerUtils.shouldExecuteOnDockerHost(executeOnDockerHost)
        && DockerUtils.isWindowsDockerHost());
  }

  /**
   * Get kamehouse shell scripts base path.
   */
  private String getKameHouseShellBasePath() {
    if (executeOnDockerHost()) {
      return DockerUtils.getUserHome() + KAMEHOUSE_SHELL_BASE;
    }
    return PropertiesUtils.getUserHome() + KAMEHOUSE_SHELL_BASE;
  }

  /**
   * Get git-bash.bat to run kamehouse shell scripts on windows.
   */
  private String getGitBashBatScript() {
    return getKameHouseShellBasePath() + GIT_BASH_BAT;
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
