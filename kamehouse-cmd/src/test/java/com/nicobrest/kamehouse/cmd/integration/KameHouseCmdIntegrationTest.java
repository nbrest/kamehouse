package com.nicobrest.kamehouse.cmd.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.nicobrest.kamehouse.commons.utils.ProcessUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration tests for KameHouseCmd app.
 *
 * @author nbrest
 */
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class KameHouseCmdIntegrationTest {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private static final String KAMEHOUSE_CMD_WIN = PropertiesUtils.getUserHome()
      + "\\programs\\kamehouse-cmd\\bin\\kamehouse-cmd.bat";
  private static final String IN_FILE_PATH = PropertiesUtils.getUserHome()
      + "/kamehouse-cmd-integration-tests-in-file.txt";
  private static final File DECRYPTED_FILE = new File(IN_FILE_PATH);
  private static final String OUT_FILE_PATH = PropertiesUtils.getUserHome()
      + "/kamehouse-cmd-integration-tests-out-file.enc";
  private static final File ENCRYPTED_FILE = new File(OUT_FILE_PATH);

  @Test
  @Order(1)
  public void encryptTest() throws IOException, InterruptedException {
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
  }

  @Test
  @Order(2)
  public void decryptTest() throws IOException, InterruptedException {
    if (!ENCRYPTED_FILE.exists()) {
      fail("Input encrypted file doesn't exist. Run encryptTest() first");
    }
    if (DECRYPTED_FILE.exists()) {
      DECRYPTED_FILE.delete();
    }
    List<String> command = getDecryptCommand();

    execute(command);

    assertTrue(DECRYPTED_FILE.exists(), "Couldn't create decrypted file");
    ENCRYPTED_FILE.delete();
    logger.info("Finished executing {} successfully", command);
  }

  @Test
  @Order(3)
  public void jVncSenderTest() throws IOException, InterruptedException {
    List<String> command = getJvncSenderCommand();
    execute(command);

    logger.info("Finished executing {} successfully", command);
  }

  /**
   * Execute the specified command line process.
   */
  private void execute(List<String> command) throws InterruptedException, IOException {
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
    assertEquals(0, exitValue);
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
   * Get the jvncsender command.
   */
  private List<String> getJvncSenderCommand() {
    return getCommand(getJvncSenderOperation());
  }

  /**
   * Get the command as a list of strings.
   */
  private List<String> getCommand(String operationCommand) {
    List<String> command;
    if (PropertiesUtils.isWindowsHost()) {
      command = List.of("cmd.exe", "/c", KAMEHOUSE_CMD_WIN + operationCommand);
    } else {
      command = new ArrayList<>();
      command.add("kamehouse-cmd.sh");
      command.addAll(Arrays.asList(getDecryptOperation().split(" ")));
    }
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
   * Get jvncsender operation.
   */
  private String getJvncSenderOperation() {
    String hostname = PropertiesUtils.getHostname();
    return " -o jvncsender -host '" + hostname + "' -port 5900 -password 'd' -text 'A'";
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
