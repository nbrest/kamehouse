package com.nicobrest.kamehouse.tennisworld.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.TennisWorldUserDto;
import com.nicobrest.kamehouse.tennisworld.testutils.TennisWorldUserTestUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit tests for the TennisWorldUserDaoJpa class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class TennisWorldUserDaoJpaTest
    extends AbstractCrudDaoJpaTest<TennisWorldUser, TennisWorldUserDto> {

  private TennisWorldUser tennisWorldUser;

  @Autowired
  private TennisWorldUserDao tennisWorldUserDaoJpa;

  /**
   * Clears data from the repository before each test.
   */
  @Before
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
    TennisWorldUser updatedEntity = (TennisWorldUser) BeanUtils.cloneBean(tennisWorldUser);
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
    thrown.expect(KameHouseServerErrorException.class);
    thrown.expectMessage("PersistenceException");
    persistEntityInRepository(tennisWorldUser);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 70; i++) {
      sb.append("goku");
    }
    String email = sb.toString();
    tennisWorldUser.setEmail(email);

    tennisWorldUserDaoJpa.update(tennisWorldUser);
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
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("NoResultException: Entity not found in the repository.");

    tennisWorldUserDaoJpa.getByEmail(TennisWorldUserTestUtils.INVALID_EMAIL);
  }
}
