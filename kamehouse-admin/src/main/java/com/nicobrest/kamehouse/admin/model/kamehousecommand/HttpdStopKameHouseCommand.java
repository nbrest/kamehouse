package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;
import java.util.Collections;
import java.util.List;

/**
 * KameHouse command to stop the httpd server.
 *
 * @author nbrest
 */
public class HttpdStopKameHouseCommand extends KameHouseShellScript {

  @Override
  public boolean hasSensitiveInformation() {
    return false;
  }

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
    return Collections.emptyList();
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
