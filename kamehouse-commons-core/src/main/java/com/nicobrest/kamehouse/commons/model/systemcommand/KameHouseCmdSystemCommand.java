package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import java.util.Arrays;

/**
 * Base class for KameHouse CMD system commands.
 *
 * @author nbrest
 */
public abstract class KameHouseCmdSystemCommand extends SystemCommand {

  private static final String KAMEHOUSE_CMD_WIN =
      "\\programs\\kamehouse-cmd\\bin\\kamehouse-cmd.bat";
  private static final String KAMEHOUSE_CMD_LINUX = "/programs/kamehouse-cmd/bin/kamehouse-cmd.sh";

  /**
   * Sets a kamehouse-cmd system command.
   */
  public void setKameHouseCmdCommands() {
    linuxCommand.add(getKameHouseCmdLinuxPath());
    linuxCommand.addAll(Arrays.asList(getKameHouseCmdArguments().split(" ")));
    addWindowsCmdStartPrefix();
    windowsCommand.add(getKameHouseCmdWinPath());
    windowsCommand.addAll(Arrays.asList(getKameHouseCmdArguments().split(" ")));
    setOutputCommand();
  }

  /**
   * Get the arguments to pass to kamehouse-cmd including the operation to execute. For example "-o
   * encrypt -if in.txt -of out.enc".
   */
  protected abstract String getKameHouseCmdArguments();

  /**
   * Get kamehouse cmd win path.
   */
  private String getKameHouseCmdWinPath() {
    if (executeOnDockerHost()) {
      return DockerUtils.getUserHome() + KAMEHOUSE_CMD_WIN;
    }
    return PropertiesUtils.getUserHome() + KAMEHOUSE_CMD_WIN;
  }

  /**
   * Get kamehouse cmd linux path.
   */
  private String getKameHouseCmdLinuxPath() {
    if (executeOnDockerHost()) {
      return DockerUtils.getUserHome() + KAMEHOUSE_CMD_LINUX;
    }
    return PropertiesUtils.getUserHome() + KAMEHOUSE_CMD_LINUX;
  }
}
