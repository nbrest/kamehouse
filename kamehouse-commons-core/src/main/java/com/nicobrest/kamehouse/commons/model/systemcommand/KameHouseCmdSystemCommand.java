package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for VncDo system commands.
 *
 * @author nbrest
 */
public abstract class KameHouseCmdSystemCommand extends SystemCommand {

  private static final String KAMEHOUSE_CMD_WIN = PropertiesUtils.getUserHome()
      + "\\programs\\kamehouse-cmd\\bin\\kamehouse-cmd.bat ";
  private static final String KAMEHOUSE_CMD_LINUX = PropertiesUtils.getUserHome()
      + "/programs/kamehouse-cmd/bin/kamehouse-cmd.sh";

  /**
   * Sets a kamehouse-cmd system command.
   */
  public void setKameHouseCmdCommands() {
    linuxCommand.add(KAMEHOUSE_CMD_LINUX);
    linuxCommand.addAll(Arrays.asList(getKameHouseCmdArguments().split(" ")));
    windowsCommand.addAll(List.of("cmd.exe", "/c",
        KAMEHOUSE_CMD_WIN + getKameHouseCmdArguments()));
    setOutputCommand();
  }

  /**
   * Get the arguments to pass to kamehouse-cmd including the operation to execute.
   * For example "-o encrypt -if in.txt -of out.enc".
   */
  protected abstract String getKameHouseCmdArguments();
}