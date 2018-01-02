package com.nicobrest.kamehouse.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.nicobrest.kamehouse.dao.DragonBallUserDaoInMemory;
import com.nicobrest.kamehouse.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.model.DragonBallUser;

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

  @Autowired
  @Qualifier("dragonBallUserDaoInMemory")
  private DragonBallUserDaoInMemory dragonBallUserDao;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

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

    DragonBallUser dragonBallUser = new DragonBallUser(0L, "vegeta", "vegeta@dbz.com", 49, 40,
        1000);

    try {
      assertEquals(3, dragonBallUserDao.getAllDragonBallUsers().size());
      dragonBallUserDao.createDragonBallUser(dragonBallUser);
      assertEquals(4, dragonBallUserDao.getAllDragonBallUsers().size());
      dragonBallUserDao
          .deleteDragonBallUser(dragonBallUserDao.getDragonBallUser("vegeta").getId());
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

    DragonBallUser dragonBallUser = new DragonBallUser(0L, "goku", "goku@dbz.com", 49, 40, 1000);

    thrown.expect(KameHouseConflictException.class);
    thrown.expectMessage("DragonBallUser with username goku already exists in the repository.");
    dragonBallUserDao.createDragonBallUser(dragonBallUser);
  }

  /**
   * Test for getting a single DragonBallUser in the repository by its id.
   */
  @Test
  public void getDragonBallUserTest() {

    try {
      DragonBallUser userByUsername = dragonBallUserDao.getDragonBallUser("goku");
      DragonBallUser user = dragonBallUserDao.getDragonBallUser(userByUsername.getId());

      assertNotNull(user);
      assertEquals(userByUsername.getId().toString(), user.getId().toString());
    } catch (KameHouseNotFoundException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for getting a single DragonBallUser in the repository by its username.
   */
  @Test
  public void getDragonBallUserByUsernameTest() {
    
    try {
      DragonBallUser user = dragonBallUserDao.getDragonBallUser("goku");
      
      assertNotNull(user);
      assertEquals("goku", user.getUsername());
    } catch (KameHouseNotFoundException e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for getting a single DragonBallUser in the repository by email.
   */
  @Test
  public void getDragonBallUserByEmailTest() {
    
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage(
        "This functionality is not implemented for the DragonBallUserInMemory repository.");
    dragonBallUserDao.getDragonBallUserByEmail("yukimura");
  }

  /**
   * Test for getting a single DragonBallUser in the repository Exception flows.
   */
  @Test
  public void getDragonBallUserByUsernameNotFoundExceptionTest() {

    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("DragonBallUser with username yukimura was not found in the repository.");
    dragonBallUserDao.getDragonBallUser("yukimura");
  }

  /**
   * Test for updating an existing user in the repository.
   */
  @Test
  public void updateDragonBallUserTest() {

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
    dragonBallUserDao.updateDragonBallUser(dragonBallUser);
  }

  /**
   * Test for deleting an existing user from the repository.
   */
  @Test
  public void deleteDragonBallUserTest() {

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
    dragonBallUserDao.deleteDragonBallUser(987L);
  }

  /**
   * Test for getting all the DragonBallUsers in the repository.
   */
  @Test
  public void getAllDragonBallUsersTest() {
    try {
      assertEquals(3, dragonBallUserDao.getAllDragonBallUsers().size()); 
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }
}
