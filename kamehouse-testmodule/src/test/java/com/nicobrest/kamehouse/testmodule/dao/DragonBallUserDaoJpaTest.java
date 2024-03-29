package com.nicobrest.kamehouse.testmodule.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.testutils.DragonBallUserTestUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Unit tests for the DragonBallUserDaoJpa class.
 *
 * @author nbrest
 */
class DragonBallUserDaoJpaTest
    extends AbstractCrudDaoJpaTest<DragonBallUser, DragonBallUserDto> {

  private DragonBallUserDao dragonBallUserDaoJpa;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public DragonBallUserDaoJpaTest(DragonBallUserDao dragonBallUserDaoJpa,
      EntityManagerFactory entityManagerFactory) {
    super(entityManagerFactory);
    this.dragonBallUserDaoJpa = dragonBallUserDaoJpa;
  }

  @Override
  public Class<DragonBallUser> getEntityClass() {
    return DragonBallUser.class;
  }

  @Override
  public CrudDao<DragonBallUser> getCrudDao() {
    return dragonBallUserDaoJpa;
  }

  @Override
  public TestUtils<DragonBallUser, DragonBallUserDto> getTestUtils() {
    return new DragonBallUserTestUtils();
  }

  @Override
  public String[] getTablesToClear() {
    return new String[]{"DRAGONBALL_USER"};
  }

  @Override
  public void updateEntity(DragonBallUser entity) {
    entity.setEmail("gokuUpdated@dbz.com");
  }

  @Override
  public void updateEntityServerError(DragonBallUser entity) {
    entity.setUsername(getInvalidString());
    entity.setEmail(getInvalidString());
  }

  /**
   * Tests getting a single DragonBallUser in the repository by username.
   */
  @Test
  void getByUsernameTest() {
    DragonBallUser dragonBallUser = testUtils.getSingleTestData();
    persistEntityInRepository(dragonBallUser);

    DragonBallUser returnedUser = dragonBallUserDaoJpa.getByUsername(dragonBallUser.getUsername());

    testUtils.assertEqualsAllAttributes(dragonBallUser, returnedUser);
  }

  /**
   * Tests getting a single DragonBallUser in the repository Exception flows.
   */
  @Test
  void getByUsernameNotFoundExceptionTest() {
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          dragonBallUserDaoJpa.getByUsername(DragonBallUserTestUtils.INVALID_USERNAME);
        });
  }

  /**
   * Tests getting a single DragonBallUser in the repository by its email.
   */
  @Test
  void getByEmailTest() {
    DragonBallUser dragonBallUser = testUtils.getSingleTestData();
    persistEntityInRepository(dragonBallUser);

    DragonBallUser returnedUser = dragonBallUserDaoJpa.getByEmail(dragonBallUser.getEmail());

    testUtils.assertEqualsAllAttributes(dragonBallUser, returnedUser);
  }

  /**
   * Tests getting a single DragonBallUser in the repository by its email Exception flows.
   */
  @Test
  void getByEmailNotFoundExceptionTest() {
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          dragonBallUserDaoJpa.getByEmail(DragonBallUserTestUtils.INVALID_EMAIL);
        });
  }
}
