package com.nicobrest.kamehouse.commons.utils;

import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.model.KameHouseCommandStatus;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.Charsets;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.PtyChannelConfiguration;
import org.apache.sshd.common.future.CancelOption;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to manage all the ssh client logic.
 *
 * @author nbrest
 */
public class SshClientUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(SshClientUtils.class);
  private static final int SSH_SERVER_PORT = 22;
  private static final long SSH_CONNECTION_TIMEOUT_MS = 30000; // in ms
  private static final String RSA = "RSA";
  private static final int PTY_COLUMNS = 999;
  private static final String WINDOWS_EXIT = " & exit \r\n";
  private static final String LINUX_EXIT = " ; exit \n";

  private SshClientUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Execute the kamehouse command over ssh on the docker host setting up the channel as a shell.
   * This was used when getting the playlists and playlists content from docker via ssh. Now those
   * are retrieved via https using groot.
   */
  public static KameHouseCommandResult executeShell(String host, String username,
      KameHouseCommand kameHouseCommand, boolean isWindowsShell) {
    return execute(host, username, kameHouseCommand, true, isWindowsShell);
  }

  /**
   * Execute the kamehouse command over ssh on the docker host setting up the channel as exec. This
   * was used to execute remote commands from docker on the host via ssh. Now those commands are
   * being executed by https via groot.
   */
  public static KameHouseCommandResult execute(String host, String username,
      KameHouseCommand kameHouseCommand) {
    return execute(host, username, kameHouseCommand, false, false);
  }

  /**
   * Execute the kamehouse command over ssh on the docker host. Only kamehouse-shell commands are
   * supported.
   */
  private static KameHouseCommandResult execute(String host, String username,
      KameHouseCommand kameHouseCommand, boolean useShellChannel, boolean isWindowsShell) {
    if (!(kameHouseCommand instanceof KameHouseShellScript)) {
      throw new KameHouseServerErrorException(
          "Only KameHouseShellScript commands can be executed via ssh at the moment");
    }
    KameHouseShellScript kameHouseShellScript = (KameHouseShellScript) kameHouseCommand;
    kameHouseShellScript.init();
    KameHouseCommandResult kameHouseCommandResult = kameHouseShellScript.initResult();
    String command = kameHouseShellScript.getCommand();
    LOGGER.debug("Command to execute {}", kameHouseShellScript);
    SshClient client = SshClient.setUpDefaultClient();
    client.start();
    client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
    ClientChannel channel = null;

    try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream()) {
      ConnectFuture connectFuture = client.connect(username, host, SSH_SERVER_PORT);
      ConnectFuture connectFuture1 =
          connectFuture.verify(SSH_CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS,
              (CancelOption) null);
      ClientSession session = connectFuture1.getSession();
      session.addPublicKeyIdentity(getKeyPair());
      session.auth().verify(SSH_CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
      channel = getChannel(session, useShellChannel, command);
      channel.setOut(responseStream);
      channel.setErr(errorStream);
      channel.open().verify(SSH_CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
      OutputStream sshChannelWriter = channel.getInvertedIn();
      sshChannelWriter.write(getCommandBytes(command, useShellChannel, isWindowsShell));
      sshChannelWriter.flush();
      long sshTimeout = SSH_CONNECTION_TIMEOUT_MS;
      if (kameHouseShellScript.getSshTimeout() > 0) {
        sshTimeout = kameHouseShellScript.getSshTimeout();
      }
      channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), sshTimeout);
      String standardOutput = responseStream.toString(Charsets.UTF_8);
      kameHouseCommandResult.setStandardOutput(Arrays.asList(standardOutput));
      LOGGER.debug("standardOutput: {}", standardOutput);

      String standardError = errorStream.toString(Charsets.UTF_8);
      if (!StringUtils.isEmpty(standardError)) {
        kameHouseCommandResult.setStandardError(Arrays.asList(standardError));
        LOGGER.debug("standardError: {}", standardError);
      }
      kameHouseCommandResult.setExitCode(0);
      kameHouseCommandResult.setStatus(KameHouseCommandStatus.COMPLETED.getStatus());
    } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
      LOGGER.error("Error executing ssh command", e);
      kameHouseCommandResult.setExitCode(1);
      kameHouseCommandResult.setStatus(KameHouseCommandStatus.FAILED.getStatus());
    } finally {
      if (channel != null) {
        channel.close(false);
      }
      client.stop();
    }
    kameHouseCommandResult.setHtmlOutputs();
    return kameHouseCommandResult;
  }

  /**
   * Get the channel to execute the commands over it.
   */
  private static ClientChannel getChannel(ClientSession session, boolean useShellChannel,
      String command)
      throws IOException {
    if (useShellChannel) {
      PtyChannelConfiguration ptyChannelConfiguration = new PtyChannelConfiguration();
      ptyChannelConfiguration.setPtyColumns(PTY_COLUMNS);
      return session.createShellChannel(ptyChannelConfiguration, null);
    } else {
      return session.createExecChannel(command);
    }
  }

  /**
   * Get the bytes to send to the ssh server as a command.
   */
  private static byte[] getCommandBytes(String command, boolean useShellChannel,
      boolean isWindowsShell) {
    if (useShellChannel) {
      if (isWindowsShell) {
        command = command + WINDOWS_EXIT;
      } else {
        command = command + LINUX_EXIT;
      }
    }
    return command.getBytes(Charsets.UTF_8);
  }

  /**
   * Get public/private key pair to execute the ssh connection.
   */
  private static KeyPair getKeyPair()
      throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
    return new KeyPair(getPublicKey(), getPrivateKey());
  }

  /**
   * Get the public key to establish the ssh connection.
   */
  private static PublicKey getPublicKey()
      throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
    KeyFactory factory = KeyFactory.getInstance(RSA);
    String publicKeyFile = PropertiesUtils.getUserHome() + File.separator
        + PropertiesUtils.getProperty("ssh.public.key");
    File file = new File(publicKeyFile);
    try (FileReader keyReader = new FileReader(file, Charsets.UTF_8);
        PemReader pemReader = new PemReader(keyReader)) {
      PemObject pemObject = pemReader.readPemObject();
      byte[] content = pemObject.getContent();
      X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
      return factory.generatePublic(pubKeySpec);
    }
  }

  /**
   * Get the private key to establish the ssh connection.
   */
  private static PrivateKey getPrivateKey() throws IOException {
    String privateKeyFile = PropertiesUtils.getUserHome() + File.separator
        + PropertiesUtils.getProperty("ssh.private.key");
    File file = new File(privateKeyFile);
    try (FileReader keyReader = new FileReader(file, Charsets.UTF_8);
        PEMParser pemParser = new PEMParser(keyReader)) {
      JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
      PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject());
      return converter.getPrivateKey(privateKeyInfo);
    }
  }
}
