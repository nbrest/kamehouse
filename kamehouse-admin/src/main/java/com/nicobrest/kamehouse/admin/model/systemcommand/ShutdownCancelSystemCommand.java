package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import java.util.List;

/**
 * System command to cancel a scheduled shutdown of the server.
 *
 * @author nbrest
 */
public class ShutdownCancelSystemCommand extends KameHouseShellSystemCommand {

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "win/shutdown/shutdown-cancel.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    return null;
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/shutdown/shutdown-cancel.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return null;
  }
}
