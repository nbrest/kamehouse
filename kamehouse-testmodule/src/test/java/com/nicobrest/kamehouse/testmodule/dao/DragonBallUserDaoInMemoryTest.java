package com.nicobrest.kamehouse.testmodule.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.testutils.DragonBallUserTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for the DragonBallUserInMemoryDao class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
class DragonBallUserDaoInMemoryTest {

  private TestUtils<DragonBallUser, DragonBallUserDto> testUtils;
  private DragonBallUser dragonBallUser;

  @Autowired
  @Qualifier("dragonBallUserDaoInMemory")
  private DragonBallUserDaoInMemory dragonBallUserDao;

  /** Clears data from the repository before each test. */
  @BeforeEach
  void setUp() {
    testUtils = new DragonBallUserTestUtils();
    testUtils.initTestData();
    dragonBallUser = testUtils.getSingleTestData();
    DragonBallUserDaoInMemory.initRepository();
  }

  /** Tests the autowired beans. */
  @Test
  void autoWiredBeansTest() {
    DragonBallUser gohan = dragonBallUserDao.getGohanDragonBallUser();
    DragonBallUser goten = dragonBallUserDao.getGotenDragonBallUser();

    assertNotNull(gohan);
    assertEquals("gohanBean", gohan.getUsername());
    assertNotNull(goten);
    assertEquals("gotenBean", goten.getUsername());
  }

  /** Tests creating a DragonBallUser in the repository. */
  @Test
  void createTest() {
    Long createdId = dragonBallUserDao.create(dragonBallUser);

    DragonBallUser createdUser = dragonBallUserDao.read(createdId);

    testUtils.assertEqualsAllAttributes(dragonBallUser, createdUser);
  }

  /** Tests creating a DragonBallUser in the repository Exception flows. */
  @Test
  void createConflictExceptionTest() {
    dragonBallUserDao.create(dragonBallUser);
    assertThrows(
        KameHouseConflictException.class,
        () -> {
          dragonBallUserDao.create(dragonBallUser);
        });
  }

  /** Tests getting a single DragonBallUser in the repository by its id. */
  @Test
  void readTest() {
    DragonBallUser userByUsername = dragonBallUserDao.getByUsername("goku");

    DragonBallUser userById = dragonBallUserDao.read(userByUsername.getId());

    assertNotNull(userById);
    testUtils.assertEqualsAllAttributes(userByUsername, userById);
  }

  /** Tests getting all the DragonBallUsers in the repository. */
  @Test
  void readAllTest() {
    assertEquals(3, dragonBallUserDao.readAll().size());
  }

  /** Tests updating an existing user in the repository. */
  @Test
  void updateTest() {
    DragonBallUser originalUser = dragonBallUserDao.getByUsername("goku");
    originalUser.setEmail("gokuUpdated@dbz.com");

    dragonBallUserDao.update(originalUser);

    DragonBallUser updatedUser = dragonBallUserDao.getByUsername("goku");
    testUtils.assertEqualsAllAttributes(originalUser, updatedUser);
  }

  /** Tests updating an existing user in the repository Exception flows. */
  @Test
  void updateNotFoundExceptionTest() {
    dragonBallUser.setId(DragonBallUserTestUtils.INVALID_ID);
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          dragonBallUserDao.update(dragonBallUser);
        });
  }

  /** Tests deleting an existing user from the repository. */
  @Test
  void deleteTest() {
    dragonBallUserDao.create(dragonBallUser);

    DragonBallUser deletedUser = dragonBallUserDao.delete(dragonBallUser.getId());

    testUtils.assertEqualsAllAttributes(dragonBallUser, deletedUser);
  }

  /** Tests deleting an existing user from the repository Exception flows. */
  @Test
  void deleteNotFoundExceptionTest() {
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          dragonBallUserDao.delete(DragonBallUserTestUtils.INVALID_ID);
        });
  }

  /** Tests getting a single DragonBallUser in the repository by its username. */
  @Test
  void getByUsernameTest() {
    DragonBallUser userByUsername = dragonBallUserDao.getByUsername("goku");

    assertNotNull(userByUsername);
    assertEquals("goku", userByUsername.getUsername());
  }

  /** Tests getting a single DragonBallUser in the repository Exception flows. */
  @Test
  void getByUsernameNotFoundExceptionTest() {
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          dragonBallUserDao.getByUsername(DragonBallUserTestUtils.INVALID_EMAIL);
        });
  }

  /** Tests getting a single DragonBallUser in the repository by email. */
  @Test
  void getByEmailTest() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> {
          dragonBallUserDao.getByEmail(DragonBallUserTestUtils.INVALID_EMAIL);
        });
  }
}
