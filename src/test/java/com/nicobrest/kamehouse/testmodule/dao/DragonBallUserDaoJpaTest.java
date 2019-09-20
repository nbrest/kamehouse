package com.nicobrest.kamehouse.testmodule.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.nicobrest.kamehouse.main.dao.AbstractDaoJpaTest;
import com.nicobrest.kamehouse.main.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.main.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.testutils.DragonBallUserTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Unit tests for the DragonBallUserDaoJpa class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class DragonBallUserDaoJpaTest extends AbstractDaoJpaTest {

  private static DragonBallUser dragonBallUser;
  private static List<DragonBallUser> dragonBallUsersList;

  @Autowired
  private DragonBallUserDao dragonBallUserDaoJpa;

  /**
   * Clear data from the repository before each test.
   */
  @Before
  public void setUp() {
    DragonBallUserTestUtils.initTestData();
    dragonBallUser = DragonBallUserTestUtils.getSingleTestData();
    dragonBallUsersList = DragonBallUserTestUtils.getTestDataList();

    clearTable("DRAGONBALL_USER");
  }

  /**
   * Test for creating a DragonBallUser in the repository.
   */
  @Test
  public void createDragonBallUserTest() {
    Long returnedId = dragonBallUserDaoJpa.createDragonBallUser(dragonBallUser);

    DragonBallUser returnedUser = findById(DragonBallUser.class, returnedId);
    assertEquals(dragonBallUser, returnedUser);
  }

  /**
   * Test for creating a DragonBallUser in the repository Exception flows.
   */
  @Test
  public void createDragonBallUserConflictExceptionTest() {
    thrown.expect(KameHouseConflictException.class);
    thrown.expectMessage("ConstraintViolationException: Error inserting data");

    dragonBallUserDaoJpa.createDragonBallUser(dragonBallUser);
    dragonBallUser.setId(null);
    dragonBallUserDaoJpa.createDragonBallUser(dragonBallUser);
  }

  /**
   * Test for getting a single DragonBallUser in the repository by id.
   */
  @Test
  public void getDragonBallUserTest() {

    try {
      dragonBallUserDaoJpa.createDragonBallUser(dragonBallUser);
      DragonBallUser userByUsername =
          dragonBallUserDaoJpa.getDragonBallUser(dragonBallUser.getUsername());
      DragonBallUser user = dragonBallUserDaoJpa.getDragonBallUser(userByUsername.getId());

      assertNotNull(user);
      assertEquals(userByUsername.getId().toString(), user.getId().toString());
    } catch (KameHouseNotFoundException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for getting a single DragonBallUser in the repository by username.
   */
  @Test
  public void getDragonBallUserByUsernameTest() {

    try {
      dragonBallUserDaoJpa.createDragonBallUser(dragonBallUser);

      DragonBallUser user = dragonBallUserDaoJpa.getDragonBallUser(dragonBallUser.getUsername());

      assertNotNull(user);
      assertEquals(dragonBallUser.getUsername(), user.getUsername());
    } catch (KameHouseNotFoundException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for getting a single DragonBallUser in the repository Exception flows.
   */
  @Test
  public void getDragonBallUserNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("Entity not found in the repository.");

    dragonBallUserDaoJpa.getDragonBallUser(DragonBallUserTestUtils.INVALID_USERNAME);
  }

  /**
   * Test for getting a single DragonBallUser in the repository by its email.
   */
  @Test
  public void getDragonBallUserByEmailTest() {
    try {
      dragonBallUserDaoJpa.createDragonBallUser(dragonBallUser);

      DragonBallUser user =
          dragonBallUserDaoJpa.getDragonBallUserByEmail(dragonBallUser.getEmail());

      assertNotNull(user);
      assertEquals(dragonBallUser.getUsername(), user.getUsername());
    } catch (KameHouseNotFoundException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for getting a single DragonBallUser in the repository by its email
   * Exception flows.
   */
  @Test
  public void getDragonBallUserByEmailNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("NoResultException: Entity not found in the repository.");

    dragonBallUserDaoJpa.getDragonBallUserByEmail(DragonBallUserTestUtils.INVALID_EMAIL);
  }

  /**
   * Test for updating an existing user in the repository.
   */
  @Test
  public void updateDragonBallUserTest() {

    try {
      dragonBallUserDaoJpa.createDragonBallUser(dragonBallUser);
      DragonBallUser originalUser =
          dragonBallUserDaoJpa.getDragonBallUser(dragonBallUser.getUsername());
      originalUser.setEmail("gokuUpdated@dbz.com");

      dragonBallUserDaoJpa.updateDragonBallUser(originalUser);

      DragonBallUser updatedUser = dragonBallUserDaoJpa.getDragonBallUser(originalUser.getId());
      assertEquals(originalUser.getId().toString(), updatedUser.getId().toString());
      assertEquals(originalUser.getUsername(), updatedUser.getUsername());
      assertEquals(originalUser.getEmail(), updatedUser.getEmail());
    } catch (KameHouseNotFoundException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
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

    dragonBallUserDaoJpa.updateDragonBallUser(dragonBallUser);
  }

  /**
   * Test for updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateDragonBallUserServerErrorExceptionTest() {
    thrown.expect(KameHouseServerErrorException.class);
    thrown.expectMessage("PersistenceException");
    dragonBallUserDaoJpa.createDragonBallUser(dragonBallUser);
    DragonBallUser originalUser =
        dragonBallUserDaoJpa.getDragonBallUser(dragonBallUser.getUsername());
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 70; i++) {
      sb.append("goku");
    }
    String username = sb.toString();
    originalUser.setUsername(username);
    originalUser.setEmail("gokuUpdated@dbz.com");

    dragonBallUserDaoJpa.updateDragonBallUser(originalUser);
  }

  /**
   * Test for deleting an existing user from the repository.
   */
  @Test
  public void deleteDragonBallUserTest() {
    dragonBallUserDaoJpa.createDragonBallUser(dragonBallUser);
    DragonBallUser deletedUser = dragonBallUserDaoJpa.deleteDragonBallUser(dragonBallUser.getId());

    assertEquals(dragonBallUser.getUsername(), deletedUser.getUsername());
    assertEquals(dragonBallUser.getEmail(), deletedUser.getEmail());
  }

  /**
   * Test for deleting an existing user from the repository Exception flows.
   */
  @Test
  public void deleteDragonBallUserNotFoundExceptionTest() {

    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("DragonBallUser with id " + DragonBallUserTestUtils.INVALID_ID
        + " was not found in the repository.");
    dragonBallUserDaoJpa.deleteDragonBallUser(DragonBallUserTestUtils.INVALID_ID);
  }

  /**
   * Test for getting all the DragonBallUsers in the repository.
   */
  @Test
  public void getAllDragonBallUsersTest() {

    dragonBallUserDaoJpa.createDragonBallUser(dragonBallUsersList.get(0));
    dragonBallUserDaoJpa.createDragonBallUser(dragonBallUsersList.get(1));
    try {
      List<DragonBallUser> usersList = dragonBallUserDaoJpa.getAllDragonBallUsers();
      assertEquals(2, usersList.size());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }
}
