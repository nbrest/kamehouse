package com.nicobrest.kamehouse.commons.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.exception.KameHouseForbiddenException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests for the abstract kamehouse servlet.
 *
 * @author nbrest
 */
public class AbstractKameHouseServletTest {

  private MockHttpServletRequest request = new MockHttpServletRequest();
  private MockHttpServletResponse response = new MockHttpServletResponse();

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Tests a sample doGet reading a request url parameter and writing it to the response body.
   */
  @Test
  public void doGetTest() throws ServletException, IOException {
    SampleKameHouseServlet sampleKameHouseServlet = new SampleKameHouseServlet();
    request.setParameter("my-param", "mada mada dane");
    request.setParameter("my-long-param", "22");

    sampleKameHouseServlet.doGet(request, response);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals("mada mada dane22", response.getContentAsString());
    assertEquals(ContentType.APPLICATION_JSON.getMimeType(), response.getContentType());
  }

  /**
   * Tests error getting a parameter that's not set.
   */
  @Test
  public void doGetErrorTest() throws ServletException, IOException {
    SampleKameHouseServlet sampleKameHouseServlet = new SampleKameHouseServlet();

    sampleKameHouseServlet.doGet(request, response);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    assertEquals("{\"message\":\"Error getting url parameter my-param\"}", response.getContentAsString());
    assertEquals(ContentType.APPLICATION_JSON.getMimeType(), response.getContentType());
  }

  /**
   * Tests exception handler: bad request.
   */
  @Test
  public void doBadRequestTest() throws IOException {
    SampleKameHouseServlet sampleKameHouseServlet = new SampleKameHouseServlet();

    sampleKameHouseServlet.doException(new KameHouseBadRequestException("bad request"), response);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    assertEquals("{\"message\":\"bad request\"}", response.getContentAsString());
    assertEquals(ContentType.APPLICATION_JSON.getMimeType(), response.getContentType());
  }

  /**
   * Tests exception handler: conflict.
   */
  @Test
  public void doConflictTest() throws IOException {
    SampleKameHouseServlet sampleKameHouseServlet = new SampleKameHouseServlet();

    sampleKameHouseServlet.doException(new KameHouseConflictException("conflict"), response);

    assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    assertEquals("{\"message\":\"conflict\"}", response.getContentAsString());
    assertEquals(ContentType.APPLICATION_JSON.getMimeType(), response.getContentType());
  }

  /**
   * Tests exception handler: forbidden.
   */
  @Test
  public void doForbiddenTest() throws IOException {
    SampleKameHouseServlet sampleKameHouseServlet = new SampleKameHouseServlet();

    sampleKameHouseServlet.doException(new KameHouseForbiddenException("forbidden"), response);

    assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    assertEquals("{\"message\":\"forbidden\"}", response.getContentAsString());
    assertEquals(ContentType.APPLICATION_JSON.getMimeType(), response.getContentType());
  }

  /**
   * Tests exception handler: invalid command.
   */
  @Test
  public void doInvalidCommandTest() throws IOException {
    SampleKameHouseServlet sampleKameHouseServlet = new SampleKameHouseServlet();

    sampleKameHouseServlet.doException(new KameHouseInvalidCommandException("invalid command"), response);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    assertEquals("{\"message\":\"invalid command\"}", response.getContentAsString());
    assertEquals(ContentType.APPLICATION_JSON.getMimeType(), response.getContentType());
  }

  /**
   * Tests exception handler: invalid data.
   */
  @Test
  public void doInvalidDataTest() throws IOException {
    SampleKameHouseServlet sampleKameHouseServlet = new SampleKameHouseServlet();

    sampleKameHouseServlet.doException(new KameHouseInvalidDataException("invalid data"), response);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    assertEquals("{\"message\":\"invalid data\"}", response.getContentAsString());
    assertEquals(ContentType.APPLICATION_JSON.getMimeType(), response.getContentType());
  }

  /**
   * Tests exception handler: not found.
   */
  @Test
  public void doNotFoundTest() throws IOException {
    SampleKameHouseServlet sampleKameHouseServlet = new SampleKameHouseServlet();

    sampleKameHouseServlet.doException(new KameHouseNotFoundException("not found"), response);

    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    assertEquals("{\"message\":\"not found\"}", response.getContentAsString());
    assertEquals(ContentType.APPLICATION_JSON.getMimeType(), response.getContentType());
  }

  /**
   * Tests exception handler: server error.
   */
  @Test
  public void doServerErrorTest() throws IOException {
    SampleKameHouseServlet sampleKameHouseServlet = new SampleKameHouseServlet();

    sampleKameHouseServlet.doException(new KameHouseServerErrorException("server error"), response);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
    assertEquals("{\"message\":\"server error\"}", response.getContentAsString());
    assertEquals(ContentType.APPLICATION_JSON.getMimeType(), response.getContentType());
  }

  /**
   * Sample servlet to test AbstractKameHouseServlet.
   */
  public static class SampleKameHouseServlet extends AbstractKameHouseServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
      try {
        String myParam = getUrlDecodedParam(request, "my-param");
        Long myLongParam = getLongUrlDecodedParam(request, "my-long-param");
        setResponseBody(response, myParam + myLongParam);
      } catch (KameHouseException e) {
        handleKameHouseException(response, e);
      }
    }

    /**
     * Throws the kamehouse exception passed to test the exception handler.
     */
    public void doException(KameHouseException exception, HttpServletResponse response) {
      try {
        throw exception;
      } catch (KameHouseException e) {
        handleKameHouseException(response, e);
      }
    }
  }
}
