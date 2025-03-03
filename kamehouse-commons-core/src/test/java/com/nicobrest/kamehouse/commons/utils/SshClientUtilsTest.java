package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.model.SystemCommandStatus;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand.Output;
import com.nicobrest.kamehouse.commons.model.systemcommand.TestDaemonCommand;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ChannelShell;
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
class SshClientUtilsTest {

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
  private ConnectFuture connectFutureMock1;

  @Mock
  private AuthFuture authFutureMock;

  @Mock
  private ChannelShell channelShellMock;

  @Mock
  private ChannelExec channelExecMock;

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
    Mockito.reset(sshClientMock, clientSessionMock, connectFutureMock, connectFutureMock1,
        authFutureMock, openFutureMock, outputStreamMock, channelShellMock, channelExecMock);
    when(SshClient.setUpDefaultClient()).thenReturn(sshClientMock);
    when(sshClientMock.connect(any(), any(), anyInt())).thenReturn(connectFutureMock);
    when(connectFutureMock.verify(anyLong(), any())).thenReturn(connectFutureMock1);
    when(connectFutureMock.verify(anyLong(), any(), any())).thenReturn(connectFutureMock1);
    when(connectFutureMock1.getSession()).thenReturn(clientSessionMock);
    when(clientSessionMock.auth()).thenReturn(authFutureMock);
    when(clientSessionMock.createShellChannel(any(), any())).thenReturn(channelShellMock);
    when(clientSessionMock.createExecChannel(any())).thenReturn(channelExecMock);
    when(channelShellMock.open()).thenReturn(openFutureMock);
    when(channelShellMock.getInvertedIn()).thenReturn(outputStreamMock);
    when(channelExecMock.open()).thenReturn(openFutureMock);
    when(channelExecMock.getInvertedIn()).thenReturn(outputStreamMock);

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
  void executeSuccessTest() {
    Output output = SshClientUtils.execute("local.kamehouse.com", "goku",
        new TestDaemonCommand());

    assertEquals("[/bin/bash, -c, ./programs/kamehouse-shell/bin/test-script.sh]",
        output.getCommand());
    assertEquals(SystemCommandStatus.COMPLETED.getStatus(), output.getStatus());
    assertEquals(0, output.getExitCode());
    assertEquals(-1, output.getPid());
    assertEquals(Arrays.asList(""), output.getStandardOutput());
    assertNull(output.getStandardError());
  }

  /**
   * Execute an ssh command throwing a IOException test.
   */
  @Test
  void executeIOExceptionTest() throws IOException {
    when(channelExecMock.open()).thenThrow(new IOException("Test Exception"));

    Output output = SshClientUtils.execute("local.kamehouse.com", "goku",
        new TestDaemonCommand());

    assertEquals("[/bin/bash, -c, ./programs/kamehouse-shell/bin/test-script.sh]",
        output.getCommand());
    assertEquals(SystemCommandStatus.FAILED.getStatus(), output.getStatus());
    assertEquals(1, output.getExitCode());
    assertEquals(-1, output.getPid());
    assertEquals(null, output.getStandardOutput());
    assertEquals(null, output.getStandardError());
  }
}
