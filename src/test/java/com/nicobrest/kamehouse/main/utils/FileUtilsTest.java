package com.nicobrest.kamehouse.main.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}
