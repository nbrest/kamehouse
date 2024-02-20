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
class CmdArgumentHandlerTest {

  private MockedStatic<ProcessUtils> processUtilsMockedStatic;

  @BeforeEach
  public void before() {
    processUtilsMockedStatic = Mockito.mockStatic(ProcessUtils.class);
  }

  @AfterEach
  public void close() {
    processUtilsMockedStatic.close();
  }

  /**
   * Tests parsing the arguments successfully for operation encrypt.
   */
  @Test
  void encryptSuccessfulTest() {
    String[] args = new String[]{"-o", "encrypt", "-if", "in.txt", "-of", "out.enc"};
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
  void encryptErrorTest() {
    String[] args = new String[]{"-o", "encrypt", "-if", "in.txt"};
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    assertNotNull(cmdArgumentHandler);
    assertEquals(Operation.ENCRYPT, cmdArgumentHandler.getOperation());
    assertEquals("in.txt", cmdArgumentHandler.getArgument("if"));
    assertNull(cmdArgumentHandler.getArgument("of"));
  }

  /**
   * Tests parsing the arguments successfully for operation decrypt.
   */
  @Test
  void decryptSuccessfulTest() {
    String[] args = new String[]{"-o", "decrypt", "-if", "in.enc", "-of", "out.txt"};
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    assertNotNull(cmdArgumentHandler);
    assertEquals(Operation.DECRYPT, cmdArgumentHandler.getOperation());
    assertEquals("in.enc", cmdArgumentHandler.getArgument("if"));
    assertEquals("out.txt", cmdArgumentHandler.getArgument("of"));
  }


  /**
   * Tests parsing the arguments successfully for operation jvncsender.
   */
  @Test
  void jvncSenderTextSuccessfulTest() {
    String[] args = new String[]{"-o", "jvncsender", "-host", "goku-server", "-password",
        "gokupass", "-port", "5900", "-text", "madamadadane"};
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    assertNotNull(cmdArgumentHandler);
    assertEquals(Operation.JVNCSENDER, cmdArgumentHandler.getOperation());
    assertEquals("goku-server", cmdArgumentHandler.getArgument("host"));
    assertEquals("gokupass", cmdArgumentHandler.getArgument("password"));
    assertEquals("5900", cmdArgumentHandler.getArgument("port"));
    assertEquals("madamadadane", cmdArgumentHandler.getArgument("text"));
  }

  /**
   * Tests parsing the arguments successfully for operation jvncsender.
   */
  @Test
  void jvncSenderMouseClickSuccessfulTest() {
    String[] args = new String[]{"-o", "jvncsender", "-host", "goku-server", "-password",
        "gokupass", "-port", "5900", "-mouseClick", "LEFT,100,100,1"};
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    assertNotNull(cmdArgumentHandler);
    assertEquals(Operation.JVNCSENDER, cmdArgumentHandler.getOperation());
    assertEquals("goku-server", cmdArgumentHandler.getArgument("host"));
    assertEquals("gokupass", cmdArgumentHandler.getArgument("password"));
    assertEquals("5900", cmdArgumentHandler.getArgument("port"));
    assertEquals("LEFT,100,100,1", cmdArgumentHandler.getArgument("mouseClick"));
  }

  /**
   * Tests parsing the arguments with error for operation jvncsender.
   */
  @Test
  void jvncSenderNoTextOrMouseClickErrorTest() {
    String[] args = new String[]{"-o", "jvncsender", "-host", "goku-server", "-password",
        "gokupass", "-port", "5900"};
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    assertNotNull(cmdArgumentHandler);
    assertEquals(Operation.JVNCSENDER, cmdArgumentHandler.getOperation());
    assertEquals("goku-server", cmdArgumentHandler.getArgument("host"));
    assertEquals("gokupass", cmdArgumentHandler.getArgument("password"));
    assertEquals("5900", cmdArgumentHandler.getArgument("port"));
    assertNull(cmdArgumentHandler.getArgument("mouseClick"));
  }

  /**
   * Tests parsing the arguments successfully for operation wol.
   */
  @Test
  void wolSuccessfulTest() {
    String[] args = new String[]{"-o", "wol", "-mac", "aa:bb:cc:dd:ee", "-broadcast",
        "192.168.29.255"};
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    assertNotNull(cmdArgumentHandler);
    assertEquals(Operation.WOL, cmdArgumentHandler.getOperation());
    assertEquals("aa:bb:cc:dd:ee", cmdArgumentHandler.getArgument("mac"));
    assertEquals("192.168.29.255", cmdArgumentHandler.getArgument("broadcast"));
  }

  /**
   * Tests parsing the arguments for help.
   */
  @Test
  void helpTest() {
    String[] args = new String[]{"-h"};
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
  void invalidOperationTest() {
    String[] args = new String[]{"-o", "dinner-out"};
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    assertNotNull(cmdArgumentHandler);
    assertNull(cmdArgumentHandler.getOperation());
    assertNull(cmdArgumentHandler.getArgument("if"));
    assertNull(cmdArgumentHandler.getArgument("of"));
  }
}
