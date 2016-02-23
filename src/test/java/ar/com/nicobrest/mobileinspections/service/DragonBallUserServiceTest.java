package ar.com.nicobrest.mobileinspections.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.com.nicobrest.mobileinspections.dao.DragonBallUserDao;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserAlreadyExistsException;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserNotFoundException;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;

import org.junit.Before;
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
 *         Unit tests for the DragonBallUserService class
 *         
 * @since v0.03 
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContextService.xml"})
public class DragonBallUserServiceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DragonBallUserServiceTest.class);

  private static List<DragonBallUser> dragonBallUsersList;
  
  @Autowired
  @Qualifier("dragonBallUserService")
  private DragonBallUserService dragonBallUserService;
  
  @Autowired
  @Qualifier("dragonBallUserDao")
  private DragonBallUserDao dragonBallUserDaoMock;

  /**
   *      Resets mock objects and initializes test repository
   *      
   * @since v0.03
   * @author nbrest 
   */
  @Before
  public void beforeTest() {
    /* Actions to perform before each test in the class */
    
    // Create test data to be returned by mock object dragonBallUserServiceMock
    DragonBallUser user1 = new DragonBallUser();
    user1.setId(1000L);
    user1.setAge(49);
    user1.setEmail("gokuTestMock@dbz.com");
    user1.setUsername("gokuTestMock");
    user1.setPowerLevel(30);
    user1.setStamina(1000);
    
    DragonBallUser user2 = new DragonBallUser();
    user2.setId(1001L);
    user2.setAge(29);
    user2.setEmail("gohanTestMock@dbz.com");
    user2.setUsername("gohanTestMock");
    user2.setPowerLevel(20);
    user2.setStamina(1000);
    
    DragonBallUser user3 = new DragonBallUser();
    user3.setId(1002L);
    user3.setAge(19);
    user3.setEmail("gotenTestMock@dbz.com");
    user3.setUsername("gotenTestMock");
    user3.setPowerLevel(10);
    user3.setStamina(1000);
    
    dragonBallUsersList = new ArrayList<DragonBallUser>();
    dragonBallUsersList.add(user1);
    dragonBallUsersList.add(user2);
    dragonBallUsersList.add(user3);
    
    // Reset mock objects before each test
    Mockito.reset(dragonBallUserDaoMock);
  }
  
  /**
   *      Test for calling the service to create a DragonBallUser in the repository
   * 
   * @since v0.03
   * @author nbrest
   * @throws DragonBallUserAlreadyExistsException User defined exception
   */
  @Test
  public void createDragonBallUserTest() throws DragonBallUserAlreadyExistsException {
    LOGGER.info("****************** Executing createDragonBallUserTest ******************");
    
    // Normal flow
    try {
      DragonBallUser userToAdd = new DragonBallUser(0L, "vegeta", "vegeta@dbz.com", 
          50, 50, 50);
      Mockito.doNothing().when(dragonBallUserDaoMock).createDragonBallUser(userToAdd);
      
      dragonBallUserService.createDragonBallUser(userToAdd);
      
      verify(dragonBallUserDaoMock, times(1)).createDragonBallUser(userToAdd);
    } catch (DragonBallUserAlreadyExistsException e) {
      e.printStackTrace();
      fail();
    }
    
    // Exception flow
    try {
      Mockito.doThrow(new DragonBallUserAlreadyExistsException("User goku already exists"))
        .when(dragonBallUserDaoMock).createDragonBallUser(dragonBallUsersList.get(0));
      
      dragonBallUserService.createDragonBallUser(dragonBallUsersList.get(0));
      
    } catch (DragonBallUserAlreadyExistsException e) {
      verify(dragonBallUserDaoMock, times(1)).createDragonBallUser(dragonBallUsersList.get(0));
    }
  }
  
  /**
   *      Test for calling the service to get a single DragonBallUser in the repository
   *      
   * @since v0.03
   * @author nbrest
   * @throws DragonBallUserNotFoundException User defined exception
   */
  @Test
  public void getDragonBallUserTest() throws DragonBallUserNotFoundException {
    LOGGER.info("****************** Executing getDragonBallUserTest ******************");
    
    // Normal flow
    try {
      when(dragonBallUserDaoMock.getDragonBallUser("gokuTestMock"))
        .thenReturn(dragonBallUsersList.get(0));
      
      DragonBallUser user = dragonBallUserService.getDragonBallUser("gokuTestMock");
      
      LOGGER.info("user: " + user.getUsername());
      
      assertNotNull(user);
      assertEquals("gokuTestMock", user.getUsername());
      verify(dragonBallUserDaoMock, times(1)).getDragonBallUser("gokuTestMock"); 
    } catch (DragonBallUserNotFoundException e) {
      e.printStackTrace();
      fail();
    }
    
    // Exception flow
    try {
      Mockito.doThrow(new DragonBallUserNotFoundException("User seiya not found"))
        .when(dragonBallUserDaoMock).getDragonBallUser("seiya");
      
      dragonBallUserService.getDragonBallUser("seiya");
      
    } catch (DragonBallUserNotFoundException e) {
      verify(dragonBallUserDaoMock, times(1)).getDragonBallUser("seiya");
    }
  }
  
  /**
   *      Test for calling the service to update an existing DragonBallUser in the repository
   *      
   * @since v0.03
   * @author nbrest
   * @throws DragonBallUserNotFoundException User defined exception
   */
  @Test
  public void updateDragonBallUserTest() throws DragonBallUserNotFoundException {
    LOGGER.info("****************** Executing updateDragonBallUserTest ******************");
    
    // Normal flow
    try {
      DragonBallUser userToUpdate = new DragonBallUser(0L, "goku", "gokuUpdated@dbz.com", 
          30, 30, 30);
      Mockito.doNothing().when(dragonBallUserDaoMock).updateDragonBallUser(userToUpdate);
      
      dragonBallUserService.updateDragonBallUser(userToUpdate);
      
      verify(dragonBallUserDaoMock, times(1)).updateDragonBallUser(userToUpdate);
    } catch (DragonBallUserNotFoundException e) {
      e.printStackTrace();
      fail();
    }
    
    // Exception flows
    DragonBallUser inexistentUserToUpdate = new DragonBallUser(0L, "sanada", "sanada@pot.com", 
        30, 30, 30);
    try {
      Mockito.doThrow(new DragonBallUserNotFoundException("User sanada doesn´t exist"))
        .when(dragonBallUserDaoMock).updateDragonBallUser(inexistentUserToUpdate);
      
      dragonBallUserService.updateDragonBallUser(inexistentUserToUpdate);
      
    } catch (DragonBallUserNotFoundException e) {
      verify(dragonBallUserDaoMock, times(1)).updateDragonBallUser(inexistentUserToUpdate);
    }
  }
  
  /**
   *      Test for calling the service to delete an existing user in the repository
   *     
   * @since v0.03
   * @author nbrest
   * @throws DragonBallUserNotFoundException User defined exception
   */
  @Test
  public void deleteDragonBallUserTest() throws DragonBallUserNotFoundException {
    LOGGER.info("****************** Executing deleteDragonBallUserTest ******************");
    
    // Normal flow
    try {
      Mockito.doNothing().when(dragonBallUserDaoMock).deleteDragonBallUser("goku");
      
      dragonBallUserService.deleteDragonBallUser("goku");
      
      verify(dragonBallUserDaoMock, times(1)).deleteDragonBallUser("goku");
    } catch (DragonBallUserNotFoundException e) {
      e.printStackTrace();
      fail();
    }
    
    // Exception flows
    try {
      Mockito.doThrow(new DragonBallUserNotFoundException("User ryoma doesn´t exist"))
        .when(dragonBallUserDaoMock).deleteDragonBallUser("ryoma");
      
      dragonBallUserService.deleteDragonBallUser("ryoma");
      
    } catch (DragonBallUserNotFoundException e) {
      verify(dragonBallUserDaoMock, times(1)).deleteDragonBallUser("ryoma");
    }
  }
  
  /**
   *      Test for calling the service to get all the DragonBallUsers in the repository
   *      
   * @since v0.03
   * @author nbrest
   */
  @Test
  public void getAllDragonBallUsersTest() {
    LOGGER.info("****************** Executing getAllDragonBallUsersTest ******************");
    
    when(dragonBallUserDaoMock.getAllDragonBallUsers()).thenReturn(dragonBallUsersList);
    
    List<DragonBallUser> usersList = dragonBallUserService.getAllDragonBallUsers();
    
    LOGGER.info("dragonBallUsers.get(0): " + usersList.get(0).getUsername());
    LOGGER.info("dragonBallUsers.get(1): " + usersList.get(1).getUsername());
    LOGGER.info("dragonBallUsers.get(2): " + usersList.get(2).getUsername());
    
    assertEquals("gokuTestMock", usersList.get(0).getUsername());
    assertEquals("gokuTestMock@dbz.com", usersList.get(0).getEmail());
    assertEquals(49, usersList.get(0).getAge());
    assertEquals("1000", usersList.get(0).getId().toString());
    assertEquals(30, usersList.get(0).getPowerLevel());
    assertEquals(1000, usersList.get(0).getStamina());
    
    assertEquals("gohanTestMock", usersList.get(1).getUsername());
    assertEquals("gohanTestMock@dbz.com", usersList.get(1).getEmail());
    assertEquals(29, usersList.get(1).getAge());
    assertEquals("1001", usersList.get(1).getId().toString());
    assertEquals(20, usersList.get(1).getPowerLevel());
    assertEquals(1000, usersList.get(1).getStamina());
    
    assertEquals("gotenTestMock", usersList.get(2).getUsername());
    assertEquals("gotenTestMock@dbz.com", usersList.get(2).getEmail());
    assertEquals(19, usersList.get(2).getAge());
    assertEquals("1002", usersList.get(2).getId().toString());
    assertEquals(10, usersList.get(2).getPowerLevel());
    assertEquals(1000, usersList.get(2).getStamina());
    
    verify(dragonBallUserDaoMock, times(1)).getAllDragonBallUsers();
  }
}
