package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import java.util.List;

/**
 * Base class for KameHouse Shell system commands that need to be executed from the exec-script.sh
 * on linux to handle sudo calls. <br/><br/> By default linux kamehouse-shell commands here are
 * executed with sudo. But it can be overriden to execute without it.
 *
 * @author nbrest
 */
public abstract class KameHouseShellSystemCommand extends SystemCommand {

  private static final String KAMEHOUSE_SHELL_BASE = "/programs/kamehouse-shell/bin/";
  private static final String GIT_BASH_BAT = "win/bat/git-bash.bat";
  private static final String GIT_BASH_BAT_WIN = "win\\bat\\git-bash.bat";
  private static final String GIT_BASH_SHELL_BASE = "${HOME}/programs/kamehouse-shell/bin/";

  /**
   * Build the kamehouse-shell system command.
   */
  protected KameHouseShellSystemCommand() {
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


  @Override
  protected List<String> buildLinuxCommand() {
    addBashPrefix();
    StringBuilder sb = new StringBuilder();
    sb.append(getKameHouseShellBasePath());
    sb.append(getLinuxKameHouseShellScript());
    if (getLinuxKameHouseShellScriptArguments() != null) {
      sb.append(" ");
      sb.append(getLinuxKameHouseShellScriptArguments());
    }
    linuxCommand.add(sb.toString());
    return linuxCommand;
  }

  @Override
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
      windowsCommand.add(sb.toString());
      return windowsCommand;
    }
    StringBuilder sb = new StringBuilder();
    sb.append("\"");
    sb.append(script);
    scriptArgs.forEach(arg -> {
      sb.append(" ").append(arg);
    });
    sb.append("\"");
    windowsCommand.add(sb.toString());
    return windowsCommand;
  }

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
   * Build a kamehouse shell script command to execute on docker host.
   */
  public static String getDockerHostKameHouseShellCommand(String script, String args) {
    StringBuilder command = new StringBuilder(DockerUtils.getDockerHostUserHome());
    if (DockerUtils.isWindowsDockerHost()) {
      command.append(KAMEHOUSE_SHELL_BASE.replace("/", "\\"));
      command.append(GIT_BASH_BAT_WIN);
      command.append(" -c \"");
      command.append(GIT_BASH_SHELL_BASE);
    } else {
      command.append(KAMEHOUSE_SHELL_BASE);
    }
    command.append(script);
    if (!StringUtils.isEmpty(args)) {
      command.append(" ");
      command.append(args);
    }
    if (DockerUtils.isWindowsDockerHost()) {
      command.append("\"");
    }
    return command.toString();
  }
}
