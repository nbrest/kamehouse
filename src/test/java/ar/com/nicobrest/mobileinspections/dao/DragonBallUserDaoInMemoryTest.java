package ar.com.nicobrest.mobileinspections.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import ar.com.nicobrest.mobileinspections.exception.DragonBallUserAlreadyExistsException;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserNotFoundException;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 *         Unit tests for the DragonBallUserInMemoryDao class
 *         
 * @since v0.03 
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContextDao.xml"})
public class DragonBallUserDaoInMemoryTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DragonBallUserDaoInMemoryTest.class);

  @Autowired
  @Qualifier("dragonBallUserDaoInMemory")
  private DragonBallUserDaoInMemory dragonBallUserDaoInMemory;
  
  @Rule
  public ExpectedException thrown = ExpectedException.none();
  
  /**
   *      Test for the autowired beans
   *      
   * @since v0.03
   * @author nbrest
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
  
  /**
   *      Test for creating a DragonBallUser in the repository
   *  
   * @since v0.03
   * @author nbrest
   */
  @Test
  public void createDragonBallUserTest() {
    LOGGER.info("****************** Executing createDragonBallUserTest ******************");
    
    DragonBallUser dragonBallUser = new DragonBallUser(0L, "vegeta", "vegeta@dbz.com", 
        49, 40, 1000);
    
    try {
      assertEquals(3, dragonBallUserDaoInMemory.getAllDragonBallUsers().size());
      dragonBallUserDaoInMemory.createDragonBallUser(dragonBallUser);
      assertEquals(4, dragonBallUserDaoInMemory.getAllDragonBallUsers().size());
      
      dragonBallUserDaoInMemory.deleteDragonBallUser("vegeta");
    } catch (DragonBallUserAlreadyExistsException | DragonBallUserNotFoundException e) {
      e.printStackTrace();
      fail();
    }
  }
  
  /**
   *      Test for creating a DragonBallUser in the repository
   *      Exception flows
   *  
   * @since v0.03
   * @author nbrest
   * @throws DragonBallUserAlreadyExistsException User defined exception
   */
  @Test
  public void createDragonBallUserExceptionTest() throws DragonBallUserAlreadyExistsException {
    LOGGER.info("****************** Executing createDragonBallUserExceptionTest ***************");
    
    DragonBallUser dragonBallUser = new DragonBallUser(0L, "goku", "goku@dbz.com", 
        49, 40, 1000);
    
    thrown.expect(DragonBallUserAlreadyExistsException.class);
    thrown.expectMessage("DragonBallUser with username goku already exists in the repository.");
    dragonBallUserDaoInMemory.createDragonBallUser(dragonBallUser);
  }
  
  /**
   *      Test for getting a single DragonBallUser in the repository
   *      
   * @since v0.03
   * @author nbrest
   */
  @Test
  public void getDragonBallUserTest() {
    LOGGER.info("****************** Executing getDragonBallUserTest ******************");
        
    try {
      DragonBallUser user = dragonBallUserDaoInMemory.getDragonBallUser("goku");
      
      LOGGER.info("user: " + user.getUsername());
      
      assertNotNull(user);
      assertEquals("goku", user.getUsername());
    } catch (DragonBallUserNotFoundException e) {
      e.printStackTrace();
      fail();
    }
  }

  /**
   *      Test for getting a single DragonBallUser in the repository
   *      Exception flows
   *      
   * @since v0.03
   * @author nbrest
   * @throws DragonBallUserNotFoundException User defined exception
   */
  @Test
  public void getDragonBallUserExceptionTest() throws DragonBallUserNotFoundException {
    LOGGER.info("****************** Executing getDragonBallUserExceptionTest ******************");
        
    thrown.expect(DragonBallUserNotFoundException.class);
    thrown.expectMessage("DragonBallUser with username yukimura was not found in the repository.");
    dragonBallUserDaoInMemory.getDragonBallUser("yukimura");
  }
  
  /**
   *      Test for updating an existing user in the repository
   * 
   * @since v0.03
   * @author nbrest
   */
  @Test
  public void updateDragonBallUserTest() {
    LOGGER.info("****************** Executing updateDragonBallUserTest ******************");
    
    DragonBallUser modifiedUser = new DragonBallUser(0L,"goku", "gokuUpdated@dbz.com", 
        51, 52, 53);
    try {
      DragonBallUser originalUser = dragonBallUserDaoInMemory.getDragonBallUser("goku");
      assertEquals("goku", originalUser.getUsername());
      
      dragonBallUserDaoInMemory.updateDragonBallUser(modifiedUser);
      DragonBallUser updatedUser = dragonBallUserDaoInMemory.getDragonBallUser("goku");
      
      assertEquals("1", updatedUser.getId().toString());
      assertEquals("goku", updatedUser.getUsername());
      assertEquals("gokuUpdated@dbz.com", updatedUser.getEmail());
      assertEquals(51, updatedUser.getAge());
      assertEquals(52, updatedUser.getPowerLevel());
      assertEquals(53, updatedUser.getStamina());
      
      dragonBallUserDaoInMemory.updateDragonBallUser(originalUser);
    } catch (DragonBallUserNotFoundException e) {
      e.printStackTrace();
      fail();
    }
  }
  
  /**
   *      Test for updating an existing user in the repository
   *      Exception flows
   *      
   * @since v0.03
   * @author nbrest
   * @throws DragonBallUserNotFoundException User defined exception
   */
  @Test
  public void updateDragonBallUserExceptionTest() throws DragonBallUserNotFoundException {
    LOGGER.info("****************** Executing updateDragonBallUserExceptionTest ****************");
    
    DragonBallUser dragonBallUser = new DragonBallUser(0L, "yukimura", "yukimura@pot.com", 
        10, 10, 10);
    thrown.expect(DragonBallUserNotFoundException.class);
    thrown.expectMessage("DragonBallUser with username yukimura was not found in the repository.");
    dragonBallUserDaoInMemory.updateDragonBallUser(dragonBallUser);
  }
  
  /**
   *      Test for deleting an existing user from the repository
   * 
   * @since v0.03
   * @author nbrest
   */
  @Test
  public void deleteDragonBallUserTest() {
    LOGGER.info("****************** Executing deleteDragonBallUserTest ******************");
    
    try {
      DragonBallUser userToDelete = new DragonBallUser(0L, "piccolo", "piccolo@dbz.com", 
          20, 20, 20);
      dragonBallUserDaoInMemory.createDragonBallUser(userToDelete);
      assertEquals(4, dragonBallUserDaoInMemory.getAllDragonBallUsers().size());
      dragonBallUserDaoInMemory.deleteDragonBallUser("piccolo");
      assertEquals(3, dragonBallUserDaoInMemory.getAllDragonBallUsers().size());
    } catch (DragonBallUserNotFoundException | DragonBallUserAlreadyExistsException e) {
      e.printStackTrace();
      fail();
    }
  }
  
  /**
   *      Test for deleting an existing user from the repository
   *      Exception flows
   * 
   * @since v0.03
   * @author nbrest
   * @throws DragonBallUserNotFoundException User defined exception
   */
  @Test
  public void deleteDragonBallUserExceptionTest() throws DragonBallUserNotFoundException {
    LOGGER.info("****************** Executing deleteDragonBallUserExceptionTest ****************");
    
    thrown.expect(DragonBallUserNotFoundException.class);
    thrown.expectMessage("DragonBallUser with username yukimura was not found in the repository.");
    dragonBallUserDaoInMemory.deleteDragonBallUser("yukimura");
  }
  
  /**
   *      Test for getting all the DragonBallUsers in the repository
   *      
   * @since v0.03
   * @author nbrest
   */
  @Test
  public void getAllDragonBallUsersTest() {
    LOGGER.info("****************** Executing getAllDragonBallUsersTest ******************");
        
    List<DragonBallUser> usersList = dragonBallUserDaoInMemory.getAllDragonBallUsers();
    
    LOGGER.info("dragonBallUsers.get(0): " + usersList.get(0).getUsername());
    LOGGER.info("dragonBallUsers.get(1): " + usersList.get(1).getUsername());
    LOGGER.info("dragonBallUsers.get(2): " + usersList.get(2).getUsername());

    assertEquals(3, dragonBallUserDaoInMemory.getAllDragonBallUsers().size());
    
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
}
