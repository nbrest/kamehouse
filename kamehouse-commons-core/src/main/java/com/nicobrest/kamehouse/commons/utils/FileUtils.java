package com.nicobrest.kamehouse.commons.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to access files in the local filesystem.
 *
 * @author nbrest
 */
public class FileUtils {

  public static final String ERROR_READING_FILE = "ERROR_READING_FILE";
  public static final String EMPTY_FILE_CONTENT = "''";
  private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

  private FileUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Decodes the contents of the encoded file and return it as a string.
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
      LOGGER.error("Error decoding file " + filename, e);
      decodedFileContent = ERROR_READING_FILE;
    }
    if (StringUtils.isEmpty(decodedFileContent)) {
      decodedFileContent = EMPTY_FILE_CONTENT;
    }
    return decodedFileContent;
  }

  /**
   * Checks if the specified file path is a remote file or a local file.
   */
  public static boolean isRemoteFile(String filepath) {
    if (filepath == null) {
      return false;
    }
    return filepath.startsWith("smb://")
        || filepath.startsWith("http://")
        || filepath.startsWith("https://")
        || filepath.startsWith("sftp://")
        || filepath.startsWith("\\");
  }

  /**
   * Checks if the specified file is valid in the local filesystem.
   */
  public static boolean isValidLocalFile(String filename) {
    if (filename == null) {
      return false;
    }
    File file = new File(filename);
    return file.exists();
  }

  /**
   * Wrapper to apache commons readFileToByteArray.
   */
  public static byte[] readFileToByteArray(File file) throws IOException {
    return org.apache.commons.io.FileUtils.readFileToByteArray(file);
  }

  /**
   * Wrapper to apache commons writeByteArrayToFile.
   */
  public static void writeByteArrayToFile(File file, byte[] bytes) throws IOException {
    org.apache.commons.io.FileUtils.writeByteArrayToFile(file, bytes);
  }
}
