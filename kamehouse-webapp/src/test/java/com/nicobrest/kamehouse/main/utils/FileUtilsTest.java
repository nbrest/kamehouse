package com.nicobrest.kamehouse.main.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * FileUtils tests.
 *
 * @author nbrest
 */
public class FileUtilsTest {

  private static final String TEST_FILES_PATH = "src/test/resources/main/";

  @Test
  public void getDecodedFileContentSuccessTest() {
    String output = FileUtils.getDecodedFileContent(TEST_FILES_PATH + "encodedFile.txt");
    assertEquals("goku", output);
  }

  @Test
  public void getDecodedFileContentErrorDecodingFileTest() {
    String output = FileUtils.getDecodedFileContent(TEST_FILES_PATH + "nonEncodedFile.txt");
    assertEquals(FileUtils.ERROR_READING_FILE, output);
  }

  @Test
  public void getDecodedFileContentErrorReadingFileTest() {
    String output = FileUtils.getDecodedFileContent("invalid/file.txt");
    assertEquals(FileUtils.ERROR_READING_FILE, output);
  }

  @Test
  public void getDecodedFileContentEmptyFileTest() {
    String output = FileUtils.getDecodedFileContent(TEST_FILES_PATH + "emptyFile.txt");
    assertEquals(FileUtils.EMPTY_DECODED_FILE, output);
  }

  @Test
  public void isRemoteFileSuccessTest() {
    String remoteFile = "smb://this/is/a/remote/file.txt";
    assertTrue(FileUtils.isRemoteFile(remoteFile));
  }

  @Test
  public void isRemoteFileFalseTest() {
    String localFile = "/this/is/a/local/file.txt";
    assertFalse(FileUtils.isRemoteFile(localFile));
  }

  @Test
  public void isValidLocalFileSuccessfulTest() {
    String localFile = TEST_FILES_PATH + "encodedFile.txt";
    assertTrue(FileUtils.isValidLocalFile(localFile));
  }

  @Test
  public void isValidLocalFileFalseTest() {
    String invalidLocalFile = TEST_FILES_PATH + "invalid-file.txt";
    assertFalse(FileUtils.isValidLocalFile(invalidLocalFile));
  }
}
