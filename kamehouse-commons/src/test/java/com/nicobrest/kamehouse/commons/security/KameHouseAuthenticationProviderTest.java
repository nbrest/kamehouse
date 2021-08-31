package com.nicobrest.kamehouse.commons.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.service.KameHouseUserAuthenticationService;
import com.nicobrest.kamehouse.commons.testutils.KameHouseUserTestUtils;
import com.nicobrest.kamehouse.commons.utils.PasswordUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
 */
public class KameHouseAuthenticationProviderTest {

  private KameHouseUserTestUtils testUtils = new KameHouseUserTestUtils();
  private KameHouseUser kameHouseUser;
  private KameHouseUser badUsernameKameHouseUser;
  private KameHouseUser badPasswordKameHouseUser;

  @InjectMocks private KameHouseAuthenticationProvider kameHouseAuthenticationProvider;

  @Mock private KameHouseUserAuthenticationService kameHouseUserServiceMock;

  /** Resets mock objects and initializes test repository. */
  @BeforeEach
  public void beforeTest() {
    testUtils.initTestData();
    kameHouseUser = testUtils.getSingleTestData();
    badUsernameKameHouseUser = testUtils.getBadUsernameKameHouseUser();
    badPasswordKameHouseUser = testUtils.getBadPasswordKameHouseUser();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(kameHouseUserServiceMock);
  }

  /** Tests a successful authentication. */
  @Test
  public void authenticateTest() {
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            kameHouseUser.getUsername(), kameHouseUser.getPassword());
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    kameHouseUser.setPassword(PasswordUtils.generateHashedPassword(kameHouseUser.getPassword()));
    when(kameHouseUserServiceMock.loadUserByUsername(kameHouseUser.getUsername()))
        .thenReturn(kameHouseUser);

    kameHouseAuthenticationProvider.authenticate(authentication);

    verify(kameHouseUserServiceMock, times(1)).loadUserByUsername(kameHouseUser.getUsername());
  }

  /** Tests failing authentication with an invalid username. */
  @Test
  public void authenticateBadUsernameTest() {
    assertThrows(
        BadCredentialsException.class,
        () -> {
          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                  badUsernameKameHouseUser.getUsername(), badUsernameKameHouseUser.getPassword());
          authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
          when(kameHouseUserServiceMock.loadUserByUsername(badUsernameKameHouseUser.getUsername()))
              .thenReturn(badUsernameKameHouseUser);

          kameHouseAuthenticationProvider.authenticate(authentication);
        });
  }

  /** Tests failing authentication with an invalid password. */
  @Test
  public void authenticateBadPasswordTest() {
    assertThrows(
        BadCredentialsException.class,
        () -> {
          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                  badPasswordKameHouseUser.getUsername(), badPasswordKameHouseUser.getPassword());
          authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
          when(kameHouseUserServiceMock.loadUserByUsername(badPasswordKameHouseUser.getUsername()))
              .thenReturn(badPasswordKameHouseUser);

          kameHouseAuthenticationProvider.authenticate(authentication);
        });
  }
}
