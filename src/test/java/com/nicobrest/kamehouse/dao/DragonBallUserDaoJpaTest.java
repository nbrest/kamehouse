package com.nicobrest.kamehouse.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.nicobrest.kamehouse.dao.DragonBallUserDaoJpa;
import com.nicobrest.kamehouse.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.model.DragonBallUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 * Unit tests for the DragonBallUserDaoJpa class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class DragonBallUserDaoJpaTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DragonBallUserDaoJpaTest.class);

  @Autowired
  @Qualifier("dragonBallUserDaoJpa")
  private DragonBallUserDaoJpa dragonBallUserDaoJpa;

  @Autowired
  private EntityManagerFactory entityManagerFactory;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Clear data from the repository before each test.
   *
   * @author nbrest
   */
  @Before
  public void clearData() {

    LOGGER.info("***** Clearing database");

    EntityManager em = entityManagerFactory.createEntityManager();
    em.getTransaction().begin();
    Query query = em.createNativeQuery("DELETE FROM DRAGONBALLUSER");
    query.executeUpdate();
    em.getTransaction().commit();
    em.close();
  }

  /**
   * Test for creating a DragonBallUser in the repository.
   *
   * @author nbrest
   */
  @Test
  public void createDragonBallUserTest() {
    LOGGER.info("***** Executing createDragonBallUserTest");

    DragonBallUser dragonBallUser = new DragonBallUser(null, "vegeta", "vegeta@dbz.com", 49, 40,
        1000);

    try {
      assertEquals(0, dragonBallUserDaoJpa.getAllDragonBallUsers().size());
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
   *
   * @author nbrest
   */
  @Test
  public void createDragonBallUserConflictExceptionTest() {
    LOGGER.info("***** Executing createDragonBallUserConflictExceptionTest");

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
   *
   * @author nbrest
   */
  @Test
  public void getDragonBallUserTest() {
    LOGGER.info("***** Executing getDragonBallUserTest");

    try {
      DragonBallUser dbUser = new DragonBallUser(null, "goku", "goku@dbz.com", 20, 21, 22);
      dragonBallUserDaoJpa.createDragonBallUser(dbUser);
      DragonBallUser userByUsername = dragonBallUserDaoJpa.getDragonBallUser("goku");
      DragonBallUser user = dragonBallUserDaoJpa.getDragonBallUser(userByUsername.getId());

      LOGGER.info("user: " + user.getUsername());

      assertNotNull(user);
      assertEquals(userByUsername.getId().toString(), user.getId().toString());
    } catch (KameHouseNotFoundException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for getting a single DragonBallUser in the repository by username.
   *
   * @author nbrest
   */
  @Test
  public void getDragonBallUserByUsernameTest() {
    LOGGER.info("***** Executing getDragonBallUserByUsernameTest");

    try {
      DragonBallUser dbUser = new DragonBallUser(null, "goku", "goku@dbz.com", 20, 21, 22);
      dragonBallUserDaoJpa.createDragonBallUser(dbUser);

      DragonBallUser user = dragonBallUserDaoJpa.getDragonBallUser("goku");

      LOGGER.info("user: " + user.getUsername());

      assertNotNull(user);
      assertEquals("goku", user.getUsername());
    } catch (KameHouseNotFoundException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for getting a single DragonBallUser in the repository Exception flows.
   *
   * @author nbrest
   */
  @Test
  public void getDragonBallUserNotFoundExceptionTest() {
    LOGGER.info("***** Executing getDragonBallUserNotFoundExceptionTest");

    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("DragonBallUser with username yukimura was not found in the repository.");
    dragonBallUserDaoJpa.getDragonBallUser("yukimura");
  }

  /**
   * Test for getting a single DragonBallUser in the repository by its email.
   *
   * @author nbrest
   */
  @Test
  public void getDragonBallUserByEmailTest() {
    LOGGER.info("***** Executing getDragonBallUserByEmailTest");

    try {
      DragonBallUser dbUser = new DragonBallUser(null, "goku", "goku@dbz.com", 20, 21, 22);
      dragonBallUserDaoJpa.createDragonBallUser(dbUser);

      DragonBallUser user = dragonBallUserDaoJpa.getDragonBallUserByEmail("goku@dbz.com");

      LOGGER.info("user: " + user.getUsername());

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
   *
   * @author nbrest
   */
  @Test
  public void getDragonBallUserByEmailNotFoundExceptionTest() {
    LOGGER.info("***** Executing getDragonBallUserByEmailNotFoundExceptionTest");

    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage(
        "DragonBallUser with email yukimura@dbz.com was not found in the repository.");
    dragonBallUserDaoJpa.getDragonBallUserByEmail("yukimura@dbz.com");
  }

  /**
   * Test for updating an existing user in the repository.
   *
   * @author nbrest
   */
  @Test
  public void updateDragonBallUserTest() {
    LOGGER.info("***** Executing updateDragonBallUserTest");

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
   *
   * @author nbrest
   */
  @Test
  public void updateDragonBallUserNotFoundExceptionTest() {
    LOGGER.info("***** Executing updateDragonBallUserNotFoundExceptionTest");

    DragonBallUser dragonBallUser = new DragonBallUser(0L, "yukimura", "yukimura@pot.com", 10, 10,
        10);
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("DragonBallUser with id 0 was not found in the repository.");
    dragonBallUserDaoJpa.updateDragonBallUser(dragonBallUser);
  }

  /**
   * Test for updating an existing user in the repository Exception flows.
   *
   * @author nbrest
   */
  @Test
  public void updateDragonBallUserServerErrorExceptionTest() {
    LOGGER.info("***** Executing updateDragonBallUserServerErrorExceptionTest");

    thrown.expect(KameHouseServerErrorException.class);
    thrown.expectMessage("PersistenceException in updateDragonBallUser");

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
   *
   * @author nbrest
   */
  @Test
  public void deleteDragonBallUserTest() {
    LOGGER.info("***** Executing deleteDragonBallUserTest");

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
   *
   * @author nbrest
   */
  @Test
  public void deleteDragonBallUserNotFoundExceptionTest() {
    LOGGER.info("***** Executing deleteDragonBallUserNotFoundExceptionTest");

    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("DragonBallUser with id " + 987L + " was not found in the repository.");
    dragonBallUserDaoJpa.deleteDragonBallUser(987L);
  }

  /**
   * Test for getting all the DragonBallUsers in the repository.
   *
   * @author nbrest
   */
  @Test
  public void getAllDragonBallUsersTest() {
    LOGGER.info("***** Executing getAllDragonBallUsersTest");

    DragonBallUser dbUser1 = new DragonBallUser(null, "piccolo", "piccolo@dbz.com", 20, 21, 22);
    dragonBallUserDaoJpa.createDragonBallUser(dbUser1);
    DragonBallUser dbUser2 = new DragonBallUser(null, "goten", "goten@dbz.com", 30, 31, 32);
    dragonBallUserDaoJpa.createDragonBallUser(dbUser2);

    List<DragonBallUser> usersList = dragonBallUserDaoJpa.getAllDragonBallUsers();

    assertEquals(2, usersList.size());
  }
}
