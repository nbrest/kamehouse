package com.nicobrest.kamehouse.ui.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.controller.AbstractControllerTest;
import com.nicobrest.kamehouse.ui.model.SessionStatus;
import com.nicobrest.kamehouse.ui.service.SessionStatusService;
import com.nicobrest.kamehouse.ui.testutils.SessionStatusTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Test class for the SessionStatusController.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
class SessionStatusControllerTest extends AbstractControllerTest<SessionStatus, Object> {

  private SessionStatus sessionStatus;

  @InjectMocks
  private SessionStatusController sessionStatusController;

  @Mock
  private SessionStatusService sessionStatusServiceMock;

  /**
   * Resets mock objects.
   */
  @BeforeEach
  void beforeTest() {
    testUtils = new SessionStatusTestUtils();
    testUtils.initTestData();
    sessionStatus = testUtils.getSingleTestData();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(sessionStatusServiceMock);
    mockMvc = MockMvcBuilders.standaloneSetup(sessionStatusController).build();
  }

  /**
   * Tests getting the current session information.
   */
  @Test
  void getSessionStatusTest() throws Exception {
    when(sessionStatusServiceMock.get(any())).thenReturn(sessionStatus);

    MockHttpServletResponse response = doGet("/api/v1/ui/session/status");
    SessionStatus responseBody = getResponseBody(response, SessionStatus.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributes(sessionStatus, responseBody);
    verify(sessionStatusServiceMock, times(1)).get(any());
    verifyNoMoreInteractions(sessionStatusServiceMock);
  }
}
