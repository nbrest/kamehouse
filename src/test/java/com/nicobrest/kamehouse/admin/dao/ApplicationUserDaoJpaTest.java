package com.nicobrest.kamehouse.admin.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.admin.testutils.ApplicationUserTestUtils;
import com.nicobrest.kamehouse.main.dao.AbstractDaoJpaTest;
import com.nicobrest.kamehouse.main.exception.KameHouseConflictException;
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
public class ApplicationUserDaoJpaTest extends AbstractDaoJpaTest {

  private static ApplicationUser applicationUser;
  private static List<ApplicationUser> applicationUsersList;

  @Autowired
  private ApplicationUserDao applicationUserDaoJpa;

  /**
   * Clear data from the repository before each test.
   */
  @Before
  public void setUp() {
    ApplicationUserTestUtils.initTestData();
    applicationUser = ApplicationUserTestUtils.getSingleTestData();
    applicationUsersList = ApplicationUserTestUtils.getTestDataList();
    clearTable("APPLICATION_ROLE");
    clearTable("APPLICATION_USER");
  }

  /**
   * Test for creating a ApplicationUser in the repository.
   */
  @Test
  public void createApplicationUserTest() {
    Long returnedId = applicationUserDaoJpa.createUser(applicationUser);

    assertNotEquals(applicationUser.getId(), returnedId);
    ApplicationUser returnedUser = findById(ApplicationUser.class, returnedId);
    applicationUser.setId(returnedId);
    assertEquals(applicationUser, returnedUser);
  }

  /**
   * Test for creating a ApplicationUser in the repository Exception flows.
   */
  @Test
  public void createApplicationUserConflictExceptionTest() {
    thrown.expect(KameHouseConflictException.class);
    thrown.expectMessage("ConstraintViolationException: Error inserting data");
    applicationUserDaoJpa.createUser(applicationUser);
    applicationUser.setId(null);

    applicationUserDaoJpa.createUser(applicationUser);
  }

  /**
   * Test for getting a single ApplicationUser in the repository by id.
   */
  @Test
  public void getApplicationUserByIdTest() {
    Long createId = mergeEntityInRepository(applicationUser).getId();
    applicationUser.setId(createId);
    
    ApplicationUser returnedUser = applicationUserDaoJpa.getUser(createId);

    assertNotNull(returnedUser);
    assertEquals(applicationUser, returnedUser);
  }
  
  /**
   * Test for getting a single ApplicationUser in the repository by username.
   */
  @Test
  public void getApplicationUserByUsernameTest() {
    mergeEntityInRepository(applicationUser);

    ApplicationUser returnedUser = applicationUserDaoJpa.loadUserByUsername(applicationUser
        .getUsername());

    assertNotNull(returnedUser);
    applicationUser.setId(returnedUser.getId());
    assertEquals(applicationUser, returnedUser);
  }

  /**
   * Test for getting a single ApplicationUser in the repository Exception
   * flows.
   */
  @Test
  public void getApplicationUserNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("Entity not found in the repository.");

    applicationUserDaoJpa.loadUserByUsername(ApplicationUserTestUtils.INVALID_USERNAME);
  }

  /**
   * Test for updating an existing user in the repository.
   */
  @Test
  public void updateApplicationUserTest() {
    ApplicationUser userToUpdate = mergeEntityInRepository(applicationUser);
    userToUpdate.setEmail("updatedGoku@dbz.com");
    userToUpdate.getAuthorities();

    applicationUserDaoJpa.updateUser(userToUpdate);

    ApplicationUser updatedUser = findById(ApplicationUser.class, userToUpdate.getId());
    assertEquals(userToUpdate, updatedUser);
  }

  /**
   * Test for updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateApplicationUserNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("ApplicationUser with id " + ApplicationUserTestUtils.INVALID_ID
        + " was not found in the repository.");
    applicationUser.setId(ApplicationUserTestUtils.INVALID_ID);

    applicationUserDaoJpa.updateUser(applicationUser);
  }

  /**
   * Test for deleting an existing user from the repository.
   */
  @Test
  public void deleteApplicationUserTest() {
    Long userToDeleteId = mergeEntityInRepository(applicationUser).getId();
    applicationUser.setId(userToDeleteId);

    ApplicationUser deletedUser = applicationUserDaoJpa.deleteUser(userToDeleteId);

    assertEquals(applicationUser, deletedUser);
  }

  /**
   * Test for deleting an existing user from the repository Exception flows.
   */
  @Test
  public void deleteApplicationUserNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("ApplicationUser with id " + ApplicationUserTestUtils.INVALID_ID
        + " was not found in the repository.");

    applicationUserDaoJpa.deleteUser(ApplicationUserTestUtils.INVALID_ID);
  }

  /**
   * Test for getting all the ApplicationUsers in the repository.
   */
  @Test
  public void getAllApplicationUsersTest() { 
    for(ApplicationUser applicationUser : applicationUsersList) {
      Long createdId = mergeEntityInRepository(applicationUser).getId();
      applicationUser.setId(createdId);
    }
    
    List<ApplicationUser> returnedUsersList = applicationUserDaoJpa.getAllUsers();
    
    assertEquals(applicationUsersList.size(), returnedUsersList.size());
    assertEquals(applicationUsersList, returnedUsersList);
  }
}
