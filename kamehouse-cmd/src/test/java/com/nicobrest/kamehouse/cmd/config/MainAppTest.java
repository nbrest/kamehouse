package com.nicobrest.kamehouse.cmd.config;

import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.when;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.ProcessUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

/**
 * Tests for the MainApp. It uses the application context from the main code so it loads
 * all the beans and triggers the flow in all the classes.
 *
 * @author nbrest
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ProcessUtils.class, EncryptionUtils.class, FileUtils.class })
public class MainAppTest {

  @Before
  public void before() {
    PowerMockito.mockStatic(ProcessUtils.class, EncryptionUtils.class, FileUtils.class);
  }

  /**
   * Tests running kamehouse-cmd with operation decrypt.
   */
  @Test
  public void decryptSuccessfulTest() throws IOException {
    String[] args = new String[] { "-o", "decrypt", "-if", "in.enc", "-of", "out.dec"};
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
    String[] args = new String[] { "-o", "encrypt", "-if", "in.txt", "-of", "out.enc", "-v"};
    MainApp.main(args);
    // no exceptions thrown

    when(FileUtils.readFileToByteArray(any())).thenCallRealMethod();
    MainApp.main(args);
    // no exceptions thrown
  }
}
