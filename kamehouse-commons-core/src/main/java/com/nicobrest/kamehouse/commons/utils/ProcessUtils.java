package com.nicobrest.kamehouse.commons.utils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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
  public static void waitFor(Process process) throws InterruptedException {
    process.waitFor();
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
  public static InputStream getInputStream(Process process) {
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
   * <bold>DONT'T call this from any webapp!!!.</bold>
   *
   */
  @SuppressFBWarnings(value = "DM_EXIT", justification = "It's ok to exit here")
  public static void exitProcess(int status) {
    System.exit(status);
  }
}
