package com.nicobrest.kamehouse.admin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.admin.dao.ApplicationUserDao;
import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.admin.service.dto.ApplicationRoleDto;
import com.nicobrest.kamehouse.admin.service.dto.ApplicationUserDto;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Unit tests for the ApplicationUserService class.
 *
 * @author nbrest
 */
public class ApplicationUserServiceTest {

  private ApplicationUser applicationUserMock;
  private ApplicationUserDto applicationUserDtoMock;
  private static List<ApplicationUser> applicationUsersList;
  
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

    applicationUserDtoMock = new ApplicationUserDto();
    applicationUserDtoMock.setId(1000L);
    applicationUserDtoMock.setEmail("gokuTestMock@dbz.com");
    applicationUserDtoMock.setUsername("gokuTestMock");
    applicationUserDtoMock.setPassword("gokupass");
    applicationUserDtoMock.setFirstName("Goku");
    applicationUserDtoMock.setLastName("Son");
    applicationUserDtoMock.setAccountNonExpired(true);
    applicationUserDtoMock.setAccountNonLocked(true);
    applicationUserDtoMock.setCredentialsNonExpired(true);
    applicationUserDtoMock.setEnabled(true);
    applicationUserDtoMock.setLastLogin(new Date()); 
    Set<ApplicationRoleDto> authorities = new HashSet<>();
    ApplicationRoleDto applicationRoleDto = new ApplicationRoleDto();
    applicationRoleDto.setId(10L);
    applicationRoleDto.setName("ADMIN_ROLE");
    authorities.add(applicationRoleDto);
    applicationUserDtoMock.setAuthorities(authorities);
    
    ApplicationUser applicationUserMock2 = new ApplicationUser();
    applicationUserMock2.setId(1002L);
    applicationUserMock2.setEmail("gohan@dbz.com");
    applicationUserMock2.setUsername("gohan");
    applicationUserMock2.setPassword("gohan");

    ApplicationUser applicationUserMock3 = new ApplicationUser();
    applicationUserMock3.setId(1003L);
    applicationUserMock3.setEmail("goten@dbz.com");
    applicationUserMock3.setUsername("goten");
    applicationUserMock3.setPassword("goten");

    applicationUsersList = new LinkedList<ApplicationUser>();
    applicationUsersList.add(applicationUserMock);
    applicationUsersList.add(applicationUserMock2);
    applicationUsersList.add(applicationUserMock3);
    
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
      assertEquals("1000", user.getId().toString());
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
      when(applicationUserDaoMock.getAllUsers()).thenReturn(applicationUsersList);
      List<ApplicationUser> returnedApplicationUsers = applicationUserService.getAllUsers();
      assertEquals(applicationUsersList.size(), returnedApplicationUsers.size());
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
