package com.nicobrest.kamehouse.testmodule.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
public class DragonBallUserServiceTest {

  private static DragonBallUser dragonBallUser;
  private static DragonBallUserDto dragonBallUserDto;
  private static List<DragonBallUser> dragonBallUsersList;

  @InjectMocks
  private DragonBallUserService dragonBallUserService;

  @Mock(name = "dragonBallUserDao")
  private DragonBallUserDao dragonBallUserDaoMock;

  /**
   * Resets mock objects and initializes test repository.
   */
  @Before
  public void beforeTest() {
    DragonBallUserTestUtils.initTestData();
    DragonBallUserTestUtils.setIds();
    dragonBallUser = DragonBallUserTestUtils.getSingleTestData();
    dragonBallUserDto = DragonBallUserTestUtils.getTestDataDto();
    dragonBallUsersList = DragonBallUserTestUtils.getTestDataList();

    // Reset mock objects before each test
    MockitoAnnotations.initMocks(this);
    Mockito.reset(dragonBallUserDaoMock);
  }

  /**
   * Test for calling the service to create a DragonBallUser in the repository.
   */
  @Test
  public void createDragonBallUserTest() {
    Mockito.doReturn(dragonBallUser.getId()).when(dragonBallUserDaoMock)
        .createDragonBallUser(dragonBallUser);

    Long createdId = dragonBallUserService.createDragonBallUser(dragonBallUserDto);

    assertEquals(dragonBallUser.getId(), createdId);
    verify(dragonBallUserDaoMock, times(1)).createDragonBallUser(dragonBallUser);
  }

  /**
   * Test for calling the service to get a single DragonBallUser in the repository
   * by id.
   */
  @Test
  public void getDragonBallUserTest() {
    when(dragonBallUserDaoMock.getDragonBallUser(dragonBallUser.getId()))
        .thenReturn(dragonBallUser);

    DragonBallUser returnedUser = dragonBallUserService.getDragonBallUser(dragonBallUser.getId());

    assertEquals(dragonBallUser, returnedUser);
    verify(dragonBallUserDaoMock, times(1)).getDragonBallUser(dragonBallUser.getId());
  }

  /**
   * Test for calling the service to get a single DragonBallUser in the repository
   * by username.
   */
  @Test
  public void getDragonBallUserByUsernameTest() {
    when(dragonBallUserDaoMock.getDragonBallUser(dragonBallUser.getUsername()))
        .thenReturn(dragonBallUser);

    DragonBallUser returnedUser =
        dragonBallUserService.getDragonBallUser(dragonBallUser.getUsername());

    assertEquals(dragonBallUser, returnedUser);
    verify(dragonBallUserDaoMock, times(1)).getDragonBallUser(dragonBallUser.getUsername());
  }

  /**
   * Test for calling the service to get a single DragonBallUser in the repository
   * by its email.
   */
  @Test
  public void getDragonBallUserByEmailTest() {
    when(dragonBallUserDaoMock.getDragonBallUserByEmail(dragonBallUser.getEmail()))
        .thenReturn(dragonBallUser);

    DragonBallUser returnedUser =
        dragonBallUserService.getDragonBallUserByEmail(dragonBallUser.getEmail());

    assertEquals(dragonBallUser, returnedUser);
    verify(dragonBallUserDaoMock, times(1)).getDragonBallUserByEmail(dragonBallUser.getEmail());
  }

  /**
   * Test for calling the service to update an existing DragonBallUser in the
   * repository.
   */
  @Test
  public void updateDragonBallUserTest() {
    Mockito.doNothing().when(dragonBallUserDaoMock).updateDragonBallUser(dragonBallUser);

    dragonBallUserService.updateDragonBallUser(dragonBallUserDto);

    verify(dragonBallUserDaoMock, times(1)).updateDragonBallUser(dragonBallUser);
  }

  /**
   * Test for calling the service to delete an existing user in the repository.
   */
  @Test
  public void deleteDragonBallUserTest() {
    when(dragonBallUserDaoMock.deleteDragonBallUser(dragonBallUser.getId()))
        .thenReturn(dragonBallUser);

    DragonBallUser deletedUser = dragonBallUserService.deleteDragonBallUser(dragonBallUser.getId());

    assertEquals(dragonBallUser, deletedUser);
    verify(dragonBallUserDaoMock, times(1)).deleteDragonBallUser(dragonBallUser.getId());
  }

  /**
   * Test for calling the service to get all the DragonBallUsers in the
   * repository.
   */
  @Test
  public void getAllDragonBallUsersTest() {
    when(dragonBallUserDaoMock.getAllDragonBallUsers()).thenReturn(dragonBallUsersList);

    List<DragonBallUser> returnedList = dragonBallUserService.getAllDragonBallUsers();

    assertEquals(dragonBallUsersList, returnedList);
    verify(dragonBallUserDaoMock, times(1)).getAllDragonBallUsers();
  }
}
