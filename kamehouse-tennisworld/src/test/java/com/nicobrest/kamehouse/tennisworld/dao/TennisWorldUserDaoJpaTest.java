package com.nicobrest.kamehouse.tennisworld.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;
import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.TennisWorldUserDto;
import com.nicobrest.kamehouse.tennisworld.testutils.TennisWorldUserTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit tests for the TennisWorldUserDaoJpa class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class TennisWorldUserDaoJpaTest
    extends AbstractCrudDaoJpaTest<TennisWorldUser, TennisWorldUserDto> {

  private TennisWorldUser tennisWorldUser;

  @Autowired
  private TennisWorldUserDao tennisWorldUserDaoJpa;

  /**
   * Clears data from the repository before each test.
   */
  @BeforeEach
  public void setUp() {
    testUtils = new TennisWorldUserTestUtils();
    testUtils.initTestData();
    testUtils.removeIds();
    tennisWorldUser = testUtils.getSingleTestData();

    clearTable("TENNISWORLD_USER");
  }

  /**
   * Tests creating a TennisWorldUser in the repository.
   */
  @Test
  public void createTest() {
    createTest(tennisWorldUserDaoJpa, TennisWorldUser.class);
  }

  /**
   * Tests creating a TennisWorldUser in the repository Exception flows.
   */
  @Test
  public void createConflictExceptionTest() {
    createConflictExceptionTest(tennisWorldUserDaoJpa);
  }

  /**
   * Tests getting a single TennisWorldUser from the repository by id.
   */
  @Test
  public void readTest() {
    readTest(tennisWorldUserDaoJpa);
  }

  /**
   * Tests getting all the TennisWorldUsers in the repository.
   */
  @Test
  public void readAllTest() {
    readAllTest(tennisWorldUserDaoJpa);
  }

  /**
   * Tests updating an existing user in the repository.
   */
  @Test
  public void updateTest() throws IllegalAccessException, InstantiationException,
      InvocationTargetException, NoSuchMethodException {
    TennisWorldUser updatedEntity = tennisWorldUser;
    updatedEntity.setEmail("gokuUpdated@dbz.com");

    updateTest(tennisWorldUserDaoJpa, TennisWorldUser.class, updatedEntity);
  }

  /**
   * Tests updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateNotFoundExceptionTest() {
    updateNotFoundExceptionTest(tennisWorldUserDaoJpa, TennisWorldUser.class);
  }

  /**
   * Tests updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateServerErrorExceptionTest() {
    assertThrows(KameHouseServerErrorException.class, () -> {
      persistEntityInRepository(tennisWorldUser);
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < 70; i++) {
        sb.append("goku");
      }
      String email = sb.toString();
      tennisWorldUser.setEmail(email);

      tennisWorldUserDaoJpa.update(tennisWorldUser);
    });
  }

  /**
   * Tests deleting an existing user from the repository.
   */
  @Test
  public void deleteTest() {
    deleteTest(tennisWorldUserDaoJpa);
  }

  /**
   * Tests deleting an existing user from the repository Exception flows.
   */
  @Test
  public void deleteNotFoundExceptionTest() {
    deleteNotFoundExceptionTest(tennisWorldUserDaoJpa, TennisWorldUser.class);
  }


  /**
   * Tests getting a single TennisWorldUser in the repository by its email.
   */
  @Test
  public void getByEmailTest() {
    persistEntityInRepository(tennisWorldUser);

    TennisWorldUser returnedUser = tennisWorldUserDaoJpa.getByEmail(tennisWorldUser.getEmail());

    testUtils.assertEqualsAllAttributes(tennisWorldUser, returnedUser);
  }

  /**
   * Tests getting a single TennisWorldUser in the repository by its email
   * Exception flows.
   */
  @Test
  public void getByEmailNotFoundExceptionTest() {
    assertThrows(KameHouseNotFoundException.class, () -> {
      tennisWorldUserDaoJpa.getByEmail(TennisWorldUserTestUtils.INVALID_EMAIL);
    });
  }
}
