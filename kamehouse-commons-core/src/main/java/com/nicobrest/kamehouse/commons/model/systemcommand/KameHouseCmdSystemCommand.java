package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.utils.StringUtils;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for KameHouse CMD system commands.
 *
 * @author nbrest
 */
public abstract class KameHouseCmdSystemCommand extends KameHouseShellSystemCommand {

  /**
   * Get the arguments to pass to kamehouse-cmd including the operation to execute. For example "-o
   * encrypt -if in.txt -of out.enc".
   */
  protected abstract String getKameHouseCmdArguments();

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "kamehouse/kamehouse-cmd.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    String args = getKameHouseCmdArguments();
    if (StringUtils.isEmpty(args)) {
      return null;
    }
    return Arrays.asList(getKameHouseCmdArguments().split(" "));
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "kamehouse/kamehouse-cmd.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return getKameHouseCmdArguments();
  }
}
