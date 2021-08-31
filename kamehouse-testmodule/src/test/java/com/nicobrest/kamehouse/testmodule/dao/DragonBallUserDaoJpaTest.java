package com.nicobrest.kamehouse.testmodule.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.testutils.DragonBallUserTestUtils;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for the DragonBallUserDaoJpa class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class DragonBallUserDaoJpaTest
    extends AbstractCrudDaoJpaTest<DragonBallUser, DragonBallUserDto> {

  private DragonBallUser dragonBallUser;

  @Autowired private DragonBallUserDao dragonBallUserDaoJpa;

  /** Clears data from the repository before each test. */
  @BeforeEach
  public void setUp() {
    testUtils = new DragonBallUserTestUtils();
    testUtils.initTestData();
    testUtils.removeIds();
    dragonBallUser = testUtils.getSingleTestData();

    clearTable("DRAGONBALL_USER");
  }

  /** Tests creating a DragonBallUser in the repository. */
  @Test
  public void createTest() {
    createTest(dragonBallUserDaoJpa, DragonBallUser.class);
  }

  /** Tests creating a DragonBallUser in the repository Exception flows. */
  @Test
  public void createConflictExceptionTest() {
    createConflictExceptionTest(dragonBallUserDaoJpa);
  }

  /** Tests getting a single DragonBallUser from the repository by id. */
  @Test
  public void readTest() {
    readTest(dragonBallUserDaoJpa);
  }

  /** Tests getting all the DragonBallUsers in the repository. */
  @Test
  public void readAllTest() {
    readAllTest(dragonBallUserDaoJpa);
  }

  /** Tests updating an existing user in the repository. */
  @Test
  public void updateTest()
      throws IllegalAccessException, InstantiationException, InvocationTargetException,
          NoSuchMethodException {
    DragonBallUser updatedEntity = dragonBallUser;
    updatedEntity.setEmail("gokuUpdated@dbz.com");

    updateTest(dragonBallUserDaoJpa, DragonBallUser.class, updatedEntity);
  }

  /** Tests updating an existing user in the repository Exception flows. */
  @Test
  public void updateNotFoundExceptionTest() {
    updateNotFoundExceptionTest(dragonBallUserDaoJpa, DragonBallUser.class);
  }

  /** Tests updating an existing user in the repository Exception flows. */
  @Test
  public void updateServerErrorExceptionTest() {
    assertThrows(
        KameHouseServerErrorException.class,
        () -> {
          persistEntityInRepository(dragonBallUser);
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < 70; i++) {
            sb.append("goku");
          }
          String username = sb.toString();
          dragonBallUser.setUsername(username);
          dragonBallUser.setEmail("gokuUpdated@dbz.com");

          dragonBallUserDaoJpa.update(dragonBallUser);
        });
  }

  /** Tests deleting an existing user from the repository. */
  @Test
  public void deleteTest() {
    deleteTest(dragonBallUserDaoJpa);
  }

  /** Tests deleting an existing user from the repository Exception flows. */
  @Test
  public void deleteNotFoundExceptionTest() {
    deleteNotFoundExceptionTest(dragonBallUserDaoJpa, DragonBallUser.class);
  }

  /** Tests getting a single DragonBallUser in the repository by username. */
  @Test
  public void getByUsernameTest() {
    persistEntityInRepository(dragonBallUser);

    DragonBallUser returnedUser = dragonBallUserDaoJpa.getByUsername(dragonBallUser.getUsername());

    testUtils.assertEqualsAllAttributes(dragonBallUser, returnedUser);
  }

  /** Tests getting a single DragonBallUser in the repository Exception flows. */
  @Test
  public void getByUsernameNotFoundExceptionTest() {
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          dragonBallUserDaoJpa.getByUsername(DragonBallUserTestUtils.INVALID_USERNAME);
        });
  }

  /** Tests getting a single DragonBallUser in the repository by its email. */
  @Test
  public void getByEmailTest() {
    persistEntityInRepository(dragonBallUser);

    DragonBallUser returnedUser = dragonBallUserDaoJpa.getByEmail(dragonBallUser.getEmail());

    testUtils.assertEqualsAllAttributes(dragonBallUser, returnedUser);
  }

  /** Tests getting a single DragonBallUser in the repository by its email Exception flows. */
  @Test
  public void getByEmailNotFoundExceptionTest() {
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          dragonBallUserDaoJpa.getByEmail(DragonBallUserTestUtils.INVALID_EMAIL);
        });
  }
}
