package com.nicobrest.kamehouse.commons.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand.Output;
import com.nicobrest.kamehouse.commons.model.systemcommand.TextJvncSenderSystemCommand;
import org.junit.jupiter.api.Test;

/**
 * JvncSenderSystemCommand test.
 *
 * @author nbrest
 */
class JvncSenderSystemCommandTest {

  /**
   * Test execute jvncsender command.
   */
  @Test
  void executeTest() {
    TextJvncSenderSystemCommand jvncSenderTextSystemCommand = new TextJvncSenderSystemCommand("");
    Output output = jvncSenderTextSystemCommand.execute();
    assertNotNull(output);
    assertTrue(output.getCommand().contains("jvncsender"), "Invalid output command");
  }
}
