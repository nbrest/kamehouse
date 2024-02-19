package com.nicobrest.kamehouse.commons.model;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.JvncSenderSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.VncDoKeyPressSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.VncDoMouseClickSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.VncDoTypeSystemCommand;

/**
 * Test KameHouseSystemCommand to test the SystemCommandService. VncDo replaced now with
 * {@link JvncSenderSystemCommand}.
 *
 * @author nbrest
 */
public class TestKameHouseSystemCommand extends KameHouseSystemCommand {

  /**
   * Test KameHouseSystemCommand to test the SystemCommandService.
   */
  public TestKameHouseSystemCommand() {
    systemCommands.add(new VncDoMouseClickSystemCommand("1", "400", "400"));
    systemCommands.add(new VncDoKeyPressSystemCommand("1"));
    systemCommands.add(new VncDoTypeSystemCommand("22"));
  }
}
