package ar.com.nicobrest.mobileinspections.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ar.com.nicobrest.mobileinspections.exception.DragonBallUserAlreadyExistsException;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserNotFoundException;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;

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

/**
 * Unit tests for the DragonBallUserInMemoryDao class.
 * 
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:testContextDao.xml" })
public class DragonBallUserDaoInMemoryTest {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(DragonBallUserDaoInMemoryTest.class);

  @Autowired
  @Qualifier("dragonBallUserDaoInMemory")
  private DragonBallUserDaoInMemory dragonBallUserDaoInMemory;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Test for the autowired beans.
   * 
   * @author nbrest
   */
  @Test
  public void autoWiredBeansTest() {
    LOGGER
        .info("****************** Executing autoWiredBeansTest ******************");

    DragonBallUser gohan = dragonBallUserDaoInMemory.getGohanDragonBallUser();
    DragonBallUser goten = dragonBallUserDaoInMemory.getGotenDragonBallUser();

    LOGGER.info("gohan: " + gohan.getUsername());
    LOGGER.info("goten: " + goten.getUsername());

    assertNotNull(gohan);
    assertEquals("gohanTestBean", gohan.getUsername());
    assertNotNull(goten);
    assertEquals("gotenTestBean", goten.getUsername());
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

    DragonBallUser dragonBallUser = new DragonBallUser(0L, "vegeta",
        "vegeta@dbz.com", 49, 40, 1000);

    try {
      assertEquals(3, dragonBallUserDaoInMemory.getAllDragonBallUsers().size());
      dragonBallUserDaoInMemory.createDragonBallUser(dragonBallUser);
      assertEquals(4, dragonBallUserDaoInMemory.getAllDragonBallUsers().size());
      dragonBallUserDaoInMemory.deleteDragonBallUser(dragonBallUserDaoInMemory
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

    DragonBallUser dragonBallUser = new DragonBallUser(0L, "goku",
        "goku@dbz.com", 49, 40, 1000);

    thrown.expect(DragonBallUserAlreadyExistsException.class);
    thrown
        .expectMessage("DragonBallUser with username goku already exists in the repository.");
    dragonBallUserDaoInMemory.createDragonBallUser(dragonBallUser);
  }

  /**
   * Test for getting a single DragonBallUser in the repository.
   * 
   * @author nbrest
   */
  @Test
  public void getDragonBallUserTest() {
    LOGGER
        .info("****************** Executing getDragonBallUserTest ******************");

    try {
      DragonBallUser user = dragonBallUserDaoInMemory.getDragonBallUser("goku");

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
   * @throws DragonBallUserNotFoundException
   *           User defined exception
   */
  @Test
  public void getDragonBallUserDragonBallUserNotFoundExceptionTest()
      throws DragonBallUserNotFoundException {
    LOGGER
        .info("****************** Executing "
            + "getDragonBallUserDragonBallUserNotFoundExceptionTest ******************");

    thrown.expect(DragonBallUserNotFoundException.class);
    thrown
        .expectMessage("DragonBallUser with username yukimura was not found in the repository.");
    dragonBallUserDaoInMemory.getDragonBallUser("yukimura");
  }

  /**
   * Test for updating an existing user in the repository.
   * 
   * @author nbrest
   */
  @Test
  public void updateDragonBallUserTest() {
    LOGGER
        .info("****************** Executing updateDragonBallUserTest ******************");

    DragonBallUser modifiedUser = new DragonBallUser(0L, "goku",
        "gokuUpdated@dbz.com", 51, 52, 53);
    try {
      DragonBallUser originalUser = dragonBallUserDaoInMemory
          .getDragonBallUser("goku");
      assertEquals("goku", originalUser.getUsername());

      dragonBallUserDaoInMemory.updateDragonBallUser(modifiedUser);
      DragonBallUser updatedUser = dragonBallUserDaoInMemory
          .getDragonBallUser("goku");

      assertEquals("1", updatedUser.getId().toString());
      assertEquals("goku", updatedUser.getUsername());
      assertEquals("gokuUpdated@dbz.com", updatedUser.getEmail());
      assertEquals(51, updatedUser.getAge());
      assertEquals(52, updatedUser.getPowerLevel());
      assertEquals(53, updatedUser.getStamina());

      dragonBallUserDaoInMemory.updateDragonBallUser(originalUser);
    } catch (DragonBallUserNotFoundException e) {
      e.printStackTrace();
      fail("Caught DragonBallUserNotFoundException.");
    }
  }

  /**
   * Test for updating an existing user in the repository Exception flows.
   * 
   * @author nbrest
   * @throws DragonBallUserNotFoundException
   *           User defined exception
   */
  @Test
  public void updateDragonBallUserDragonBallUserNotFoundExceptionTest()
      throws DragonBallUserNotFoundException {
    LOGGER
        .info("****************** Executing "
            + "updateDragonBallUserDragonBallUserNotFoundExceptionTest ****************");

    DragonBallUser dragonBallUser = new DragonBallUser(0L, "yukimura",
        "yukimura@pot.com", 10, 10, 10);
    thrown.expect(DragonBallUserNotFoundException.class);
    thrown
        .expectMessage("DragonBallUser with username yukimura was not found in the repository.");
    dragonBallUserDaoInMemory.updateDragonBallUser(dragonBallUser);
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
      DragonBallUser userToDelete = new DragonBallUser(0L, "piccolo",
          "piccolo@dbz.com", 20, 21, 22);
      dragonBallUserDaoInMemory.createDragonBallUser(userToDelete);
      assertEquals(4, dragonBallUserDaoInMemory.getAllDragonBallUsers().size());
      DragonBallUser deletedUser = dragonBallUserDaoInMemory
          .deleteDragonBallUser(dragonBallUserDaoInMemory.getDragonBallUser("piccolo").getId());
      assertEquals(3, dragonBallUserDaoInMemory.getAllDragonBallUsers().size());
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
    dragonBallUserDaoInMemory.deleteDragonBallUser(987L);
  }

  /**
   * Test for getting all the DragonBallUsers in the repository.
   * 
   * @author nbrest
   */
  @Test
  public void getAllDragonBallUsersTest() {
    LOGGER
        .info("****************** Executing getAllDragonBallUsersTest ******************");

    List<DragonBallUser> usersList = dragonBallUserDaoInMemory
        .getAllDragonBallUsers();

    LOGGER.info("dragonBallUsers.get(0): " + usersList.get(0).getUsername());
    LOGGER.info("dragonBallUsers.get(1): " + usersList.get(1).getUsername());
    LOGGER.info("dragonBallUsers.get(2): " + usersList.get(2).getUsername());

    assertEquals(3, dragonBallUserDaoInMemory.getAllDragonBallUsers().size());

    DragonBallUser expectedStoredDbUser = new DragonBallUser();
    expectedStoredDbUser.setAge(19);
    expectedStoredDbUser.setEmail("goten@dbz.com");
    expectedStoredDbUser.setId(new Long(3));
    expectedStoredDbUser.setPowerLevel(10);
    expectedStoredDbUser.setStamina(1000);
    expectedStoredDbUser.setUsername("goten");

    assertTrue(usersList.contains(expectedStoredDbUser));
  }
}
