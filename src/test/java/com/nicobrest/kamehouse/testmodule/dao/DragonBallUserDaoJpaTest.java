package com.nicobrest.kamehouse.testmodule.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.nicobrest.kamehouse.main.dao.AbstractDaoJpaTest;
import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.main.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.main.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
   
  @Autowired
  private DragonBallUserDao dragonBallUserDaoJpa;
  
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Clear data from the repository before each test.
   */
  @Before
  public void setUp() {
    clearTable("DRAGONBALL_USER");
  }

  /**
   * Test for creating a DragonBallUser in the repository.
   */
  @Test
  public void createDragonBallUserTest() {

    DragonBallUser dragonBallUser = new DragonBallUser(null, "vegeta", "vegeta@dbz.com", 49, 40,
        1000);

    try {
      assertEquals(0, findAll(DragonBallUser.class).size());
      dragonBallUserDaoJpa.createDragonBallUser(dragonBallUser);
      assertEquals(1, dragonBallUserDaoJpa.getAllDragonBallUsers().size());
      dragonBallUserDaoJpa
          .deleteDragonBallUser(dragonBallUserDaoJpa.getDragonBallUser("vegeta").getId());
    } catch (KameHouseBadRequestException | KameHouseNotFoundException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for creating a DragonBallUser in the repository Exception flows.
   */
  @Test
  public void createDragonBallUserConflictExceptionTest() {

    thrown.expect(KameHouseConflictException.class);
    thrown.expectMessage("ConstraintViolationException: Error inserting data");

    DragonBallUser dragonBallUser = new DragonBallUser(null, "goku", "goku@dbz.com", 49, 40, 1000);
    DragonBallUser dragonBallUser2 = new DragonBallUser(null, "goku", "goku@dbz.com", 49, 40,
        1000);

    dragonBallUserDaoJpa.createDragonBallUser(dragonBallUser);
    dragonBallUserDaoJpa.createDragonBallUser(dragonBallUser2);
  }

  /**
   * Test for getting a single DragonBallUser in the repository by id.
   */
  @Test
  public void getDragonBallUserTest() {

    try {
      DragonBallUser dbUser = new DragonBallUser(null, "goku", "goku@dbz.com", 20, 21, 22);
      dragonBallUserDaoJpa.createDragonBallUser(dbUser);
      DragonBallUser userByUsername = dragonBallUserDaoJpa.getDragonBallUser("goku");
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
      DragonBallUser dbUser = new DragonBallUser(null, "goku", "goku@dbz.com", 20, 21, 22);
      dragonBallUserDaoJpa.createDragonBallUser(dbUser);

      DragonBallUser user = dragonBallUserDaoJpa.getDragonBallUser("goku");
      
      assertNotNull(user);
      assertEquals("goku", user.getUsername());
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
    dragonBallUserDaoJpa.getDragonBallUser("yukimura");
  }

  /**
   * Test for getting a single DragonBallUser in the repository by its email.
   */
  @Test
  public void getDragonBallUserByEmailTest() {
    try {
      DragonBallUser dbUser = new DragonBallUser(null, "goku", "goku@dbz.com", 20, 21, 22);
      dragonBallUserDaoJpa.createDragonBallUser(dbUser);

      DragonBallUser user = dragonBallUserDaoJpa.getDragonBallUserByEmail("goku@dbz.com");

      assertNotNull(user);
      assertEquals("goku", user.getUsername());
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
    dragonBallUserDaoJpa.getDragonBallUserByEmail("yukimura@dbz.com");
  }

  /**
   * Test for updating an existing user in the repository.
   */
  @Test
  public void updateDragonBallUserTest() {

    try {
      DragonBallUser userToInsert = new DragonBallUser(null, "goku", "goku@dbz.com", 20, 21, 22);
      dragonBallUserDaoJpa.createDragonBallUser(userToInsert);

      DragonBallUser originalUser = dragonBallUserDaoJpa.getDragonBallUser("goku");
      assertEquals("goku", originalUser.getUsername());

      DragonBallUser modifiedUser = new DragonBallUser(originalUser.getId(), "goku",
          "gokuUpdated@dbz.com", 51, 52, 53);

      dragonBallUserDaoJpa.updateDragonBallUser(modifiedUser);
      DragonBallUser updatedUser = dragonBallUserDaoJpa.getDragonBallUser("goku");

      assertEquals(originalUser.getId().toString(), updatedUser.getId().toString());
      assertEquals("goku", updatedUser.getUsername());
      assertEquals("gokuUpdated@dbz.com", updatedUser.getEmail());
      assertEquals(51, updatedUser.getAge());
      assertEquals(52, updatedUser.getPowerLevel());
      assertEquals(53, updatedUser.getStamina());

      dragonBallUserDaoJpa.updateDragonBallUser(originalUser);
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

    DragonBallUser dragonBallUser = new DragonBallUser(0L, "yukimura", "yukimura@pot.com", 10, 10,
        10);
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("DragonBallUser with id 0 was not found in the repository.");
    dragonBallUserDaoJpa.updateDragonBallUser(dragonBallUser);
  }

  /**
   * Test for updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateDragonBallUserServerErrorExceptionTest() {

    thrown.expect(KameHouseServerErrorException.class);
    thrown.expectMessage("PersistenceException");

    try {
      DragonBallUser userToInsert = new DragonBallUser(null, "goku", "goku@dbz.com", 20, 21, 22);
      dragonBallUserDaoJpa.createDragonBallUser(userToInsert);

      DragonBallUser originalUser = dragonBallUserDaoJpa.getDragonBallUser("goku");
      assertEquals("goku", originalUser.getUsername());

      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < 70 ; i++) {
        sb.append("goku");
      }
      String username = sb.toString();

      DragonBallUser modifiedUser = new DragonBallUser(originalUser.getId(), username,
          "gokuUpdated@dbz.com", 51, 52, 53);

      dragonBallUserDaoJpa.updateDragonBallUser(modifiedUser);
    } catch (KameHouseNotFoundException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for deleting an existing user from the repository.
   */
  @Test
  public void deleteDragonBallUserTest() {

    try {
      DragonBallUser userToDelete = new DragonBallUser(null, "piccolo", "piccolo@dbz.com", 20, 21,
          22);
      dragonBallUserDaoJpa.createDragonBallUser(userToDelete);
      assertEquals(1, dragonBallUserDaoJpa.getAllDragonBallUsers().size());
      DragonBallUser deletedUser = dragonBallUserDaoJpa
          .deleteDragonBallUser(dragonBallUserDaoJpa.getDragonBallUser("piccolo").getId());
      assertEquals(0, dragonBallUserDaoJpa.getAllDragonBallUsers().size());
      assertEquals("piccolo", deletedUser.getUsername());
      assertEquals("piccolo@dbz.com", deletedUser.getEmail());
      assertEquals(20, deletedUser.getAge());
      assertEquals(21, deletedUser.getPowerLevel());
      assertEquals(22, deletedUser.getStamina());
    } catch (KameHouseNotFoundException | KameHouseBadRequestException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for deleting an existing user from the repository Exception flows.
   */
  @Test
  public void deleteDragonBallUserNotFoundExceptionTest() {

    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("DragonBallUser with id " + 987L + " was not found in the repository.");
    dragonBallUserDaoJpa.deleteDragonBallUser(987L);
  }

  /**
   * Test for getting all the DragonBallUsers in the repository.
   */
  @Test
  public void getAllDragonBallUsersTest() {

    DragonBallUser dbUser1 = new DragonBallUser(null, "piccolo", "piccolo@dbz.com", 20, 21, 22);
    dragonBallUserDaoJpa.createDragonBallUser(dbUser1);
    DragonBallUser dbUser2 = new DragonBallUser(null, "goten", "goten@dbz.com", 30, 31, 32);
    dragonBallUserDaoJpa.createDragonBallUser(dbUser2);
    try {
      List<DragonBallUser> usersList = dragonBallUserDaoJpa.getAllDragonBallUsers();
      assertEquals(2, usersList.size()); 
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }
}
