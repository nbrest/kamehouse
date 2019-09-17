package com.nicobrest.kamehouse.admin.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.admin.testutils.ApplicationUserTestUtils;
import com.nicobrest.kamehouse.main.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 * Unit tests for the ApplicationUserDaoJpa class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class ApplicationUserDaoJpaTest {

  private static ApplicationUser applicationUser;
  private static List<ApplicationUser> applicationUsersList;

  @Autowired
  private ApplicationUserDao applicationUserDaoJpa;

  @Autowired
  private EntityManagerFactory entityManagerFactory;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Clear data from the repository before each test.
   */
  @Before
  public void setUp() {
    ApplicationUserTestUtils.initApplicationUserTestData();
    applicationUser = ApplicationUserTestUtils.getApplicationUser();
    applicationUsersList = ApplicationUserTestUtils.getApplicationUsersList();
    EntityManager em = entityManagerFactory.createEntityManager();
    em.getTransaction().begin();
    Query deleteRoles = em.createNativeQuery("DELETE FROM APPLICATION_ROLE");
    deleteRoles.executeUpdate();
    Query deleteUsers = em.createNativeQuery("DELETE FROM APPLICATION_USER");
    deleteUsers.executeUpdate();
    em.getTransaction().commit();
    em.close();
  }

  /**
   * Test for creating a ApplicationUser in the repository.
   */
  @Test
  public void createApplicationUserTest() {
    Long returnedId = applicationUserDaoJpa.createUser(applicationUser);

    assertNotEquals(applicationUser.getId(), returnedId);
    ApplicationUser returnedUser = applicationUserDaoJpa.getUser(returnedId);
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
    Long createId = applicationUserDaoJpa.createUser(applicationUser);
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
    applicationUserDaoJpa.createUser(applicationUser);

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
    Long createId = applicationUserDaoJpa.createUser(applicationUser);
    ApplicationUser userToUpdate = applicationUserDaoJpa.getUser(createId);
    userToUpdate.setEmail("updatedGoku@dbz.com");
    userToUpdate.getAuthorities();

    applicationUserDaoJpa.updateUser(userToUpdate);

    ApplicationUser updatedUser = applicationUserDaoJpa.getUser(userToUpdate.getId());
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
    Long userToDeleteId = applicationUserDaoJpa.createUser(applicationUser);
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
      Long createdId = applicationUserDaoJpa.createUser(applicationUser);
      applicationUser.setId(createdId);
    }
    
    List<ApplicationUser> returnedUsersList = applicationUserDaoJpa.getAllUsers();
    
    assertEquals(3, returnedUsersList.size());
    assertEquals(applicationUsersList, returnedUsersList);
  }
}
