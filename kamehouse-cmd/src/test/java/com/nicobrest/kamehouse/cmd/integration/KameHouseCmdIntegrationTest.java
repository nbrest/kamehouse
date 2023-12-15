package com.nicobrest.kamehouse.cmd.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nicobrest.kamehouse.commons.utils.ProcessUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration tests for KameHouseCmd app.
 *
 * @author nbrest
 */
class KameHouseCmdIntegrationTest {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private static final String KAMEHOUSE_CMD_WIN = PropertiesUtils.getUserHome()
      + "\\programs\\kamehouse-cmd\\bin\\kamehouse-cmd.bat";
  private static final String KAMEHOUSE_CMD_LIN = PropertiesUtils.getUserHome()
      + "/programs/kamehouse-cmd/bin/kamehouse-cmd.sh";
  private static final String IN_FILE_PATH = PropertiesUtils.getUserHome()
      + "/kamehouse-cmd-integration-tests-in-file.txt";
  private static final File DECRYPTED_FILE = new File(IN_FILE_PATH);
  private static final String OUT_FILE_PATH = PropertiesUtils.getUserHome()
      + "/kamehouse-cmd-integration-tests-out-file.enc";
  private static final File ENCRYPTED_FILE = new File(OUT_FILE_PATH);

  /**
   * Execute encrypt and decrypt operations.
   */
  @Test
  void encryptAndDecryptTest() throws IOException, InterruptedException {
    // encrypt file
    if (!DECRYPTED_FILE.exists()) {
      DECRYPTED_FILE.createNewFile();
    }
    if (ENCRYPTED_FILE.exists()) {
      ENCRYPTED_FILE.delete();
    }
    List<String> command = getEncryptCommand();

    execute(command);

    assertTrue(ENCRYPTED_FILE.exists(), "Couldn't create encrypted file");
    logger.info("Finished executing {} successfully", command);

    // decrypt file
    command = getDecryptCommand();

    execute(command);

    assertTrue(DECRYPTED_FILE.exists(), "Couldn't create decrypted file");
    ENCRYPTED_FILE.delete();
    logger.info("Finished executing {} successfully", command);
  }

  /**
   * Execute jvncsender text operation to an invalid host.
   */
  @Test
  void jVncSenderTextTest() throws IOException, InterruptedException {
    List<String> command = getJvncSenderTextCommand();
    execute(command, List.of(0, 255));

    logger.info("Finished executing {}", command);
  }

  /**
   * Execute jvncsender mouse click operation to an invalid host.
   */
  @Test
  void jVncSenderMouseClickTest() throws IOException, InterruptedException {
    List<String> command = getJvncSenderMouseClickCommand();
    execute(command, List.of(0, 255));

    logger.info("Finished executing {}", command);
  }

  /**
   * Execute the specified command line process.
   */
  private void execute(List<String> command) throws IOException, InterruptedException {
    execute(command, List.of(0));
  }

  /**
   * Execute the specified command line process with the expected ouputs.
   */
  private void execute(List<String> command, List<Integer> expectedOutputs)
      throws InterruptedException, IOException {
    logger.info("Executing {}", command);
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command(command);

    Process process = ProcessUtils.start(processBuilder);

    ProcessUtils.waitFor(process);
    int exitValue = ProcessUtils.getExitValue(process);
    String output = IOUtils.toString(ProcessUtils.getInputStream(process), StandardCharsets.UTF_8);
    String error = IOUtils.toString(ProcessUtils.getErrorStream(process), StandardCharsets.UTF_8);
    logger.info("Output: {}", output);
    logger.info("Error: {}", error);
    assertTrue(expectedOutputs.contains(exitValue));
  }

  /**
   * Get the encrypt command.
   */
  private List<String> getEncryptCommand() {
    return getCommand(getEncryptOperation());
  }

  /**
   * Get the decrypt command.
   */
  private List<String> getDecryptCommand() {
    return getCommand(getDecryptOperation());
  }

  /**
   * Get the jvncsender text command.
   */
  private List<String> getJvncSenderTextCommand() {
    return getCommand(getJvncSenderTextOperation());
  }

  /**
   * Get the jvncsender mouse click command.
   */
  private List<String> getJvncSenderMouseClickCommand() {
    return getCommand(getJvncSenderMouseClickOperation());
  }

  /**
   * Get the command as a list of strings.
   */
  private List<String> getCommand(String operationCommand) {
    List<String> command = new ArrayList<>();;
    if (PropertiesUtils.isWindowsHost()) {
      command.addAll(List.of("cmd.exe", "/c", "start", "/min", KAMEHOUSE_CMD_WIN));
    } else {
      command.add(KAMEHOUSE_CMD_LIN);
    }
    command.addAll(Arrays.asList(operationCommand.split(" ")));
    return command;
  }

  /**
   * Get encrypt operation.
   */
  private String getEncryptOperation() {
    return " -o encrypt -if " + getAbsoluteFilePath(DECRYPTED_FILE)
        + " -of " + getAbsoluteFilePath(ENCRYPTED_FILE);
  }

  /**
   * Get decrypt operation.
   */
  private String getDecryptOperation() {
    return " -o decrypt -if " + getAbsoluteFilePath(ENCRYPTED_FILE)
        + " -of " + getAbsoluteFilePath(DECRYPTED_FILE);
  }

  /**
   * Get jvncsender text operation.
   */
  private String getJvncSenderTextOperation() {
    return " -o jvncsender -host \"invalid-host\" -port 5900 -password \"d\" -text \"<ESC>\"";
  }

  /**
   * Get jvncsender mouse click operation.
   */
  private String getJvncSenderMouseClickOperation() {
    return " -o jvncsender -host \"invalid-host\" -port 5900 -password \"d\" -mouseClick \"1,1,1\"";
  }

  /**
   * Get the absolute path for a file.
   */
  private String getAbsoluteFilePath(File file) {
    String filePath = file.getAbsolutePath();
    if (PropertiesUtils.isWindowsHost()) {
      filePath = filePath.replace("\\", "/");
    }
    return filePath;
  }
}
