package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

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
  protected String getLinuxKameHouseShellScript() {
    return "lin/kamehouse/httpd-status.sh";
  }
}
