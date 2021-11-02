package com.nicobrest.kamehouse.commons.utils;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
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
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;
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

  private SshClientUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Execute the system command over ssh on the docker host.
   */
  public static void execute(String host, String username, SystemCommand systemCommand) {
    String command = systemCommand.getCommandForSsh();
    SshClient client = SshClient.setUpDefaultClient();
    client.start();
    client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
    ClientChannel channel = null;

    try (ClientSession session = client.connect(username, host, SSH_SERVER_PORT)
        .verify(SSH_CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS).getSession();
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream()) {
      session.addPublicKeyIdentity(getKeyPair());
      session.auth().verify(SSH_CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
      channel = session.createChannel(Channel.CHANNEL_EXEC, command);
      channel.setOut(responseStream);
      channel.setErr(errorStream);
      channel.open().verify(SSH_CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
      OutputStream sshChannelWriter = channel.getInvertedIn();
      sshChannelWriter.write(command.getBytes(Charsets.UTF_8));
      sshChannelWriter.flush();
      channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), SSH_CONNECTION_TIMEOUT_MS);
      String standardOutput = responseStream.toString(Charsets.UTF_8);
      String standardError = errorStream.toString(Charsets.UTF_8);
      systemCommand.getOutput().setStandardOutput(Arrays.asList(standardOutput));
      systemCommand.getOutput().setStandardError(Arrays.asList(standardError));
      LOGGER.trace("Ssh command {} standardOutput: {}", command, standardOutput);
      LOGGER.trace("Ssh command {} standardError: {}", command, standardError);
      systemCommand.getOutput().setStatus("completed");
    } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
      LOGGER.error("Error executing ssh command", e);
      systemCommand.getOutput().setStatus("failed");
    } finally {
      if (channel != null) {
        channel.close(false);
      }
      client.stop();
    }
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
