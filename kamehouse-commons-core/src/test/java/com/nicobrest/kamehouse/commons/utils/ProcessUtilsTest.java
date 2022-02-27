package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * ProcessUtils tests.
 *
 * @author nbrest
 */
public class ProcessUtilsTest {

  /**
   * Execute process through the process utils test. I can modify the command in this test to test
   * other commands I expect to execute through the ProcessUtils.
   */
  @Test
  public void executeProcessTest() throws IOException, InterruptedException {
    List<String> command;
    if (PropertiesUtils.isWindowsHost()) {
      command = Arrays.asList("cmd.exe", "/c", "start", "exit");
    } else {
      command = Arrays.asList("/bin/bash", "-c", "exit");
    }

    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command(command);

    Process process = ProcessUtils.start(processBuilder);
    assertNotNull(process);

    ProcessUtils.waitFor(process);
    InputStream inputStream = ProcessUtils.getInputStream(process);
    InputStream errorStream = ProcessUtils.getErrorStream(process);
    assertNotNull(inputStream);
    assertNotNull(errorStream);

    int exitValue = ProcessUtils.getExitValue(process);
    assertEquals(0, exitValue);
  }
}