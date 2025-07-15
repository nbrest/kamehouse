package com.nicobrest.kamehouse.commons.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.utils.StringUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Base class for KameHouse CMD kamehouse commands.
 *
 * @author nbrest
 */
public abstract class KameHouseCmdKameHouseCommand extends KameHouseShellScript {

  /**
   * Get the arguments to pass to kamehouse-cmd including the operation to execute. For example "-o
   * encrypt -if in.txt -of out.enc".
   */
  protected abstract String getKameHouseCmdArguments();

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "kamehouse/cmd/kamehouse-cmd.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    String args = getKameHouseCmdArguments();
    if (StringUtils.isEmpty(args)) {
      return Collections.emptyList();
    }
    return Arrays.asList(getKameHouseCmdArguments().split(" "));
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "kamehouse/cmd/kamehouse-cmd.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return getKameHouseCmdArguments();
  }
}
