package com.nicobrest.kamehouse.admin.dao;

import com.nicobrest.kamehouse.commons.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.testutils.KameHouseUserTestUtils;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit tests for the KameHouseUserDaoJpa class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class KameHouseUserDaoJpaTest
    extends AbstractCrudDaoJpaTest<KameHouseUser, KameHouseUserDto> {

  private KameHouseUser kameHouseUser;

  @Autowired
  private KameHouseUserDao kameHouseUserDaoJpa;

  /**
   * Clear data from the repository before each test.
   */
  @Before
  public void setUp() {
    testUtils = new KameHouseUserTestUtils();
    testUtils.initTestData();
    testUtils.removeIds();
    kameHouseUser = testUtils.getSingleTestData();

    clearTable("KAMEHOUSE_ROLE");
    clearTable("KAMEHOUSE_USER");
  }

  /**
   * Test for creating a KameHouseUser in the repository.
   */
  @Test
  public void createTest() {
    createTest(kameHouseUserDaoJpa, KameHouseUser.class);
  }

  /**
   * Test for creating a KameHouseUser in the repository Exception flows.
   */
  @Test
  public void createConflictExceptionTest() {
    createConflictExceptionTest(kameHouseUserDaoJpa);
  }

  /**
   * Test for getting a single KameHouseUser in the repository by id.
   */
  @Test
  public void readTest() {
    readTest(kameHouseUserDaoJpa);
  }

  /**
   * Test for getting all the KameHouseUser in the repository.
   */
  @Test
  public void readAllTest() {
    readAllTest(kameHouseUserDaoJpa);
  }

  /**
   * Test for updating an existing user in the repository.
   */
  @Test
  public void updateTest() throws IllegalAccessException, InstantiationException,
      InvocationTargetException, NoSuchMethodException {
    KameHouseUser updatedEntity = (KameHouseUser) BeanUtils.cloneBean(kameHouseUser);
    updatedEntity.setEmail("gokuUpdatedEmail@dbz.com");

    updateTest(kameHouseUserDaoJpa, KameHouseUser.class, updatedEntity);
  }

  /**
   * Test for updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateNotFoundExceptionTest() {
    updateNotFoundExceptionTest(kameHouseUserDaoJpa, KameHouseUser.class);
  }

  /**
   * Test for deleting an existing user from the repository.
   */
  @Test
  public void deleteTest() {
    deleteTest(kameHouseUserDaoJpa);
  }

  /**
   * Test for deleting an existing user from the repository Exception flows.
   */
  @Test
  public void deleteNotFoundExceptionTest() {
    deleteNotFoundExceptionTest(kameHouseUserDaoJpa, KameHouseUser.class);
  }

  /**
   * Test for getting a single KameHouseUser in the repository by username.
   */
  @Test
  public void loadUserByUsernameTest() {
    mergeEntityInRepository(kameHouseUser);

    KameHouseUser returnedUser =
        kameHouseUserDaoJpa.loadUserByUsername(kameHouseUser.getUsername());

    kameHouseUser.setId(returnedUser.getId());
    testUtils.assertEqualsAllAttributes(kameHouseUser, returnedUser);
  }

  /**
   * Test for getting a single KameHouseUser in the repository Exception flows.
   */
  @Test
  public void loadUserByUsernameNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("Entity not found in the repository.");

    kameHouseUserDaoJpa.loadUserByUsername(KameHouseUserTestUtils.INVALID_USERNAME);
  }
}
