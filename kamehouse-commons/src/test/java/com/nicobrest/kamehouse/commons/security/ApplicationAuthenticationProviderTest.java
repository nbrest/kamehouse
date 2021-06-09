package com.nicobrest.kamehouse.commons.security;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.nicobrest.kamehouse.commons.model.ApplicationUser;
import com.nicobrest.kamehouse.commons.service.ApplicationUserAuthenticationService;
import com.nicobrest.kamehouse.commons.testutils.ApplicationUserTestUtils;
import com.nicobrest.kamehouse.commons.utils.PasswordUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * Test class for the custom AuthenticationProvider implementation.
 * 
 * @author nbrest
 *
 */
public class ApplicationAuthenticationProviderTest {

  private ApplicationUserTestUtils testUtils = new ApplicationUserTestUtils();
  private ApplicationUser applicationUser;
  private ApplicationUser badUsernameApplicationUser;
  private ApplicationUser badPasswordApplicationUser;

  @InjectMocks
  private ApplicationAuthenticationProvider applicationAuthenticationProvider;

  @Mock
  private ApplicationUserAuthenticationService applicationUserServiceMock;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Resets mock objects and initializes test repository.
   */
  @Before
  public void beforeTest() {
    testUtils.initTestData();
    applicationUser = testUtils.getSingleTestData();
    badUsernameApplicationUser = testUtils.getBadUsernameApplicationUser();
    badPasswordApplicationUser = testUtils.getBadPasswordApplicationUser();

    MockitoAnnotations.initMocks(this);
    Mockito.reset(applicationUserServiceMock);
  }

  /**
   * Tests a successful authentication.
   */
  @Test
  public void authenticateTest() {
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        applicationUser.getUsername(), applicationUser.getPassword());
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    applicationUser
        .setPassword(PasswordUtils.generateHashedPassword(applicationUser.getPassword()));
    when(applicationUserServiceMock.loadUserByUsername(applicationUser.getUsername()))
        .thenReturn(applicationUser);

    applicationAuthenticationProvider.authenticate(authentication);

    verify(applicationUserServiceMock, times(1)).loadUserByUsername(applicationUser.getUsername());
  }

  /**
   * Tests failing authentication with an invalid username.
   */
  @Test
  public void authenticateBadUsernameTest() {
    thrown.expect(BadCredentialsException.class);
    thrown.expectMessage("Username not found.");
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        badUsernameApplicationUser.getUsername(), badUsernameApplicationUser.getPassword());
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    when(applicationUserServiceMock.loadUserByUsername(badUsernameApplicationUser.getUsername()))
        .thenReturn(badUsernameApplicationUser);

    applicationAuthenticationProvider.authenticate(authentication);

    verify(applicationUserServiceMock, times(1))
        .loadUserByUsername(badUsernameApplicationUser.getUsername());
  }

  /**
   * Tests failing authentication with an invalid password.
   */
  @Test
  public void authenticateBadPasswordTest() {
    thrown.expect(BadCredentialsException.class);
    thrown.expectMessage("Wrong password.");
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        badPasswordApplicationUser.getUsername(), badPasswordApplicationUser.getPassword());
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    when(applicationUserServiceMock.loadUserByUsername(badPasswordApplicationUser.getUsername()))
        .thenReturn(badPasswordApplicationUser);

    applicationAuthenticationProvider.authenticate(authentication);

    verify(applicationUserServiceMock, times(1))
        .loadUserByUsername(badPasswordApplicationUser.getUsername());
  }
}
