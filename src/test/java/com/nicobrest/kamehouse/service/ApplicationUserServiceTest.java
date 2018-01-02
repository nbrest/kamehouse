package com.nicobrest.kamehouse.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.dao.ApplicationUserDao;
import com.nicobrest.kamehouse.model.ApplicationUser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the ApplicationUserService class.
 *
 * @author nbrest
 */
public class ApplicationUserServiceTest {

  private ApplicationUser applicationUserMock;

  @InjectMocks
  private ApplicationUserService applicationUserService;

  @Mock
  private ApplicationUserDao applicationUserDaoMock;

  /**
   * Resets mock objects and initializes test repository.
   */
  @Before
  public void beforeTest() {
    applicationUserMock = new ApplicationUser();
    applicationUserMock.setId(1000L);
    applicationUserMock.setEmail("gokuTestMock@dbz.com");
    applicationUserMock.setUsername("gokuTestMock");
    applicationUserMock.setPassword("gokupass");
    applicationUserMock.setFirstName("Goku");
    applicationUserMock.setLastName("Son");

    MockitoAnnotations.initMocks(this);
    Mockito.reset(applicationUserDaoMock);
  }

  /**
   * Test for calling the service to create an ApplicationUser in the
   * repository.
   */
  @Test
  public void createUserTest() {
    try {
      Mockito.doReturn(1L).when(applicationUserDaoMock).createUser(applicationUserMock);
      applicationUserService.createUser(applicationUserMock);
      verify(applicationUserDaoMock, times(1)).createUser(applicationUserMock);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for calling the service to get a single ApplicationUser in the
   * repository by username.
   */
  @Test
  public void loadUserByUsernameTest() {

    try {
      when(applicationUserDaoMock.loadUserByUsername(applicationUserMock.getUsername()))
          .thenReturn(applicationUserMock);
      ApplicationUser user = applicationUserService.loadUserByUsername(applicationUserMock
          .getUsername());
      assertNotNull(user);
      assertEquals("1000", user.getId().toString());
      verify(applicationUserDaoMock, times(1)).loadUserByUsername(applicationUserMock
          .getUsername());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for calling the service to update an existing ApplicationUser in the
   * repository.
   */
  @Test
  public void updateUserTest() {

    try {
      Mockito.doNothing().when(applicationUserDaoMock).updateUser(applicationUserMock);
      applicationUserService.updateUser(applicationUserMock);
      verify(applicationUserDaoMock, times(1)).updateUser(applicationUserMock);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for calling the service to delete an existing user in the repository.
   */
  @Test
  public void deleteDragonBallUserTest() {

    try {
      when(applicationUserDaoMock.deleteUser(1000L)).thenReturn(applicationUserMock);
      applicationUserService.deleteUser(1000L);
      verify(applicationUserDaoMock, times(1)).deleteUser(1000L);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }
}
