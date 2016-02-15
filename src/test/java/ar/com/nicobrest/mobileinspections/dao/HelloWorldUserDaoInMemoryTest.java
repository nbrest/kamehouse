package ar.com.nicobrest.mobileinspections.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import ar.com.nicobrest.mobileinspections.model.HelloWorldUser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @since v0.03 
 * @author nicolas.brest
 *
 *         Unit tests for the HelloWorldUserService class
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContextDao.xml"})
public class HelloWorldUserDaoInMemoryTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldUserDaoInMemoryTest.class);

  @Autowired
  @Qualifier("helloWorldUserDaoInMemory")
  private HelloWorldUserDaoInMemory helloWorldUserDaoInMemory;
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Test for getting all the HelloWorldUsers in the repository
   */
  @Test
  public void getAllHelloWorldUsersTest() {
    LOGGER.info("****************** Executing getAllHelloWorldUsersTest ******************");
        
    List<HelloWorldUser> usersList = helloWorldUserDaoInMemory.getAllHelloWorldUsers();
    
    LOGGER.info("helloWorldUsers.get(0): " + usersList.get(0).getUsername());
    LOGGER.info("helloWorldUsers.get(1): " + usersList.get(1).getUsername());
    LOGGER.info("helloWorldUsers.get(2): " + usersList.get(2).getUsername());

    assertEquals("goten", usersList.get(0).getUsername());
    assertEquals("goten@dbz.com", usersList.get(0).getEmail());
    assertEquals(19, usersList.get(0).getAge());
    
    assertEquals("gohan", usersList.get(1).getUsername());
    assertEquals("gohan@dbz.com", usersList.get(1).getEmail());
    assertEquals(29, usersList.get(1).getAge());
    
    assertEquals("goku", usersList.get(2).getUsername());
    assertEquals("goku@dbz.com", usersList.get(2).getEmail());
    assertEquals(49, usersList.get(2).getAge());
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
        
    HelloWorldUser user = helloWorldUserDaoInMemory.getHelloWorldUser("goku");
    
    LOGGER.info("user: " + user.getUsername());
    
    assertNotNull(user);
    assertEquals("goku", user.getUsername());
  }
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Test for the autowired beans
   */
  @Test
  public void autoWiredBeansTest() {
    LOGGER.info("****************** Executing autoWiredBeansTest ******************");
        
    HelloWorldUser gohan = helloWorldUserDaoInMemory.getGohanHelloWorldUser();
    HelloWorldUser goten = helloWorldUserDaoInMemory.getGotenHelloWorldUser();
    
    LOGGER.info("gohan: " + gohan.getUsername());
    LOGGER.info("goten: " + goten.getUsername());
    
    assertNotNull(gohan);
    assertEquals("gohanTestBean", gohan.getUsername());
    assertNotNull(goten);
    assertEquals("gotenTestBean", goten.getUsername());
  }
}
