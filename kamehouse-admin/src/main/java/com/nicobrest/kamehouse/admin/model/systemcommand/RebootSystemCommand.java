package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import java.util.Arrays;
import java.util.List;

/**
 * System command to reboot the server.
 *
 * @author nbrest
 */
public class RebootSystemCommand extends KameHouseShellSystemCommand {

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected List<String> getWindowsCommand() {
    return Arrays.asList("shutdown", "/r", "/f", "/t", "0");
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/shutdown/reboot.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return null;
  }
}
