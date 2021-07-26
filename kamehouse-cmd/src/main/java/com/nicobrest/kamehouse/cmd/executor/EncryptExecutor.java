package com.nicobrest.kamehouse.cmd.executor;

import com.nicobrest.kamehouse.cmd.model.CmdArgumentHandler;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.security.cert.X509Certificate;

/**
 * Executor for the encrypt operation.
 *
 * @author nbrest
 */
@Component
public class EncryptExecutor {

  private final Logger logger = LoggerFactory.getLogger(EncryptExecutor.class);

  /**
   * Execute the operation.
   */
  public void execute(CmdArgumentHandler cmdArgumentHandler) {
    String inputFileName = cmdArgumentHandler.getArgument("if");
    String outputFileName = cmdArgumentHandler.getArgument("of");
    try {
      logger.info("Encrypting contents of {} into {}", inputFileName, outputFileName);
      File inputFile = new File(inputFileName);
      File outputFile = new File(outputFileName);
      byte[] inputBytes = FileUtils.readFileToByteArray(inputFile);
      X509Certificate cert = EncryptionUtils.getKameHouseCertificate();
      byte[] encryptedBytes = EncryptionUtils.encrypt(inputBytes, cert);
      FileUtils.writeByteArrayToFile(outputFile, encryptedBytes);
      logger.info("Finished encrypting content");
    } catch (IOException e) {
      logger.error("Error encrypting file", e);
    }
  }
}
