package ar.com.nicobrest.mobileinspections.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.com.nicobrest.mobileinspections.dao.HelloWorldUserDao;
import ar.com.nicobrest.mobileinspections.model.HelloWorldUser;

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
 * @author nicolas.brest
 *
 *         Unit tests for the HelloWorldUserService class
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContextService.xml"})
public class HelloWorldUserServiceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldUserServiceTest.class);

  private static List<HelloWorldUser> helloWorldUsers;
  
  @Autowired
  @Qualifier("helloWorldUserService")
  private HelloWorldUserService helloWorldUserService;
  
  @Autowired
  @Qualifier("helloWorldUserDao")
  private HelloWorldUserDao helloWorldUserDaoMock;

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

    // Create test data to be returned by mock object helloWorldUserServiceMock
    HelloWorldUser helloWorldUser1 = new HelloWorldUser();
    helloWorldUser1.setAge(49);
    helloWorldUser1.setEmail("gokuTestMock@dbz.com");
    helloWorldUser1.setUsername("gokuTestMock");
    
    HelloWorldUser helloWorldUser2 = new HelloWorldUser();
    helloWorldUser2.setAge(29);
    helloWorldUser2.setEmail("gohanTestMock@dbz.com");
    helloWorldUser2.setUsername("gohanTestMock");
    
    HelloWorldUser helloWorldUser3 = new HelloWorldUser();
    helloWorldUser3.setAge(19);
    helloWorldUser3.setEmail("gotenTestMock@dbz.com");
    helloWorldUser3.setUsername("gotenTestMock");
    
    helloWorldUsers = new ArrayList<HelloWorldUser>();
    helloWorldUsers.add(helloWorldUser1);
    helloWorldUsers.add(helloWorldUser2);
    helloWorldUsers.add(helloWorldUser3);
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
    Mockito.reset(helloWorldUserDaoMock);
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Test for getting all the HelloWorldUsers in the repository
   */
  @Test
  public void getAllHelloWorldUsersTest() {
    LOGGER.info("****************** Executing getAllHelloWorldUsersTest ******************");
    
    when(helloWorldUserDaoMock.getAllHelloWorldUsers()).thenReturn(helloWorldUsers);
    
    List<HelloWorldUser> usersList = helloWorldUserService.getAllHelloWorldUsers();
    
    LOGGER.info("helloWorldUsers.get(0): " + usersList.get(0).getUsername());
    LOGGER.info("helloWorldUsers.get(1): " + usersList.get(1).getUsername());
    LOGGER.info("helloWorldUsers.get(2): " + usersList.get(2).getUsername());
    
    assertEquals("gokuTestMock", usersList.get(0).getUsername());
    assertEquals("gokuTestMock@dbz.com", usersList.get(0).getEmail());
    assertEquals(49, usersList.get(0).getAge());
    
    assertEquals("gohanTestMock", usersList.get(1).getUsername());
    assertEquals("gohanTestMock@dbz.com", usersList.get(1).getEmail());
    assertEquals(29, usersList.get(1).getAge());
    
    assertEquals("gotenTestMock", usersList.get(2).getUsername());
    assertEquals("gotenTestMock@dbz.com", usersList.get(2).getEmail());
    assertEquals(19, usersList.get(2).getAge());
    
    verify(helloWorldUserDaoMock, times(1)).getAllHelloWorldUsers();
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Test for getting a single HelloWorldUser in the repository
   */
  @Test
  public void getHelloWorldUserTest() {
    LOGGER.info("****************** Executing getHelloWorldUserTest ******************");
    
    when(helloWorldUserDaoMock.getHelloWorldUser("gokuTestMock"))
      .thenReturn(helloWorldUsers.get(0));
    
    HelloWorldUser user = helloWorldUserService.getHelloWorldUser("gokuTestMock");
    
    LOGGER.info("user: " + user.getUsername());
    
    assertNotNull(user);
    assertEquals("gokuTestMock", user.getUsername());
    verify(helloWorldUserDaoMock, times(1)).getHelloWorldUser("gokuTestMock"); 
  }
}
