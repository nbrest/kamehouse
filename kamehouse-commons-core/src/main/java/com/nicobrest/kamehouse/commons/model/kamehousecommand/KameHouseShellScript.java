package com.nicobrest.kamehouse.commons.model.kamehousecommand;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nicobrest.kamehouse.commons.model.KameHouseCommandStatus;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.ProcessUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a command to execute as a system process via kamehouse-shell. It's a single operation
 * executed through the command line via kamehouse-shell. Most of the KameHouseCommands are
 * kamehouse shell commands.
 *
 * @author nbrest
 */
public abstract class KameHouseShellScript implements KameHouseCommand {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private static final String KAMEHOUSE_SHELL_BASE = "/programs/kamehouse-shell/bin/";
  private static final String GIT_BASH_BAT = "win/bat/git-bash.bat";
  private static final String GIT_BASH_SHELL_BASE = "${HOME}/programs/kamehouse-shell/bin/";
  private static final List<String> BASH_START = Arrays.asList("/bin/bash", "-c");
  private static final List<String> WINDOWS_CMD_START = Arrays.asList("cmd.exe", "/c", "start");
  private static final List<String> WINDOWS_CMD_START_MIN =
      Arrays.asList("cmd.exe", "/c", "start", "/min");
  private static final String EXCEPTION_EXECUTING_PROCESS =
      "Error occurred while executing the process.";

  @JsonIgnore
  private int sleepTime = 0;
  @JsonIgnore
  private List<String> linuxCommand = new ArrayList<>();
  @JsonIgnore
  private List<String> windowsCommand = new ArrayList<>();
  @JsonIgnore
  private String command = null;
  @JsonIgnore
  private boolean isInitialized = false;

  /**
   * Get sleep time (in seconds) to sleep AFTER the command executes.
   */
  public int getSleepTime() {
    return sleepTime;
  }

  /**
   * Set sleep time (in seconds) to sleep AFTER the command executes.
   */
  public void setSleepTime(int sleepTime) {
    this.sleepTime = sleepTime;
  }

  /**
   * Get the kamehouse-shell script to execute relative to/programs/kamehouse-shell/bin.
   */
  protected abstract String getWindowsKameHouseShellScript();

  /**
   * Get the arguments to pass to the kamehouse-shell script.
   *
   * <p>Avoid using quotes, escape characters such as \\ and other characters that could break the
   * script execution in windows or even in linux. Though it's more unlikely they will break in
   * linux. If I need to pass special characters to the scripts as arguments, consider encoding the
   * arguments with either base64, base64url, urlencode or other encoding that will not break the
   * execution on the shell or break while being transferred to a remote server via groot api. But
   * if I do use some encoding, I need to validate the decoded string in the script before
   * processing it. If I need to send something that has special characters like a password in
   * kamehouse-cmd, it's probably best to send encoded</p>
   */
  protected abstract List<String> getWindowsKameHouseShellScriptArguments();

  /**
   * Get the kamehouse-shell script to execute relative to/programs/kamehouse-shell/bin.
   */
  protected abstract String getLinuxKameHouseShellScript();

  /**
   * Get the arguments to pass to the kamehouse-shell script.
   *
   * <p>Same as for windows arguments. Avoid certain characters that can break the flow.</p>
   */
  protected abstract String getLinuxKameHouseShellScriptArguments();

  @Override
  public KameHouseCommandResult execute() {
    init();
    KameHouseCommandResult kameHouseCommandResult = new KameHouseCommandResult(this);
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command(getCommandList());
    logger.debug("execute {}", kameHouseCommandResult.getCommand());
    Process process;
    try {
      process = ProcessUtils.start(processBuilder);
      if (!isDaemon()) {
        // Not an ongoing process. Wait until the process finishes and then read
        // standard kameHouseCommandResult and error streams.
        boolean finished = ProcessUtils.waitFor(process, 300L);
        if (finished) {
          getStreamsFromProcess(process, kameHouseCommandResult);
          int exitValue = ProcessUtils.getExitValue(process);
          kameHouseCommandResult.setExitCode(exitValue);
          if (exitValue > 0) {
            kameHouseCommandResult.setStatus(KameHouseCommandStatus.FAILED.getStatus());
          } else {
            kameHouseCommandResult.setStatus(KameHouseCommandStatus.COMPLETED.getStatus());
          }
        } else {
          // Ongoing process
          kameHouseCommandResult.setExitCode(-1);
          kameHouseCommandResult.setStatus(KameHouseCommandStatus.RUNNING.getStatus());
        }
      } else {
        // Ongoing process
        kameHouseCommandResult.setExitCode(-1); // process is still running.
        kameHouseCommandResult.setStatus(KameHouseCommandStatus.RUNNING.getStatus());
      }
    } catch (IOException e) {
      logger.error(EXCEPTION_EXECUTING_PROCESS, e);
      kameHouseCommandResult.setExitCode(1);
      kameHouseCommandResult.setStatus(KameHouseCommandStatus.FAILED.getStatus());
      kameHouseCommandResult.setStandardError(
          Arrays.asList("An error occurred executing the command. Message: " + e.getMessage()));
    } catch (InterruptedException e) {
      logger.error(EXCEPTION_EXECUTING_PROCESS, e);
      Thread.currentThread().interrupt();
    }
    if (KameHouseCommandStatus.FAILED.getStatus().equals(kameHouseCommandResult.getStatus())) {
      logger.error("execute {} response {}", kameHouseCommandResult.getCommand(),
          kameHouseCommandResult);
    } else {
      logger.debug("execute {} response {}", kameHouseCommandResult.getCommand(),
          kameHouseCommandResult);
    }
    try {
      if (getSleepTime() > 0) {
        logger.debug("Sleeping for {} seconds", getSleepTime());
        TimeUnit.SECONDS.sleep(getSleepTime());
      }
    } catch (InterruptedException e) {
      logger.warn("Interrupted exception", e);
      Thread.currentThread().interrupt();
    }
    kameHouseCommandResult.setHtmlOutputs();
    return kameHouseCommandResult;
  }

