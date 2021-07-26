package com.nicobrest.kamehouse.cmd.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import com.nicobrest.kamehouse.commons.utils.ProcessUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit tests for the CmdArgumentHandler.
 *
 * @author nbrest
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ProcessUtils.class })
public class CmdArgumentHandlerTest {

  @Before
  public void before() {
    PowerMockito.mockStatic(ProcessUtils.class);
  }

  /**
   * Tests parsing the arguments successfully for operation encrypt.
   */
  @Test
  public void encryptSuccessfulTest() {
    String[] args = new String[] { "-o", "encrypt", "-if", "in.txt", "-of", "out.enc"};
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    assertNotNull(cmdArgumentHandler);
    assertEquals(Operation.ENCRYPT, cmdArgumentHandler.getOperation());
    assertEquals("in.txt", cmdArgumentHandler.getArgument("if"));
    assertEquals("out.enc", cmdArgumentHandler.getArgument("of"));
  }

  /**
   * Tests error parsing the arguments for operation encrypt.
   */
  @Test
  public void encryptErrorTest() {
    String[] args = new String[] { "-o", "encrypt", "-if", "in.txt"};
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    assertNotNull(cmdArgumentHandler);
    assertEquals(Operation.ENCRYPT, cmdArgumentHandler.getOperation());
    assertEquals("in.txt", cmdArgumentHandler.getArgument("if"));
    assertNull(cmdArgumentHandler.getArgument("of"));
  }

  /**
   * Tests parsing the arguments for help.
   */
  @Test
  public void helpTest() {
    String[] args = new String[] { "-h"};
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    assertNotNull(cmdArgumentHandler);
    assertNull(cmdArgumentHandler.getOperation());
    assertNull(cmdArgumentHandler.getArgument("if"));
    assertNull(cmdArgumentHandler.getArgument("of"));
  }


  /**
   * Tests an invalid operation.
   */
  @Test
  public void invalidOperationTest() {
    String[] args = new String[] { "-o", "dinner-out"};
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    assertNotNull(cmdArgumentHandler);
    assertNull(cmdArgumentHandler.getOperation());
    assertNull(cmdArgumentHandler.getArgument("if"));
    assertNull(cmdArgumentHandler.getArgument("of"));
  }

}
