package com.nicobrest.kamehouse.cmd.executor;

import com.nicobrest.kamehouse.cmd.model.CmdArgumentHandler;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Executor for the decrypt operation.
 *
 * @author nbrest
 */
@Component
public class DecryptExecutor {

  private final Logger logger = LoggerFactory.getLogger(DecryptExecutor.class);

  /** Execute the operation. */
  public void execute(CmdArgumentHandler cmdArgumentHandler) {
    String inputFileName = cmdArgumentHandler.getArgument("if");
    String outputFileName = cmdArgumentHandler.getArgument("of");
    try {
      logger.info("Decrypting contents of {} into {}", inputFileName, outputFileName);
      File inputFile = new File(inputFileName);
      File outputFile = new File(outputFileName);
      byte[] inputBytes = FileUtils.readFileToByteArray(inputFile);
      PrivateKey key = EncryptionUtils.getKameHousePrivateKey();
      byte[] decryptedOutput = EncryptionUtils.decrypt(inputBytes, key);
      FileUtils.writeByteArrayToFile(outputFile, decryptedOutput);
      if (decryptedOutput != null && logger.isTraceEnabled()) {
        logger.trace("Decrypted content:");
        System.out.println(new String(decryptedOutput, StandardCharsets.UTF_8));
      }
      logger.info("Finished decrypting content");
    } catch (IOException e) {
      logger.error("Error decrypting file", e);
    }
  }
}
