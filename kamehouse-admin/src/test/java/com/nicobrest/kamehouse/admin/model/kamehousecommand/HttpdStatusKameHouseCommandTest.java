package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class HttpdStatusKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new HttpdStatusKameHouseCommand();
  }

  @Override
  protected String getWindowsShellCommand() {
    return "kamehouse/httpd/httpd-status.sh";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "kamehouse/httpd/httpd-status.sh";
  }
}
