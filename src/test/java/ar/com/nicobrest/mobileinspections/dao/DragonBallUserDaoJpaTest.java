package ar.com.nicobrest.mobileinspections.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import ar.com.nicobrest.mobileinspections.exception.DragonBallUserAlreadyExistsException;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserNotFoundException;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;

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
@ContextConfiguration(locations = { "classpath:testContextDao.xml" })
public class DragonBallUserDaoJpaTest {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(DragonBallUserDaoJpaTest.class);

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
    LOGGER
        .info("****************** Executing createDragonBallUserTest ******************");

    DragonBallUser dragonBallUser = new DragonBallUser(null, "vegeta",
        "vegeta@dbz.com", 49, 40, 1000);

    try {
      assertEquals(0, dragonBallUserDaoJpa.getAllDragonBallUsers().size());
      dragonBallUserDaoJpa.createDragonBallUser(dragonBallUser);
      assertEquals(1, dragonBallUserDaoJpa.getAllDragonBallUsers().size());
      dragonBallUserDaoJpa.deleteDragonBallUser(dragonBallUserDaoJpa
          .getDragonBallUser("vegeta").getId());
    } catch (DragonBallUserAlreadyExistsException
        | DragonBallUserNotFoundException e) {
      e.printStackTrace();
      fail("Caught DragonBallUserAlreadyExistsException or DragonBallUserNotFoundException.");
    }
  }

  /**
   * Test for creating a DragonBallUser in the repository Exception flows.
   * 
   * @author nbrest
   * @throws DragonBallUserAlreadyExistsException
   *           User defined exception
   */
  @Test
  public void createDragonBallUserDragonBallUserAlreadyExistsExceptionTest()
      throws DragonBallUserAlreadyExistsException {
    LOGGER
        .info("****************** Executing "
            + "createDragonBallUserDragonBallUserAlreadyExistsExceptionTest ***************");

    thrown.expect(javax.persistence.RollbackException.class);
    thrown.expectMessage("Error while committing the transaction");
    
    DragonBallUser dragonBallUser = new DragonBallUser(null, "goku",
        "goku@dbz.com", 49, 40, 1000);
    DragonBallUser dragonBallUser2 = new DragonBallUser(null, "goku",
        "goku@dbz.com", 49, 40, 1000);

    dragonBallUserDaoJpa.createDragonBallUser(dragonBallUser);
    dragonBallUserDaoJpa.createDragonBallUser(dragonBallUser2);
  }

  /**
   * Test for getting a single DragonBallUser in the repository.
   * 
   * @author nbrest
   */
  @Test
  public void getDragonBallUserTest() throws DragonBallUserAlreadyExistsException {
    LOGGER
        .info("****************** Executing getDragonBallUserTest ******************");

    try {
      DragonBallUser dbUser = new DragonBallUser(null, "goku",
          "goku@dbz.com", 20, 21, 22);
      dragonBallUserDaoJpa.createDragonBallUser(dbUser);
      
      DragonBallUser user = dragonBallUserDaoJpa.getDragonBallUser("goku");

      LOGGER.info("user: " + user.getUsername());

      assertNotNull(user);
      assertEquals("goku", user.getUsername());
    } catch (DragonBallUserNotFoundException e) {
      e.printStackTrace();
      fail("Caught DragonBallUserNotFoundException.");
    }
  }

  /**
   * Test for getting a single DragonBallUser in the repository Exception flows.
   * 
   * @author nbrest
   */
  @Test
  public void getDragonBallUserExceptionTest()
      throws DragonBallUserNotFoundException {
    LOGGER
        .info("****************** Executing "
            + "getDragonBallUserExceptionTest ******************");

    thrown.expect(javax.persistence.NoResultException.class);
    thrown.expectMessage("No entity found for query");
    dragonBallUserDaoJpa.getDragonBallUser("yukimura");
  }

  /**
   * Test for updating an existing user in the repository.
   * 
   * @author nbrest
   */
  @Test
  public void updateDragonBallUserTest() throws DragonBallUserAlreadyExistsException {
    LOGGER
        .info("****************** Executing updateDragonBallUserTest ******************");

    try {
      DragonBallUser userToInsert = new DragonBallUser(null, "goku",
          "goku@dbz.com", 20, 21, 22);
      dragonBallUserDaoJpa.createDragonBallUser(userToInsert);
      
      DragonBallUser originalUser = dragonBallUserDaoJpa
          .getDragonBallUser("goku");
      assertEquals("goku", originalUser.getUsername());

      DragonBallUser modifiedUser = new DragonBallUser(originalUser.getId(), "goku",
          "gokuUpdated@dbz.com", 51, 52, 53);
      
      dragonBallUserDaoJpa.updateDragonBallUser(modifiedUser);
      DragonBallUser updatedUser = dragonBallUserDaoJpa
          .getDragonBallUser("goku");

      assertEquals(originalUser.getId().toString(), updatedUser.getId().toString());
      assertEquals("goku", updatedUser.getUsername());
      assertEquals("gokuUpdated@dbz.com", updatedUser.getEmail());
      assertEquals(51, updatedUser.getAge());
      assertEquals(52, updatedUser.getPowerLevel());
      assertEquals(53, updatedUser.getStamina());

      dragonBallUserDaoJpa.updateDragonBallUser(originalUser);
    } catch (DragonBallUserNotFoundException e) {
      e.printStackTrace();
      fail("Caught DragonBallUserNotFoundException.");
    }
  }

  /**
   * Test for updating an existing user in the repository Exception flows.
   * 
   * @author nbrest
   */
  @Test
  public void updateDragonBallUserDragonBallUserNotFoundExceptionTest()
      throws DragonBallUserNotFoundException, DragonBallUserAlreadyExistsException {
    LOGGER
        .info("****************** Executing "
            + "updateDragonBallUserDragonBallUserNotFoundExceptionTest ****************");

    DragonBallUser dragonBallUser = new DragonBallUser(0L, "yukimura",
        "yukimura@pot.com", 10, 10, 10);
    thrown.expect(DragonBallUserNotFoundException.class);
    thrown
        .expectMessage("DragonBallUser with id 0 was not found in the repository.");
    dragonBallUserDaoJpa.updateDragonBallUser(dragonBallUser);
  }

  /**
   * Test for deleting an existing user from the repository.
   * 
   * @author nbrest
   */
  @Test
  public void deleteDragonBallUserTest() {
    LOGGER
        .info("****************** Executing deleteDragonBallUserTest ******************");

    try {
      DragonBallUser userToDelete = new DragonBallUser(null, "piccolo",
          "piccolo@dbz.com", 20, 21, 22);
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
    } catch (DragonBallUserNotFoundException
        | DragonBallUserAlreadyExistsException e) {
      e.printStackTrace();
      fail("Caught DragonBallUserNotFoundException or DragonBallUserAlreadyExistsException.");
    }
  }

  /**
   * Test for deleting an existing user from the repository Exception flows.
   * 
   * @author nbrest
   * @throws DragonBallUserNotFoundException
   *           User defined exception
   */
  @Test
  public void deleteDragonBallUserDragonBallUserNotFoundExceptionTest()
      throws DragonBallUserNotFoundException {
    LOGGER
        .info("****************** Executing "
            + "deleteDragonBallUserDragonBallUserNotFoundExceptionTest ****************");

    thrown.expect(DragonBallUserNotFoundException.class);
    thrown
        .expectMessage("DragonBallUser with id " + 987L + " was not found in the repository.");
    dragonBallUserDaoJpa.deleteDragonBallUser(987L);
  }

  /**
   * Test for getting all the DragonBallUsers in the repository.
   * 
   * @author nbrest
   */
  @Test
  public void getAllDragonBallUsersTest() throws DragonBallUserAlreadyExistsException {
    LOGGER
        .info("****************** Executing getAllDragonBallUsersTest ******************");

    DragonBallUser dbUser1 = new DragonBallUser(null, "piccolo",
        "piccolo@dbz.com", 20, 21, 22);
    dragonBallUserDaoJpa.createDragonBallUser(dbUser1);
    DragonBallUser dbUser2 = new DragonBallUser(null, "goten",
        "goten@dbz.com", 30, 31, 32);
    dragonBallUserDaoJpa.createDragonBallUser(dbUser2);
    
    List<DragonBallUser> usersList = dragonBallUserDaoJpa.getAllDragonBallUsers();

    assertEquals(2, usersList.size());
  }
}
