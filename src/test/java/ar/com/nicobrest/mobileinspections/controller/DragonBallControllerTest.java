package ar.com.nicobrest.mobileinspections.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import ar.com.nicobrest.mobileinspections.model.DragonBallUser;
import ar.com.nicobrest.mobileinspections.service.DragonBallUserService;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 *         Unit tests for the DragonBallController class
 *         
 * @since v0.02 
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContextController.xml", 
                                   "classpath:applicationContext-web.xml"})
@WebAppConfiguration
public class DragonBallControllerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DragonBallControllerTest.class);

  private MockMvc mockMvc;
  private static List<DragonBallUser> dragonBallUsers;

  @Autowired
  @Qualifier("dragonBallUserService")
  private DragonBallUserService dragonBallUserServiceMock;

  @Autowired
  private WebApplicationContext webApplicationContext;
    
  /**
   *      Initializes test repositories
   *      
   * @since v0.02
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
    
    dragonBallUsers = new ArrayList<DragonBallUser>();
    dragonBallUsers.add(user1);
    dragonBallUsers.add(user2);
    dragonBallUsers.add(user3);
  }

  /**
   *      Resets mock objects
   *      
   * @since v0.02
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
   * @since v0.02
   * @author nbrest
   */
  @After
  public void afterTest() {
    /* Actions to perform after each test */

  }

  /**
   * @since v0.02
   * @author nbrest
   * @throws Exception Throws unhandled exceptions
   */
  @AfterClass
  public static void afterClassTest() throws Exception {
    /* Actions to perform ONCE after all tests in the class */

  }

  /**
   *           Test the endpoint /dragonball/modelAndView with the HTTP method
   *           GET. The service should respond with HTTP status 200 OK and a
   *           view defined in dragonball/modelAndView.jsp
   *           
   * @since v0.02
   * @author nbrest
   * @throws Exception Exceptions thrown by MockMvc
   */
  @Test
  public void getModelAndViewSuccessTest() throws Exception {
    LOGGER.info("****************** Executing getModelAndViewSuccessTest ******************");
    
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
   *           Test the rest web service on the endpoint /dragonball/users with
   *           the HTTP method GET. The service should respond with HTTP status
   *           200 OK and a json array in the response body.
   *           
   * @since v0.02
   * @author nbrest
   * @throws Exception Exceptions thrown by MockMvc
   */
  @Test
  public void getUsersSuccessTest() throws Exception {
    LOGGER.info("****************** Executing getUsersSuccessTest ******************");
    
    // Setup mock object dragonBallUserServiceMock 
    when(dragonBallUserServiceMock.getAllDragonBallUsers()).thenReturn(dragonBallUsers);
 
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
   *           Test the rest web service on the endpoint /dragonball/users with
   *           the HTTP method GET. The service should respond with HTTP status
   *           404 and 500 for the different invocations throwing the correct
   *           Exception in each case
   *           
   * @since v0.02
   * @author nbrest
   * @throws Exception Exceptions thrown by MockMvc
   */
  @Test
  public void getUsersExceptionTest() throws Exception {
    LOGGER.info("****************** Executing getUsersExceptionTest ******************");

    // Setup mock object dragonBallUserServiceMock 
    when(dragonBallUserServiceMock.getAllDragonBallUsers()).thenReturn(dragonBallUsers);

    // Execute HTTP GET on the /dragonball/users endpoint where it throws Exception
    mockMvc.perform(get("/dragonball/users?action=Exception"))
      .andDo(print())
      .andExpect(status().isInternalServerError())
      .andExpect(view().name("error/error"))
      .andExpect(forwardedUrl("/WEB-INF/jsp/error/error.jsp"));
    
    // Execute HTTP GET on the /dragonball/users endpoint where it throws RuntimeException
    mockMvc.perform(get("/dragonball/users?action=RuntimeException"))
      .andDo(print())
      .andExpect(status().isInternalServerError())
      .andExpect(view().name("error/error"))
      .andExpect(forwardedUrl("/WEB-INF/jsp/error/error.jsp"));
    
    // Execute HTTP GET on the /dragonball/users endpoint where it throws 
    // DragonBallUserNotFoundException
    mockMvc.perform(get("/dragonball/users?action=DragonBallUserNotFoundException"))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(view().name("error/404"))
      .andExpect(forwardedUrl("/WEB-INF/jsp/error/404.jsp"));
    
    // Verify gotenDragonBallUserMock invocations
    verifyZeroInteractions(dragonBallUserServiceMock); 
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
