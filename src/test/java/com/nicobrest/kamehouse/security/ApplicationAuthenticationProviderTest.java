package com.nicobrest.kamehouse.security;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.model.ApplicationUser;
import com.nicobrest.kamehouse.service.ApplicationUserService;

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

public class ApplicationAuthenticationProviderTest {

  private ApplicationUser applicationUserMock;
  private ApplicationUser badUsernameApplicationUserMock;
  private ApplicationUser badPasswordApplicationUserMock;

  @InjectMocks
  private ApplicationAuthenticationProvider applicationAuthenticationProvider;

  @Mock
  private ApplicationUserService applicationUserServiceMock;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Resets mock objects and initializes test repository.
   */
  @Before
  public void beforeTest() {
    applicationUserMock = new ApplicationUser();
    applicationUserMock.setId(1000L);
    applicationUserMock.setEmail("goku@dbz.com");
    applicationUserMock.setUsername("gokuuser");
    applicationUserMock.setPassword("gokupass");

    badUsernameApplicationUserMock = new ApplicationUser();
    badUsernameApplicationUserMock.setId(1000L);
    badUsernameApplicationUserMock.setEmail("goku@dbz.com");
    badUsernameApplicationUserMock.setUsername(null);
    badUsernameApplicationUserMock.setPassword("gokupass");

    badPasswordApplicationUserMock = new ApplicationUser();
    badPasswordApplicationUserMock.setId(1000L);
    badPasswordApplicationUserMock.setEmail("goku@dbz.com");
    badPasswordApplicationUserMock.setUsername("gokuuser");
    badPasswordApplicationUserMock.setPassword(null);

    MockitoAnnotations.initMocks(this);
    Mockito.reset(applicationUserServiceMock);
  }

  /**
   * Tests a successful authentication.
   */
  @Test
  public void authenticateTest() {
    try {
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          applicationUserMock.getUsername(), applicationUserMock.getPassword());
      authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
      applicationUserMock.setPassword(PasswordUtils.generateHashedPassword(applicationUserMock
          .getPassword()));
      when(applicationUserServiceMock.loadUserByUsername(applicationUserMock.getUsername()))
          .thenReturn(applicationUserMock);
      applicationAuthenticationProvider.authenticate(authentication);
      verify(applicationUserServiceMock, times(1)).loadUserByUsername(applicationUserMock
          .getUsername());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * Tests failing authentication with an invalid username.
   */
  @Test
  public void authenticateBadUsernameTest() {

    thrown.expect(BadCredentialsException.class);
    thrown.expectMessage("Username not found.");

    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        badUsernameApplicationUserMock.getUsername(), badUsernameApplicationUserMock
            .getPassword());
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    when(applicationUserServiceMock.loadUserByUsername(badUsernameApplicationUserMock
        .getUsername())).thenReturn(badUsernameApplicationUserMock);
    applicationAuthenticationProvider.authenticate(authentication);
    verify(applicationUserServiceMock, times(1)).loadUserByUsername(badUsernameApplicationUserMock
        .getUsername());
  }

  /**
   * Tests failing authentication with an invalid password.
   */
  @Test
  public void authenticateBadPasswordTest() {

    thrown.expect(BadCredentialsException.class);
    thrown.expectMessage("Wrong password.");

    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        badPasswordApplicationUserMock.getUsername(), badPasswordApplicationUserMock
            .getPassword());
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    when(applicationUserServiceMock.loadUserByUsername(badPasswordApplicationUserMock
        .getUsername())).thenReturn(badPasswordApplicationUserMock);
    applicationAuthenticationProvider.authenticate(authentication);
    verify(applicationUserServiceMock, times(1)).loadUserByUsername(badPasswordApplicationUserMock
        .getUsername());
  }
}
