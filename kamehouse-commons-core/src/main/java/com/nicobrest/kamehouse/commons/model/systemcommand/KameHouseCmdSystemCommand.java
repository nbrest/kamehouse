package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import java.util.Arrays;
import java.util.List;

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
   * Get the arguments to pass to kamehouse-cmd including the operation to execute. For example "-o
   * encrypt -if in.txt -of out.enc".
   */
  protected abstract String getKameHouseCmdArguments();

  @Override
  protected List<String> buildLinuxCommand() {
    linuxCommand.add(getKameHouseCmdLinuxPath());
    linuxCommand.addAll(Arrays.asList(getKameHouseCmdArguments().split(" ")));
    return linuxCommand;
  }

  @Override
  protected List<String> buildWindowsCommand() {
    addWindowsCmdStartPrefix();
    windowsCommand.add(getKameHouseCmdWinPath());
    windowsCommand.addAll(Arrays.asList(getKameHouseCmdArguments().split(" ")));
    return windowsCommand;
  }

  /**
   * Get kamehouse cmd win path.
   */
  private String getKameHouseCmdWinPath() {
    return DockerUtils.getUserHome(executeOnDockerHost()) + KAMEHOUSE_CMD_WIN;
  }

  /**
   * Get kamehouse cmd linux path.
   */
  private String getKameHouseCmdLinuxPath() {
    return "DISPLAY=:0.0 " + DockerUtils.getUserHome(executeOnDockerHost()) + KAMEHOUSE_CMD_LINUX;
  }
}
