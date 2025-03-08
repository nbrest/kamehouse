package com.nicobrest.kamehouse.commons.model.kamehousecommand;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * JvncSenderKameHouseCommand test.
 *
 * @author nbrest
 */
class JvncSenderKameHouseCommandTest {

  /**
   * Test execute jvncsender text command.
   */
  @Test
  void executeSendTextTest() {
    TextJvncSenderKameHouseCommand jvncSenderTextKameHouseCommand = new TextJvncSenderKameHouseCommand(
        "");

    var result = jvncSenderTextKameHouseCommand.execute();

    assertNotNull(result);
    assertTrue(result.getCommand().contains("Command executed has sensitive information"),
        "Invalid kameHouseCommandResult command");
  }

  /**
   * Test execute jvncsender mouse click command.
   */
  @Test
  void executeMouseClickTest() {
    MouseClickJvncSenderKameHouseCommand mouseClickJvncSenderKameHouseCommand =
        new MouseClickJvncSenderKameHouseCommand(0, 0, 1);

    var result = mouseClickJvncSenderKameHouseCommand.execute();

    assertNotNull(result);
    assertTrue(result.getCommand().contains("Command executed has sensitive information"),
        "Invalid kameHouseCommandResult command");
  }
}
