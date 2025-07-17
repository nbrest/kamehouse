package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;

/**
 * KameHouse command to start the httpd server.
 *
 * @author nbrest
 */
public class HttpdStartKameHouseCommand extends KameHouseShellScript {

  @Override
  public boolean hasSensitiveInformation() {
    return false;
  }

  @Override
  public boolean isDaemon() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "kamehouse/httpd/httpd-startup.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "kamehouse/httpd/httpd-startup.sh";
  }
}
