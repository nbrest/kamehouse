package com.nicobrest.kamehouse.testmodule.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.main.service.AbstractCrudServiceTest;
import com.nicobrest.kamehouse.testmodule.dao.DragonBallUserDao;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.service.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.testutils.DragonBallUserTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

/**
 * Unit tests for the DragonBallUserService class.
 *
 * @author nbrest
 */
public class DragonBallUserServiceTest extends
    AbstractCrudServiceTest<DragonBallUser, DragonBallUserDto> {

  private DragonBallUser dragonBallUser;
  private DragonBallUserDto dragonBallUserDto;
  private List<DragonBallUser> dragonBallUsersList;

  @InjectMocks
  private DragonBallUserService dragonBallUserService;

  @Mock(name = "dragonBallUserDao")
  private DragonBallUserDao dragonBallUserDaoMock;

  /**
   * Resets mock objects and initializes test repository.
   */
  @Before
  public void beforeTest() {
    testUtils = new DragonBallUserTestUtils();
    testUtils.initTestData();
    testUtils.setIds();
    dragonBallUser = testUtils.getSingleTestData();
    dragonBallUserDto = testUtils.getTestDataDto();
    dragonBallUsersList = testUtils.getTestDataList();

    // Reset mock objects before each test
    MockitoAnnotations.initMocks(this);
    Mockito.reset(dragonBallUserDaoMock);
  }

  /**
   * Test for calling the service to create a DragonBallUser in the repository.
   */
  @Test
  public void createTest() {
    createTest(dragonBallUserService, dragonBallUserDaoMock, dragonBallUser, dragonBallUserDto);
  }

  /**
   * Test for calling the service to get a single DragonBallUser in the
   * repository by id.
   */
  @Test
  public void readTest() {
    readTest(dragonBallUserService, dragonBallUserDaoMock, dragonBallUser);
  }

  /**
   * Test for calling the service to get all the DragonBallUsers in the
   * repository.
   */
  @Test
  public void readAllTest() {
    readAllTest(dragonBallUserService, dragonBallUserDaoMock, dragonBallUsersList);
  }

  /**
   * Test for calling the service to update an existing DragonBallUser in the
   * repository.
   */
  @Test
  public void updateTest() {
    updateTest(dragonBallUserService, dragonBallUserDaoMock, dragonBallUser, dragonBallUserDto);
  }

  /**
   * Test for calling the service to delete an existing user in the repository.
   */
  @Test
  public void deleteTest() {
    deleteTest(dragonBallUserService, dragonBallUserDaoMock, dragonBallUser);
  }

  /**
   * Test for calling the service to get a single DragonBallUser in the
   * repository by username.
   */
  @Test
  public void getByUsernameTest() {
    when(dragonBallUserDaoMock.getByUsername(dragonBallUser.getUsername())).thenReturn(
        dragonBallUser);

    DragonBallUser returnedUser = dragonBallUserService.getByUsername(dragonBallUser
        .getUsername());

    assertEquals(dragonBallUser, returnedUser);
    testUtils.assertEqualsAllAttributes(dragonBallUser, returnedUser);
    verify(dragonBallUserDaoMock, times(1)).getByUsername(dragonBallUser.getUsername());
  }

  /**
   * Test for calling the service to get a single DragonBallUser in the
   * repository by its email.
   */
  @Test
  public void getByEmailTest() {
    when(dragonBallUserDaoMock.getByEmail(dragonBallUser.getEmail())).thenReturn(dragonBallUser);

    DragonBallUser returnedUser = dragonBallUserService.getByEmail(dragonBallUser.getEmail());

    assertEquals(dragonBallUser, returnedUser);
    testUtils.assertEqualsAllAttributes(dragonBallUser, returnedUser);
    verify(dragonBallUserDaoMock, times(1)).getByEmail(dragonBallUser.getEmail());
  }
}
