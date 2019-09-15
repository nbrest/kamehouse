package com.nicobrest.kamehouse.admin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.admin.dao.ApplicationUserDao;
import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.admin.service.dto.ApplicationUserDto;
import com.nicobrest.kamehouse.admin.testutils.ApplicationUserTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

/**
 * Unit tests for the ApplicationUserService class.
 *
 * @author nbrest
 */
public class ApplicationUserServiceTest {

  private static ApplicationUser applicationUserMock = ApplicationUserTestUtils
      .getApplicationUserMock();
  private static List<ApplicationUser> applicationUsersMockList = ApplicationUserTestUtils
      .getApplicationUsersMockList();
  private static ApplicationUserDto applicationUserDtoMock = ApplicationUserTestUtils
      .getApplicationUserDtoMock();
  
  @InjectMocks
  private ApplicationUserService applicationUserService;

  @Mock
  private ApplicationUserDao applicationUserDaoMock;

  /**
   * Resets mock objects and initializes test repository.
   */
  @Before
  public void beforeTest() {    
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
      applicationUserService.createUser(applicationUserDtoMock);
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
      assertEquals("1001", user.getId().toString());
      verify(applicationUserDaoMock, times(1)).loadUserByUsername(applicationUserMock
          .getUsername());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for getting all users of the application.
   */
  @Test
  public void getAllUsersTest() { 
    try {
      when(applicationUserDaoMock.getAllUsers()).thenReturn(applicationUsersMockList);
      List<ApplicationUser> returnedApplicationUsers = applicationUserService.getAllUsers();
      assertEquals(applicationUsersMockList.size(), returnedApplicationUsers.size());
      verify(applicationUserDaoMock, times(1)).getAllUsers();
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
      applicationUserService.updateUser(applicationUserDtoMock);
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
