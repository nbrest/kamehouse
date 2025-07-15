package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;
import java.util.Collections;
import java.util.List;

/**
 * KameHouse command to get the status of the httpd server.
 *
 * @author nbrest
 */
public class HttpdStatusKameHouseCommand extends KameHouseShellScript {

  @Override
  public boolean hasSensitiveInformation() {
    return false;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "win/kamehouse/httpd-status.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    return Collections.emptyList();
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
