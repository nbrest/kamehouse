package ar.com.nicobrest.mobileinspections.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import ar.com.nicobrest.mobileinspections.model.DragonBallUser;

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
 * @author nbrest
 *
 *         Unit tests for the DragonBallUserService class
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContextDao.xml"})
public class DragonBallUserDaoInMemoryTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DragonBallUserDaoInMemoryTest.class);

  @Autowired
  @Qualifier("dragonBallUserDaoInMemory")
  private DragonBallUserDaoInMemory dragonBallUserDaoInMemory;
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Test for getting all the DragonBallUsers in the repository
   */
  @Test
  public void getAllDragonBallUsersTest() {
    LOGGER.info("****************** Executing getAllDragonBallUsersTest ******************");
        
    List<DragonBallUser> usersList = dragonBallUserDaoInMemory.getAllDragonBallUsers();
    
    LOGGER.info("dragonBallUsers.get(0): " + usersList.get(0).getUsername());
    LOGGER.info("dragonBallUsers.get(1): " + usersList.get(1).getUsername());
    LOGGER.info("dragonBallUsers.get(2): " + usersList.get(2).getUsername());

    assertEquals("3", usersList.get(0).getId().toString());
    assertEquals("goten", usersList.get(0).getUsername());
    assertEquals("goten@dbz.com", usersList.get(0).getEmail());
    assertEquals(19, usersList.get(0).getAge());
    assertEquals(10, usersList.get(0).getPowerLevel());
    assertEquals(1000, usersList.get(0).getStamina());
    
    assertEquals("2", usersList.get(1).getId().toString());
    assertEquals("gohan", usersList.get(1).getUsername());
    assertEquals("gohan@dbz.com", usersList.get(1).getEmail());
    assertEquals(29, usersList.get(1).getAge());
    assertEquals(20, usersList.get(1).getPowerLevel());
    assertEquals(1000, usersList.get(1).getStamina());
    
    assertEquals("1", usersList.get(2).getId().toString());
    assertEquals("goku", usersList.get(2).getUsername());
    assertEquals("goku@dbz.com", usersList.get(2).getEmail());
    assertEquals(49, usersList.get(2).getAge());
    assertEquals(30, usersList.get(2).getPowerLevel());
    assertEquals(1000, usersList.get(2).getStamina());
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
        
    DragonBallUser user = dragonBallUserDaoInMemory.getDragonBallUser("goku");
    
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
        
    DragonBallUser gohan = dragonBallUserDaoInMemory.getGohanDragonBallUser();
    DragonBallUser goten = dragonBallUserDaoInMemory.getGotenDragonBallUser();
    
    LOGGER.info("gohan: " + gohan.getUsername());
    LOGGER.info("goten: " + goten.getUsername());
    
    assertNotNull(gohan);
    assertEquals("gohanTestBean", gohan.getUsername());
    assertNotNull(goten);
    assertEquals("gotenTestBean", goten.getUsername());
  }
}
