package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AbstractKameHouseCommandTest;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;

/**
 * Test kamehouse command.
 */
class WolKameHouseCommandTest extends AbstractKameHouseCommandTest {

  @Override
  protected KameHouseCommand getKameHouseCommand() {
    return new WolKameHouseCommand("AA:BB:CC:DD:EE:FF", "192.168.99.255");
  }

  @Override
  protected String getWindowsShellCommand() {
    return "kamehouse/cmd/kamehouse-cmd.sh -o wol -mac AA:BB:CC:DD:EE:FF -broadcast 192.168.99.255";
  }

  @Override
  protected String getLinuxShellCommand() {
    return "kamehouse/cmd/kamehouse-cmd.sh -o wol -mac AA:BB:CC:DD:EE:FF -broadcast 192.168.99.255";
  }
}
