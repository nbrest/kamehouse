package com.nicobrest.kamehouse.main.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * HttpClient tests.
 *
 * @author nbrest
 */
public class HttpClientUtilsTest {

  /**
   * Tests encoding successfully a url parameter.
   */
  @Test
  public void urlEncodeSuccessTest() {
    String nonEncodedParam = "pegasus seiya <$1>/?";
    String encodedParam = HttpClientUtils.urlEncode(nonEncodedParam);
    String expectedOutput = "pegasus%20seiya%20%3C$1%3E/?";
    assertEquals(expectedOutput, encodedParam);
  }

  /**
   * Tests returning null when failing to encode the url parameter.
   */
  @Test
  public void urlEncodeErrorEncodingTest() {
    String encodedParam = HttpClientUtils.urlEncode(null);
    assertNull("Expected null from urlEncode", encodedParam);
  }

  /**
   * Tests decoding successfully a url parameter.
   */
  @Test
  public void urlDecodeSuccessTest() {
    String encodedParam = "pegasus%20seiya%20%3C$1%3E/?";
    String nonEncodedParam = HttpClientUtils.urlDecode(encodedParam);
    String expectedOutput = "pegasus seiya <$1>/?";
    assertEquals(expectedOutput, nonEncodedParam);
  }

  /**
   * Tests returning null when failing to decode the url parameter.
   */
  @Test
  public void urlDecodeErrorDecodingTest() {
    String decodedParam = HttpClientUtils.urlDecode(null);
    assertNull("Expected null from urlDecode", decodedParam);
  }
}
