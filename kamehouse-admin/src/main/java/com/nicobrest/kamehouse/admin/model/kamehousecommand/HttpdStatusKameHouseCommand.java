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
    return "kamehouse/httpd/httpd-status.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "kamehouse/httpd/httpd-status.sh";
  }
}
