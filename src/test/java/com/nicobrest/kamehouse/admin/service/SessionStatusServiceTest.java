package com.nicobrest.kamehouse.admin.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.admin.model.ApplicationUser;

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

import java.util.Map;

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

  @InjectMocks
  private SessionStatusService sessionStatusService;

  @Mock
  private ApplicationUserService applicationUserServiceMock;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    Mockito.reset(applicationUserServiceMock);
  }

  /**
   * Tests getting the current session information.
   */
  @Test
  public void getSessionStatusTest() throws Exception {
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        "anonymousUser", "anonymousUser");
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    SessionStatusService sessionStatusServiceSpy = PowerMockito.spy(sessionStatusService);
    PowerMockito.when(sessionStatusServiceSpy, "getAuthentication").thenReturn(authentication);
    ApplicationUser applicationUserMock = new ApplicationUser();
    applicationUserMock.setFirstName(null);
    applicationUserMock.setLastName(null);
    applicationUserMock.setEmail(null);
    when(applicationUserServiceMock.loadUserByUsername("anonymousUser")).thenReturn(
        applicationUserMock);

    Map<String, Object> returnedSessionStatus = sessionStatusServiceSpy.get();
    
    assertEquals("anonymousUser", returnedSessionStatus.get("username"));
    assertEquals(null, returnedSessionStatus.get("firstName"));
    assertEquals(null, returnedSessionStatus.get("lastName"));
    assertEquals(null, returnedSessionStatus.get("email"));
  }
}
