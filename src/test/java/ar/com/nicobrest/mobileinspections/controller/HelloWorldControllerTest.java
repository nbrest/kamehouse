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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import ar.com.nicobrest.mobileinspections.model.HelloWorldUser;
import ar.com.nicobrest.mobileinspections.service.HelloWorldUserService;

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
 * @since v0.02 
 * @author nbrest
 *
 *         Unit tests for the HelloWorldController class
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContextController.xml", 
                                   "classpath:applicationContext-web.xml"})
@WebAppConfiguration
public class HelloWorldControllerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldControllerTest.class);

  private MockMvc mockMvc;
  private static List<HelloWorldUser> helloWorldUsers;

  @Autowired
  @Qualifier("helloWorldUserService")
  private HelloWorldUserService helloWorldUserServiceMock;

  @Autowired
  private WebApplicationContext webApplicationContext;
    
  /**
   * @since v0.02
   * @author nbrest
   * @throws Exception Throws unhandled exceptions
   * 
   *      Initializes test repositories
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
   * @since v0.02
   * @author nbrest
   * @throws Exception MockMvc Exceptions
   * 
   *      Resets mock objects
   */
  @Before
  public void beforeTest() throws Exception {
    /* Actions to perform before each test in the class */
    
    // Reset mock objects before each test
    Mockito.reset(helloWorldUserServiceMock);

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
   * @since v0.02
   * @author nbrest
   * @throws Exception Exceptions thrown by MockMvc
   * 
   *           Test the endpoint /helloWorld/modelAndView with the HTTP method
   *           GET. The service should respond with HTTP status 200 OK and a
   *           view defined in helloWorld/modelAndView.jsp
   */
  @Test
  public void getModelAndViewSuccessTest() throws Exception {
    LOGGER.info("****************** Executing getModelAndViewSuccessTest ******************");
    
    mockMvc.perform(get("/helloWorld/modelAndView"))
      .andExpect(status().isOk())
      .andExpect(view().name("helloWorld/modelAndView"))
      .andExpect(forwardedUrl("/WEB-INF/jsp/helloWorld/modelAndView.jsp"))
      .andExpect(model().attribute("name", isA(String.class)))
      .andExpect(model().attribute("name", equalTo("Goku")))
      .andExpect(model().attribute("message", equalTo("message: HelloWorld ModelAndView!")));
    
    verifyZeroInteractions(helloWorldUserServiceMock);
  }
  
  /**
   * @since v0.02
   * @author nbrest
   * @throws Exception Exceptions thrown by MockMvc
   * 
   *           Test the rest web service on the endpoint /helloWorld/json with
   *           the HTTP method GET. The service should respond with HTTP status
   *           200 OK and a json array in the response body.
   */
  @Test
  public void getJsonSuccessTest() throws Exception {
    LOGGER.info("****************** Executing getJsonSuccessTest ******************");
    
    // Setup mock object helloWorldUserServiceMock 
    when(helloWorldUserServiceMock.getAllHelloWorldUsers()).thenReturn(helloWorldUsers);
 
    // Execute HTTP GET on the /helloWorld/json endpoint
    mockMvc.perform(get("/helloWorld/json"))
      .andExpect(status().isOk())
      .andExpect(content().contentType("application/json;charset=UTF-8"))
      .andExpect(jsonPath("$", hasSize(3)))
      .andExpect(jsonPath("$[0].username", equalTo("gokuTestMock")))
      .andExpect(jsonPath("$[0].email", equalTo("gokuTestMock@dbz.com")))
      .andExpect(jsonPath("$[0].age", equalTo(49)))
      .andExpect(jsonPath("$[1].username", equalTo("gohanTestMock")))
      .andExpect(jsonPath("$[1].email", equalTo("gohanTestMock@dbz.com")))
      .andExpect(jsonPath("$[1].age", equalTo(29)))
      .andExpect(jsonPath("$[2].username", equalTo("gotenTestMock")))
      .andExpect(jsonPath("$[2].email", equalTo("gotenTestMock@dbz.com")))
      .andExpect(jsonPath("$[2].age", equalTo(19)));
        
    // Verify gotenHelloWorldUserMock invocations
    verify(helloWorldUserServiceMock, times(1)).getAllHelloWorldUsers();
    verifyNoMoreInteractions(helloWorldUserServiceMock);
  }

  /**
   * @since v0.02
   * @author nbrest
   * @throws Exception Exceptions thrown by MockMvc
   * 
   *           Test the rest web service on the endpoint /helloWorld/json with
   *           the HTTP method GET. The service should respond with HTTP status
   *           404 and 500 for the different invocations throwing the correct
   *           Exception in each case
   */
  @Test
  public void getJsonExceptionTest() throws Exception {
    LOGGER.info("****************** Executing getJsonExceptionTest ******************");

    // Setup mock object helloWorldUserServiceMock 
    when(helloWorldUserServiceMock.getAllHelloWorldUsers()).thenReturn(helloWorldUsers);

    // Execute HTTP GET on the /helloWorld/json endpoint where it throws Exception
    mockMvc.perform(get("/helloWorld/json?action=Exception"))
      .andExpect(status().isInternalServerError())
      .andExpect(view().name("error/error"))
      .andExpect(forwardedUrl("/WEB-INF/jsp/error/error.jsp"));
    
    // Execute HTTP GET on the /helloWorld/json endpoint where it throws RuntimeException
    mockMvc.perform(get("/helloWorld/json?action=RuntimeException"))
      .andExpect(status().isInternalServerError())
      .andExpect(view().name("error/error"))
      .andExpect(forwardedUrl("/WEB-INF/jsp/error/error.jsp"));
    
    // Execute HTTP GET on the /helloWorld/json endpoint where it throws HelloWorldNotFoundException
    mockMvc.perform(get("/helloWorld/json?action=HelloWorldNotFoundException"))
      .andExpect(status().isNotFound())
      .andExpect(view().name("error/404"))
      .andExpect(forwardedUrl("/WEB-INF/jsp/error/404.jsp"));
    
    // Verify gotenHelloWorldUserMock invocations
    verifyZeroInteractions(helloWorldUserServiceMock); 
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
