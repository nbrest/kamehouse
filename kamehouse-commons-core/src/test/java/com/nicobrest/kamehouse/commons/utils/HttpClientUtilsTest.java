package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.Test;

/**
 * HttpClient tests.
 *
 * @author nbrest
 */
public class HttpClientUtilsTest {

  private StatusLine statusLine = new BasicStatusLine(new ProtocolVersion("http", 1, 1), 200, "OK");
  private HttpResponse response = new BasicHttpResponse(statusLine);
  private HttpRequest request = new BasicHttpRequest("GET","http://mada.mada");

  /**
   * Tests getting an http client.
   */
  @Test
  public void getClientTest() {
    HttpClient client = HttpClientUtils.getClient("goku", "trunks");
    assertNotNull(client);
  }

  /**
   *
   */
  @Test
  public void getClientNullParametersTest() {
    HttpClient client = HttpClientUtils.getClient(null, null);
    assertNotNull(client);
  }

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
    assertNull(encodedParam, "Expected null from urlEncode");
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
    assertNull(decodedParam, "Expected null from urlDecode");
  }

  /**
   * urlDecode IllegalArgumentException test.
   */
  @Test
  public void urlDecodeIllegalArgumentExceptionTest() {
    String encodedParam = "pegasus%X0seiya";
    assertNull(HttpClientUtils.urlDecode(encodedParam));
  }

  /**
   * getInputStream test.
   */
  @Test
  public void getInputStreamTest() throws IOException {
    InputStream inputStream = new ByteArrayInputStream("mada".getBytes(Charsets.UTF_8));
    BasicHttpEntity httpEntity = new BasicHttpEntity();
    httpEntity.setContent(inputStream);
    response.setEntity(httpEntity);

    assertNotNull(HttpClientUtils.getInputStream(response));
  }

  /**
   * getStatusLine test.
   */
  @Test
  public void getStatusLineTest() {
    StatusLine returnedStatusLine = HttpClientUtils.getStatusLine(response);

    assertNotNull(returnedStatusLine);
    assertEquals(200, returnedStatusLine.getStatusCode());
    assertEquals("OK", returnedStatusLine.getReasonPhrase());
  }

  /**
   * getStatusCode test.
   */
  @Test
  public void getStatusCodeTest() {
    assertEquals(200, HttpClientUtils.getStatusCode(response));
  }

  /**
   * getHeader test.
   */
  @Test
  public void getHeaderTest() {
    response.setHeader("ContentType", "application/json");

    assertEquals("application/json", HttpClientUtils.getHeader(response, "ContentType"));
  }

  /**
   * hasHeaders and getAllHeaders test.
   */
  @Test
  public void hasHeaderTest() {
    response.setHeaders(null);
    assertEquals(false, HttpClientUtils.hasHeaders(response));

    response.setHeader("ContentType", "application/json");
    assertEquals(true, HttpClientUtils.hasHeaders(response));

    request.setHeaders(null);
    assertEquals(false, HttpClientUtils.hasHeaders(request));

    request.setHeader("ContentType", "application/json");
    assertEquals(true, HttpClientUtils.hasHeaders(request));
  }

  /**
   * getHeader unset value test.
   */
  @Test
  public void getHeaderEmptyValueTest() {
    assertEquals(null, HttpClientUtils.getHeader(response, "ContentType"));
  }

  /**
   * hasResponseBody test.
   */
  @Test
  public void hasResponseBodyTest() throws IOException {
    assertEquals(false, HttpClientUtils.hasResponseBody(response));

    BasicHttpEntity entity = new BasicHttpEntity();
    entity.setContent(new ByteArrayInputStream("Content".getBytes(Charsets.UTF_8)));
    response.setEntity(entity);
    assertEquals(true, HttpClientUtils.hasResponseBody(response));
  }

  /**
   * httpGet test.
   */
  @Test
  public void httpGetTest() {
    HttpGet httpGet = HttpClientUtils.httpGet("http://kamehouse.unlimited.com/kamehameha");

    assertEquals("kamehouse.unlimited.com", httpGet.getURI().getHost());
    assertEquals("/kamehameha", httpGet.getURI().getPath());
    assertEquals("GET", httpGet.getMethod());
  }

  /**
   * execRequest test.
   */
  @Test
  public void execRequestTest() {
    HttpGet httpGet = HttpClientUtils.httpGet(
        "hs://kamehouse.unlimited.com/kamehameha/this-site-should-never-exist.mp4xv3");
    HttpClient httpClient = HttpClientUtils.getClient("", "");

    assertThrows(
        org.apache.http.client.ClientProtocolException.class,
        () -> {
          HttpClientUtils.execRequest(httpClient, httpGet);
        });
  }
}
