package com.nicobrest.kamehouse.commons.utils;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.GetKameHouseSecretKameHouseCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.KeyTransRecipientInformation;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OutputEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to manage encryption and decryption in the application.
 *
 * @author nbrest
 */
public class EncryptionUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionUtils.class);
  private static final String BC = "BC";
  private static final String X509 = "X.509";
  private static final String PKCS12 = "PKCS12";

  private static final String ERROR_ENCRYPTING_DATA = "Error encrypting data";
  private static final String ERROR_DECRYPTING_DATA = "Error decrypting data";
  private static final String ERROR_GETTING_CERTIFICATE = "Error getting the certificate";
  private static final String ERROR_GETTING_PRIVATE_KEY = "Error getting the private key";

  private static PrivateKey kamehousePrivateKey;
  private static X509Certificate kamehouseCertificate;

  static {
    try {
      kamehousePrivateKey = getKameHousePrivateKey();
    } catch (KameHouseInvalidDataException e) {
      kamehousePrivateKey = null;
    }

    try {
      kamehouseCertificate = getKameHouseCertificate();
    } catch (KameHouseInvalidDataException e) {
      kamehouseCertificate = null;
    }
  }

  private EncryptionUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Get the specified kamehouse secret from the encrypted secrets store.
   */
  public static String getKameHouseSecret(String secretKey) {
    KameHouseCommandResult kameHouseCommandResult = new GetKameHouseSecretKameHouseCommand(
        secretKey).execute();
    List<String> secretValue = kameHouseCommandResult.getStandardOutput();
    if (secretValue == null || secretValue.size() != 1) {
      throw new KameHouseInvalidDataException("Invalid secretValue for secretKey " + secretKey);
    }
    return secretValue.get(0);
  }

  /**
   * Encrypt the specified data with the certificate.
   */
  public static byte[] encrypt(byte[] data, X509Certificate certificate) {
    if (data == null || certificate == null) {
      throw new KameHouseInvalidDataException("data or certificate are null");
    }
    try {
      CMSEnvelopedDataGenerator cmsEnvelopedDataGenerator = new CMSEnvelopedDataGenerator();
      JceKeyTransRecipientInfoGenerator jceKey = new JceKeyTransRecipientInfoGenerator(certificate);
      cmsEnvelopedDataGenerator.addRecipientInfoGenerator(jceKey);
      CMSTypedData msg = new CMSProcessableByteArray(data);
      OutputEncryptor outputEncryptor =
          new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC).setProvider(BC).build();
      CMSEnvelopedData cmsEnvelopedData = cmsEnvelopedDataGenerator.generate(msg, outputEncryptor);
      return cmsEnvelopedData.getEncoded();
    } catch (CertificateEncodingException | CMSException | IOException e) {
      LOGGER.error(ERROR_ENCRYPTING_DATA, e);
      throw new KameHouseInvalidDataException(ERROR_ENCRYPTING_DATA);
    }
  }

  /**
   * Decrypt the specified data with the private key.
   */
  public static byte[] decrypt(byte[] data, PrivateKey privateKey) {
    if (data == null || privateKey == null) {
      throw new KameHouseInvalidDataException("data or private key are null");
    }
    try {
      CMSEnvelopedData envelopedData = new CMSEnvelopedData(data);
      Collection<RecipientInformation> recipients =
          envelopedData.getRecipientInfos().getRecipients();
      KeyTransRecipientInformation recipientInfo =
          (KeyTransRecipientInformation) recipients.iterator().next();
      JceKeyTransRecipient recipient = new JceKeyTransEnvelopedRecipient(privateKey);
      return recipientInfo.getContent(recipient);
    } catch (CMSException e) {
      LOGGER.error(ERROR_DECRYPTING_DATA, e);
      throw new KameHouseInvalidDataException(ERROR_DECRYPTING_DATA);
    }
  }

  /**
   * Decrypt the data into a string.
   */
  public static String decryptToString(byte[] data, PrivateKey privateKey) {
    return new String(decrypt(data, privateKey), StandardCharsets.UTF_8);
  }

  /**
   * Decrypt the specified file into a string using kamehouse keys.
   */
  public static String decryptKameHouseFileToString(String filename) {
    return decryptFileToString(filename, getKameHousePrivateKey());
  }

  /**
   * Decrypt the specified file into a string.
   */
  public static String decryptFileToString(String filename, PrivateKey privateKey) {
    try {
      byte[] encryptedFile = FileUtils.readFileToByteArray(new File(filename));
      return decryptToString(encryptedFile, privateKey);
    } catch (IOException e) {
      LOGGER.error(ERROR_DECRYPTING_DATA, e);
      throw new KameHouseInvalidDataException(ERROR_DECRYPTING_DATA);
    }
  }

  /**
   * Get the certificate used to encrypt kamehouse content.
   */
  @SuppressFBWarnings(value = "MS_EXPOSE_REP")
  public static synchronized X509Certificate getKameHouseCertificate() {
    if (kamehouseCertificate != null) {
      return kamehouseCertificate;
    }
    String certPath = PropertiesUtils.getUserHome() + File.separator
        + PropertiesUtils.getProperty("kamehouse.crt");
    kamehouseCertificate = getCertificate(certPath);
    return kamehouseCertificate;
  }

  /**
   * Get the specified certificate to encrypt content with.
   */
  public static X509Certificate getCertificate(String certPath) {
    try (FileInputStream fis = new FileInputStream(certPath)) {
      Security.addProvider(new BouncyCastleProvider());
      CertificateFactory certFactory = CertificateFactory.getInstance(X509, BC);
      return (X509Certificate) certFactory.generateCertificate(fis);
    } catch (CertificateException | NoSuchProviderException | IOException e) {
      LOGGER.error(ERROR_GETTING_CERTIFICATE, e);
      throw new KameHouseInvalidDataException(ERROR_GETTING_CERTIFICATE);
    }
  }

  /**
   * Get the private key used to decrypt kamehouse content.
   */
  public static synchronized PrivateKey getKameHousePrivateKey() {
    if (kamehousePrivateKey != null) {
      return kamehousePrivateKey;
    }
    String keyStorePath = PropertiesUtils.getUserHome() + File.separator
        + PropertiesUtils.getProperty("kamehouse.pkcs12");
    kamehousePrivateKey = getPrivateKey(keyStorePath, PKCS12, null, "1", null);
    return kamehousePrivateKey;
  }

  /**
   * Get the private key from the specified keystore and alias.
   */
  public static PrivateKey getPrivateKey(String keyStorePath, String keyStoreType,
      char[] keyStorePassword, String keyAlias, char[] keyPassword) {
    try (FileInputStream fis = new FileInputStream(keyStorePath)) {
      KeyStore keystore = KeyStore.getInstance(keyStoreType);
      keystore.load(fis, keyStorePassword);
      return (PrivateKey) keystore.getKey(keyAlias, keyPassword);
    } catch (KeyStoreException | CertificateException | UnrecoverableKeyException
             | NoSuchAlgorithmException | IOException e) {
      LOGGER.error(ERROR_GETTING_PRIVATE_KEY, e);
      throw new KameHouseInvalidDataException(ERROR_GETTING_PRIVATE_KEY);
    }
  }
}
