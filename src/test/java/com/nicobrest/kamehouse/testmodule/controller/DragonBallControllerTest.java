package com.nicobrest.kamehouse.testmodule.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.main.controller.AbstractCrudControllerTest;
import com.nicobrest.kamehouse.main.exception.KameHouseException;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.service.DragonBallUserService;
import com.nicobrest.kamehouse.testmodule.testutils.DragonBallUserTestUtils;

import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
//import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.io.IOException;

/**
 * Unit tests for the DragonBallController class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class DragonBallControllerTest extends
    AbstractCrudControllerTest<DragonBallUser, DragonBallUserDto> {

  private static final String API_V1_DRAGONBALL_USERS =
      DragonBallUserTestUtils.API_V1_DRAGONBALL_USERS;
  private DragonBallUser dragonBallUser;

  @InjectMocks
  private DragonBallController dragonBallController;

  @Mock(name = "dragonBallUserService")
  private DragonBallUserService dragonBallUserServiceMock;

  /**
   * Actions to perform once before all tests.
   */
  @BeforeClass
  public static void beforeClassTest() {
    /* Initialization tasks that happen once for all tests. */
  }

  /**
   * Resets mock objects and test data.
   */
  @Before
  public void beforeTest() {
    testUtils = new DragonBallUserTestUtils();
    testUtils.initTestData();
    testUtils.setIds();
    dragonBallUser = testUtils.getSingleTestData();

    MockitoAnnotations.initMocks(this);
    Mockito.reset(dragonBallUserServiceMock);
    mockMvc = MockMvcBuilders.standaloneSetup(dragonBallController).build();
  }

  /**
   * Clean up after each test.
   */
  @After
  public void afterTest() {
    /* Actions to perform after each test */

  }

  /**
   * Cleanup after all tests have executed.
   */
  @AfterClass
  public static void afterClassTest() {
    /* Actions to perform ONCE after all tests in the class */
  }

  /**
   * /dragonball/users (POST) Test creating a new DragonBallUser in the
   * repository.
   */
  @Test
  public void createTest() throws Exception {
    createTest(API_V1_DRAGONBALL_USERS, dragonBallUserServiceMock);
  }

  /**
   * /dragonball/users (POST) Test creating a new DragonBallUser in the
   * repository that already exists.
   */
  @Test
  public void createConflictExceptionTest() throws Exception {
    createConflictExceptionTest(API_V1_DRAGONBALL_USERS, dragonBallUserServiceMock);
  }

  /**
   * /dragonball/users/{id} (GET) Tests getting a specific user from the
   * repository.
   */
  @Test
  public void readTest() throws Exception {
    readTest(API_V1_DRAGONBALL_USERS, dragonBallUserServiceMock, DragonBallUser.class);
  }

  /**
   * /dragonball/users (GET) Test the rest web service on the endpoint
   * /dragonball/users with the HTTP method GET. The service should respond with
   * HTTP status 200 OK and a json array in the response body.
   */
  @Test
  public void readAllTest() throws Exception {
    readAllTest(API_V1_DRAGONBALL_USERS, dragonBallUserServiceMock, DragonBallUser.class);
  }

  /**
   * /dragonball/users (GET) Test the rest web service on the endpoint
   * /dragonball/users with the parameter to throw an exception.
   */
  @Test
  public void readAllExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseException.class));

    executeGet(API_V1_DRAGONBALL_USERS + "?action=KameHouseException");
  }

  /**
   * /dragonball/users (GET) Test the rest web service on the endpoint
   * /dragonball/users with the parameter to throw an exception.
   */
  @Test
  public void readAllNotFoundExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseNotFoundException.class));

    executeGet(API_V1_DRAGONBALL_USERS + "?action=KameHouseNotFoundException");
  }

  /**
   * /dragonball/users/{id} (PUT) Tests updating an existing user in the
   * repository.
   */
  @Test
  public void updateTest() throws Exception {
    updateTest(API_V1_DRAGONBALL_USERS, dragonBallUserServiceMock);
  }

  /**
   * /dragonball/users/{id} (PUT) Tests failing to update an existing user in
   * the repository with bad request.
   */
  @Test
  public void updateInvalidPathId() throws IOException, Exception {
    updateInvalidPathId(API_V1_DRAGONBALL_USERS);
  }

  /**
   * /dragonball/users/{id} (PUT) Tests trying to update a non existing user in
   * the repository.
   */
  @Test
  public void updateNotFoundExceptionTest() throws Exception {
    updateNotFoundExceptionTest(API_V1_DRAGONBALL_USERS, dragonBallUserServiceMock);
  }

  /**
   * /dragonball/users/{id} (DELETE) Tests for deleting an existing user from
   * the repository.
   */
  @Test
  public void deleteTest() throws Exception {
    deleteTest(API_V1_DRAGONBALL_USERS, dragonBallUserServiceMock, DragonBallUser.class);
  }

  /**
   * /dragonball/users/{id} (DELETE) Tests for deleting an user not found in the
   * repository.
   */
  @Test
  public void deleteNotFoundExceptionTest() throws Exception {
    deleteNotFoundExceptionTest(API_V1_DRAGONBALL_USERS, dragonBallUserServiceMock);
  }

  /**
   * /dragonball/users/username/{username} (GET) Tests getting a specific user
   * from the repository.
   */
  @Test
  public void getByUsernameTest() throws Exception {
    when(dragonBallUserServiceMock.getByUsername(dragonBallUser.getUsername())).thenReturn(
        dragonBallUser);

    MockHttpServletResponse response = executeGet(API_V1_DRAGONBALL_USERS + "username/"
        + dragonBallUser.getUsername());
    DragonBallUser responseBody = getResponseBody(response, DragonBallUser.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributes(dragonBallUser, responseBody);
  }

  /**
   * /dragonball/users/username/{username} (GET) Tests user not found when
   * getting a specific user from the repository.
   */
  @Test
  public void getByUsernameNotFoundExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseNotFoundException.class));
    Mockito.doThrow(new KameHouseNotFoundException("")).when(dragonBallUserServiceMock)
        .getByUsername(DragonBallUserTestUtils.INVALID_USERNAME);

    executeGet(API_V1_DRAGONBALL_USERS + "username/" + DragonBallUserTestUtils.INVALID_USERNAME);
  }

  /**
   * /dragonball/users/emails/{email} (GET) Tests getting a specific user from
   * the repository by email.
   */
  @Test
  public void getByEmailTest() throws Exception {
    when(dragonBallUserServiceMock.getByEmail(dragonBallUser.getEmail())).thenReturn(
        dragonBallUser);

    MockHttpServletResponse response = executeGet(API_V1_DRAGONBALL_USERS + "emails/"
        + dragonBallUser.getEmail());
    DragonBallUser responseBody = getResponseBody(response, DragonBallUser.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributes(dragonBallUser, responseBody);
  }

  /**
   * /dragonball/model-and-view (GET) Test the endpoint
   * /dragonball/model-and-view with the HTTP method GET. The service should
   * respond with HTTP status 200 OK and a view defined in
   * dragonball/modelAndView.jsp.
   */
  @Test
  public void getModelAndViewTest() throws Exception {
    MockHttpServletResponse response = executeGet("/api/v1/dragonball/model-and-view");

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals("/test-module/jsp/dragonball/model-and-view", response.getForwardedUrl());
    verifyZeroInteractions(dragonBallUserServiceMock);
  }

  /*
   * @Ignore("Disabled test example")
   *
   * @Test public void disabledTest() { // @Ignore disables the execution of the
   * test assertEquals("disabledTest not yet implemented", 0, 0);
   *
   * // Assert statements:
   *
   * // fail(message) assertTrue([message,] boolean condition) //
   * assertFalse([message,] boolean condition) assertEquals([message,] //
   * expected, actual) assertEquals([message,] expected, actual, tolerance) //
   * assertNull([message,] object) assertNotNull([message,] object) //
   * assertSame([message,] expected, actual) assertNotSame([message,] //
   * expected, actual) }
   */
}
