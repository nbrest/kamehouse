package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import java.util.List;

/**
 * System command to stop the httpd server.
 *
 * @author nbrest
 */
public class HttpdStopSystemCommand extends KameHouseShellSystemCommand {

  @Override
  public int getSleepTime() {
    return 7;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "win/kamehouse/httpd-stop.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    return null;
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/kamehouse/httpd-stop.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return null;
  }
}
