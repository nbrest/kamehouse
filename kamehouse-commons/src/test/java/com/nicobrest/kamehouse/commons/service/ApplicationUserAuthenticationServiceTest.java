package com.nicobrest.kamehouse.commons.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.dao.ApplicationUserAuthenticationDao;
import com.nicobrest.kamehouse.commons.model.ApplicationUser;
import com.nicobrest.kamehouse.commons.model.dto.ApplicationUserDto;
import com.nicobrest.kamehouse.commons.testutils.ApplicationUserTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the ApplicationUserAuthenticationService class.
 *
 * @author nbrest
 */
public class ApplicationUserAuthenticationServiceTest
    extends AbstractCrudServiceTest<ApplicationUser, ApplicationUserDto> {

  private ApplicationUser applicationUser;

  @InjectMocks
  private ApplicationUserAuthenticationService applicationUserService;

  @Mock
  private ApplicationUserAuthenticationDao applicationUserDaoMock;

  /**
   * Resets mock objects and initializes test repository.
   */
  @Before
  public void beforeTest() {
    testUtils = new ApplicationUserTestUtils();
    testUtils.initTestData();
    applicationUser = testUtils.getSingleTestData();

    MockitoAnnotations.initMocks(this);
    Mockito.reset(applicationUserDaoMock);
  }

  /**
   * Tests calling the service to get a single ApplicationUser in the repository
   * by username.
   */
  @Test
  public void loadUserByUsernameTest() {
    when(applicationUserDaoMock.loadUserByUsername(applicationUser.getUsername()))
        .thenReturn(applicationUser);

    ApplicationUser returnedUser =
        applicationUserService.loadUserByUsername(applicationUser.getUsername());

    testUtils.assertEqualsAllAttributes(applicationUser, returnedUser);
    verify(applicationUserDaoMock, times(1)).loadUserByUsername(applicationUser.getUsername());
  }
}
