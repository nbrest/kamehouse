package com.nicobrest.kamehouse.testmodule.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.service.AbstractCrudServiceTest;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.testmodule.dao.DragonBallUserDao;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.testutils.DragonBallUserTestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * Unit tests for the DragonBallUserService class.
 *
 * @author nbrest
 */
class DragonBallUserServiceTest
    extends AbstractCrudServiceTest<DragonBallUser, DragonBallUserDto> {

  @InjectMocks
  private DragonBallUserService dragonBallUserService;

  @Mock(name = "dragonBallUserDao")
  private DragonBallUserDao dragonBallUserDaoMock;

  @Override
  public CrudService<DragonBallUser, DragonBallUserDto> getCrudService() {
    return dragonBallUserService;
  }

  @Override
  public CrudDao<DragonBallUser> getCrudDao() {
    return dragonBallUserDaoMock;
  }

  @Override
  public TestUtils<DragonBallUser, DragonBallUserDto> getTestUtils() {
    return new DragonBallUserTestUtils();
  }

  /**
   * Tests calling the service to get a single DragonBallUser in the repository by username.
   */
  @Test
  void getByUsernameTest() {
    DragonBallUser dragonBallUser = testUtils.getSingleTestData();
    when(dragonBallUserDaoMock.getByUsername(dragonBallUser.getUsername()))
        .thenReturn(dragonBallUser);

    DragonBallUser returnedUser = dragonBallUserService.getByUsername(dragonBallUser.getUsername());

    testUtils.assertEqualsAllAttributes(dragonBallUser, returnedUser);
    verify(dragonBallUserDaoMock, times(1)).getByUsername(dragonBallUser.getUsername());
  }

  /**
   * Tests calling the service to get a single DragonBallUser in the repository by its email.
   */
  @Test
  void getByEmailTest() {
    DragonBallUser dragonBallUser = testUtils.getSingleTestData();
    when(dragonBallUserDaoMock.getByEmail(dragonBallUser.getEmail())).thenReturn(dragonBallUser);

    DragonBallUser returnedUser = dragonBallUserService.getByEmail(dragonBallUser.getEmail());

    testUtils.assertEqualsAllAttributes(dragonBallUser, returnedUser);
    verify(dragonBallUserDaoMock, times(1)).getByEmail(dragonBallUser.getEmail());
  }
}
