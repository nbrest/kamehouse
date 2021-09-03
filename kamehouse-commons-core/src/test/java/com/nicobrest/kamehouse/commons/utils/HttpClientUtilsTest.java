package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.Test;

/**
 * HttpClient tests.
 *
 * @author nbrest
 */
public class HttpClientUtilsTest {

  /** Tests getting an http client. */
  @Test
  public void getClientTest() {
    HttpClient client = HttpClientUtils.getClient("goku", "trunks");
    assertNotNull(client);
  }

  /** Tests encoding successfully a url parameter. */
  @Test
  public void urlEncodeSuccessTest() {
    String nonEncodedParam = "pegasus seiya <$1>/?";
    String encodedParam = HttpClientUtils.urlEncode(nonEncodedParam);
    String expectedOutput = "pegasus%20seiya%20%3C$1%3E/?";
    assertEquals(expectedOutput, encodedParam);
  }

  /** Tests returning null when failing to encode the url parameter. */
  @Test
  public void urlEncodeErrorEncodingTest() {
    String encodedParam = HttpClientUtils.urlEncode(null);
    assertNull(encodedParam, "Expected null from urlEncode");
  }

  /** Tests decoding successfully a url parameter. */
  @Test
  public void urlDecodeSuccessTest() {
    String encodedParam = "pegasus%20seiya%20%3C$1%3E/?";
    String nonEncodedParam = HttpClientUtils.urlDecode(encodedParam);
    String expectedOutput = "pegasus seiya <$1>/?";
    assertEquals(expectedOutput, nonEncodedParam);
  }

  /** Tests returning null when failing to decode the url parameter. */
  @Test
  public void urlDecodeErrorDecodingTest() {
    String decodedParam = HttpClientUtils.urlDecode(null);
    assertNull(decodedParam, "Expected null from urlDecode");
  }
}
