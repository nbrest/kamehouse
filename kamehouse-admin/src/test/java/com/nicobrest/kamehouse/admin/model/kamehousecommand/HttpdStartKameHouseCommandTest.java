package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class HttpdStartKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new HttpdStartKameHouseCommand();
  }

  @Override
  protected String getWindowsShellCommand() {
    return "kamehouse/httpd-startup.sh";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "kamehouse/httpd-startup.sh";
  }
}
