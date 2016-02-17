package ar.com.nicobrest.mobileinspections.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.com.nicobrest.mobileinspections.dao.DragonBallUserDao;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @since v0.03 
 * @author nbrest
 *
 *         Unit tests for the DragonBallUserService class
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContextService.xml"})
public class DragonBallUserServiceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DragonBallUserServiceTest.class);

  private static List<DragonBallUser> dragonBallUsers;
  
  @Autowired
  @Qualifier("dragonBallUserService")
  private DragonBallUserService dragonBallUserService;
  
  @Autowired
  @Qualifier("dragonBallUserDao")
  private DragonBallUserDao dragonBallUserDaoMock;

  /**
   * @since v0.03
   * @author nbrest
   * @throws Exception Throws any type of exception in the method
   * 
   *      Initializes test repository
   */
  @BeforeClass
  public static void beforeClassTest() throws Exception {
    /* Actions to perform ONCE before all tests in the class */

    // Create test data to be returned by mock object dragonBallUserServiceMock
    DragonBallUser user1 = new DragonBallUser();
    user1.setAge(49);
    user1.setEmail("gokuTestMock@dbz.com");
    user1.setUsername("gokuTestMock");
    
    DragonBallUser user2 = new DragonBallUser();
    user2.setAge(29);
    user2.setEmail("gohanTestMock@dbz.com");
    user2.setUsername("gohanTestMock");
    
    DragonBallUser user3 = new DragonBallUser();
    user3.setAge(19);
    user3.setEmail("gotenTestMock@dbz.com");
    user3.setUsername("gotenTestMock");
    
    dragonBallUsers = new ArrayList<DragonBallUser>();
    dragonBallUsers.add(user1);
    dragonBallUsers.add(user2);
    dragonBallUsers.add(user3);
  }  

  /**
   * @since v0.03
   * @author nbrest 
   * 
   *      Resets mock objects
   */
  @Before
  public void beforeTest() {
    /* Actions to perform before each test in the class */
    
    // Reset mock objects before each test
    Mockito.reset(dragonBallUserDaoMock);
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Test for getting all the DragonBallUsers in the repository
   */
  @Test
  public void getAllDragonBallUsersTest() {
    LOGGER.info("****************** Executing getAllDragonBallUsersTest ******************");
    
    when(dragonBallUserDaoMock.getAllDragonBallUsers()).thenReturn(dragonBallUsers);
    
    List<DragonBallUser> usersList = dragonBallUserService.getAllDragonBallUsers();
    
    LOGGER.info("dragonBallUsers.get(0): " + usersList.get(0).getUsername());
    LOGGER.info("dragonBallUsers.get(1): " + usersList.get(1).getUsername());
    LOGGER.info("dragonBallUsers.get(2): " + usersList.get(2).getUsername());
    
    assertEquals("gokuTestMock", usersList.get(0).getUsername());
    assertEquals("gokuTestMock@dbz.com", usersList.get(0).getEmail());
    assertEquals(49, usersList.get(0).getAge());
    
    assertEquals("gohanTestMock", usersList.get(1).getUsername());
    assertEquals("gohanTestMock@dbz.com", usersList.get(1).getEmail());
    assertEquals(29, usersList.get(1).getAge());
    
    assertEquals("gotenTestMock", usersList.get(2).getUsername());
    assertEquals("gotenTestMock@dbz.com", usersList.get(2).getEmail());
    assertEquals(19, usersList.get(2).getAge());
    
    verify(dragonBallUserDaoMock, times(1)).getAllDragonBallUsers();
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Test for getting a single DragonBallUser in the repository
   */
  @Test
  public void getDragonBallUserTest() {
    LOGGER.info("****************** Executing getDragonBallUserTest ******************");
    
    when(dragonBallUserDaoMock.getDragonBallUser("gokuTestMock"))
      .thenReturn(dragonBallUsers.get(0));
    
    DragonBallUser user = dragonBallUserService.getDragonBallUser("gokuTestMock");
    
    LOGGER.info("user: " + user.getUsername());
    
    assertNotNull(user);
    assertEquals("gokuTestMock", user.getUsername());
    verify(dragonBallUserDaoMock, times(1)).getDragonBallUser("gokuTestMock"); 
  }
}
