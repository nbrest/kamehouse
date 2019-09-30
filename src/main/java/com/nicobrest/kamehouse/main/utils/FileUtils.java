package com.nicobrest.kamehouse.main.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

/**
 * Utility class to access files in the local filesystem.
 * 
 * @author nbrest
 *
 */
public class FileUtils {

  private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

  private FileUtils() {
    throw new IllegalStateException("Utility class");
  }
  
  /**
   * Decode the contents of the encoded file and return it as a string.
   */
  public static String getDecodedFileContent(String filename) {
    String decodedFileContent = null;
    try {
      List<String> encodedFileContentList = Files.readAllLines(Paths.get(filename));
      if (encodedFileContentList != null && !encodedFileContentList.isEmpty()) {
        String encodedFileContent = encodedFileContentList.get(0);
        byte[] decodedFileContentBytes = Base64.getDecoder().decode(encodedFileContent);
        decodedFileContent = new String(decodedFileContentBytes, StandardCharsets.UTF_8);
      }
    } catch (IOException | IllegalArgumentException e) {
      logger.error("Error decoding file {}", filename, e);
      decodedFileContent = "ERROR_READING_FILE";
    }
    if (StringUtils.isEmpty(decodedFileContent)) {
      decodedFileContent = "''";
    }
    return decodedFileContent;
  }
}
