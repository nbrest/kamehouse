package com.nicobrest.kamehouse.cmd.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * Unit tests for the CmdArgumentHandler.
 *
 * @author nbrest
 */
public class CmdArgumentHandlerTest {

  /**
   * Tests parsing the arguments successfully.
   */
  @Test
  public void cmdArgumentHandlerSuccessfulTest() {
    String[] args = new String[] { "-o", "encrypt", "-if", "in.txt", "-of", "out.enc"};
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    assertNotNull(cmdArgumentHandler);
    assertEquals(Operation.ENCRYPT, cmdArgumentHandler.getOperation());
    assertEquals("in.txt", cmdArgumentHandler.getArgument("if"));
    assertEquals("out.enc", cmdArgumentHandler.getArgument("of"));
  }
}
