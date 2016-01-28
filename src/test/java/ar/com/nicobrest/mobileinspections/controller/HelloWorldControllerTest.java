package ar.com.nicobrest.mobileinspections.controller;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @since v0.01 
 * @author nicolas.brest
 *
 *         Unit tests for the HelloWorldController class
 */
public class HelloWorldControllerTest {

  @BeforeClass
  public static void beforeClassTest() throws Exception {
    /* Actions to perform ONCE before all tests in the class */

  }

  @Before
  public void beforeTest() throws Exception {
    /* Actions to perform before each test in the class */

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
    assertEquals("getJsonTest not yet implemented", 0, 0);
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
