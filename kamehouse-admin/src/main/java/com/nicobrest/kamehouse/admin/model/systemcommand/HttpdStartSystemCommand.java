package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import java.util.Arrays;
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
  protected List<String> getWindowsCommand() {
    String userHome = PropertiesUtils.getUserHome();
    String httpdExecutableWin = PropertiesUtils.getProperty("httpd.executable.win");
    return Arrays.asList(userHome + httpdExecutableWin);
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
