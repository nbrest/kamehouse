package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import java.util.List;

/**
 * System command to check the uptime of the server.
 *
 * @author nbrest
 */
public class UptimeSystemCommand extends KameHouseShellSystemCommand {

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "win/sysadmin/uptime.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    return null;
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/sysadmin/uptime.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return null;
  }
}
