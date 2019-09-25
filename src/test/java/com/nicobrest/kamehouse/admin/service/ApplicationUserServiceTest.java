package com.nicobrest.kamehouse.admin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.admin.dao.ApplicationUserDao;
import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.admin.service.dto.ApplicationUserDto;
import com.nicobrest.kamehouse.admin.testutils.ApplicationUserTestUtils;
import com.nicobrest.kamehouse.main.service.AbstractCrudServiceTest;

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
public class ApplicationUserServiceTest
    extends AbstractCrudServiceTest<ApplicationUser, ApplicationUserDto> {

  private ApplicationUser applicationUser;
  private List<ApplicationUser> applicationUsersList;
  private ApplicationUserDto applicationUserDto;

  @InjectMocks
  private ApplicationUserService applicationUserService;

  @Mock
  private ApplicationUserDao applicationUserDaoMock;

  /**
   * Resets mock objects and initializes test repository.
   */
  @Before
  public void beforeTest() {
    testUtils = new ApplicationUserTestUtils();
    testUtils.initTestData(); 
    applicationUser = testUtils.getSingleTestData();
    applicationUsersList = testUtils.getTestDataList();
    applicationUserDto = testUtils.getTestDataDto();

    MockitoAnnotations.initMocks(this);
    Mockito.reset(applicationUserDaoMock);
  }

  /**
   * Test for calling the service to create an ApplicationUser in the repository.
   */
  @Test
  public void createTest() {
    createTest(applicationUserService, applicationUserDaoMock, applicationUser, applicationUserDto);
  }

  /**
   * Test for getting all users of the application.
   */
  @Test
  public void readAllTest() {
    when(applicationUserDaoMock.readAll()).thenReturn(applicationUsersList);

    List<ApplicationUser> returnedApplicationUsers = applicationUserService.readAll();

    assertEquals(applicationUsersList.size(), returnedApplicationUsers.size());
    assertEquals(applicationUsersList, returnedApplicationUsers);
    verify(applicationUserDaoMock, times(1)).readAll();
  }

  /**
   * Test for calling the service to update an existing ApplicationUser in the
   * repository.
   */
  @Test
  public void updateTest() {
    Mockito.doNothing().when(applicationUserDaoMock).update(applicationUser);

    applicationUserService.update(applicationUserDto);

    verify(applicationUserDaoMock, times(1)).update(applicationUser);
  }

  /**
   * Test for calling the service to delete an existing user in the repository.
   */
  @Test
  public void deleteTest() {
    when(applicationUserDaoMock.delete(applicationUser.getId())).thenReturn(applicationUser);

    ApplicationUser deletedUser = applicationUserService.delete(applicationUser.getId());

    assertEquals(applicationUser, deletedUser);
    verify(applicationUserDaoMock, times(1)).delete(applicationUser.getId());
  }

  /**
   * Test for calling the service to get a single ApplicationUser in the
   * repository by username.
   */
  @Test
  public void loadUserByUsernameTest() {
    when(applicationUserDaoMock.loadUserByUsername(applicationUser.getUsername()))
        .thenReturn(applicationUser);

    ApplicationUser returnedUser =
        applicationUserService.loadUserByUsername(applicationUser.getUsername());

    assertNotNull(returnedUser);
    assertEquals(applicationUser, returnedUser);
    verify(applicationUserDaoMock, times(1)).loadUserByUsername(applicationUser.getUsername());
  }
}
