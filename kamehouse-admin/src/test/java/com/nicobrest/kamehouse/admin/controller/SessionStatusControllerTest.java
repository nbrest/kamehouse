package com.nicobrest.kamehouse.admin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.admin.model.SessionStatus;
import com.nicobrest.kamehouse.admin.service.SessionStatusService;
import com.nicobrest.kamehouse.admin.testutils.SessionStatusTestUtils;
import com.nicobrest.kamehouse.commons.controller.AbstractControllerTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Test class for the SessionStatusController.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class SessionStatusControllerTest extends AbstractControllerTest<SessionStatus, Object> {

  private SessionStatus sessionStatus;
  
  @Autowired
  private FilterChainProxy springSecurityFilterChain;

  @InjectMocks
  private SessionStatusController sessionStatusController;

  @Mock
  private SessionStatusService sessionStatusServiceMock;

  /**
   * Resets mock objects.
   */
  @Before
  public void beforeTest() {
    testUtils = new SessionStatusTestUtils();
    testUtils.initTestData();
    sessionStatus = testUtils.getSingleTestData();
    
    MockitoAnnotations.initMocks(this);
    Mockito.reset(sessionStatusServiceMock);
    mockMvc = MockMvcBuilders.standaloneSetup(sessionStatusController)
        .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain)).build();
  }

  /**
   * Tests getting the current session information.
   */
  @Test
  public void getSessionStatusTest() throws Exception { 
    when(sessionStatusServiceMock.get(any())).thenReturn(sessionStatus);

    MockHttpServletResponse response = doGet("/api/v1/session/status");
    SessionStatus responseBody = getResponseBody(response, SessionStatus.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributes(sessionStatus, responseBody);
    verify(sessionStatusServiceMock, times(1)).get(any());
    verifyNoMoreInteractions(sessionStatusServiceMock);
  }
}
