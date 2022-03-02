package com.nicobrest.kamehouse.cmd.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.ProcessUtils;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Tests for the MainApp. It uses the application context from the main code so it loads all the
 * beans and triggers the flow in all the classes.
 *
 * @author nbrest
 */
public class MainAppTest {

  private MockedStatic<ProcessUtils> processUtilsMockedStatic;
  private MockedStatic<EncryptionUtils> encryptionUtilsMockedStatic;
  private MockedStatic<FileUtils> fileUtilsMockedStatic;

  /**
   * Tests setup.
   */
  @BeforeEach
  public void before() {
    processUtilsMockedStatic = Mockito.mockStatic(ProcessUtils.class);
    encryptionUtilsMockedStatic = Mockito.mockStatic(EncryptionUtils.class);
    fileUtilsMockedStatic = Mockito.mockStatic(FileUtils.class);
  }

  /**
   * Tests cleanup.
   */
  @AfterEach
  public void close() {
    processUtilsMockedStatic.close();
    encryptionUtilsMockedStatic.close();
    fileUtilsMockedStatic.close();
  }

  /**
   * Tests running kamehouse-cmd with operation decrypt.
   */
  @Test
  public void decryptSuccessfulTest() throws IOException {
    String[] args = new String[]{"-o", "decrypt", "-if", "in.enc", "-of", "out.dec"};
    MainApp.main(args);
    // no exceptions thrown

    when(FileUtils.readFileToByteArray(any())).thenCallRealMethod();
    MainApp.main(args);
    // no exceptions thrown
  }

  /**
   * Tests running kamehouse-cmd with operation encrypt.
   */
  @Test
  public void encryptSuccessfulTest() throws IOException {
    String[] args = new String[]{"-o", "encrypt", "-if", "in.txt", "-of", "out.enc", "-v"};
    MainApp.main(args);
    // no exceptions thrown

    when(FileUtils.readFileToByteArray(any())).thenCallRealMethod();
    MainApp.main(args);
    // no exceptions thrown
  }

  /**
   * Tests running kamehouse-cmd with operation jvncsender.
   */
  @Test
  public void jVncSenderUnknownHostTest() {
    String[] args = new String[]{"-o", "jvncsender", "-host", "invalid-server", "-port", "5900",
        "-password", "", "-text", "<ESC>"};
    MainApp.main(args);
    // no exceptions thrown
  }

  /**
   * Tests running kamehouse-cmd with operation wol.
   */
  @Test
  public void wolTest() {
    String[] args = new String[]{"-o", "wol", "-mac", "AA:BB:CC:DD:EE:FF", "-broadcast",
        "192.168.29.255"};
    MainApp.main(args);
    // no exceptions thrown
  }
}
