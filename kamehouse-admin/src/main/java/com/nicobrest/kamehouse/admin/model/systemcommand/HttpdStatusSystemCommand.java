package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import java.util.Arrays;
import java.util.List;

/**
 * System command to get the status of the httpd server.
 *
 * @author nbrest
 */
public class HttpdStatusSystemCommand extends KameHouseShellSystemCommand {

  @Override
  protected List<String> getWindowsCommand() {
    return Arrays.asList("tasklist", "/FI", "IMAGENAME eq httpd.exe");
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/kamehouse/httpd-status.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return null;
  }
}
