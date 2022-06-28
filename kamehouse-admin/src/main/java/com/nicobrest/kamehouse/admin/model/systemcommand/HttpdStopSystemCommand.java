package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import java.util.Arrays;
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
  protected List<String> getWindowsCommand() {
    return Arrays.asList("taskkill", "/im", "httpd.exe", "&", "echo", "Stopping apache httpd");
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
