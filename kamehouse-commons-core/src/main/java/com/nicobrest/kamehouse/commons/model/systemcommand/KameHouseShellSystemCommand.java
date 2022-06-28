package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import java.util.List;

/**
 * Base class for KameHouse Shell system commands that need to be executed from the exec-script.sh
 * on linux to handle sudo calls. For windows the command at the moment is a standard windows
 * command, not going through kamehouse-shell.
 *
 * @author nbrest
 */
public abstract class KameHouseShellSystemCommand extends SystemCommand {

  private static final String KAMEHOUSE_CMD_LINUX = PropertiesUtils.getUserHome()
      + "/programs/kamehouse-shell/bin/common/sudoers/www-data/exec-script.sh";

  public KameHouseShellSystemCommand() {
    executeOnDockerHost = executeOnDockerHost();
    linuxCommand.add(KAMEHOUSE_CMD_LINUX);
    linuxCommand.add("-s");
    linuxCommand.add(getLinuxKameHouseShellScript());
    if (getLinuxKameHouseShellScriptArguments() != null) {
      linuxCommand.add("-a");
      linuxCommand.add(getLinuxKameHouseShellScriptArguments());
    }
    addWindowsCmdStartPrefix();
    windowsCommand.addAll(getWindowsCommand());
    setOutputCommand();
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
}
