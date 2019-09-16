package com.nicobrest.kamehouse.admin.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
    assertEquals(0, applicationUserDaoJpa.getAllUsers().size());

    Long returnedId = applicationUserDaoJpa.createUser(applicationUser);

    assertEquals(applicationUser.getId(), returnedId);
    assertEquals(1, applicationUserDaoJpa.getAllUsers().size());
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
   * Test for getting a single ApplicationUser in the repository by username.
   */
  @Test
  public void getApplicationUserByUsernameTest() {
    applicationUserDaoJpa.createUser(applicationUser);

    ApplicationUser returnedUser =
        applicationUserDaoJpa.loadUserByUsername(applicationUser.getUsername());
    assertNotNull(returnedUser);
    applicationUser.setId(returnedUser.getId());
    assertEquals(applicationUser, returnedUser);
  }

  /**
   * Test for getting a single ApplicationUser in the repository Exception flows.
   */
  @Test
  public void getApplicationUserNotFoundExceptionTest() {
    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("Entity not found in the repository.");

    applicationUserDaoJpa.loadUserByUsername("yukimura");
  }

  /**
   * Test for updating an existing user in the repository.
   */
  @Test
  public void updateApplicationUserTest() {
    applicationUserDaoJpa.createUser(applicationUser);
    ApplicationUser userToUpdate = applicationUserDaoJpa.loadUserByUsername("goku");
    userToUpdate.setEmail("updatedGoku@dbz.com");
    userToUpdate.getAuthorities();
    applicationUserDaoJpa.updateUser(userToUpdate);
    ApplicationUser updatedUser = applicationUserDaoJpa.loadUserByUsername("goku");

    assertEquals("goku", updatedUser.getUsername());
    assertEquals("updatedGoku@dbz.com", updatedUser.getEmail());
    assertEquals("goku", updatedUser.getPassword());
  }

  /**
   * Test for updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateApplicationUserNotFoundExceptionTest() {

    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("ApplicationUser with id " + 987L + " was not found in the repository.");
    applicationUser.setId(987L);
    applicationUserDaoJpa.updateUser(applicationUser);
  }

  /**
   * Test for deleting an existing user from the repository.
   */
  @Test
  public void deleteApplicationUserTest() {

    try {
      applicationUserDaoJpa.createUser(applicationUser);
      assertEquals(1, applicationUserDaoJpa.getAllUsers().size());
      ApplicationUser deletedUser = applicationUserDaoJpa
          .deleteUser(applicationUserDaoJpa.loadUserByUsername("goku").getId());
      assertEquals(0, applicationUserDaoJpa.getAllUsers().size());
      assertEquals("goku", deletedUser.getUsername());
      assertEquals("goku@dbz.com", deletedUser.getEmail());
      assertEquals("goku", deletedUser.getPassword());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for deleting an existing user from the repository Exception flows.
   */
  @Test
  public void deleteApplicationUserNotFoundExceptionTest() {

    thrown.expect(KameHouseNotFoundException.class);
    thrown.expectMessage("ApplicationUser with id " + 987L + " was not found in the repository.");
    applicationUserDaoJpa.deleteUser(987L);
  }

  /**
   * Test for getting all the ApplicationUsers in the repository.
   */
  @Test
  public void getAllApplicationUsersTest() {

    applicationUserDaoJpa.createUser(applicationUsersList.get(0));
    applicationUserDaoJpa.createUser(applicationUsersList.get(1));
    applicationUserDaoJpa.createUser(applicationUsersList.get(2));
    try {
      List<ApplicationUser> usersList = applicationUserDaoJpa.getAllUsers();
      assertEquals(3, usersList.size());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }
}
