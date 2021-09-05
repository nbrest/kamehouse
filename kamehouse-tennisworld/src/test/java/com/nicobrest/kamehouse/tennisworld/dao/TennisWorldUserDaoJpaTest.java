package com.nicobrest.kamehouse.tennisworld.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.TennisWorldUserDto;
import com.nicobrest.kamehouse.tennisworld.testutils.TennisWorldUserTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Unit tests for the TennisWorldUserDaoJpa class.
 *
 * @author nbrest
 */
public class TennisWorldUserDaoJpaTest
    extends AbstractCrudDaoJpaTest<TennisWorldUser, TennisWorldUserDto> {

  @Autowired
  private TennisWorldUserDao tennisWorldUserDaoJpa;

  @Override
  public Class<TennisWorldUser> getEntityClass() {
    return TennisWorldUser.class;
  }

  @Override
  public CrudDao<TennisWorldUser> getCrudDao() {
    return tennisWorldUserDaoJpa;
  }

  @Override
  public TestUtils<TennisWorldUser, TennisWorldUserDto> getTestUtils() {
    return new TennisWorldUserTestUtils();
  }

  @Override
  public String[] getTablesToClear() {
    return new String[]{"TENNISWORLD_USER"};
  }

  @Override
  public void updateEntity(TennisWorldUser entity) {
    entity.setEmail("gokuUpdated@dbz.com");
  }

  @Override
  public void updateEntityServerError(TennisWorldUser entity) {
    entity.setEmail(getInvalidString());
  }

  /**
   * Tests getting a single TennisWorldUser in the repository by its email.
   */
  @Test
  public void getByEmailTest() {
    TennisWorldUser tennisWorldUser = testUtils.getSingleTestData();
    persistEntityInRepository(tennisWorldUser);

    TennisWorldUser returnedUser = tennisWorldUserDaoJpa.getByEmail(tennisWorldUser.getEmail());

    testUtils.assertEqualsAllAttributes(tennisWorldUser, returnedUser);
  }

  /**
   * Tests getting a single TennisWorldUser in the repository by its email Exception flows.
   */
  @Test
  public void getByEmailNotFoundExceptionTest() {
    assertThrows(
        KameHouseNotFoundException.class,
        () -> {
          tennisWorldUserDaoJpa.getByEmail(TennisWorldUserTestUtils.INVALID_EMAIL);
        });
  }
}
