package com.nicobrest.kamehouse.admin.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import com.nicobrest.kamehouse.admin.service.LogLevelManagerService;
import com.nicobrest.kamehouse.main.controller.AbstractControllerTest;
import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.Arrays;
import java.util.List;

/**
 * Test class for the LogLevelManagerController.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class LogLevelManagerControllerTest extends AbstractControllerTest<List<String>, Object> {

  private List<String> logLevelSingleElement = Arrays.asList("com.nicobrest.kamehouse:TRACE");
  private List<String> logLevelMultipleElements = Arrays.asList("com.nicobrest.kamehouse:INFO",
      "com.nicobrest.kamehouse.vlcrc:TRACE", "com.nicobrest.kamehouse.main:DEBUG");

  @InjectMocks
  private LogLevelManagerController logLevelManagerController;

  @Mock
  private LogLevelManagerService logLevelManagerService;

  /**
   * Resets mock objects.
   */
  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    Mockito.reset(logLevelManagerService);
    mockMvc = MockMvcBuilders.standaloneSetup(logLevelManagerController).build();
  }

  /**
   * Tests getting the log level for a specific package.
   */
  @Test
  public void getLogLevelSinglePackageSuccessTest() throws Exception {
    when(logLevelManagerService.getLogLevel("com.nicobrest.kamehouse")).thenReturn(logLevelSingleElement);

    MockHttpServletResponse response = doGet("/api/v1/admin/log-level?package=com.nicobrest"
        + ".kamehouse");
    List<String> responseBody = getResponseBodyList(response, String.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEqualsStringList(logLevelSingleElement, responseBody);
    verify(logLevelManagerService, times(1)).getLogLevel("com.nicobrest.kamehouse");
    verifyNoMoreInteractions(logLevelManagerService);
  }

  /**
   * Tests getting the log level for all packages with log level set.
   */
  @Test
  public void getLogLevelSuccessTest() throws Exception {
    when(logLevelManagerService.getLogLevel(null)).thenReturn(logLevelMultipleElements);

    MockHttpServletResponse response = doGet("/api/v1/admin/log-level");
    List<String> responseBody = getResponseBodyList(response, String.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEqualsStringList(logLevelMultipleElements, responseBody);
    verify(logLevelManagerService, times(1)).getLogLevel(null);
    verifyNoMoreInteractions(logLevelManagerService);
  }

  /**
   * Tests setting the log level for a specific package.
   */
  @Test
  public void setLogLevelSuccessTest() throws Exception {
    doNothing().when(logLevelManagerService).setLogLevel("TRACE","com.nicobrest.kamehouse");
    when(logLevelManagerService.getLogLevel("com.nicobrest.kamehouse")).thenReturn(logLevelSingleElement);

    MockHttpServletResponse response = doPut("/api/v1/admin/log-level?level=TRACE&package=com"
        + ".nicobrest.kamehouse");
    List<String> responseBody = getResponseBodyList(response, String.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEqualsStringList(logLevelSingleElement, responseBody);
    verify(logLevelManagerService, times(1)).getLogLevel("com.nicobrest.kamehouse");
    verify(logLevelManagerService, times(1)).setLogLevel("TRACE","com.nicobrest.kamehouse");
    verify(logLevelManagerService, times(1)).validateLogLevel("TRACE");
    verifyNoMoreInteractions(logLevelManagerService);
  }

  /**
   * Tests setting the log level for the default package.
   */
  @Test
  public void setLogLevelDefaultPackageSuccessTest() throws Exception {
    doNothing().when(logLevelManagerService).setLogLevel("TRACE","com.nicobrest.kamehouse");
    when(logLevelManagerService.getLogLevel("com.nicobrest.kamehouse")).thenReturn(logLevelSingleElement);

    MockHttpServletResponse response = doPut("/api/v1/admin/log-level?level=TRACE");
    List<String> responseBody = getResponseBodyList(response, String.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEqualsStringList(logLevelSingleElement, responseBody);
    verify(logLevelManagerService, times(1)).getLogLevel("com.nicobrest.kamehouse");
    verify(logLevelManagerService, times(1)).setLogLevel("TRACE","com.nicobrest.kamehouse");
    verify(logLevelManagerService, times(1)).validateLogLevel("TRACE");
    verifyNoMoreInteractions(logLevelManagerService);
  }

  /**
   * Tests setting an invalid log level for the default package.
   */
  @Test
  public void setLogLevelInvalidLogLevelTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable>instanceOf(KameHouseBadRequestException.class));

    doThrow(new KameHouseBadRequestException("Invalid log level TRACEs"))
        .when(logLevelManagerService).validateLogLevel("TRACEs");

    doPut("/api/v1/admin/log-level?level=TRACEs");
  }

  /**
   * Tests setting the log level for all kamehouse packages to DEBUG.
   */
  @Test
  public void setKamehouseLogLevelsToDebugSuccessTest() throws Exception {
    doNothing().when(logLevelManagerService).setKamehouseLogLevelsToTrace();

    MockHttpServletResponse response = doPut("/api/v1/admin/log-level/debug");

    verifyResponseStatus(response, HttpStatus.OK);
    verify(logLevelManagerService, times(1)).setKamehouseLogLevelsToDebug();
    verify(logLevelManagerService, times(1)).getLogLevel(null);
    verifyNoMoreInteractions(logLevelManagerService);
  }

  /**
   * Tests setting the log level for all kamehouse packages to TRACE.
   */
  @Test
  public void setKamehouseLogLevelsToTraceSuccessTest() throws Exception {
    doNothing().when(logLevelManagerService).setKamehouseLogLevelsToTrace();

    MockHttpServletResponse response = doPut("/api/v1/admin/log-level/trace");

    verifyResponseStatus(response, HttpStatus.OK);
    verify(logLevelManagerService, times(1)).setKamehouseLogLevelsToTrace();
    verify(logLevelManagerService, times(1)).getLogLevel(null);
    verifyNoMoreInteractions(logLevelManagerService);
  }

  /**
   * Tests resetting all the log levels to their default value.
   */
  @Test
  public void resetLogLevelsSuccessTest() throws Exception {
    doNothing().when(logLevelManagerService).resetLogLevels();

    MockHttpServletResponse response = doDelete("/api/v1/admin/log-level");

    verifyResponseStatus(response, HttpStatus.OK);
    verify(logLevelManagerService, times(1)).resetLogLevels();
    verify(logLevelManagerService, times(1)).getLogLevel(null);
    verifyNoMoreInteractions(logLevelManagerService);
  }

  /**
   * Assert that two lists of strings are equal.
   * TODO: Make this generic and put it in a test utility class.
   */
  private void assertEqualsStringList(List<String> expected, List<String> returned) {
    assertEquals(expected, returned);
    if (expected != null && returned != null) {
      assertEquals(expected.size(), returned.size());
      for (int i = 0 ; i < expected.size() ; i++) {
        assertEquals(expected.get(i), returned.get(i));
      }
    }
  }
}
