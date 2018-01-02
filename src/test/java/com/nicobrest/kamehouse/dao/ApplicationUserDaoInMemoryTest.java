package com.nicobrest.kamehouse.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.nicobrest.kamehouse.dao.ApplicationUserDaoInMemory;
import com.nicobrest.kamehouse.model.ApplicationUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.LinkedList;
import java.util.List;

/**
 * Unit tests for the ApplicationUserInMemoryDao class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class ApplicationUserDaoInMemoryTest {
  
  private static ApplicationUser applicationUserMock;
  private static List<ApplicationUser> applicationUsersList;
  
  @Autowired
  @Qualifier("applicationUserDaoInMemory")
  private ApplicationUserDaoInMemory applicationUserDao;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Initializes test repositories.
   */
  @Before
  public void init() {
        
    applicationUserMock = new ApplicationUser();
    applicationUserMock.setId(1001L);
    applicationUserMock.setEmail("goku@dbz.com");
    applicationUserMock.setUsername("goku");
    applicationUserMock.setPassword("goku");

    ApplicationUser applicationUserMock2 = new ApplicationUser();
    applicationUserMock2.setId(1002L);
    applicationUserMock2.setEmail("gohan@dbz.com");
    applicationUserMock2.setUsername("gohan");
    applicationUserMock2.setPassword("gohan");

    ApplicationUser applicationUserMock3 = new ApplicationUser();
    applicationUserMock3.setId(1003L);
    applicationUserMock3.setEmail("goten@dbz.com");
    applicationUserMock3.setUsername("goten");
    applicationUserMock3.setPassword("goten");

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
      assertEquals(4, applicationUserDao.getAllUsers().size());
      applicationUserDao.createUser(applicationUserMock);
      assertEquals(5, applicationUserDao.getAllUsers().size());
      applicationUserDao.deleteUser(applicationUserDao.loadUserByUsername("goku")
          .getId());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for getting a single ApplicationUser in the repository by its username.
   */
  @Test
  public void loadUserByUsernameTest() {

    try {
      ApplicationUser user = applicationUserDao.loadUserByUsername("admin");
      assertNotNull(user);
      assertEquals("admin", user.getUsername());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught unexpected exception.");
    }
  }

  /**
   * Test for getting a single ApplicationUser in the repository Exception flows.
   */
  @Test
  public void loadUserByUsernameNotFoundExceptionTest() {

    thrown.expect(UsernameNotFoundException.class);
    thrown.expectMessage("User with username yukimura not found.");
    applicationUserDao.loadUserByUsername("yukimura");
  }

  /**
   * Test for updating an existing user in the repository.
   */
  @Test
  public void updateApplicationUserTest() {

    try {
      ApplicationUser originalUser = applicationUserDao.loadUserByUsername("admin");
      assertEquals("admin", originalUser.getUsername());

      applicationUserMock.setId(originalUser.getId());
      applicationUserMock.setUsername(originalUser.getUsername());

      applicationUserDao.updateUser(applicationUserMock);
      ApplicationUser updatedUser = applicationUserDao.loadUserByUsername("admin");

      assertEquals(originalUser.getId().toString(), updatedUser.getId().toString());
      assertEquals("admin", updatedUser.getUsername());
      assertEquals("goku@dbz.com", updatedUser.getEmail());
      assertEquals("goku", updatedUser.getPassword());
      
      applicationUserDao.updateUser(originalUser);
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

    applicationUserMock.setId(1234L);
    thrown.expect(UsernameNotFoundException.class);
    thrown.expectMessage("User with username goku not found.");
    applicationUserDao.updateUser(applicationUserMock);
  }

  /**
   * Test for deleting an existing user from the repository.
   */
  @Test
  public void deleteApplicationUserTest() {
    try {
      applicationUserMock.setId(12345L);
      applicationUserDao.createUser(applicationUserMock);
      assertEquals(5, applicationUserDao.getAllUsers().size());
      ApplicationUser deletedUser = applicationUserDao.deleteUser(applicationUserDao
          .loadUserByUsername("goku").getId());
      assertEquals(4, applicationUserDao.getAllUsers().size());
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
    thrown.expectMessage("User with id " + 987L + " not found.");
    applicationUserDao.deleteUser(987L);
  }

  /**
   * Test for getting all the ApplicationUsers in the repository.
   */
  @Test
  public void getAllApplicationUsersTest() {
    try {
      assertEquals(4, applicationUserDao.getAllUsers().size());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }
}
