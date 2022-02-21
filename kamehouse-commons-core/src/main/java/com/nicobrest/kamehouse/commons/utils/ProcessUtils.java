package com.nicobrest.kamehouse.commons.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class to execute and manage the execution of system processes so that they are decoupled
 * from the classes that require to call external processes and it's also easier to mock them in
 * test.
 *
 * @author nbrest
 */
public class ProcessUtils {

  private ProcessUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Starts the specified process.
   */
  public static Process start(ProcessBuilder processBuilder) throws IOException {
    return processBuilder.start();
  }

  /**
   * Waits for the specified process to finish.
   */
  public static int waitFor(Process process) throws InterruptedException {
    process.waitFor();
    return 0;
  }

  /**
   * Gets the exit value of the specified process.
   */
  public static int getExitValue(Process process) {
    return process.exitValue();
  }

  /**
   * Gets the input stream from the specified process.
   */
  public static InputStream getInputStream(Process process) throws IOException {
    return process.getInputStream();
  }

  /**
   * Gets the error stream from the specified process.
   */
  public static InputStream getErrorStream(Process process) {
    return process.getErrorStream();
  }

  /**
   * Exits the entire process and kills the JVM.
   *
   * <p><bold>DONT'T call this from any webapp!!!.</bold>
   */
  public static void exitProcess(int status) {
    System.exit(status);
  }
}
