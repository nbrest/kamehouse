package com.nicobrest.kamehouse.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class to execute and manage the execution of system processes so that they are
 * decoupled from the classes that require to call external processes and
 * it's also easier to mock them in test.
 * 
 * @author nbrest
 *
 */
public class ProcessUtils {

  public static Process startProcess(ProcessBuilder processBuilder) throws IOException {
    return processBuilder.start();
  }
  
  public static void waitForProcess(Process process) throws InterruptedException {
    process.waitFor();
  }
  
  public static int getExitValue(Process process) {
    return process.exitValue();
  }

  public static InputStream getInputStreamFromProcess(Process process) {
    return process.getInputStream();
  }

  public static InputStream getErrorStreamFromProcess(Process process) {
    return process.getErrorStream();
  }
}
