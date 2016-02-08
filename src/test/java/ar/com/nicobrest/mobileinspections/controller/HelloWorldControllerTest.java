package ar.com.nicobrest.mobileinspections.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertEquals;
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

/**
 * @since v0.02 
 * @author nicolas.brest
 *
 *         Unit tests for the HelloWorldController class
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml", 
                                   "classpath:applicationContext-web.xml"})
@WebAppConfiguration
public class HelloWorldControllerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldControllerTest.class);

  private MockMvc mockMvc;

  @Autowired
  private HelloWorldUser gohanHelloWorldUser;
  
  @Autowired
  @Qualifier("gotenHelloWorldUser")
  private HelloWorldUser gotenHelloWorldUserMock;

  @Autowired
  private WebApplicationContext webApplicationContext;
    
  @BeforeClass
  public static void beforeClassTest() throws Exception {
    /* Actions to perform ONCE before all tests in the class */

  }

  /**
   * @since v0.02
   * @author nbrest
   * @throws Exception MockMvc Exceptions
   */
  @Before
  public void beforeTest() throws Exception {
    /* Actions to perform before each test in the class */
    
    // Reset mock objects before each test
    Mockito.reset(gotenHelloWorldUserMock);

    // Setup mockMvc test object
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @After
  public void afterTest() {
    /* Actions to perform after each test */

  }

  @AfterClass
  public static void afterClassTest() throws Exception {
    /* Actions to perform ONCE after all tests in the class */

  }

  /**
   * Test the endpoint /helloWorld/modelAndView with the HTTP method GET. The
   * service should respond with HTTP status 200 OK and a view defined in
   * helloWorld/modelAndView.jsp
   * @throws Exception Exceptions thrown by MockMvc
   */
  @Test
  public void getModelAndViewTestSuccess() throws Exception {
    LOGGER.info("****************** Executing getModelAndViewTestSuccess ******************");
    
    mockMvc.perform(get("/helloWorld/modelAndView"))
      .andExpect(status().isOk())
      .andExpect(view().name("helloWorld/modelAndView"))
      .andExpect(forwardedUrl("/WEB-INF/jsp/helloWorld/modelAndView.jsp"))
      .andExpect(model().attribute("name", isA(String.class)))
      .andExpect(model().attribute("name", equalTo("Goku")))
      .andExpect(model().attribute("message", equalTo("message: HelloWorld ModelAndView!")));
    
    verifyZeroInteractions(gotenHelloWorldUserMock);
  }
  
  /**
   * Test the rest web service on the endpoint /helloWorld/json with the HTTP
   * method GET. The service should respond with HTTP status 200 OK and a json
   * array in the response body.
   * @throws Exception Exceptions thrown by MockMvc
   */
  @Test
  public void getJsonTestSuccess() throws Exception {
    LOGGER.info("****************** Executing getJsonTestSuccess ******************");
    
    LOGGER.info("gohanHelloWorldUser: " + gohanHelloWorldUser.getUsername() + " " 
        + gohanHelloWorldUser.getEmail() + " " + gohanHelloWorldUser.getAge());

    // Setup mock object gotenHelloWorldUserMock 
    when(gotenHelloWorldUserMock.getUsername()).thenReturn("gotenTestMock");
    when(gotenHelloWorldUserMock.getEmail()).thenReturn("gotenTestMock@dbz.com");
    when(gotenHelloWorldUserMock.getAge()).thenReturn(17);

    // Assert autowired mock bean gohanHelloWorldUser
    assertEquals("autowired mock bean gotenHelloWorldUserMock username should be gotenTestMock",
        "gotenTestMock", gotenHelloWorldUserMock.getUsername());
    assertEquals("autowired mock bean gotenHelloWorldUserMock email should be "
        + "gotenTestMock@dbz.com", "gotenTestMock@dbz.com", gotenHelloWorldUserMock.getEmail());
    assertEquals("autowired mock bean gotenHelloWorldUserMock age should be 17", 17,
        gotenHelloWorldUserMock.getAge());
    
    // Assert autowired test bean gohanHelloWorldUser
    assertEquals("autowired test bean gohanHelloWorldUser username should be gohanTest", 
        "gohanTest", gohanHelloWorldUser.getUsername());
    assertEquals("autowired test bean gohanHelloWorldUser email should be gohanTest@dbz.com", 
        "gohanTest@dbz.com", gohanHelloWorldUser.getEmail());
    assertEquals("autowired test bean gohanHelloWorldUser age should be 29", 29,
        gohanHelloWorldUser.getAge());
     
    // Execute HTTP GET on the /helloWorld/json endpoint
    mockMvc.perform(get("/helloWorld/json"))
      .andExpect(status().isOk())
      .andExpect(content().contentType("application/json;charset=UTF-8"))
      .andExpect(jsonPath("$", hasSize(3)))
      .andExpect(jsonPath("$[0].username", equalTo("goku")))
      .andExpect(jsonPath("$[0].email", equalTo("goku@dbz.com")))
      .andExpect(jsonPath("$[0].age", equalTo(21)))
      .andExpect(jsonPath("$[1].username", equalTo("gotenTestMock")))
      .andExpect(jsonPath("$[1].email", equalTo("gotenTestMock@dbz.com")))
      .andExpect(jsonPath("$[1].age", equalTo(17)))
      .andExpect(jsonPath("$[2].username", equalTo("gohanTest")))
      .andExpect(jsonPath("$[2].email", equalTo("gohanTest@dbz.com")))
      .andExpect(jsonPath("$[2].age", equalTo(29)));
    
    // Log gotenHelloWorldUserMock output behavior
    LOGGER.info("gotenHelloWorldUserMock: " + gotenHelloWorldUserMock.getUsername() + " " 
        + gotenHelloWorldUserMock.getEmail() + " " + gotenHelloWorldUserMock.getAge());
    
    // Verify gotenHelloWorldUserMock invocations
    verify(gotenHelloWorldUserMock, times(3)).getUsername();
    verify(gotenHelloWorldUserMock, times(3)).getEmail();
    verify(gotenHelloWorldUserMock, times(3)).getAge();
    verifyNoMoreInteractions(gotenHelloWorldUserMock);
  }

  /**
   * Test the rest web service on the endpoint /helloWorld/json with the HTTP
   * method GET. The service should respond with HTTP status 404 and 500 
   * for the different invocations throwing the correct Exception in each case
   * @throws Exception Exceptions thrown by MockMvc
   */
  @Test
  public void getJsonTestException() throws Exception {
    LOGGER.info("****************** Executing getJsonTestException ******************");
    
    LOGGER.info("gohanHelloWorldUser: " + gohanHelloWorldUser.getUsername() + " " 
        + gohanHelloWorldUser.getEmail() + " " + gohanHelloWorldUser.getAge());

    // Setup mock object gotenHelloWorldUserMock 
    when(gotenHelloWorldUserMock.getUsername()).thenReturn("gotenTestMock");
    when(gotenHelloWorldUserMock.getEmail()).thenReturn("gotenTestMock@dbz.com");
    when(gotenHelloWorldUserMock.getAge()).thenReturn(17);

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
    verify(gotenHelloWorldUserMock, times(3)).getUsername();
    verify(gotenHelloWorldUserMock, times(3)).getEmail();
    verify(gotenHelloWorldUserMock, times(3)).getAge();
    verifyNoMoreInteractions(gotenHelloWorldUserMock);
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
