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
   * Test execute jvncsender command.
   */
  @Test
  void executeTest() {
    TextJvncSenderKameHouseCommand jvncSenderTextKameHouseCommand = new TextJvncSenderKameHouseCommand(
        "");
    KameHouseCommandResult kameHouseCommandResult = jvncSenderTextKameHouseCommand.execute();
    assertNotNull(kameHouseCommandResult);
    assertTrue(
        kameHouseCommandResult.getCommand().contains("Command executed has sensitive information"),
        "Invalid kameHouseCommandResult command");
  }
}
