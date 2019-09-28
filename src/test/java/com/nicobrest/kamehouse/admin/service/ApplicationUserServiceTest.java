package com.nicobrest.kamehouse.admin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.admin.dao.ApplicationUserDao;
import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.admin.model.dto.ApplicationUserDto;
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
public class ApplicationUserServiceTest extends
    AbstractCrudServiceTest<ApplicationUser, ApplicationUserDto> {

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
   * Test for calling the service to create an ApplicationUser in the
   * repository.
   */
  @Test
  public void createTest() {
    createTest(applicationUserService, applicationUserDaoMock, applicationUser,
        applicationUserDto);
  }

  /**
   * Test for calling the service to get a single ApplicationUser.
   */
  @Test
  public void readTest() {
    readTest(applicationUserService, applicationUserDaoMock, applicationUser);
  }

  /**
   * Test for getting all users of the application.
   */
  @Test
  public void readAllTest() {
    readAllTest(applicationUserService, applicationUserDaoMock, applicationUsersList);
  }

  /**
   * Test for calling the service to update an existing ApplicationUser in the
   * repository.
   */
  @Test
  public void updateTest() {
    updateTest(applicationUserService, applicationUserDaoMock, applicationUser,
        applicationUserDto);
  }

  /**
   * Test for calling the service to delete an existing user in the repository.
   */
  @Test
  public void deleteTest() {
    deleteTest(applicationUserService, applicationUserDaoMock, applicationUser);
  }

  /**
   * Test for calling the service to get a single ApplicationUser in the
   * repository by username.
   */
  @Test
  public void loadUserByUsernameTest() {
    when(applicationUserDaoMock.loadUserByUsername(applicationUser.getUsername())).thenReturn(
        applicationUser);

    ApplicationUser returnedUser = applicationUserService.loadUserByUsername(applicationUser
        .getUsername());

    assertNotNull(returnedUser);
    assertEquals(applicationUser, returnedUser);
    testUtils.assertEqualsAllAttributes(applicationUser, returnedUser);
    verify(applicationUserDaoMock, times(1)).loadUserByUsername(applicationUser.getUsername());
  }
}
