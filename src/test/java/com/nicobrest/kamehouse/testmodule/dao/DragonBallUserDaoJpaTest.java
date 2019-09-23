package com.nicobrest.kamehouse.testmodule.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    DragonBallUserTestUtils.removeIds();
    dragonBallUser = DragonBallUserTestUtils.getSingleTestData();
    dragonBallUsersList = DragonBallUserTestUtils.getTestDataList();

    clearTable("DRAGONBALL_USER");
  }

  /**
   * Test for creating a DragonBallUser in the repository.
   */
  @Test
  public void createDragonBallUserTest() {
    Long returnedId = dragonBallUserDaoJpa.create(dragonBallUser);

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
    dragonBallUserDaoJpa.create(dragonBallUser);
    dragonBallUser.setId(null);

    dragonBallUserDaoJpa.create(dragonBallUser);
  }

  /**
   * Test for getting a single DragonBallUser in the repository by id.
   */
  @Test
  public void getDragonBallUserTest() {
    persistEntityInRepository(dragonBallUser);

    DragonBallUser returnedUser = dragonBallUserDaoJpa.read(dragonBallUser.getId());

    assertNotNull(returnedUser);
    assertEquals(dragonBallUser, returnedUser);
  }

  /**
   * Test for getting a single DragonBallUser in the repository by username.
   */
  @Test
  public void getDragonBallUserByUsernameTest() {
    persistEntityInRepository(dragonBallUser);

    DragonBallUser returnedUser =
        dragonBallUserDaoJpa.getByUsername(dragonBallUser.getUsername());

    assertNotNull(returnedUser);
    assertEquals(dragonBallUser, returnedUser);
  }

  /**
   * Test for getting a single DragonBallUser in the repository Exception flows.
   */
  @Test
  public void getDragonBallUserNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("Entity not found in the repository.");

    dragonBallUserDaoJpa.getByUsername(DragonBallUserTestUtils.INVALID_USERNAME);
  }

  /**
   * Test for getting a single DragonBallUser in the repository by its email.
   */
  @Test
  public void getDragonBallUserByEmailTest() {
    persistEntityInRepository(dragonBallUser);

    DragonBallUser returnedUser =
        dragonBallUserDaoJpa.getByEmail(dragonBallUser.getEmail());

    assertNotNull(returnedUser);
    assertEquals(dragonBallUser, returnedUser);
  }

  /**
   * Test for getting a single DragonBallUser in the repository by its email
   * Exception flows.
   */
  @Test
  public void getDragonBallUserByEmailNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("NoResultException: Entity not found in the repository.");

    dragonBallUserDaoJpa.getByEmail(DragonBallUserTestUtils.INVALID_EMAIL);
  }

  /**
   * Test for updating an existing user in the repository.
   */
  @Test
  public void updateDragonBallUserTest() {
    persistEntityInRepository(dragonBallUser);
    dragonBallUser.setEmail("gokuUpdated@dbz.com");

    dragonBallUserDaoJpa.update(dragonBallUser);

    DragonBallUser updatedUser = dragonBallUserDaoJpa.read(dragonBallUser.getId());
    assertEquals(dragonBallUser, updatedUser);
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

    dragonBallUserDaoJpa.update(dragonBallUser);
  }

  /**
   * Test for updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateDragonBallUserServerErrorExceptionTest() {
    thrown.expect(KameHouseServerErrorException.class);
    thrown.expectMessage("PersistenceException");
    persistEntityInRepository(dragonBallUser);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 70; i++) {
      sb.append("goku");
    }
    String username = sb.toString();
    dragonBallUser.setUsername(username);
    dragonBallUser.setEmail("gokuUpdated@dbz.com");

    dragonBallUserDaoJpa.update(dragonBallUser);
  }

  /**
   * Test for deleting an existing user from the repository.
   */
  @Test
  public void deleteDragonBallUserTest() {
    persistEntityInRepository(dragonBallUser);

    DragonBallUser deletedUser = dragonBallUserDaoJpa.delete(dragonBallUser.getId());

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

    dragonBallUserDaoJpa.delete(DragonBallUserTestUtils.INVALID_ID);
  }

  /**
   * Test for getting all the DragonBallUsers in the repository.
   */
  @Test
  public void readAllTest() {
    for (DragonBallUser dragonBallUserToAdd : dragonBallUsersList) {
      persistEntityInRepository(dragonBallUserToAdd);
    }

    List<DragonBallUser> returnedList = dragonBallUserDaoJpa.readAll();

    assertEquals(dragonBallUsersList.size(), returnedList.size());
    assertEquals(dragonBallUsersList, returnedList);
  }
}
