package com.nicobrest.kamehouse.testmodule.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.nicobrest.kamehouse.main.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.main.testutils.TestUtils;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
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

  private TestUtils<DragonBallUser, DragonBallUserDto> testUtils;
  private DragonBallUser dragonBallUser;

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
    testUtils = new DragonBallUserTestUtils();
    testUtils.initTestData();
    dragonBallUser = testUtils.getSingleTestData();
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
  public void createTest() {
    Long createdId = dragonBallUserDao.create(dragonBallUser);

    DragonBallUser createdUser = dragonBallUserDao.read(createdId);
    assertEquals(dragonBallUser, createdUser);
  }

  /**
   * Test for creating a DragonBallUser in the repository Exception flows.
   */
  @Test
  public void createConflictExceptionTest() {
    thrown.expect(KameHouseConflictException.class);
    thrown.expectMessage("DragonBallUser with username " + dragonBallUser.getUsername()
        + " already exists in the repository.");
    dragonBallUserDao.create(dragonBallUser);

    dragonBallUserDao.create(dragonBallUser);
  }

  /**
   * Test for getting a single DragonBallUser in the repository by its id.
   */
  @Test
  public void readTest() {
    DragonBallUser userByUsername = dragonBallUserDao.getByUsername("goku");

    DragonBallUser userById = dragonBallUserDao.read(userByUsername.getId());

    assertNotNull(userById);
    assertEquals(userByUsername, userById);
  }

  /**
   * Test for getting all the DragonBallUsers in the repository.
   */
  @Test
  public void readAllTest() {
    assertEquals(3, dragonBallUserDao.readAll().size());
  }

  /**
   * Test for updating an existing user in the repository.
   */
  @Test
  public void updateTest() {
    DragonBallUser originalUser = dragonBallUserDao.getByUsername("goku");
    originalUser.setEmail("gokuUpdated@dbz.com");

    dragonBallUserDao.update(originalUser);

    DragonBallUser updatedUser = dragonBallUserDao.getByUsername("goku");
    assertEquals(originalUser, updatedUser);
  }

  /**
   * Test for updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("DragonBallUser with id " + DragonBallUserTestUtils.INVALID_ID
        + " was not found in the repository.");
    dragonBallUser.setId(DragonBallUserTestUtils.INVALID_ID);

    dragonBallUserDao.update(dragonBallUser);
  }

  /**
   * Test for deleting an existing user from the repository.
   */
  @Test
  public void deleteTest() {
    dragonBallUserDao.create(dragonBallUser);

    DragonBallUser deletedUser = dragonBallUserDao.delete(dragonBallUser.getId());

    assertEquals(dragonBallUser, deletedUser);
  }

  /**
   * Test for deleting an existing user from the repository Exception flows.
   */
  @Test
  public void deleteNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("DragonBallUser with id " + DragonBallUserTestUtils.INVALID_ID
        + " was not found in the repository.");

    dragonBallUserDao.delete(DragonBallUserTestUtils.INVALID_ID);
  }

  /**
   * Test for getting a single DragonBallUser in the repository by its username.
   */
  @Test
  public void getByUsernameTest() {
    DragonBallUser userByUsername = dragonBallUserDao.getByUsername("goku");

    assertNotNull(userByUsername);
    assertEquals("goku", userByUsername.getUsername());
  }

  /**
   * Test for getting a single DragonBallUser in the repository Exception flows.
   */
  @Test
  public void getByUsernameNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("DragonBallUser with username " + DragonBallUserTestUtils.INVALID_EMAIL
        + " was not found in the repository.");

    dragonBallUserDao.getByUsername(DragonBallUserTestUtils.INVALID_EMAIL);
  }

  /**
   * Test for getting a single DragonBallUser in the repository by email.
   */
  @Test
  public void getByEmailTest() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage(
        "This functionality is not implemented for the DragonBallUserInMemory repository.");

    dragonBallUserDao.getByEmail(DragonBallUserTestUtils.INVALID_EMAIL);
  }
}
