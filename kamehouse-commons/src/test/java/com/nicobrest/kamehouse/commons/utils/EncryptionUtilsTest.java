package com.nicobrest.kamehouse.commons.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * EncryptionUtils tests.
 *
 * @author nbrest
 */
public class EncryptionUtilsTest {

  private static final String TEST_FILES_PATH = "src/test/resources/commons/";
  private static final String SAMPLE_CERT = TEST_FILES_PATH + "keys/sample.crt";
  private static final String SAMPLE_KEYSTORE = TEST_FILES_PATH + "keys/sample.pkcs12";

  /**
   * Test encrypt and decrypt.
   */
  @Test
  public void encryptAndDecryptTest() {
    String inputString = "mada mada dane echizen kun";

    X509Certificate cert = EncryptionUtils.getCertificate(SAMPLE_CERT);
    byte[] encryptedData = EncryptionUtils.encrypt(inputString.getBytes(), cert);

    PrivateKey privateKey = EncryptionUtils.getPrivateKey(SAMPLE_KEYSTORE, "PKCS12",
        null, "1", null);
    byte[] outputRawData = EncryptionUtils.decrypt(encryptedData, privateKey);
    String decryptedString = new String(outputRawData);

    assertNotEquals(inputString, new String(encryptedData));
    assertEquals(inputString, decryptedString);
  }
}
