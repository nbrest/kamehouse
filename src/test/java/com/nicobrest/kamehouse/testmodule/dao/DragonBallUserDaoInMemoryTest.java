package com.nicobrest.kamehouse.testmodule.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.nicobrest.kamehouse.main.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.testutils.DragonBallUserTestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit tests for the DragonBallUserInMemoryDao class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class DragonBallUserDaoInMemoryTest {

  private static DragonBallUser dragonBallUser;

  @Autowired
  @Qualifier("dragonBallUserDaoInMemory")
  private DragonBallUserDaoInMemory dragonBallUserDao;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Clear data from the repository before each test.
   */
  @Before
  public void setUp() {
    DragonBallUserTestUtils.initTestData();
    dragonBallUser = DragonBallUserTestUtils.getSingleTestData();
    DragonBallUserDaoInMemory.initRepository();
  }

  /**
   * Test for the autowired beans.
   */
  @Test
  public void autoWiredBeansTest() {
    DragonBallUser gohan = dragonBallUserDao.getGohanDragonBallUser();
    DragonBallUser goten = dragonBallUserDao.getGotenDragonBallUser();

    assertNotNull(gohan);
    assertEquals("gohanBean", gohan.getUsername());
    assertNotNull(goten);
    assertEquals("gotenBean", goten.getUsername());
  }

  /**
   * Test for creating a DragonBallUser in the repository.
   */
  @Test
  public void createDragonBallUserTest() {
    Long createdId = dragonBallUserDao.createDragonBallUser(dragonBallUser);

    DragonBallUser createdUser = dragonBallUserDao.getDragonBallUser(createdId);
    assertEquals(dragonBallUser, createdUser);
  }

  /**
   * Test for creating a DragonBallUser in the repository Exception flows.
   */
  @Test
  public void createDragonBallUserConflictExceptionTest() {
    thrown.expect(KameHouseConflictException.class);
    thrown.expectMessage("DragonBallUser with username " + dragonBallUser.getUsername()
        + " already exists in the repository.");
    dragonBallUserDao.createDragonBallUser(dragonBallUser);

    dragonBallUserDao.createDragonBallUser(dragonBallUser);
  }

  /**
   * Test for getting a single DragonBallUser in the repository by its id.
   */
  @Test
  public void getDragonBallUserTest() {
    DragonBallUser userByUsername = dragonBallUserDao.getDragonBallUser("goku");

    DragonBallUser userById = dragonBallUserDao.getDragonBallUser(userByUsername.getId());

    assertNotNull(userById);
    assertEquals(userByUsername, userById);
  }

  /**
   * Test for getting a single DragonBallUser in the repository by its username.
   */
  @Test
  public void getDragonBallUserByUsernameTest() {
    DragonBallUser userByUsername = dragonBallUserDao.getDragonBallUser("goku");

    assertNotNull(userByUsername);
    assertEquals("goku", userByUsername.getUsername());
  }

  /**
   * Test for getting a single DragonBallUser in the repository by email.
   */
  @Test
  public void getDragonBallUserByEmailTest() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage(
        "This functionality is not implemented for the DragonBallUserInMemory repository.");

    dragonBallUserDao.getDragonBallUserByEmail(DragonBallUserTestUtils.INVALID_EMAIL);
  }

  /**
   * Test for getting a single DragonBallUser in the repository Exception flows.
   */
  @Test
  public void getDragonBallUserByUsernameNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("DragonBallUser with username " + DragonBallUserTestUtils.INVALID_EMAIL
        + " was not found in the repository.");

    dragonBallUserDao.getDragonBallUser(DragonBallUserTestUtils.INVALID_EMAIL);
  }

  /**
   * Test for updating an existing user in the repository.
   */
  @Test
  public void updateDragonBallUserTest() {
    DragonBallUser originalUser = dragonBallUserDao.getDragonBallUser("goku");
    originalUser.setEmail("gokuUpdated@dbz.com");

    dragonBallUserDao.updateDragonBallUser(originalUser);

    DragonBallUser updatedUser = dragonBallUserDao.getDragonBallUser("goku");
    assertEquals(originalUser, updatedUser);
  }

  /**
   * Test for updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateDragonBallUserNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("DragonBallUser with id " + DragonBallUserTestUtils.INVALID_ID
        + " was not found in the repository.");
    dragonBallUser.setId(DragonBallUserTestUtils.INVALID_ID);

    dragonBallUserDao.updateDragonBallUser(dragonBallUser);
  }

  /**
   * Test for deleting an existing user from the repository.
   */
  @Test
  public void deleteDragonBallUserTest() {
    dragonBallUserDao.createDragonBallUser(dragonBallUser);

    DragonBallUser deletedUser = dragonBallUserDao.deleteDragonBallUser(dragonBallUser.getId());

    assertEquals(dragonBallUser, deletedUser);
  }

  /**
   * Test for deleting an existing user from the repository Exception flows.
   */
  @Test
  public void deleteDragonBallUserNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("DragonBallUser with id " + DragonBallUserTestUtils.INVALID_ID
        + " was not found in the repository.");

    dragonBallUserDao.deleteDragonBallUser(DragonBallUserTestUtils.INVALID_ID);
  }

  /**
   * Test for getting all the DragonBallUsers in the repository.
   */
  @Test
  public void getAllDragonBallUsersTest() {
    assertEquals(3, dragonBallUserDao.getAllDragonBallUsers().size());
  }
}
