package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import java.util.List;

/**
 * System command to start the httpd server.
 *
 * @author nbrest
 */
public class HttpdStartSystemCommand extends KameHouseShellSystemCommand {

  @Override
  public boolean isDaemon() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "kamehouse/httpd-startup.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    return null;
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "kamehouse/httpd-startup.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return null;
  }
}