  @Override
  public void init() {
    if (isInitialized) {
      return;
    }
    if (isCommandExecutedOnWindows()) {
      buildWindowsCommand();
    } else {
      buildLinuxCommand();
    }
    buildCommand();
    isInitialized = true;
  }

  @Override
  public String getCommand() {
    return command;
  }

  @Override
  public boolean hasSensitiveInformation() {
    return true;
  }

  @Override
  public boolean executeOnDockerHost() {
    return false;
  }

  /**
   * Get the kamehouse shell script to execute via groot.
   */
  public String getShellScript() {
    if (isCommandExecutedOnWindows()) {
      return getWindowsKameHouseShellScript();
    } else {
      return getLinuxKameHouseShellScript();
    }
  }

  /**
   * Get the kamehouse shell script args to execute via groot.
   */
  @JsonIgnore
  public String getShellScriptArgs() {
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
   * Override to change the timeout to execute the command over ssh.
   */
  @JsonIgnore
  public long getSshTimeout() {
    return 0L;
  }

  /**
   * Override to set the command as a daemon.
   */
  @JsonIgnore
  public boolean isDaemon() {
    return false;
  }

  /**
   * Override in subclasses to add the cmd start prefix. This might be needed in some daemon
   * processes like starting vlc so that it starts in the UI and not in the background. However,
   * when adding the prefix, I won't get the kamehouse-shell scripts output returned.
   */
  protected boolean shouldAddWindowsCmdStartPrefix() {
    return false;
  }

  /**
   * Override if the windows cmd needs to start maximized.
   */
  @JsonIgnore
  protected boolean windowsCmdStartMinimized() {
    return true;
  }

  /**
   * Add bash -c prefix to linux commands.
   */
  private void addBashPrefix() {
    if (!DockerUtils.shouldExecuteOnDockerHost(executeOnDockerHost())) {
      linuxCommand.addAll(BASH_START);
    }
  }

  /**
   * Build kamehouse command to run on linux.
   */
  private void buildLinuxCommand() {
    addBashPrefix();
    StringBuilder sb = new StringBuilder();
    sb.append(getKameHouseShellBasePath());
    sb.append(getLinuxKameHouseShellScript());
    if (getLinuxKameHouseShellScriptArguments() != null) {
      sb.append(" ");
      sb.append(getLinuxKameHouseShellScriptArguments());
    }
    linuxCommand.add(sb.toString().trim());
  }

  /**
   * Build kamehouse command to run on windows.
   */
  private void buildWindowsCommand() {
    if (shouldAddWindowsCmdStartPrefix()) {
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
      return;
    }
    StringBuilder sb = new StringBuilder();
    sb.append("\"");
    sb.append(script);
    scriptArgs.forEach(arg -> {
      sb.append(" ").append(arg);
    });
    sb.append("\"");
    windowsCommand.add(sb.toString().trim());
  }

  /**
   * Add cmd.exe start command prefix when not executing the windows command on docker host from
   * docker.
   */
  private void addWindowsCmdStartPrefix() {
    if (!DockerUtils.shouldExecuteOnDockerHost(executeOnDockerHost())) {
      if (windowsCmdStartMinimized()) {
        windowsCommand.addAll(WINDOWS_CMD_START_MIN);
      } else {
        windowsCommand.addAll(WINDOWS_CMD_START);
      }
    }
  }

  /**
   * Build os dependant command string.
   */
  private void buildCommand() {
    List<String> commandList = getCommandList();
    StringBuilder sb = new StringBuilder();
    for (String command : commandList) {
      sb.append(command).append(" ");
    }
    command = sb.toString().trim();
  }

  /**
   * Get the os dependant command to execute as a list.
   */
  private List<String> getCommandList() {
    if (isCommandExecutedOnWindows()) {
      return List.copyOf(windowsCommand);
    } else {
      return List.copyOf(linuxCommand);
    }
  }

  /**
   * Check if the command should be executed on a windows host.
   */
  private boolean isCommandExecutedOnWindows() {
    return PropertiesUtils.isWindowsHost()
        || (DockerUtils.shouldExecuteOnDockerHost(executeOnDockerHost())
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
   * Gets input and error streams from process and add them to the kamehouse command
   * kameHouseCommandResult.
   */
  private void getStreamsFromProcess(Process process, KameHouseCommandResult kameHouseCommandResult)
      throws IOException {
    try (InputStream processInputStream = ProcessUtils.getInputStream(process);
        BufferedReader processBufferedReader =
            new BufferedReader(new InputStreamReader(processInputStream, StandardCharsets.UTF_8));
        InputStream processErrorStream = ProcessUtils.getErrorStream(process);
        BufferedReader processErrorBufferedReader =
            new BufferedReader(new InputStreamReader(processErrorStream, StandardCharsets.UTF_8))) {
      // Read command standard kameHouseCommandResult stream
      List<String> processStandardOutputList = readStreamIntoList(processBufferedReader);
      kameHouseCommandResult.setStandardOutput(processStandardOutputList);
      // Read command standard error stream
      List<String> processStandardErrorList = readStreamIntoList(processErrorBufferedReader);
      kameHouseCommandResult.setStandardError(processStandardErrorList);
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
}
