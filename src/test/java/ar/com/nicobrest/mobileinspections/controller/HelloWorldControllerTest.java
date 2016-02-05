package ar.com.nicobrest.mobileinspections.controller;

import static org.junit.Assert.assertEquals; 

import ar.com.nicobrest.mobileinspections.model.HelloWorldUser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    //Mockito.reset(mockObject);

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
   * Test the rest web service on the endpoint /helloWorld/json with the HTTP
   * method GET. The service should respond with HTTP status 200 OK and a json
   * array in the response body.
   */
  @Test
  public void getJsonTest() {
    LOGGER.info("******************  Executing getJsonTest ******************");
    
    assertEquals("getJsonTest not yet implemented", 0, 0);
    mockMvc.toString();
  }

  /**
   * Test the endpoint /helloWorld/modelAndView with the HTTP method GET. The
   * service should respond with HTTP status 200 OK and a view defined in
   * helloWorld/modelAndView.jsp
   */
  @Test
  public void getModelAndViewTestSuccessfully() {
    assertEquals("getModelAndViewTest not yet implemented", 0, 0);
  }

  @Ignore("Disabled test example")
  @Test
  public void disabledTest() {
    /* @Ignore disables the execution of the test */
    assertEquals("disabledTest not yet implemented", 0, 0);

    /*
     * Assert statements:
     * 
     * fail(message) assertTrue([message,] boolean condition)
     * assertFalse([message,] boolean condition) assertEquals([message,]
     * expected, actual) assertEquals([message,] expected, actual, tolerance)
     * assertNull([message,] object) assertNotNull([message,] object)
     * assertSame([message,] expected, actual) assertNotSame([message,]
     * expected, actual)
     */
  }
}
