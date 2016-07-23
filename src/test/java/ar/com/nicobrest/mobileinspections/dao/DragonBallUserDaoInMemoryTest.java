package ar.com.nicobrest.mobileinspections.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import ar.com.nicobrest.mobileinspections.exception.MobileInspectionsBadRequestException;
import ar.com.nicobrest.mobileinspections.exception.MobileInspectionsConflictException;
import ar.com.nicobrest.mobileinspections.exception.MobileInspectionsNotFoundException;
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
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class DragonBallUserDaoInMemoryTest {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(DragonBallUserDaoInMemoryTest.class);

  @Autowired
  @Qualifier("dragonBallUserDaoInMemory")
  private DragonBallUserDaoInMemory dragonBallUserDao;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Test for the autowired beans.
   * 
   * @author nbrest
   */
  @Test
  public void autoWiredBeansTest() {
    LOGGER.info("***** Executing autoWiredBeansTest");

    DragonBallUser gohan = dragonBallUserDao.getGohanDragonBallUser();
    DragonBallUser goten = dragonBallUserDao.getGotenDragonBallUser();

    LOGGER.info("gohan: " + gohan.getUsername());
    LOGGER.info("goten: " + goten.getUsername());

    assertNotNull(gohan);
    assertEquals("gohanBean", gohan.getUsername());
    assertNotNull(goten);
    assertEquals("gotenBean", goten.getUsername());
  }

  /**
   * Test for creating a DragonBallUser in the repository.
   * 
   * @author nbrest
   */
  @Test
  public void createDragonBallUserTest() {
    LOGGER.info("***** Executing createDragonBallUserTest");

    DragonBallUser dragonBallUser = new DragonBallUser(0L, "vegeta", "vegeta@dbz.com", 49, 40,
        1000);

    try {
      assertEquals(3, dragonBallUserDao.getAllDragonBallUsers().size());
      dragonBallUserDao.createDragonBallUser(dragonBallUser);
      assertEquals(4, dragonBallUserDao.getAllDragonBallUsers().size());
      dragonBallUserDao
          .deleteDragonBallUser(dragonBallUserDao.getDragonBallUser("vegeta").getId());
    } catch (MobileInspectionsBadRequestException | MobileInspectionsNotFoundException e) {
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

    DragonBallUser dragonBallUser = new DragonBallUser(0L, "goku", "goku@dbz.com", 49, 40, 1000);

    thrown.expect(MobileInspectionsConflictException.class);
    thrown.expectMessage("DragonBallUser with username goku already exists in the repository.");
    dragonBallUserDao.createDragonBallUser(dragonBallUser);
  }

  /**
   * Test for getting a single DragonBallUser in the repository by its username.
   * 
   * @author nbrest
   */
  @Test
  public void getDragonBallUserTest() {
    LOGGER.info("***** Executing getDragonBallUserTest");

    try {
      DragonBallUser user = dragonBallUserDao.getDragonBallUser("goku");

      LOGGER.info("user: " + user.getUsername());

      assertNotNull(user);
      assertEquals("goku", user.getUsername());
    } catch (MobileInspectionsNotFoundException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for getting a single DragonBallUser in the repository by email.
   * 
   * @author nbrest
   */
  @Test
  public void getDragonBallUserByEmailTest() {
    LOGGER
        .info("***** Executing getDragonBallUserByEmailTest");

    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage(
        "This functionality is not implemented for the DragonBallUserInMemory repository.");
    dragonBallUserDao.getDragonBallUserByEmail("yukimura");
  }

  /**
   * Test for getting a single DragonBallUser in the repository Exception flows.
   * 
   * @author nbrest
   */
  @Test
  public void getDragonBallUserNotFoundExceptionTest() {
    LOGGER.info("***** Executing getDragonBallUserNotFoundExceptionTest");

    thrown.expect(MobileInspectionsNotFoundException.class);
    thrown.expectMessage("DragonBallUser with username yukimura was not found in the repository.");
    dragonBallUserDao.getDragonBallUser("yukimura");
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
      DragonBallUser originalUser = dragonBallUserDao.getDragonBallUser("goku");
      assertEquals("goku", originalUser.getUsername());

      DragonBallUser modifiedUser = new DragonBallUser(originalUser.getId(), "goku",
          "gokuUpdated@dbz.com", 51, 52, 53);

      dragonBallUserDao.updateDragonBallUser(modifiedUser);
      DragonBallUser updatedUser = dragonBallUserDao.getDragonBallUser("goku");

      assertEquals(originalUser.getId().toString(), updatedUser.getId().toString());
      assertEquals("goku", updatedUser.getUsername());
      assertEquals("gokuUpdated@dbz.com", updatedUser.getEmail());
      assertEquals(51, updatedUser.getAge());
      assertEquals(52, updatedUser.getPowerLevel());
      assertEquals(53, updatedUser.getStamina());

      dragonBallUserDao.updateDragonBallUser(originalUser);
    } catch (MobileInspectionsNotFoundException e) {
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
    thrown.expect(MobileInspectionsNotFoundException.class);
    thrown.expectMessage("DragonBallUser with id 0 was not found in the repository.");
    dragonBallUserDao.updateDragonBallUser(dragonBallUser);
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
      DragonBallUser userToDelete = new DragonBallUser(0L, "piccolo", "piccolo@dbz.com", 20, 21,
          22);
      dragonBallUserDao.createDragonBallUser(userToDelete);
      assertEquals(4, dragonBallUserDao.getAllDragonBallUsers().size());
      DragonBallUser deletedUser = dragonBallUserDao
          .deleteDragonBallUser(dragonBallUserDao.getDragonBallUser("piccolo").getId());
      assertEquals(3, dragonBallUserDao.getAllDragonBallUsers().size());
      assertEquals("piccolo", deletedUser.getUsername());
      assertEquals("piccolo@dbz.com", deletedUser.getEmail());
      assertEquals(20, deletedUser.getAge());
      assertEquals(21, deletedUser.getPowerLevel());
      assertEquals(22, deletedUser.getStamina());
    } catch (MobileInspectionsNotFoundException | MobileInspectionsBadRequestException e) {
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

    thrown.expect(MobileInspectionsNotFoundException.class);
    thrown.expectMessage("DragonBallUser with id " + 987L + " was not found in the repository.");
    dragonBallUserDao.deleteDragonBallUser(987L);
  }

  /**
   * Test for getting all the DragonBallUsers in the repository.
   * 
   * @author nbrest
   */
  @Test
  public void getAllDragonBallUsersTest() {
    LOGGER.info("***** Executing getAllDragonBallUsersTest");

    List<DragonBallUser> usersList = dragonBallUserDao.getAllDragonBallUsers();

    LOGGER.info("dragonBallUsers.get(0): " + usersList.get(0).getUsername());
    LOGGER.info("dragonBallUsers.get(1): " + usersList.get(1).getUsername());
    LOGGER.info("dragonBallUsers.get(2): " + usersList.get(2).getUsername());

    assertEquals(3, dragonBallUserDao.getAllDragonBallUsers().size());
  }
}
