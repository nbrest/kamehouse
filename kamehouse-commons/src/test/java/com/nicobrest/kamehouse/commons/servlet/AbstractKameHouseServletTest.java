package com.nicobrest.kamehouse.commons.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.exception.KameHouseForbiddenException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

/**
 * Tests for the abstract kamehouse servlet.
 *
 * @author nbrest
 */
public class AbstractKameHouseServletTest {

  private MockHttpServletRequest request = new MockHttpServletRequest();
  private MockHttpServletResponse response = new MockHttpServletResponse();
  private SampleKameHouseServlet sampleKameHouseServlet;
  private final static String SESSION_ID = "Y2VkODlmZTktYmFlZi00Njg5LTljN2MtYzI3NzYxZTQ0ZDM3";

  @Mock
  private HttpServletResponse responseMock;

  @Mock
  private SessionRepository sessionRepository;

  @Mock
  private Session session;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Authentication authentication;

  @Mock
  private GrantedAuthority grantedAuthority;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    sampleKameHouseServlet = Mockito.spy(new SampleKameHouseServlet());
    doReturn(sessionRepository).when(sampleKameHouseServlet).getSessionRepository(any());
    doReturn(session).when(sessionRepository).findById(any());
    doReturn(securityContext).when(session).getAttribute(any());
    doReturn(authentication).when(securityContext).getAuthentication();
    doReturn(Instant.now()).when(session).getLastAccessedTime();
    doReturn(Duration.ofDays(30)).when(session).getMaxInactiveInterval();
  }

  /**
   * Tests a sample doGet reading a request url parameter and writing it to the response body.
   */
  @Test
  public void doGetTest() throws ServletException, IOException {
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
    sampleKameHouseServlet.doGet(request, response);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    assertEquals("{\"message\":\"Error getting url parameter my-param\"}",
        response.getContentAsString());
    assertEquals(ContentType.APPLICATION_JSON.getMimeType(), response.getContentType());
  }

  /**
   * Tests exception handler: bad request.
   */
  @Test
  public void doBadRequestTest() throws IOException {
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
    sampleKameHouseServlet.doException(new KameHouseInvalidCommandException("invalid command"),
        response);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    assertEquals("{\"message\":\"invalid command\"}", response.getContentAsString());
    assertEquals(ContentType.APPLICATION_JSON.getMimeType(), response.getContentType());
  }

  /**
   * Tests exception handler: invalid data.
   */
  @Test
  public void doInvalidDataTest() throws IOException {
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
    sampleKameHouseServlet.doException(new KameHouseServerErrorException("server error"), response);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
    assertEquals("{\"message\":\"server error\"}", response.getContentAsString());
    assertEquals(ContentType.APPLICATION_JSON.getMimeType(), response.getContentType());
  }

  /**
   * Tests setResponseBody error.
   */
  @Test
  public void setResponseBodyExceptionTest() throws IOException {
    doThrow(new IOException("mock exception")).when(responseMock).getWriter();
    assertThrows(
        KameHouseServerErrorException.class,
        () -> {
          sampleKameHouseServlet.setResponseBody(responseMock, null);
        });
  }

  /**
   * Tests a sample authorize() no session cookie unauthorized.
   */
  @Test
  public void authorizeNoSessionCookieUnauthorizedTest() {
    assertThrows(
        KameHouseForbiddenException.class,
        () -> {
          sampleKameHouseServlet.authorize(request, List.of("ROLE_KAMISAMA"));
        });

    request.setCookies(new Cookie("invalid-key", "invalid-value"));
    assertThrows(
        KameHouseForbiddenException.class,
        () -> {
          sampleKameHouseServlet.authorize(request, List.of("ROLE_KAMISAMA"));
        });
  }

  /**
   * Tests a sample authorize() invalid session cookie unauthorized.
   */
  @Test
  public void authorizeInvalidSessionCookieUnauthorizedTest() {
    request.setCookies(new Cookie(AbstractKameHouseServlet.KAMEHOUSE_SESSION_ID, "invalid-value"));
    assertThrows(
        KameHouseForbiddenException.class,
        () -> {
          sampleKameHouseServlet.authorize(request, List.of("ROLE_KAMISAMA"));
        });
  }

  /**
   * Tests a sample authorize() no authorities (roles) unauthorized.
   */
  @Test
  public void authorizeNoAuthoritiesUnauthorizedTest() {
    request.setCookies(new Cookie(AbstractKameHouseServlet.KAMEHOUSE_SESSION_ID, SESSION_ID));
    assertThrows(
        KameHouseForbiddenException.class,
        () -> {
          sampleKameHouseServlet.authorize(request, List.of("ROLE_KAMISAMA"));
        });
  }

  /**
   * Tests a sample authorize() no session repository unauthorized.
   */
  @Test
  public void authorizeNoSessionRepositoryUnauthorizedTest() {
    doCallRealMethod().when(sampleKameHouseServlet).getSessionRepository(any());
    request.setCookies(new Cookie(AbstractKameHouseServlet.KAMEHOUSE_SESSION_ID, SESSION_ID));
    assertThrows(
        KameHouseForbiddenException.class,
        () -> {
          sampleKameHouseServlet.authorize(request, List.of("ROLE_KAMISAMA"));
        });
  }

  /**
   * Tests a sample authorize() no session unauthorized.
   */
  @Test
  public void authorizeNoSessionUnauthorizedTest() {
    doReturn(null).when(sessionRepository).findById(any());
    request.setCookies(new Cookie(AbstractKameHouseServlet.KAMEHOUSE_SESSION_ID, SESSION_ID));
    assertThrows(
        KameHouseForbiddenException.class,
        () -> {
          sampleKameHouseServlet.authorize(request, List.of("ROLE_KAMISAMA"));
        });
  }

  /**
   * Tests a sample authorize() session expired unauthorized.
   */
  @Test
  public void authorizeSessionExpiredUnauthorizedTest() {
    doReturn(Instant.now().minus(Duration.ofDays(30))).when(session).getLastAccessedTime();
    doReturn(Duration.ofMinutes(1)).when(session).getMaxInactiveInterval();
    request.setCookies(new Cookie(AbstractKameHouseServlet.KAMEHOUSE_SESSION_ID, SESSION_ID));
    assertThrows(
        KameHouseForbiddenException.class,
        () -> {
          sampleKameHouseServlet.authorize(request, List.of("ROLE_KAMISAMA"));
        });
  }

  /**
   * Tests a sample authorize() no security context unauthorized.
   */
  @Test
  public void authorizeNoSecurityContextUnauthorizedTest() {
    doReturn(null).when(session).getAttribute(any());
    request.setCookies(new Cookie(AbstractKameHouseServlet.KAMEHOUSE_SESSION_ID, SESSION_ID));
    assertThrows(
        KameHouseForbiddenException.class,
        () -> {
          sampleKameHouseServlet.authorize(request, List.of("ROLE_KAMISAMA"));
        });
  }

  /**
   * Tests a sample authorize() no authentication (user) unauthorized.
   */
  @Test
  public void authorizeNoAuthenticationUnauthorizedTest() {
    doReturn(null).when(securityContext).getAuthentication();
    request.setCookies(new Cookie(AbstractKameHouseServlet.KAMEHOUSE_SESSION_ID, SESSION_ID));
    assertThrows(
        KameHouseForbiddenException.class,
        () -> {
          sampleKameHouseServlet.authorize(request, List.of("ROLE_KAMISAMA"));
        });
  }

  /**
   * Tests a sample authorize() user without needed role unauthorized.
   */
  @Test
  public void authorizeUnauthorizedTest() {
    doReturn("ROLE_INVALID").when(grantedAuthority).getAuthority();
    doReturn(List.of(grantedAuthority)).when(authentication).getAuthorities();
    request.setCookies(new Cookie(AbstractKameHouseServlet.KAMEHOUSE_SESSION_ID, SESSION_ID));
    assertThrows(
        KameHouseForbiddenException.class,
        () -> {
          sampleKameHouseServlet.authorize(request, List.of("ROLE_KAMISAMA"));
        });
  }

  /**
   * Tests a sample authorize() success.
   */
  @Test
  public void authorizeSuccessTest() {
    doReturn("ROLE_KAMISAMA").when(grantedAuthority).getAuthority();
    doReturn(List.of(grantedAuthority)).when(authentication).getAuthorities();
    request.setCookies(new Cookie(AbstractKameHouseServlet.KAMEHOUSE_SESSION_ID, SESSION_ID));

    sampleKameHouseServlet.authorize(request, List.of("ROLE_KAMISAMA"));
    // no exception thrown, so it's authorized
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
