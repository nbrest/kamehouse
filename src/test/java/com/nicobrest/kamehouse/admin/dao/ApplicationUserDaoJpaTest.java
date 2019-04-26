package com.nicobrest.kamehouse.admin.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.nicobrest.kamehouse.admin.dao.ApplicationUserDao;
import com.nicobrest.kamehouse.admin.model.ApplicationRole;
import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.main.exception.KameHouseConflictException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.LinkedList;
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

  private static ApplicationUser applicationUserMock;
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

    EntityManager em = entityManagerFactory.createEntityManager();
    em.getTransaction().begin();
    Query deleteRoles = em.createNativeQuery("DELETE FROM APPLICATION_ROLE");
    deleteRoles.executeUpdate();
    Query deleteUsers = em.createNativeQuery("DELETE FROM APPLICATION_USER");
    deleteUsers.executeUpdate();
    em.getTransaction().commit();
    em.close();

    List<ApplicationRole> rolesMock = new ArrayList<ApplicationRole>();
    ApplicationRole userRoleMock = new ApplicationRole();
    userRoleMock.setName("ROLE_USER");
    rolesMock.add(userRoleMock);
    applicationUserMock = new ApplicationUser();
    applicationUserMock.setEmail("goku@dbz.com");
    applicationUserMock.setUsername("goku");
    applicationUserMock.setPassword("goku");
    applicationUserMock.setAuthorities(rolesMock);

    List<ApplicationRole> rolesMock2 = new ArrayList<ApplicationRole>();
    ApplicationRole userRoleMock2 = new ApplicationRole();
    userRoleMock2.setName("ROLE_USER");
    rolesMock2.add(userRoleMock2);
    ApplicationUser applicationUserMock2 = new ApplicationUser();
    applicationUserMock2.setEmail("gohan@dbz.com");
    applicationUserMock2.setUsername("gohan");
    applicationUserMock2.setPassword("gohan");
    applicationUserMock2.setAuthorities(rolesMock2);

    List<ApplicationRole> rolesMock3 = new ArrayList<ApplicationRole>();
    ApplicationRole userRoleMock3 = new ApplicationRole();
    userRoleMock3.setName("ROLE_USER");
    rolesMock3.add(userRoleMock3);
    ApplicationUser applicationUserMock3 = new ApplicationUser();
    applicationUserMock3.setEmail("goten@dbz.com");
    applicationUserMock3.setUsername("goten");
    applicationUserMock3.setPassword("goten");
    applicationUserMock3.setAuthorities(rolesMock3);

    applicationUsersList = new LinkedList<ApplicationUser>();
    applicationUsersList.add(applicationUserMock);
    applicationUsersList.add(applicationUserMock2);
    applicationUsersList.add(applicationUserMock3);
  }

  /**
   * Test for creating a ApplicationUser in the repository.
   */
  @Test
  public void createApplicationUserTest() {

    try {
      assertEquals(0, applicationUserDaoJpa.getAllUsers().size());
      applicationUserDaoJpa.createUser(applicationUserMock);
      assertEquals(1, applicationUserDaoJpa.getAllUsers().size());
      applicationUserDaoJpa.deleteUser(applicationUserDaoJpa.loadUserByUsername("goku").getId());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for creating a ApplicationUser in the repository Exception flows.
   */
  @Test
  public void createApplicationUserConflictExceptionTest() {

    thrown.expect(KameHouseConflictException.class);
    thrown.expectMessage("ConstraintViolationException: Error inserting data");
    applicationUserDaoJpa.createUser(applicationUserMock);
    applicationUserMock.setId(null);
    applicationUserDaoJpa.createUser(applicationUserMock);
  }

  /**
   * Test for getting a single ApplicationUser in the repository by username.
   */
  @Test
  public void getApplicationUserByUsernameTest() {

    try {
      applicationUserDaoJpa.createUser(applicationUserMock);
      ApplicationUser user = applicationUserDaoJpa.loadUserByUsername("goku");
      assertNotNull(user);
      assertEquals("goku", user.getUsername());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for getting a single ApplicationUser in the repository Exception
   * flows.
   */
  @Test
  public void getApplicationUserNotFoundExceptionTest() {

    thrown.expect(UsernameNotFoundException.class);
    thrown.expectMessage("User with username yukimura not found.");
    applicationUserDaoJpa.loadUserByUsername("yukimura");
  }

  /**
   * Test for updating an existing user in the repository.
   */
  @Test
  public void updateApplicationUserTest() {

    try {
      applicationUserDaoJpa.createUser(applicationUserMock);
      ApplicationUser userToUpdate = applicationUserDaoJpa.loadUserByUsername("goku");
      userToUpdate.setEmail("updatedGoku@dbz.com");
      userToUpdate.getAuthorities();
      applicationUserDaoJpa.updateUser(userToUpdate);
      ApplicationUser updatedUser = applicationUserDaoJpa.loadUserByUsername("goku");

      assertEquals("goku", updatedUser.getUsername());
      assertEquals("updatedGoku@dbz.com", updatedUser.getEmail());
      assertEquals("goku", updatedUser.getPassword());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for updating an existing user in the repository Exception flows.
   */
  @Test
  public void updateApplicationUserNotFoundExceptionTest() {

    thrown.expect(UsernameNotFoundException.class);
    thrown.expectMessage("ApplicationUser with id " + 987L + " was not found in the repository.");
    applicationUserMock.setId(987L);
    applicationUserDaoJpa.updateUser(applicationUserMock);
  }

  /**
   * Test for deleting an existing user from the repository.
   */
  @Test
  public void deleteApplicationUserTest() {

    try {
      applicationUserDaoJpa.createUser(applicationUserMock);
      assertEquals(1, applicationUserDaoJpa.getAllUsers().size());
      ApplicationUser deletedUser = applicationUserDaoJpa.deleteUser(applicationUserDaoJpa
          .loadUserByUsername("goku").getId());
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

    thrown.expect(UsernameNotFoundException.class);
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
