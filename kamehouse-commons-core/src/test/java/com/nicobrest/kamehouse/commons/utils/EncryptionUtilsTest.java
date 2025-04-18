package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.Charsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * EncryptionUtils tests.
 *
 * @author nbrest
 */
class EncryptionUtilsTest {

  private static final String TEST_RESOURCES_PATH = "src/test/resources/commons/";
  private static final String SAMPLE_CERT = TEST_RESOURCES_PATH + "keys/sample.crt";
  private static final String SAMPLE_KEYSTORE = TEST_RESOURCES_PATH + "keys/sample.pkcs12";
  private static final String SAMPLE_DECRYPTED_FILE = TEST_RESOURCES_PATH + "files/input.txt";
  private static final String SAMPLE_ENCRYPTED_FILE = TEST_RESOURCES_PATH + "files/input.enc";
  private static final String SAMPLE_ENCRYPTED_EMPTY_FILE =
      TEST_RESOURCES_PATH + "files/input-empty.enc";

  private MockedStatic<KameHouseCommandUtils> kameHouseCommandUtilsMockedStatic;

  @Mock
  private KameHouseCommandResult kameHouseCommandResult;

  /**
   * Tests setup.
   */
  @BeforeEach
  public void before() {
    MockitoAnnotations.openMocks(this);
    kameHouseCommandUtilsMockedStatic = Mockito.mockStatic(KameHouseCommandUtils.class);
    when(KameHouseCommandUtils.execute(any())).thenReturn(kameHouseCommandResult);
  }

  /**
   * Tests cleanup.
   */
  @AfterEach
  public void close() {
    kameHouseCommandUtilsMockedStatic.close();
  }

  /**
   * Test getting an invalid kamehouse secret successfully. If this test fails, run
   * deploy-kamehouse.sh -m shell on the server running the test to redeploy the kamehouse secrets.
   */
  @Test
  void getKameHouseSecretSuccessTest() {
    when(kameHouseCommandResult.getStandardOutput()).thenReturn(List.of("mariadb-pass"));

    String secretValue = EncryptionUtils.getKameHouseSecret("MARIADB_PASS_KAMEHOUSE");
    Assertions.assertNotNull(secretValue);
  }

  /**
   * Test getting a kamehouse secret with empty value.
   */
  @Test
  void getKameHouseSecretEmptyValueTest() {
    when(kameHouseCommandResult.getStandardOutput()).thenReturn(new ArrayList<>());

    String secretValue = EncryptionUtils.getKameHouseSecret("MARIADB_PASS_ROOT_WIN");
    Assertions.assertEquals("", secretValue);
  }

  /**
   * Test error getting an invalid kamehouse secret.
   */
  @Test
  void getKameHouseSecretErrorTest() {
    when(kameHouseCommandResult.getStandardOutput()).thenReturn(List.of("val1", "val2"));

    assertThrows(KameHouseInvalidDataException.class, () -> {
      EncryptionUtils.getKameHouseSecret("invalidKey-!");
    });
  }

  /**
   * Test encrypt and decrypt strings.
   */
  @Test
  void encryptAndDecryptStringsTest() {
    String inputString = "mada mada dane echizen kun";
    byte[] encryptedData = EncryptionUtils.encrypt(inputString.getBytes(Charsets.UTF_8),
        getSampleCertificate());
    byte[] outputRawData = EncryptionUtils.decrypt(encryptedData, getSamplePrivateKey());
    String decryptedString = new String(outputRawData, StandardCharsets.UTF_8);

    assertNotEquals(inputString, new String(encryptedData, StandardCharsets.UTF_8));
    assertEquals(inputString, decryptedString);
  }

  /**
   * Test encrypt a decrypted file.
   */
  @Test
  void encryptDecryptedFileTest() throws IOException {
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
  void decryptEncryptedFileTest() throws IOException {
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
  void decryptEncryptedEmptyFileTest() throws IOException {
    String expectedDecrypted = "";
    byte[] inputBytes = FileUtils.readFileToByteArray(new File(SAMPLE_ENCRYPTED_EMPTY_FILE));
    String inputString = new String(inputBytes, StandardCharsets.UTF_8);

    String decryptedString =
        EncryptionUtils.decryptFileToString(SAMPLE_ENCRYPTED_EMPTY_FILE, getSamplePrivateKey());

    assertNotEquals(inputString, decryptedString);
    assertEquals(expectedDecrypted, decryptedString);
  }

  /**
   * Test decrypt error flow with invalid file.
   */
  @Test
  void decryptInvalidFileTest() {
    PrivateKey privateKey = getSamplePrivateKey();
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          EncryptionUtils.decryptFileToString("", privateKey);
        });
  }

  /**
   * Test decrypt error flow with empty data.
   */
  @Test
  void decryptEmptyDataTest() {
    PrivateKey privateKey = getSamplePrivateKey();
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          EncryptionUtils.decrypt(null, privateKey);
        });
  }

  /**
   * Test decrypt error flow with empty private key.
   */
  @Test
  void decryptEmptyPrivateKeyTest() {
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          EncryptionUtils.decrypt(new byte[2], null);
        });
  }

  /**
   * Test encrypt error flow with empty data.
   */
  @Test
  void encryptEmptyDataTest() {
    X509Certificate certificate = getSampleCertificate();
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          EncryptionUtils.encrypt(null, certificate);
        });
  }

  /**
   * Test encrypt error flow with empty certificate.
   */
  @Test
  void decryptEmptyCertificateTest() {
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          EncryptionUtils.encrypt(new byte[2], null);
        });
  }

  /**
   * Test encrypt error flow with empty certificate.
   */
  @Test
  void kameHouseKeysTest() {
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
    return EncryptionUtils.getPrivateKey(SAMPLE_KEYSTORE, "PKCS12", null, "1", null);
  }

  /**
   * Get the sample certificate.
   */
  private static X509Certificate getSampleCertificate() {
    return EncryptionUtils.getCertificate(SAMPLE_CERT);
  }
}
