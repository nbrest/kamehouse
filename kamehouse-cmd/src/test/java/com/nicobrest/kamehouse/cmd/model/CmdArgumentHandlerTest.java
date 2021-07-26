package com.nicobrest.kamehouse.cmd.model;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class CmdArgumentHandlerTest {

  @Test
  public void cmdArgumentParserSuccessfulTest() {
    String[] args = new String[] { "-o", "encrypt", "-if", "in.txt", "-of", "out.enc"};
    CmdArgumentHandler cmdArgumentHandler = new CmdArgumentHandler(args);
    assertNotNull(cmdArgumentHandler);
  }
}
