package com.nicobrest.kamehouse.admin.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.admin.service.dto.ApplicationUserDto;
import com.nicobrest.kamehouse.admin.testutils.ApplicationUserTestUtils;
import com.nicobrest.kamehouse.main.dao.AbstractCrudDaoJpaTest;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

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
  private List<ApplicationUser> applicationUsersList;

  @Autowired
  private ApplicationUserDao applicationUserDaoJpa;

  /**
   * Clear data from the repository before each test.
   */
  @Before
  public void setUp() {
    testUtils = new ApplicationUserTestUtils();
    testUtils.initTestData();
    applicationUser = testUtils.getSingleTestData();
    applicationUsersList = testUtils.getTestDataList();
    
    clearTable("APPLICATION_ROLE");
    clearTable("APPLICATION_USER");
  }

  /**
   * Test for creating a ApplicationUser in the repository.
   */
  @Test
  public void createTest() {
    createTest(applicationUserDaoJpa, ApplicationUser.class, applicationUser);
  }

  /**
   * Test for creating a ApplicationUser in the repository Exception flows.
   */
  @Test
  public void createConflictExceptionTest() {
    createConflictExceptionTest(applicationUserDaoJpa, applicationUser);
  }

  /**
   * Test for getting a single ApplicationUser in the repository by id.
   */
  @Test
  public void readTest() {
    // TODO: Use the abstracted method once I fix persistence in the model.
    Long createId = mergeEntityInRepository(applicationUser).getId();
    applicationUser.setId(createId);

    ApplicationUser returnedUser = applicationUserDaoJpa.read(createId);

    assertNotNull(returnedUser);
    assertEquals(applicationUser, returnedUser);
  }

  /**
   * Test for getting all the ApplicationUsers in the repository.
   */
  @Test
  public void readAllTest() {
    // TODO: Use the abstracted method once I fix persistence in the model.
    for (ApplicationUser applicationUser : applicationUsersList) {
      Long createdId = mergeEntityInRepository(applicationUser).getId();
      applicationUser.setId(createdId);
    }

    List<ApplicationUser> returnedUsersList = applicationUserDaoJpa.readAll();

    assertEquals(applicationUsersList.size(), returnedUsersList.size());
    assertEquals(applicationUsersList, returnedUsersList);
  }

  /**
   * Test for updating an existing user in the repository.
   */
  @Test
  public void updateTest() {
    // TODO: Use the abstracted method once I fix persistence in the model.
    ApplicationUser userToUpdate = mergeEntityInRepository(applicationUser);
    userToUpdate.setEmail("updatedGoku@dbz.com");
    userToUpdate.getAuthorities();

    applicationUserDaoJpa.update(userToUpdate);

    ApplicationUser updatedUser = findById(ApplicationUser.class, userToUpdate.getId());
    assertEquals(userToUpdate, updatedUser);
  }

  /**
   * Test for updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateNotFoundExceptionTest() {
    updateNotFoundExceptionTest(applicationUserDaoJpa, ApplicationUser.class, applicationUser);
  }

  /**
   * Test for deleting an existing user from the repository.
   */
  @Test
  public void deleteTest() {
    // TODO: Use the abstracted method once I fix persistence in the model.
    Long userToDeleteId = mergeEntityInRepository(applicationUser).getId();
    applicationUser.setId(userToDeleteId);

    ApplicationUser deletedUser = applicationUserDaoJpa.delete(userToDeleteId);

    assertEquals(applicationUser, deletedUser);
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

    assertNotNull(returnedUser);
    applicationUser.setId(returnedUser.getId());
    assertEquals(applicationUser, returnedUser);
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
