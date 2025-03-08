package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class HttpdStopKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new HttpdStopKameHouseCommand();
  }

  @Override
  protected String getWindowsShellCommand() {
    return "win/kamehouse/httpd-stop.sh";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "lin/kamehouse/httpd-stop.sh";
  }
}
