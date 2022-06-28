package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import java.util.Arrays;
import java.util.List;

/**
 * System command to suspend the server.
 *
 * @author nbrest
 */
public class SuspendSystemCommand extends KameHouseShellSystemCommand {

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected List<String> getWindowsCommand() {
    return Arrays.asList("rundll32.exe", "powrprof.dll,SetSuspendState", "0,1,0");
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/shutdown/suspend.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return null;
  }
}
