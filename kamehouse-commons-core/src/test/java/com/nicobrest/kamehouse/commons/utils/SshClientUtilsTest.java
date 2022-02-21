package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.model.TestDaemonCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand.Output;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.future.OpenFuture;
import org.apache.sshd.client.session.ClientSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * SshClientUtils tests.
 *
 * @author nbrest
 */
public class SshClientUtilsTest {

  private MockedStatic<EncryptionUtils> encryptionUtils;
  private MockedStatic<PropertiesUtils> propertiesUtils;
  private MockedStatic<SshClient> sshClient;

  private static final String SAMPLE_PUBLIC = "src/test/resources/commons/keys/public_key.pem";
  private static final String SAMPLE_PRIVATE = "src/test/resources/commons/keys/private_key.pem";

  @Mock
  private SshClient sshClientMock;

  @Mock
  private ClientSession clientSessionMock;

  @Mock
  private ConnectFuture connectFutureMock;

  @Mock
  private AuthFuture authFutureMock;

  @Mock
  private ClientChannel channelMock;

  @Mock
  private OpenFuture openFutureMock;

  @Mock
  private OutputStream outputStreamMock;

  /**
   * Tests setup.
   */
  @BeforeEach
  public void before() throws IOException {
    encryptionUtils = Mockito.mockStatic(EncryptionUtils.class);
    propertiesUtils = Mockito.mockStatic(PropertiesUtils.class);
    sshClient = Mockito.mockStatic(SshClient.class);
    MockitoAnnotations.openMocks(this);
    Mockito.reset(sshClientMock, clientSessionMock, connectFutureMock, authFutureMock, channelMock,
        openFutureMock, outputStreamMock);
    when(SshClient.setUpDefaultClient()).thenReturn(sshClientMock);
    when(sshClientMock.connect(any(), any(), anyInt())).thenReturn(connectFutureMock);
    when(connectFutureMock.verify(anyLong(), any())).thenReturn(connectFutureMock);
    when(connectFutureMock.getSession()).thenReturn(clientSessionMock);
    when(clientSessionMock.auth()).thenReturn(authFutureMock);
    when(clientSessionMock.createChannel(any(), any())).thenReturn(channelMock);
    when(channelMock.open()).thenReturn(openFutureMock);
    when(channelMock.getInvertedIn()).thenReturn(outputStreamMock);

    when(PropertiesUtils.getUserHome()).thenReturn("."); // Use git project root as home
    when(PropertiesUtils.getProperty("ssh.public.key")).thenReturn(SAMPLE_PUBLIC);
    when(PropertiesUtils.getProperty("ssh.private.key")).thenReturn(SAMPLE_PRIVATE);

    when(EncryptionUtils.decryptKameHouseFileToString(any())).thenReturn("password");
  }

  /**
   * Tests cleanup.
   */
  @AfterEach
  public void close() {
    encryptionUtils.close();
    propertiesUtils.close();
    sshClient.close();
  }

  /**
   * Execute a successful ssh command test.
   */
  @Test
  public void executeSuccessTest() {
    Output output = SshClientUtils.execute("local.kamehouse.com", "goku",
        new TestDaemonCommand("1"));

    assertEquals("[vncdo (hidden as it contains passwords)]", output.getCommand());
    assertEquals("completed", output.getStatus());
    assertEquals(-1, output.getExitCode());
    assertEquals(-1, output.getPid());
    assertEquals(Arrays.asList(""), output.getStandardOutput());
    assertEquals(Arrays.asList(""), output.getStandardError());
  }

  /**
   * Execute an ssh command throwing a IOException test.
   */
  @Test
  public void executeIOExceptionTest() throws IOException {
    when(channelMock.open()).thenThrow(new IOException("Test Exception"));

    Output output = SshClientUtils.execute("local.kamehouse.com", "goku",
        new TestDaemonCommand("1"));

    assertEquals("[vncdo (hidden as it contains passwords)]", output.getCommand());
    assertEquals("failed", output.getStatus());
    assertEquals(-1, output.getExitCode());
    assertEquals(-1, output.getPid());
    assertEquals(null, output.getStandardOutput());
    assertEquals(null, output.getStandardError());
  }
}
