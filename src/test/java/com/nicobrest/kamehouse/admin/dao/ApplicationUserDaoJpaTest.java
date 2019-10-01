package com.nicobrest.kamehouse.admin.dao;

import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.admin.model.dto.ApplicationUserDto;
import com.nicobrest.kamehouse.admin.testutils.ApplicationUserTestUtils;
import com.nicobrest.kamehouse.main.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit tests for the ApplicationUserDaoJpa class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class ApplicationUserDaoJpaTest
    extends AbstractCrudDaoJpaTest<ApplicationUser, ApplicationUserDto> {

  private ApplicationUser applicationUser;

  @Autowired
  private ApplicationUserDao applicationUserDaoJpa;

  /**
   * Clear data from the repository before each test.
   */
  @Before
  public void setUp() {
    testUtils = new ApplicationUserTestUtils();
    testUtils.initTestData();
    testUtils.removeIds();
    applicationUser = testUtils.getSingleTestData();

    clearTable("APPLICATION_ROLE");
    clearTable("APPLICATION_USER");
  }

  /**
   * Test for creating a ApplicationUser in the repository.
   */
  @Test
  public void createTest() {
    createTest(applicationUserDaoJpa, ApplicationUser.class);
  }

  /**
   * Test for creating a ApplicationUser in the repository Exception flows.
   */
  @Test
  public void createConflictExceptionTest() {
    createConflictExceptionTest(applicationUserDaoJpa);
  }

  /**
   * Test for getting a single ApplicationUser in the repository by id.
   */
  @Test
  public void readTest() {
    readTest(applicationUserDaoJpa);
  }

  /**
   * Test for getting all the ApplicationUsers in the repository.
   */
  @Test
  public void readAllTest() {
    readAllTest(applicationUserDaoJpa);
  }

  /**
   * Test for updating an existing user in the repository.
   */
  @Test
  public void updateTest() throws IllegalAccessException, InstantiationException,
      InvocationTargetException, NoSuchMethodException {
    ApplicationUser updatedEntity = (ApplicationUser) BeanUtils.cloneBean(applicationUser);
    updatedEntity.setEmail("gokuUpdatedEmail@dbz.com");

    updateTest(applicationUserDaoJpa, ApplicationUser.class, updatedEntity);
  }

  /**
   * Test for updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateNotFoundExceptionTest() {
    updateNotFoundExceptionTest(applicationUserDaoJpa, ApplicationUser.class);
  }

  /**
   * Test for deleting an existing user from the repository.
   */
  @Test
  public void deleteTest() {
    deleteTest(applicationUserDaoJpa);
  }

  /**
   * Test for deleting an existing user from the repository Exception flows.
   */
  @Test
  public void deleteNotFoundExceptionTest() {
    deleteNotFoundExceptionTest(applicationUserDaoJpa, ApplicationUser.class);
  }

  /**
   * Test for getting a single ApplicationUser in the repository by username.
   */
  @Test
  public void loadUserByUsernameTest() {
    mergeEntityInRepository(applicationUser);

    ApplicationUser returnedUser =
        applicationUserDaoJpa.loadUserByUsername(applicationUser.getUsername());

    applicationUser.setId(returnedUser.getId());
    testUtils.assertEqualsAllAttributes(applicationUser, returnedUser);
  }

  /**
   * Test for getting a single ApplicationUser in the repository Exception flows.
   */
  @Test
  public void loadUserByUsernameNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("Entity not found in the repository.");

    applicationUserDaoJpa.loadUserByUsername(ApplicationUserTestUtils.INVALID_USERNAME);
  }
}
