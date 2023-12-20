package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import java.util.List;

/**
 * Base class for KameHouse Shell system commands that need to be executed from the exec-script.sh
 * on linux to handle sudo calls. For windows the command at the moment is a standard windows
 * command, not going through kamehouse-shell. <br/><br/> By default linux kamehouse-shell commands
 * here are executed with sudo. But it can be overriden to execute without it. <br/><br/>  For
 * Windows, it's usually makes more sense to execute the commands straight on the cmd.exe console
 * rather than going through git bash to execute kamehouse-shell commands.
 *
 * @author nbrest
 */
public abstract class KameHouseShellSystemCommand extends SystemCommand {

  private static final String KAMEHOUSE_SHELL_BASE = "/programs/kamehouse-shell/bin/";

  /**
   * Build the kamehouse-shell system command.
   */
  protected KameHouseShellSystemCommand() {
    executeOnDockerHost = executeOnDockerHost();
    sleepTime = getSleepTime();
    isDaemon = isDaemon();
    addBashPrefix();
    linuxCommand.add(buildLinuxCommand());
    if (addCmdWindowsStartPrefix()) {
      addWindowsCmdStartPrefix();
    }
    windowsCommand.addAll(getWindowsCommand());
    setOutputCommand();
  }

  /**
   * Override in subclasses to add sudo prefix.
   */
  protected boolean isSudo() {
    return false;
  }

  /**
   * Override in subclasses to skip adding the cmd start prefix.
   */
  protected boolean addCmdWindowsStartPrefix() {
    return true;
  }

  /**
   * Get the entire windows command as a list, including it's arguments.
   */
  protected abstract List<String> getWindowsCommand();

  /**
   * Get the kamehouse-shell script to execute relative to/programs/kamehouse-shell/bin.
   */
  protected abstract String getLinuxKameHouseShellScript();

  /**
   * Get the arguments to pass to the kamehouse-shell script.
   */
  protected abstract String getLinuxKameHouseShellScriptArguments();

  /**
   * Returns the linux command to excute.
   */
  private String buildLinuxCommand() {
    StringBuilder linuxCommand = new StringBuilder();
    if (isSudo()) {
      linuxCommand.append("sudo ");
    }
    linuxCommand.append(getKameHouseShellBasePath());
    linuxCommand.append(getLinuxKameHouseShellScript());
    if (getLinuxKameHouseShellScriptArguments() != null) {
      linuxCommand.append(" ");
      linuxCommand.append(getLinuxKameHouseShellScriptArguments());
    }
    return linuxCommand.toString();
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
}
