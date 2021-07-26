package com.nicobrest.kamehouse.commons.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * EncryptionUtils tests.
 *
 * @author nbrest
 */
public class EncryptionUtilsTest {

  private static final String TEST_RESOURCES_PATH = "src/test/resources/commons/";
  private static final String SAMPLE_CERT = TEST_RESOURCES_PATH + "keys/sample.crt";
  private static final String SAMPLE_KEYSTORE = TEST_RESOURCES_PATH + "keys/sample.pkcs12";
  private static final String SAMPLE_DECRYPTED_FILE = TEST_RESOURCES_PATH + "files/input.txt";
  private static final String SAMPLE_ENCRYPTED_FILE = TEST_RESOURCES_PATH + "files/input.enc";
  private static final String SAMPLE_ENCRYPTED_EMPTY_FILE = TEST_RESOURCES_PATH
      + "files/input-empty.enc";

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Test encrypt and decrypt strings.
   */
  @Test
  public void encryptAndDecryptStringsTest() {
    String inputString = "mada mada dane echizen kun";
    byte[] encryptedData = EncryptionUtils.encrypt(inputString.getBytes(), getSampleCertificate());
    byte[] outputRawData = EncryptionUtils.decrypt(encryptedData, getSamplePrivateKey());
    String decryptedString = new String(outputRawData, StandardCharsets.UTF_8);

    assertNotEquals(inputString, new String(encryptedData, StandardCharsets.UTF_8));
    assertEquals(inputString, decryptedString);
  }

  /**
   * Test encrypt a decrypted file.
   */
  @Test
  public void encryptDecryptedFileTest() throws IOException {
    byte[] inputBytes = FileUtils.readFileToByteArray(new File(SAMPLE_DECRYPTED_FILE));
    String inputString = new String(inputBytes, StandardCharsets.UTF_8);

    byte[] encryptedData = EncryptionUtils.encrypt(inputBytes, getSampleCertificate());
    String decryptedString = EncryptionUtils.decryptToString(encryptedData, getSamplePrivateKey());

    assertNotEquals(inputString, new String(encryptedData, StandardCharsets.UTF_8));
    assertEquals(inputString, decryptedString);
  }

  /**
   * Test decrypt an encrypted file.
   */
  @Test
  public void decryptEncryptedFileTest() throws IOException {
    String expectedDecrypted = "mada mada dane - pegasus seiya";
    byte[] inputBytes = FileUtils.readFileToByteArray(new File(SAMPLE_ENCRYPTED_FILE));
    String inputString = new String(inputBytes, StandardCharsets.UTF_8);

    byte[] decryptedBytes = EncryptionUtils.decrypt(inputBytes, getSamplePrivateKey());
    String decryptedString = new String(decryptedBytes, StandardCharsets.UTF_8);

    assertNotEquals(inputString, decryptedString);
    assertEquals(expectedDecrypted, decryptedString);
  }

  /**
   * Test decrypt an encrypted empty file.
   */
  @Test
  public void decryptEncryptedEmptyFileTest() throws IOException {
    String expectedDecrypted = "";
    byte[] inputBytes = FileUtils.readFileToByteArray(new File(SAMPLE_ENCRYPTED_EMPTY_FILE));
    String inputString = new String(inputBytes, StandardCharsets.UTF_8);

    String decryptedString = EncryptionUtils.decryptFileToString(SAMPLE_ENCRYPTED_EMPTY_FILE, getSamplePrivateKey());

    assertNotEquals(inputString, decryptedString);
    assertEquals(expectedDecrypted, decryptedString);
  }

  /**
   * Test decrypt error flow with invalid file.
   */
  @Test
  public void decryptInvalidFileTest() {
    thrown.expect(KameHouseInvalidDataException.class);
    EncryptionUtils.decryptFileToString("", getSamplePrivateKey());
  }

  /**
   * Test decrypt error flow with empty data.
   */
  @Test
  public void decryptEmptyDataTest() {
    thrown.expect(KameHouseInvalidDataException.class);
    EncryptionUtils.decrypt(null, getSamplePrivateKey());
  }

  /**
   * Test decrypt error flow with empty private key.
   */
  @Test
  public void decryptEmptyPrivateKeyTest() {
    thrown.expect(KameHouseInvalidDataException.class);
    EncryptionUtils.decrypt(new byte[2], null);
  }

  /**
   * Test encrypt error flow with empty data.
   */
  @Test
  public void encryptEmptyDataTest() {
    thrown.expect(KameHouseInvalidDataException.class);
    EncryptionUtils.encrypt(null, getSampleCertificate());
  }

  /**
   * Test encrypt error flow with empty certificate.
   */
  @Test
  public void decryptEmptyCertificateTest() {
    thrown.expect(KameHouseInvalidDataException.class);
    EncryptionUtils.encrypt(new byte[2], null);
  }

  /**
   * Test encrypt error flow with empty certificate.
   */
  @Test
  public void kameHouseKeysTest() {
    try {
      X509Certificate cert = EncryptionUtils.getKameHouseCertificate();
      assertNotNull(cert);
    } catch (KameHouseInvalidDataException e) {
      System.out.println("Can't find kamehouse certificate. Expected exception thrown");
    }

    try {
      PrivateKey key = EncryptionUtils.getKameHousePrivateKey();
      assertNotNull(key);
    } catch (KameHouseInvalidDataException e) {
      System.out.println("Can't find kamehouse private key. Expected exception thrown");
    }
  }

  /**
   * Get the sample private key.
   */
  private static PrivateKey getSamplePrivateKey() {
    return EncryptionUtils.getPrivateKey(SAMPLE_KEYSTORE, "PKCS12",null, "1", null);
  }

  /**
   * Get the sample certificate.
   */
  private static X509Certificate getSampleCertificate() {
    return EncryptionUtils.getCertificate(SAMPLE_CERT);
  }
}
