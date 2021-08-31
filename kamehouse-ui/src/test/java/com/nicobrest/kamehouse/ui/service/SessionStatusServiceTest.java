package com.nicobrest.kamehouse.ui.service;

import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.service.KameHouseUserAuthenticationService;
import com.nicobrest.kamehouse.ui.model.SessionStatus;
import com.nicobrest.kamehouse.ui.testutils.SessionStatusTestUtils;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Test class for the SessionStatusService.
 *
 * @author nbrest
 */
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
public class SessionStatusServiceTest {

  private SessionStatusTestUtils testUtils = new SessionStatusTestUtils();
  private SessionStatus sessionStatus;

  @InjectMocks private SessionStatusService sessionStatusService;

  @Mock private KameHouseUserAuthenticationService kameHouseUserAuthenticationService;

  @BeforeEach
  public void init() {
    testUtils.initTestData();
    sessionStatus = testUtils.getSingleTestData();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(kameHouseUserAuthenticationService);
  }

  /** Tests getting the current session information. */
  @Test
  public void getSessionStatusTest() {
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken("anonymousUser", "anonymousUser");
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    SessionStatusService sessionStatusServiceSpy = Mockito.spy(sessionStatusService);
    when(sessionStatusServiceSpy.getAuthentication()).thenReturn(authentication);
    KameHouseUser kameHouseUserMock = new KameHouseUser();
    when(kameHouseUserAuthenticationService.loadUserByUsername("anonymousUser"))
        .thenReturn(kameHouseUserMock);
    HttpSession session = new MockHttpSession();

    SessionStatus returnedSessionStatus = sessionStatusServiceSpy.get(session);

    testUtils.assertEqualsAllAttributes(sessionStatus, returnedSessionStatus);
  }

  /** Tests getting the current session information. */
  @Test
  public void getSessionStatusNullHttpSessionTest() {
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken("anonymousUser", "anonymousUser");
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    SessionStatusService sessionStatusServiceSpy = Mockito.spy(sessionStatusService);
    when(sessionStatusServiceSpy.getAuthentication()).thenReturn(authentication);
    KameHouseUser kameHouseUserMock = new KameHouseUser();
    when(kameHouseUserAuthenticationService.loadUserByUsername("anonymousUser"))
        .thenReturn(kameHouseUserMock);

    SessionStatus returnedSessionStatus = sessionStatusServiceSpy.get(null);

    testUtils.assertEqualsAllAttributes(sessionStatus, returnedSessionStatus);
  }

  /** Tests getting the current session with user not found. */
  @Test
  public void getSessionStatusUserNotFoundTest() {
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken("anonymousUser", "anonymousUser");
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    SessionStatusService sessionStatusServiceSpy = Mockito.spy(sessionStatusService);
    when(sessionStatusServiceSpy.getAuthentication()).thenReturn(authentication);
    when(kameHouseUserAuthenticationService.loadUserByUsername("anonymousUser"))
        .thenThrow(new UsernameNotFoundException("User not found"));

    SessionStatus returnedSessionStatus = sessionStatusServiceSpy.get(null);

    testUtils.assertEqualsAllAttributes(sessionStatus, returnedSessionStatus);
  }
}
