package ar.com.nicobrest.mobileinspections.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import ar.com.nicobrest.mobileinspections.exception.DragonBallUserAlreadyExistsException;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserForbiddenException;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserNotFoundException;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;
import ar.com.nicobrest.mobileinspections.service.DragonBallUserService;
import ar.com.nicobrest.mobileinspections.utils.JsonUtils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
//import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *        Unit tests for the DragonBallController class.
 *         
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContextController.xml", 
                                   "classpath:applicationContext-web.xml"})
@WebAppConfiguration
public class DragonBallControllerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DragonBallControllerTest.class);

  private MockMvc mockMvc;
  private static List<DragonBallUser> dragonBallUsersList;

  @Autowired
  @Qualifier("dragonBallUserService")
  private DragonBallUserService dragonBallUserServiceMock;

  @Autowired
  private WebApplicationContext webApplicationContext;
    
  /**
   *      Initializes test repositories.
   *      
   * @author nbrest
   * @throws Exception Throws unhandled exceptions
   */
  @BeforeClass
  public static void beforeClassTest() throws Exception {
    /* Actions to perform ONCE before all tests in the class */

    // Create test data to be returned by mock object dragonBallUserServiceMock
    DragonBallUser user1 = new DragonBallUser();
    user1.setId(101L);
    user1.setAge(49);
    user1.setEmail("gokuTestMock@dbz.com");
    user1.setUsername("gokuTestMock");
    user1.setPowerLevel(30);
    user1.setStamina(1000);
    
    DragonBallUser user2 = new DragonBallUser();
    user2.setId(102L);
    user2.setAge(29);
    user2.setEmail("gohanTestMock@dbz.com");
    user2.setUsername("gohanTestMock");
    user2.setPowerLevel(20);
    user2.setStamina(1000);
    
    DragonBallUser user3 = new DragonBallUser();
    user3.setId(103L);
    user3.setAge(19);
    user3.setEmail("gotenTestMock@dbz.com");
    user3.setUsername("gotenTestMock");
    user3.setPowerLevel(10);
    user3.setStamina(1000);
    
    dragonBallUsersList = new ArrayList<DragonBallUser>();
    dragonBallUsersList.add(user1);
    dragonBallUsersList.add(user2);
    dragonBallUsersList.add(user3);
  }

  /**
   *      Resets mock objects.
   *      
   * @author nbrest
   * @throws Exception MockMvc Exceptions
   */
  @Before
  public void beforeTest() throws Exception {
    /* Actions to perform before each test in the class */
    
    // Reset mock objects before each test
    Mockito.reset(dragonBallUserServiceMock);

    // Setup mockMvc test object
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  /**
   *      Clean up after each test.
   * 
   * @author nbrest
   */
  @After
  public void afterTest() {
    /* Actions to perform after each test */

  }

  /**
   *    Cleanup after all tests have executed.
   * 
   * @author nbrest
   * @throws Exception Throws unhandled exceptions
   */
  @AfterClass
  public static void afterClassTest() throws Exception {
    /* Actions to perform ONCE after all tests in the class */

  }

  /**
   *      /dragonball/modelAndView (GET)
   *      Test the endpoint /dragonball/modelAndView with the HTTP method
   *      GET. The service should respond with HTTP status 200 OK and a
   *      view defined in dragonball/modelAndView.jsp.
   *           
   * @author nbrest
   * @throws Exception Exceptions thrown by MockMvc
   */
  @Test
  public void getModelAndViewTest() throws Exception {
    LOGGER.info("****************** Executing getModelAndViewTest ******************");
    
    mockMvc.perform(get("/dragonball/modelAndView"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(view().name("dragonball/modelAndView"))
      .andExpect(forwardedUrl("/WEB-INF/jsp/dragonball/modelAndView.jsp"))
      .andExpect(model().attribute("name", isA(String.class)))
      .andExpect(model().attribute("name", equalTo("Goku")))
      .andExpect(model().attribute("message", equalTo("message: dragonball ModelAndView!")));
    
    verifyZeroInteractions(dragonBallUserServiceMock);
  }
  
  /**
   *      /dragonball/users (GET)
   *      Test the rest web service on the endpoint /dragonball/users with
   *      the HTTP method GET. The service should respond with HTTP status
   *      200 OK and a json array in the response body.
   *           
   * @author nbrest
   * @throws Exception Exceptions thrown by MockMvc
   */
  @Test
  public void getUsersTest() throws Exception {
    LOGGER.info("****************** Executing getUsersTest ******************");
    
    // Setup mock object dragonBallUserServiceMock 
    when(dragonBallUserServiceMock.getAllDragonBallUsers()).thenReturn(dragonBallUsersList);
 
    // Execute HTTP GET on the /dragonball/users endpoint
    mockMvc.perform(get("/dragonball/users"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType("application/json;charset=UTF-8"))
      .andExpect(jsonPath("$", hasSize(3)))
      .andExpect(jsonPath("$[0].id", equalTo(101)))
      .andExpect(jsonPath("$[0].username", equalTo("gokuTestMock")))
      .andExpect(jsonPath("$[0].email", equalTo("gokuTestMock@dbz.com")))
      .andExpect(jsonPath("$[0].age", equalTo(49)))
      .andExpect(jsonPath("$[0].powerLevel", equalTo(30)))
      .andExpect(jsonPath("$[0].stamina", equalTo(1000)))
      
      .andExpect(jsonPath("$[1].id", equalTo(102)))
      .andExpect(jsonPath("$[1].username", equalTo("gohanTestMock")))
      .andExpect(jsonPath("$[1].email", equalTo("gohanTestMock@dbz.com")))
      .andExpect(jsonPath("$[1].age", equalTo(29)))
      .andExpect(jsonPath("$[1].powerLevel", equalTo(20)))
      .andExpect(jsonPath("$[1].stamina", equalTo(1000)))
      
      .andExpect(jsonPath("$[2].id", equalTo(103)))
      .andExpect(jsonPath("$[2].username", equalTo("gotenTestMock")))
      .andExpect(jsonPath("$[2].email", equalTo("gotenTestMock@dbz.com")))
      .andExpect(jsonPath("$[2].age", equalTo(19)))
      .andExpect(jsonPath("$[2].powerLevel", equalTo(10)))
      .andExpect(jsonPath("$[2].stamina", equalTo(1000)));
        
    // Verify gotenDragonBallUserMock invocations
    verify(dragonBallUserServiceMock, times(1)).getAllDragonBallUsers();
    verifyNoMoreInteractions(dragonBallUserServiceMock);
  }

  /**
   *      /dragonball/users (GET)
   *      Test the rest web service on the endpoint /dragonball/users with
   *      the HTTP method GET. The request should return a Exception.
   *           
   * @author nbrest 
   */
  @Test
  public void getUsersExceptionTest() {
    LOGGER.info("****************** Executing getUsersExceptionTest ******************");

    // Execute HTTP GET on the /dragonball/users endpoint where it throws Exception
    try {
      mockMvc.perform(get("/dragonball/users?action=Exception"))
        .andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(view().name("error/error"))
        .andExpect(forwardedUrl("/WEB-INF/jsp/error/error.jsp"));
    } catch (Exception e) {
      e.printStackTrace();
    }
        
    // Verify gotenDragonBallUserMock invocations
    verifyZeroInteractions(dragonBallUserServiceMock); 
  }
  
  /**
   *      /dragonball/users (GET)
   *      Test the rest web service on the endpoint /dragonball/users with
   *      the HTTP method GET. The request should return a RuntimeException.
   *           
   * @author nbrest 
   */
  @Test
  public void getUsersRuntimeExceptionTest() {
    LOGGER.info("****************** Executing getUsersRuntimeExceptionTest ******************");
 
    // Execute HTTP GET on the /dragonball/users endpoint where it throws RuntimeException
    try {
      mockMvc.perform(get("/dragonball/users?action=RuntimeException"))
        .andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(view().name("error/error"))
        .andExpect(forwardedUrl("/WEB-INF/jsp/error/error.jsp"));
    } catch (Exception e) { 
      e.printStackTrace();
    }
      
    // Verify gotenDragonBallUserMock invocations
    verifyZeroInteractions(dragonBallUserServiceMock); 
  }
  
  /**
   *      /dragonball/users (GET)
   *      Test the rest web service on the endpoint /dragonball/users with
   *      the HTTP method GET. The request should return a DragonBallUserNotFoundException.
   *           
   * @author nbrest 
   */
  @Test
  public void getUsersDragonBallUserNotFoundExceptionTest() {
    LOGGER.info("****************** Executing getUsersDragonBallUserNotFoundExceptionTest ******");
  
    // Execute HTTP GET on the /dragonball/users endpoint where it throws 
    // DragonBallUserNotFoundException
    try {
      mockMvc.perform(get("/dragonball/users?action=DragonBallUserNotFoundException"))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(view().name("error/404"))
        .andExpect(forwardedUrl("/WEB-INF/jsp/error/404.jsp"));
    } catch (Exception e) { 
      e.printStackTrace();
    }
    
    // Verify gotenDragonBallUserMock invocations
    verifyZeroInteractions(dragonBallUserServiceMock); 
  }

  /**
   *      /dragonball/users (POST)
   *      Test creating a new DragonBallUser in the repository.
   * 
   * @author nbrest
   */
  @Test
  public void postUsersTest() {
    LOGGER.info("****************** Executing postUsersTest ******************");
    
    // Normal flow
    try {
      // Setup mock object dragonBallUserServiceMock 
      Mockito.doReturn(dragonBallUsersList.get(0).getId()).when(dragonBallUserServiceMock)
        .createDragonBallUser(dragonBallUsersList.get(0));
      when(dragonBallUserServiceMock.getDragonBallUser(dragonBallUsersList.get(0).getUsername()))
        .thenReturn(dragonBallUsersList.get(0));
      
      // Execute HTTP POST on the /dragonball/users endpoint
      mockMvc.perform(post("/dragonball/users")
          .contentType(MediaType.APPLICATION_JSON_UTF8)
          .content(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0)))
          )
          .andDo(print())
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
          .andExpect(content().bytes(
           JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0).getId())))
          .andExpect(content().string(dragonBallUsersList.get(0).getId().toString()));
      
      verify(dragonBallUserServiceMock, times(1)).createDragonBallUser(dragonBallUsersList.get(0));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught Exception. It should pass.");
    } 
  }
  
  /**
   *      /dragonball/users (POST)
   *      Test creating a new DragonBallUser in the repository
   *      The request should throw a DragonBallUserAlreadyExistsException.
   * 
   * @author nbrest
   */
  @Test
  public void postUsersDragonBallUserAlreadyExistsExceptionTest() {
    LOGGER.info(
        "****************** Executing postUsersDragonBallUserAlreadyExistsExceptionTest ********");
  
    // Exception flows
    try { 
      // Setup mock object dragonBallUserServiceMock 
      Mockito.doThrow(new DragonBallUserAlreadyExistsException("User already exists"))
      .when(dragonBallUserServiceMock).createDragonBallUser(dragonBallUsersList.get(0));
      
      // Execute HTTP POST on the /dragonball/users endpoint
      mockMvc.perform(post("/dragonball/users")
          .contentType(MediaType.APPLICATION_JSON_UTF8)
          .content(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0)))
          )
          .andDo(print())
          .andExpect(status().is4xxClientError())
          .andExpect(view().name("error/409"))
          .andExpect(forwardedUrl("/WEB-INF/jsp/error/409.jsp"));
      
      verify(dragonBallUserServiceMock, times(1)).createDragonBallUser(dragonBallUsersList.get(0));
    } catch (DragonBallUserAlreadyExistsException e) {
      fail("Caught DragonBallUserAlreadyExistsException. It Should have been handled in the mock");
    } catch (IOException e) {
      e.printStackTrace();
      fail("Caught IOException.");
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught Exception.");
    }
  }
  
  /**
   *      /dragonball/users/{username} (GET)
   *      Tests getting a specific user from the repository.
   * 
   * @author nbrest
   */
  @Test
  public void getUsersUsernameTest() {
    LOGGER.info("****************** Executing getUsersUsernameTest ******************");
    
    try {
      // Setup mock object dragonBallUserServiceMock
      when(dragonBallUserServiceMock.getDragonBallUser("gokuTestMock"))
        .thenReturn(dragonBallUsersList.get(0));

      // Execute HTTP GET on the /dragonball/users/{username} endpoint
      mockMvc.perform(get("/dragonball/users/gokuTestMock"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.id", equalTo(101)))
        .andExpect(jsonPath("$.username", equalTo("gokuTestMock")))
        .andExpect(jsonPath("$.email", equalTo("gokuTestMock@dbz.com")))
        .andExpect(jsonPath("$.age", equalTo(49)))
        .andExpect(jsonPath("$.powerLevel", equalTo(30)))
        .andExpect(jsonPath("$.stamina", equalTo(1000)));
    } catch (DragonBallUserNotFoundException e) {
      e.printStackTrace();
      fail("Caught DragonBallUserNotFoundException. It should pass.");
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught Exception. It should pass.");
    }
    
    // Exception flows
    try {
      // Reset mock objects before each test
      Mockito.reset(dragonBallUserServiceMock);
      
      // Setup mock object dragonBallUserServiceMock 
      Mockito.doThrow(new DragonBallUserNotFoundException("User trunks not found"))
      .when(dragonBallUserServiceMock).getDragonBallUser("trunks");
      
      // Execute HTTP GET on the /dragonball/users/{username} endpoint
      mockMvc.perform(get("/dragonball/users/trunks"))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(view().name("error/404"))
        .andExpect(forwardedUrl("/WEB-INF/jsp/error/404.jsp"));
      verify(dragonBallUserServiceMock, times(1)).getDragonBallUser("trunks");
    } catch (DragonBallUserNotFoundException e) {
      fail("Caught DragonBallUserNotFoundException. It should have been handled in the mock.");
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught Exception.");
    }
  }

  /**
   *      /dragonball/users/{username} (GET)
   *      Tests getting a specific user from the repository
   *      The request should throw a DragonBallUserNotFoundException.
   * 
   * @author nbrest
   */
  @Test
  public void getUsersUsernameDragonBallUserNotFoundExceptionTest() {
    LOGGER.info(
        "****************** Executing getUsersUsernameDragonBallUserNotFoundExceptionTest ******");
  
    // Exception flows
    try { 
      // Setup mock object dragonBallUserServiceMock 
      Mockito.doThrow(new DragonBallUserNotFoundException("User trunks not found"))
      .when(dragonBallUserServiceMock).getDragonBallUser("trunks");
      
      // Execute HTTP GET on the /dragonball/users/{username} endpoint
      mockMvc.perform(get("/dragonball/users/trunks"))
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(view().name("error/404"))
        .andExpect(forwardedUrl("/WEB-INF/jsp/error/404.jsp"));
      verify(dragonBallUserServiceMock, times(1)).getDragonBallUser("trunks");
    } catch (DragonBallUserNotFoundException e) {
      fail("Caught DragonBallUserNotFoundException. It should have been handled in the mock.");
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught Exception.");
    }
  }
  
  /**
   *      /dragonball/users/{username} (PUT)
   *      Tests updating an existing user in the repository.
   * 
   * @author nbrest
   */
  @Test
  public void putUsersUsernameTest() {
    LOGGER.info("****************** Executing putUsersUsernameTest ******************");
    
    // Normal flow
    try {
      // Setup mock object dragonBallUserServiceMock 
      Mockito.doNothing().when(dragonBallUserServiceMock)
        .updateDragonBallUser(dragonBallUsersList.get(0));
      when(dragonBallUserServiceMock.getDragonBallUser(dragonBallUsersList.get(0).getUsername()))
        .thenReturn(dragonBallUsersList.get(0));
      
      // Execute HTTP PUT on the /dragonball/users/{username} endpoint
      mockMvc.perform(put("/dragonball/users/" + dragonBallUsersList.get(0).getUsername())
          .contentType(MediaType.APPLICATION_JSON_UTF8)
          .content(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0)))
          )
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
          .andExpect(content().bytes(
           JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0).getId())))
          .andExpect(content().string(dragonBallUsersList.get(0).getId().toString()));
      
      verify(dragonBallUserServiceMock, times(1)).updateDragonBallUser(dragonBallUsersList.get(0));
      verify(dragonBallUserServiceMock, times(1)).getDragonBallUser(dragonBallUsersList.get(0)
          .getUsername());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught Exception. It should pass.");
    }
  }
  
  /**
   *      /dragonball/users/{username} (PUT)
   *      Tests updating an existing user in the repository
   *      The request should throw a DragonBallUserNotFoundException.
   * 
   * @author nbrest
   */
  @Test
  public void putUsersUsernameDragonBallUserNotFoundExceptionTest() {
    LOGGER.info(
        "****************** Executing putUsersUsernameDragonBallUserNotFoundExceptionTest ******");
  
    // Exception flows
    try { 
      // Setup mock object dragonBallUserServiceMock 
      Mockito.doThrow(new DragonBallUserNotFoundException("User not found"))
      .when(dragonBallUserServiceMock).updateDragonBallUser(dragonBallUsersList.get(0));
      
      // Execute HTTP PUT on the /dragonball/users/{username} endpoint
      mockMvc.perform(put("/dragonball/users/" + dragonBallUsersList.get(0).getUsername())
          .contentType(MediaType.APPLICATION_JSON_UTF8)
          .content(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0)))
          )
          .andDo(print())
          .andExpect(status().is4xxClientError())
          .andExpect(view().name("error/404"))
          .andExpect(forwardedUrl("/WEB-INF/jsp/error/404.jsp"));
      verify(dragonBallUserServiceMock, times(1)).updateDragonBallUser(dragonBallUsersList.get(0));
    } catch (DragonBallUserNotFoundException e) {
      fail("Caught DragonBallUserNotFoundException. It should have been handled in the mock.");
    } catch (IOException e) {
      e.printStackTrace();
      fail("Caught IOException.");
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught Exception.");
    }  
  }
  
  /**
   *      /dragonball/users/{username} (PUT)
   *      Tests updating an existing user in the repository
   *      The request should throw a DragonBallUserForbiddenException.
   * 
   * @author nbrest
   */
  @Test
  public void putUsersUsernameDragonBallUserForbiddenExceptionTest() {
    LOGGER.info(
        "****************** Executing putUsersUsernameDragonBallUserForbiddenExceptionTest ****");
  
    // Exception flows 
    try { 
      // Setup mock object dragonBallUserServiceMock 
      Mockito.doNothing().when(dragonBallUserServiceMock)
        .updateDragonBallUser(dragonBallUsersList.get(0));
      when(dragonBallUserServiceMock.getDragonBallUser(dragonBallUsersList.get(0).getUsername()))
        .thenReturn(dragonBallUsersList.get(0));
      
      // Execute HTTP PUT on the /dragonball/users/{username} endpoint
      mockMvc.perform(put("/dragonball/users/ryoma")
          .contentType(MediaType.APPLICATION_JSON_UTF8)
          .content(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0)))
          )
          .andDo(print())
          .andExpect(status().is4xxClientError())
          .andExpect(view().name("error/403"))
          .andExpect(forwardedUrl("/WEB-INF/jsp/error/403.jsp"));
      verify(dragonBallUserServiceMock, times(0)).updateDragonBallUser(dragonBallUsersList.get(0));
      verify(dragonBallUserServiceMock, times(0)).getDragonBallUser(dragonBallUsersList.get(0)
          .getUsername());
    } catch (DragonBallUserForbiddenException e) {
      fail("Caught DragonBallUserForbiddenException. It should have been handled in the mock.");
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught Exception.");
    }
  }
   
  /**
   *      /dragonball/users/{username} (DELETE)
   *      Tests for deleting an existing user from the repository.
   * 
   * @author nbrest
   */
  @Test
  public void deleteUsersUsernameTest() {
    LOGGER.info("****************** Executing deleteUsersUsernameTest ******************");
    
    // Normal flow
    try {
      // Setup mock object dragonBallUserServiceMock 
      when(dragonBallUserServiceMock.deleteDragonBallUser(dragonBallUsersList.get(0)
          .getId())).thenReturn(dragonBallUsersList.get(0));
      
      // Execute HTTP DELETE on the /dragonball/users/{id} endpoint
      mockMvc.perform(delete("/dragonball/users/" + dragonBallUsersList.get(0).getId()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
          .andExpect(content().bytes(
           JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0))));
      
      verify(dragonBallUserServiceMock, times(1)).deleteDragonBallUser(dragonBallUsersList.get(0)
          .getId());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught Exception. It should pass.");
    } 
  }
  
  /**
   *      /dragonball/users/{username} (DELETE)
   *      Tests for deleting an existing user from the repository
   *      The request should throw a DragonBallUserNotFoundException.
   * 
   * @author nbrest
   */
  @Test
  public void deleteUsersUsernameDragonBallUserNotFoundExceptionTest() {
    LOGGER.info(
        "****************** Executing deleteUsersUsernameDragonBallUserNotFoundExceptionTest **");
     
    // Exception flows
    try { 
      // Setup mock object dragonBallUserServiceMock 
      Mockito.doThrow(new DragonBallUserNotFoundException("User not found"))
          .when(dragonBallUserServiceMock).deleteDragonBallUser(dragonBallUsersList.get(0)
          .getId());
      
      // Execute HTTP DELETE on the /dragonball/users/{id} endpoint
      mockMvc.perform(delete("/dragonball/users/" + dragonBallUsersList.get(0).getId()))
          .andDo(print())
          .andExpect(status().is4xxClientError())
          .andExpect(view().name("error/404"))
          .andExpect(forwardedUrl("/WEB-INF/jsp/error/404.jsp"));
      verify(dragonBallUserServiceMock, times(1)).deleteDragonBallUser(dragonBallUsersList.get(0)
          .getId());
    } catch (DragonBallUserNotFoundException e) {
      fail("Caught DragonBallUserNotFoundException. It should have been handled in the mock.");
    } catch (IOException e) {
      e.printStackTrace();
      fail("Caught IOException.");
    } catch (Exception e) {
      e.printStackTrace();
      fail("Caught Exception.");
    }    
  }
  
  /*
  @Ignore("Disabled test example")
  @Test
  public void disabledTest() {
    // @Ignore disables the execution of the test 
    assertEquals("disabledTest not yet implemented", 0, 0);
 
    // Assert statements:
      
    // fail(message) assertTrue([message,] boolean condition)
    // assertFalse([message,] boolean condition) assertEquals([message,]
    // expected, actual) assertEquals([message,] expected, actual, tolerance)
    // assertNull([message,] object) assertNotNull([message,] object)
    // assertSame([message,] expected, actual) assertNotSame([message,]
    // expected, actual)
  }
  */
}
