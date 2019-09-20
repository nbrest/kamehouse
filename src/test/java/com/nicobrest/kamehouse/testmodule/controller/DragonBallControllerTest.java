package com.nicobrest.kamehouse.testmodule.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.admin.testutils.ApplicationUserTestUtils;
import com.nicobrest.kamehouse.main.controller.AbstractControllerTest;
import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.main.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.main.exception.KameHouseException;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.service.DragonBallUserService;
import com.nicobrest.kamehouse.testmodule.service.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.testutils.DragonBallUserTestUtils;
import com.nicobrest.kamehouse.utils.JsonUtils;

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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.io.IOException;
import java.util.List;

/**
 * Unit tests for the DragonBallController class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class DragonBallControllerTest extends AbstractControllerTest {

  public static final String API_V1_DRAGONBALL_USERS =
      DragonBallUserTestUtils.API_V1_DRAGONBALL_USERS;
  private static DragonBallUser dragonBallUser;
  private static DragonBallUserDto dragonBallUserDto;
  private static List<DragonBallUser> dragonBallUsersList;

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
    DragonBallUserTestUtils.initTestData();
  }

  /**
   * Resets mock objects and test data.
   */
  @Before
  public void beforeTest() {
    DragonBallUserTestUtils.initTestData();
    DragonBallUserTestUtils.setIds();
    dragonBallUser = DragonBallUserTestUtils.getSingleTestData();
    dragonBallUserDto = DragonBallUserTestUtils.getTestDataDto();
    dragonBallUsersList = DragonBallUserTestUtils.getTestDataList();

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
   * /dragonball/model-and-view (GET) Test the endpoint
   * /dragonball/model-and-view with the HTTP method GET. The service should
   * respond with HTTP status 200 OK and a view defined in
   * dragonball/modelAndView.jsp.
   */
  @Test
  public void getModelAndViewTest() throws Exception {
    MockHttpServletResponse response = executeGet("/api/v1/dragonball/model-and-view");
    // ModelAndView responseBody = response.getContentAsByteArray(); -> Convert
    // from byte[] to Object

    verifyResponseStatus(response, HttpStatus.OK);
    // assertEquals("jsp/test-module/jsp/dragonball/model-and-view",
    // responseBody.getViewName());
    assertEquals("jsp/test-module/jsp/dragonball/model-and-view", response.getForwardedUrl());
    // assertEquals("Goku", responseBody.getModel().get("name"));
    // assertEquals("message: dragonball ModelAndView!",
    // responseBody.getModel().get("message"));
    verifyZeroInteractions(dragonBallUserServiceMock);
  }

  /**
   * /dragonball/users (GET) Test the rest web service on the endpoint
   * /dragonball/users with the HTTP method GET. The service should respond with
   * HTTP status 200 OK and a json array in the response body.
   */
  @Test
  public void getUsersTest() throws Exception {
    when(dragonBallUserServiceMock.getAllDragonBallUsers()).thenReturn(dragonBallUsersList);

    MockHttpServletResponse response = executeGet(API_V1_DRAGONBALL_USERS);
    List<DragonBallUser> responseBody = getResponseBodyList(response, DragonBallUser.class);

    verifyResponseStatus(response, HttpStatus.OK);
    verifyContentType(response, MediaType.APPLICATION_JSON_UTF8);
    assertEquals(dragonBallUsersList.size(), responseBody.size());
    assertEquals(dragonBallUsersList, responseBody);
    verify(dragonBallUserServiceMock, times(1)).getAllDragonBallUsers();
    verifyNoMoreInteractions(dragonBallUserServiceMock);
  }

  /**
   * /dragonball/users (GET) Test the rest web service on the endpoint
   * /dragonball/users with the parameter to throw an exception.
   */
  @Test
  public void getUsersExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseException.class));

    executeGet(API_V1_DRAGONBALL_USERS + "?action=KameHouseException");
  }

  /**
   * /dragonball/users (GET) Test the rest web service on the endpoint
   * /dragonball/users with the parameter to throw an exception.
   */
  @Test
  public void getUsersNotFoundExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseNotFoundException.class));

    executeGet(API_V1_DRAGONBALL_USERS + "?action=KameHouseNotFoundException");
  }

  /**
   * /dragonball/users (POST) Test creating a new DragonBallUser in the
   * repository.
   */
  @Test
  public void postUsersTest() throws Exception {
    Mockito.doReturn(dragonBallUser.getId()).when(dragonBallUserServiceMock).createDragonBallUser(
        dragonBallUserDto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(dragonBallUserDto);

    MockHttpServletResponse response = executePost(API_V1_DRAGONBALL_USERS, requestPayload);
    Long responseBody = getResponseBody(response, Long.class);

    verifyResponseStatus(response, HttpStatus.CREATED);
    assertEquals(dragonBallUserDto.getId(), responseBody);
    verify(dragonBallUserServiceMock, times(1)).createDragonBallUser(dragonBallUserDto);
  }

  /**
   * /dragonball/users (POST) Test creating a new DragonBallUser in the
   * repository that already exists.
   */
  @Test
  public void postUsersConflictExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseConflictException.class));
    Mockito.doThrow(new KameHouseConflictException("")).when(dragonBallUserServiceMock)
        .createDragonBallUser(dragonBallUserDto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(dragonBallUserDto);

    executePost(API_V1_DRAGONBALL_USERS, requestPayload);
  }

  /**
   * /dragonball/users/{id} (GET) Tests getting a specific user from the
   * repository.
   */
  @Test
  public void getUsersIdTest() throws Exception {
    when(dragonBallUserServiceMock.getDragonBallUser(dragonBallUser.getId())).thenReturn(
        dragonBallUser);

    MockHttpServletResponse response = executeGet(API_V1_DRAGONBALL_USERS + dragonBallUser
        .getId());
    DragonBallUser responseBody = getResponseBody(response, DragonBallUser.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals(dragonBallUser, responseBody);
  }

  /**
   * /dragonball/users/username/{username} (GET) Tests getting a specific user
   * from the repository.
   */
  @Test
  public void getUsersUsernameTest() throws Exception {
    when(dragonBallUserServiceMock.getDragonBallUser(dragonBallUser.getUsername())).thenReturn(
        dragonBallUser);

    MockHttpServletResponse response = executeGet(API_V1_DRAGONBALL_USERS + "username/"
        + dragonBallUser.getUsername());
    DragonBallUser responseBody = getResponseBody(response, DragonBallUser.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals(dragonBallUser, responseBody);
  }

  /**
   * /dragonball/users/username/{username} (GET) Tests user not found when
   * getting a specific user from the repository.
   */
  @Test
  public void getUsersUsernameNotFoundExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseNotFoundException.class));
    Mockito.doThrow(new KameHouseNotFoundException("")).when(dragonBallUserServiceMock)
        .getDragonBallUser(DragonBallUserTestUtils.INVALID_USERNAME);

    executeGet(API_V1_DRAGONBALL_USERS + "username/" + DragonBallUserTestUtils.INVALID_USERNAME);
  }

  /**
   * /dragonball/users/emails/{email} (GET) Tests getting a specific user from
   * the repository by email.
   */
  @Test
  public void getUsersUsernameByEmailTest() throws Exception {
    when(dragonBallUserServiceMock.getDragonBallUserByEmail(dragonBallUser.getEmail())).thenReturn(
        dragonBallUser);

    MockHttpServletResponse response = executeGet(API_V1_DRAGONBALL_USERS + "emails/"
        + dragonBallUser.getEmail());
    DragonBallUser responseBody = getResponseBody(response, DragonBallUser.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals(dragonBallUser, responseBody);
  }

  /**
   * /dragonball/users/{id} (PUT) Tests updating an existing user in the
   * repository.
   */
  @Test
  public void putUsersUsernameTest() throws Exception {
    Mockito.doNothing().when(dragonBallUserServiceMock).updateDragonBallUser(dragonBallUserDto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(dragonBallUserDto);

    MockHttpServletResponse response = executePut(API_V1_DRAGONBALL_USERS + dragonBallUserDto
        .getId(), requestPayload);

    verifyResponseStatus(response, HttpStatus.OK);
    verify(dragonBallUserServiceMock, times(1)).updateDragonBallUser(any());
  }

  /**
   * /dragonball/users/{id} (PUT) Tests trying to update a non existing user in
   * the repository.
   */
  @Test
  public void putUsersUsernameNotFoundExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseNotFoundException.class));
    Mockito.doThrow(new KameHouseNotFoundException("")).when(dragonBallUserServiceMock)
        .updateDragonBallUser(dragonBallUserDto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(dragonBallUserDto);

    executePut(API_V1_DRAGONBALL_USERS + dragonBallUserDto.getId(), requestPayload);
  }

  /**
   * /dragonball/users/{id} (PUT) Tests failing to update an existing user in
   * the repository with forbidden access.
   */
  @Test
  public void putUsersUsernameForbiddenExceptionTest() throws IOException, Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseBadRequestException.class));
    byte[] requestPayload = JsonUtils.toJsonByteArray(dragonBallUserDto);

    executePut(API_V1_DRAGONBALL_USERS + ApplicationUserTestUtils.INVALID_ID, requestPayload);
  }

  /**
   * /dragonball/users/{id} (DELETE) Tests for deleting an existing user from
   * the repository.
   */
  @Test
  public void deleteUsersIdTest() throws Exception {
    when(dragonBallUserServiceMock.deleteDragonBallUser(dragonBallUserDto.getId())).thenReturn(
        dragonBallUser);

    MockHttpServletResponse response = executeDelete(API_V1_DRAGONBALL_USERS + dragonBallUserDto
        .getId());
    DragonBallUser responseBody = getResponseBody(response, DragonBallUser.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals(dragonBallUser, responseBody);
    verify(dragonBallUserServiceMock, times(1)).deleteDragonBallUser(dragonBallUserDto.getId());
  }

  /**
   * /dragonball/users/{id} (DELETE) Tests for deleting an user not found in the
   * repository.
   */
  @Test
  public void deleteUsersIdNotFoundExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseNotFoundException.class));
    Mockito.doThrow(new KameHouseNotFoundException("")).when(dragonBallUserServiceMock)
        .deleteDragonBallUser(dragonBallUser.getId());

    executeDelete(API_V1_DRAGONBALL_USERS + dragonBallUser.getId());
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
