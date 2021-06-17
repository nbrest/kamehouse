package com.nicobrest.kamehouse.ui.service;

import static org.mockito.Mockito.when;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.service.KameHouseUserAuthenticationService;
import com.nicobrest.kamehouse.ui.model.SessionStatus;
import com.nicobrest.kamehouse.ui.testutils.SessionStatusTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Test class for the SessionStatusService.
 * 
 * @author nbrest
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SessionStatusService.class })
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class SessionStatusServiceTest {

  private SessionStatusTestUtils testUtils = new SessionStatusTestUtils();
  private SessionStatus sessionStatus;

  @InjectMocks
  private SessionStatusService sessionStatusService;

  @Mock
  private KameHouseUserAuthenticationService kameHouseUserAuthenticationService;

  @Before
  public void init() {
    testUtils.initTestData();
    sessionStatus = testUtils.getSingleTestData();

    MockitoAnnotations.initMocks(this);
    Mockito.reset(kameHouseUserAuthenticationService);
  }

  /**
   * Tests getting the current session information.
   */
  @Test
  public void getSessionStatusTest() throws Exception {
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken("anonymousUser", "anonymousUser");
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    SessionStatusService sessionStatusServiceSpy = PowerMockito.spy(sessionStatusService);
    PowerMockito.when(sessionStatusServiceSpy, "getAuthentication")
        .thenReturn(authentication);
    KameHouseUser kameHouseUserMock = new KameHouseUser();
    when(kameHouseUserAuthenticationService.loadUserByUsername("anonymousUser"))
        .thenReturn(kameHouseUserMock);

    SessionStatus returnedSessionStatus = sessionStatusServiceSpy.get(null);

    testUtils.assertEqualsAllAttributes(sessionStatus, returnedSessionStatus);
  }
}
