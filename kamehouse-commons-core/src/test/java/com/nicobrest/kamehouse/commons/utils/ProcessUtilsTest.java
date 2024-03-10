package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * ProcessUtils tests.
 *
 * @author nbrest
 */
class ProcessUtilsTest {

  /**
   * Execute process through the process utils test. I can modify the command in this test to test
   * other commands I expect to execute through the ProcessUtils.
   */
  @Test
  void executeProcessTest() throws IOException, InterruptedException {
    List<String> command;
    if (PropertiesUtils.isWindowsHost()) {
      command = Arrays.asList("cmd.exe", "/c", "start", "/min", "exit");
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

  /**
   * Execute process through the process utils test. I can modify the command in this test to test
   * other commands I expect to execute through the ProcessUtils.
   */
  @Test
  void executeProcessWithTimeoutTest() throws IOException, InterruptedException {
    List<String> command;
    if (PropertiesUtils.isWindowsHost()) {
      command = Arrays.asList("cmd.exe", "/c", "start", "/min", "exit");
    } else {
      command = Arrays.asList("/bin/bash", "-c", "exit");
    }

    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command(command);

    Process process = ProcessUtils.start(processBuilder);
    assertNotNull(process);

    boolean finished = ProcessUtils.waitFor(process, 60L);
    if (!finished) {
      Assertions.fail("process didn't finish in the expected timeout");
    }
    InputStream inputStream = ProcessUtils.getInputStream(process);
    InputStream errorStream = ProcessUtils.getErrorStream(process);
    assertNotNull(inputStream);
    assertNotNull(errorStream);

    int exitValue = ProcessUtils.getExitValue(process);
    assertEquals(0, exitValue);
  }
}
