package com.nicobrest.kamehouse.cmd.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nicobrest.kamehouse.commons.utils.ProcessUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Unit tests for the CmdArgumentHandler.
 *
 * @author nbrest
 */
public class CmdArgumentHandlerTest {

  private MockedStatic<ProcessUtils> processUtilsMockedStatic;

  @BeforeEach
  public void before() {
    processUtilsMockedStatic = Mockito.mockStatic(ProcessUtils.class);
  }

  @AfterEach
  public void close() {
    processUtilsMockedStatic.close();
  }

  /** Tests parsing the arguments successfully for operation encrypt. */
  @Test
  public void encryptSuccessfulTest() {
    String[] args = new String[] {"-o", "encrypt", "-if", "in.txt", "-of", "out.enc"};
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    assertNotNull(cmdArgumentHandler);
    assertEquals(Operation.ENCRYPT, cmdArgumentHandler.getOperation());
    assertEquals("in.txt", cmdArgumentHandler.getArgument("if"));
    assertEquals("out.enc", cmdArgumentHandler.getArgument("of"));
  }

  /** Tests error parsing the arguments for operation encrypt. */
  @Test
  public void encryptErrorTest() {
    String[] args = new String[] {"-o", "encrypt", "-if", "in.txt"};
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    assertNotNull(cmdArgumentHandler);
    assertEquals(Operation.ENCRYPT, cmdArgumentHandler.getOperation());
    assertEquals("in.txt", cmdArgumentHandler.getArgument("if"));
    assertNull(cmdArgumentHandler.getArgument("of"));
  }

  /** Tests parsing the arguments for help. */
  @Test
  public void helpTest() {
    String[] args = new String[] {"-h"};
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    assertNotNull(cmdArgumentHandler);
    assertNull(cmdArgumentHandler.getOperation());
    assertNull(cmdArgumentHandler.getArgument("if"));
    assertNull(cmdArgumentHandler.getArgument("of"));
  }

  /** Tests an invalid operation. */
  @Test
  public void invalidOperationTest() {
    String[] args = new String[] {"-o", "dinner-out"};
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    assertNotNull(cmdArgumentHandler);
    assertNull(cmdArgumentHandler.getOperation());
    assertNull(cmdArgumentHandler.getArgument("if"));
    assertNull(cmdArgumentHandler.getArgument("of"));
  }
}
