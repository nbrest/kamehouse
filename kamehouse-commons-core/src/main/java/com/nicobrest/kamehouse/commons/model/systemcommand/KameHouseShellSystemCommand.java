package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import java.util.List;

/**
 * Base class for KameHouse Shell system commands that need to be executed from the exec-script.sh
 * on linux to handle sudo calls. For windows the command at the moment is a standard windows
 * command, not going through kamehouse-shell. By default linux kamehouse-shell commands here are
 * executed with sudo. But it can be overriden to execute without it.
 *
 * @author nbrest
 */
public abstract class KameHouseShellSystemCommand extends SystemCommand {

  private static final String KAMEHOUSE_SHELL_BASE_LINUX = PropertiesUtils.getUserHome()
      + "/programs/kamehouse-shell/bin/";

  /**
   * Build the kamehouse-shell system command.
   */
  public KameHouseShellSystemCommand() {
    executeOnDockerHost = executeOnDockerHost();
    sleepTime = getSleepTime();
    isDaemon = isDaemon();
    addBashPrefix();
    linuxCommand.add(buildLinuxCommand());
    addWindowsCmdStartPrefix();
    windowsCommand.addAll(getWindowsCommand());
    setOutputCommand();
  }

  /**
   * True if the command requires sudo permissions.
   */
  protected boolean isSudo() {
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
    linuxCommand.append(KAMEHOUSE_SHELL_BASE_LINUX);
    linuxCommand.append(getLinuxKameHouseShellScript());
    if (getLinuxKameHouseShellScriptArguments() != null) {
      linuxCommand.append(" ");
      linuxCommand.append(getLinuxKameHouseShellScriptArguments());
    }
    return linuxCommand.toString();
  }
}
