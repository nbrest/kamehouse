package com.nicobrest.kamehouse.testmodule.controller;

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

import com.nicobrest.kamehouse.main.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.testmodule.controller.DragonBallController;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.service.DragonBallUserService;
import com.nicobrest.kamehouse.testutils.JsonUtils;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.LinkedList;
import java.util.List;

/**
 * Unit tests for the DragonBallController class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class DragonBallControllerTest {

  private MockMvc mockMvc;

  private static List<DragonBallUser> dragonBallUsersList;

  @InjectMocks
  private DragonBallController dragonBallController;

  @Mock(name = "dragonBallUserService")
  private DragonBallUserService dragonBallUserServiceMock;

  /**
   * Initializes test repositories.
   */
  @BeforeClass
  public static void beforeClassTest() {
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

    dragonBallUsersList = new LinkedList<DragonBallUser>();
    dragonBallUsersList.add(user1);
    dragonBallUsersList.add(user2);
    dragonBallUsersList.add(user3);
  }

  /**
   * Resets mock objects.
   */
  @Before
  public void beforeTest() {
    /* Actions to perform before each test in the class */

    // Reset mock objects before each test
    MockitoAnnotations.initMocks(this);
    Mockito.reset(dragonBallUserServiceMock);

    // Setup mockMvc test object
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
   * /dragonball/model-and-view (GET) Test the endpoint /dragonball/model-and-view
   * with the HTTP method GET. The service should respond with HTTP status 200 OK
   * and a view defined in dragonball/modelAndView.jsp.
   */
  @Test
  public void getModelAndViewTest() {

    try {
      ResultActions requestResult = mockMvc.perform(get("/api/v1/dragonball/model-and-view"))
          .andDo(print());
      requestResult.andExpect(status().isOk());
      requestResult.andExpect(view().name("jsp/test-module/jsp/dragonball/model-and-view"));
      requestResult.andExpect(forwardedUrl("jsp/test-module/jsp/dragonball/model-and-view"));
      requestResult.andExpect(model().attribute("name", isA(String.class)));
      requestResult.andExpect(model().attribute("name", equalTo("Goku")));
      requestResult
          .andExpect(model().attribute("message", equalTo("message: dragonball ModelAndView!")));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
    verifyZeroInteractions(dragonBallUserServiceMock);
  }

  /**
   * /dragonball/users (GET) Test the rest web service on the endpoint
   * /dragonball/users with the HTTP method GET. The service should respond with
   * HTTP status 200 OK and a json array in the response body.
   */
  @Test
  public void getUsersTest() {

    // Setup mock object dragonBallUserServiceMock
    when(dragonBallUserServiceMock.getAllDragonBallUsers()).thenReturn(dragonBallUsersList);

    // Execute HTTP GET on the /dragonball/users endpoint
    try {
      ResultActions requestResult = mockMvc.perform(get("/api/v1/dragonball/users")).andDo(print());
      requestResult.andExpect(status().isOk());
      requestResult.andExpect(content().contentType("application/json;charset=UTF-8"));
      requestResult.andExpect(jsonPath("$", hasSize(3)));
      requestResult.andExpect(jsonPath("$[0].id", equalTo(101)));
      requestResult.andExpect(jsonPath("$[0].username", equalTo("gokuTestMock")));
      requestResult.andExpect(jsonPath("$[0].email", equalTo("gokuTestMock@dbz.com")));
      requestResult.andExpect(jsonPath("$[0].age", equalTo(49)));
      requestResult.andExpect(jsonPath("$[0].powerLevel", equalTo(30)));
      requestResult.andExpect(jsonPath("$[0].stamina", equalTo(1000)));

      requestResult.andExpect(jsonPath("$[1].id", equalTo(102)));
      requestResult.andExpect(jsonPath("$[1].username", equalTo("gohanTestMock")));
      requestResult.andExpect(jsonPath("$[1].email", equalTo("gohanTestMock@dbz.com")));
      requestResult.andExpect(jsonPath("$[1].age", equalTo(29)));
      requestResult.andExpect(jsonPath("$[1].powerLevel", equalTo(20)));
      requestResult.andExpect(jsonPath("$[1].stamina", equalTo(1000)));

      requestResult.andExpect(jsonPath("$[2].id", equalTo(103)));
      requestResult.andExpect(jsonPath("$[2].username", equalTo("gotenTestMock")));
      requestResult.andExpect(jsonPath("$[2].email", equalTo("gotenTestMock@dbz.com")));
      requestResult.andExpect(jsonPath("$[2].age", equalTo(19)));
      requestResult.andExpect(jsonPath("$[2].powerLevel", equalTo(10)));
      requestResult.andExpect(jsonPath("$[2].stamina", equalTo(1000)));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
    // Verify gotenDragonBallUserMock invocations
    verify(dragonBallUserServiceMock, times(1)).getAllDragonBallUsers();
    verifyNoMoreInteractions(dragonBallUserServiceMock);
  }

  /**
   * /dragonball/users (GET) Test the rest web service on the endpoint
   * /dragonball/users with the parameter to throw an exception.
   */
  @Test
  public void getUsersExceptionTest() {

    try {
      ResultActions requestResult = mockMvc
          .perform(get("/api/v1/dragonball/users?action=Exception")).andDo(print());
      requestResult.andExpect(status().isInternalServerError());
      requestResult.andExpect(view().name("error/error"));
      requestResult.andExpect(forwardedUrl("/WEB-INF/jsp/error/error.jsp"));
      fail("Expected an exception to be thrown.");
    } catch (Exception e) {
      // Do nothing. An exception was expected.
    }
    // Verify gotenDragonBallUserMock invocations
    verifyZeroInteractions(dragonBallUserServiceMock);
  }

  /**
   * /dragonball/users (GET) Test the rest web service on the endpoint
   * /dragonball/users with the parameter to throw an exception.
   */
  @Test
  public void getUsersRuntimeExceptionTest() {

    try {
      ResultActions requestResult = mockMvc
          .perform(get("/api/v1/dragonball/users?action=RuntimeException")).andDo(print());
      requestResult.andExpect(status().isInternalServerError());
      requestResult.andExpect(view().name("error/error"));
      requestResult.andExpect(forwardedUrl("/WEB-INF/jsp/error/error.jsp"));
      fail("Expected an exception to be thrown.");
    } catch (Exception e) {
      // Do nothing. Expected an exception
    }
    // Verify gotenDragonBallUserMock invocations
    verifyZeroInteractions(dragonBallUserServiceMock);
  }

  /**
   * /dragonball/users (GET) Test the rest web service on the endpoint
   * /dragonball/users with the parameter to throw an exception.
   */
  @Test
  public void getUsersNotFoundExceptionTest() {

    try {
      ResultActions requestResult = mockMvc
          .perform(get("/api/v1/dragonball/users?action=KameHouseNotFoundException"))
          .andDo(print());
      requestResult.andExpect(status().isNotFound());
      fail("Expected an exception to be thrown.");
    } catch (Exception e) {
      // Do nothing. Expected an exception
    }
    // Verify gotenDragonBallUserMock invocations
    verifyZeroInteractions(dragonBallUserServiceMock);
  }

  /**
   * /dragonball/users (POST) Test creating a new DragonBallUser in the
   * repository.
   */
  @Test
  public void postUsersTest() {

    // Normal flow
    try {
      // Setup mock object dragonBallUserServiceMock
      Mockito.doReturn(dragonBallUsersList.get(0).getId()).when(dragonBallUserServiceMock)
          .createDragonBallUser(dragonBallUsersList.get(0));
      when(dragonBallUserServiceMock.getDragonBallUser(dragonBallUsersList.get(0).getUsername()))
          .thenReturn(dragonBallUsersList.get(0));

      // Execute HTTP POST on the /dragonball/users endpoint
      ResultActions requestResult = mockMvc
          .perform(post("/api/v1/dragonball/users").contentType(MediaType.APPLICATION_JSON_UTF8)
              .content(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0))))
          .andDo(print());
      requestResult.andExpect(status().isCreated());
      requestResult.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
      requestResult.andExpect(
          content().bytes(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0).getId())));
      requestResult.andExpect(content().string(dragonBallUsersList.get(0).getId().toString()));

      verify(dragonBallUserServiceMock, times(1)).createDragonBallUser(dragonBallUsersList.get(0));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * /dragonball/users (POST) Test creating a new DragonBallUser in the repository
   * that already exists.
   */
  @Test
  public void postUsersConflictExceptionTest() {

    // Exception flows
    try {
      // Setup mock object dragonBallUserServiceMock
      Mockito.doThrow(new KameHouseConflictException("User already exists"))
          .when(dragonBallUserServiceMock).createDragonBallUser(dragonBallUsersList.get(0));

      // Execute HTTP POST on the /dragonball/users endpoint
      ResultActions requestResult = mockMvc
          .perform(post("/api/v1/dragonball/users").contentType(MediaType.APPLICATION_JSON_UTF8)
              .content(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0))))
          .andDo(print());
      requestResult.andExpect(status().is4xxClientError());

      verify(dragonBallUserServiceMock, times(1)).createDragonBallUser(dragonBallUsersList.get(0));
    } catch (Exception e) {
      if (!(e.getCause() instanceof KameHouseConflictException)) {
        e.printStackTrace();
        fail("Unexpected exception thrown.");
      }
    }
  }

  /**
   * /dragonball/users/{id} (GET) Tests getting a specific user from the
   * repository.
   */
  @Test
  public void getUsersIdTest() {

    try {
      when(dragonBallUserServiceMock.getDragonBallUser(101L))
          .thenReturn(dragonBallUsersList.get(0));

      ResultActions requestResult = mockMvc.perform(get("/api/v1/dragonball/users/101"))
          .andDo(print());
      requestResult.andExpect(status().isOk());
      requestResult.andExpect(content().contentType("application/json;charset=UTF-8"));
      requestResult.andExpect(jsonPath("$.id", equalTo(101)));
      requestResult.andExpect(jsonPath("$.username", equalTo("gokuTestMock")));
      requestResult.andExpect(jsonPath("$.email", equalTo("gokuTestMock@dbz.com")));
      requestResult.andExpect(jsonPath("$.age", equalTo(49)));
      requestResult.andExpect(jsonPath("$.powerLevel", equalTo(30)));
      requestResult.andExpect(jsonPath("$.stamina", equalTo(1000)));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * /dragonball/users/username/{username} (GET) Tests getting a specific user
   * from the repository.
   */
  @Test
  public void getUsersUsernameTest() {

    try {
      when(dragonBallUserServiceMock.getDragonBallUser("gokuTestMock"))
          .thenReturn(dragonBallUsersList.get(0));

      ResultActions requestResult = mockMvc
          .perform(get("/api/v1/dragonball/users/username/gokuTestMock")).andDo(print());
      requestResult.andExpect(status().isOk());
      requestResult.andExpect(content().contentType("application/json;charset=UTF-8"));
      requestResult.andExpect(jsonPath("$.id", equalTo(101)));
      requestResult.andExpect(jsonPath("$.username", equalTo("gokuTestMock")));
      requestResult.andExpect(jsonPath("$.email", equalTo("gokuTestMock@dbz.com")));
      requestResult.andExpect(jsonPath("$.age", equalTo(49)));
      requestResult.andExpect(jsonPath("$.powerLevel", equalTo(30)));
      requestResult.andExpect(jsonPath("$.stamina", equalTo(1000)));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }

    // Exception flows
    try {
      Mockito.reset(dragonBallUserServiceMock);
      Mockito.doThrow(new KameHouseNotFoundException("User trunks not found"))
          .when(dragonBallUserServiceMock).getDragonBallUser("trunks");

      ResultActions requestResult = mockMvc.perform(get("/api/v1/dragonball/users/username/trunks"))
          .andDo(print());
      requestResult.andExpect(status().is4xxClientError());
      verify(dragonBallUserServiceMock, times(1)).getDragonBallUser("trunks");
    } catch (Exception e) {
      if (!(e.getCause() instanceof KameHouseNotFoundException)) {
        e.printStackTrace();
        fail("Unexpected exception thrown.");
      }
    }
  }

  /**
   * /dragonball/users/username/{username} (GET) Tests user not found when getting
   * a specific user from the repository.
   */
  @Test
  public void getUsersUsernameNotFoundExceptionTest() {

    // Exception flows
    try {
      Mockito.doThrow(new KameHouseNotFoundException("User trunks not found"))
          .when(dragonBallUserServiceMock).getDragonBallUser("trunks");

      ResultActions requestResult = mockMvc.perform(get("/api/v1/dragonball/users/username/trunks"))
          .andDo(print());
      requestResult.andExpect(status().is4xxClientError());
      verify(dragonBallUserServiceMock, times(1)).getDragonBallUser("trunks");
    } catch (Exception e) {
      if (!(e.getCause() instanceof KameHouseNotFoundException)) {
        e.printStackTrace();
        fail("Unexpected exception thrown.");
      }
    }
  }

  /**
   * /dragonball/users/emails/{email} (GET) Tests getting a specific user from the
   * repository by email.
   */
  @Test
  public void getUsersUsernameByEmailTest() {

    try {
      // Setup mock object dragonBallUserServiceMock
      when(dragonBallUserServiceMock.getDragonBallUserByEmail("gokuTestMock@dbz.com"))
          .thenReturn(dragonBallUsersList.get(0));

      // Execute HTTP GET on the /dragonball/users/{username} endpoint
      ResultActions requestResult = mockMvc
          .perform(get("/api/v1/dragonball/users/emails/gokuTestMock@dbz.com")).andDo(print());
      requestResult.andExpect(status().isOk());
      requestResult.andExpect(content().contentType("application/json;charset=UTF-8"));
      requestResult.andExpect(jsonPath("$.id", equalTo(101)));
      requestResult.andExpect(jsonPath("$.username", equalTo("gokuTestMock")));
      requestResult.andExpect(jsonPath("$.email", equalTo("gokuTestMock@dbz.com")));
      requestResult.andExpect(jsonPath("$.age", equalTo(49)));
      requestResult.andExpect(jsonPath("$.powerLevel", equalTo(30)));
      requestResult.andExpect(jsonPath("$.stamina", equalTo(1000)));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * /dragonball/users/{id} (PUT) Tests updating an existing user in the
   * repository.
   */
  @Test
  public void putUsersUsernameTest() {

    // Normal flow
    try {
      // Setup mock object dragonBallUserServiceMock
      Mockito.doNothing().when(dragonBallUserServiceMock)
          .updateDragonBallUser(dragonBallUsersList.get(0));
      when(dragonBallUserServiceMock.getDragonBallUser(dragonBallUsersList.get(0).getUsername()))
          .thenReturn(dragonBallUsersList.get(0));

      // Execute HTTP PUT on the /dragonball/users/{id} endpoint
      ResultActions requestResult = mockMvc
          .perform(put("/api/v1/dragonball/users/" + dragonBallUsersList.get(0).getId())
              .contentType(MediaType.APPLICATION_JSON_UTF8)
              .content(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0))))
          .andDo(print());
      requestResult.andExpect(status().isOk());

      verify(dragonBallUserServiceMock, times(1)).updateDragonBallUser(dragonBallUsersList.get(0));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * /dragonball/users/{id} (PUT) Tests trying to update a non existing user in
   * the repository.
   */
  @Test
  public void putUsersUsernameNotFoundExceptionTest() {

    // Exception flows
    try {
      // Setup mock object dragonBallUserServiceMock
      Mockito.doThrow(new KameHouseNotFoundException("User not found"))
          .when(dragonBallUserServiceMock).updateDragonBallUser(dragonBallUsersList.get(0));

      // Execute HTTP PUT on the /dragonball/users/{id} endpoint
      ResultActions requestResult = mockMvc
          .perform(put("/api/v1/dragonball/users/" + dragonBallUsersList.get(0).getId())
              .contentType(MediaType.APPLICATION_JSON_UTF8)
              .content(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0))))
          .andDo(print());
      requestResult.andExpect(status().is4xxClientError());
      verify(dragonBallUserServiceMock, times(1)).updateDragonBallUser(dragonBallUsersList.get(0));
    } catch (Exception e) {
      if (!(e.getCause() instanceof KameHouseNotFoundException)) {
        e.printStackTrace();
        fail("Unexpected exception thrown.");
      }
    }
  }

  /**
   * /dragonball/users/{id} (PUT) Tests failing to update an existing user in the
   * repository with forbidden access.
   */
  @Test
  public void putUsersUsernameForbiddenExceptionTest() {

    // Exception flows
    try {
      // Setup mock object dragonBallUserServiceMock
      Mockito.doNothing().when(dragonBallUserServiceMock)
          .updateDragonBallUser(dragonBallUsersList.get(0));
      when(dragonBallUserServiceMock.getDragonBallUser(dragonBallUsersList.get(0).getUsername()))
          .thenReturn(dragonBallUsersList.get(0));

      // Execute HTTP PUT on the /dragonball/users/{id} endpoint
      ResultActions requestResult = mockMvc
          .perform(put("/dragonball/users/987").contentType(MediaType.APPLICATION_JSON_UTF8)
              .content(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0))))
          .andDo(print());
      requestResult.andExpect(status().is4xxClientError());
      verify(dragonBallUserServiceMock, times(0)).updateDragonBallUser(dragonBallUsersList.get(0));
      verify(dragonBallUserServiceMock, times(0))
          .getDragonBallUser(dragonBallUsersList.get(0).getUsername());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * /dragonball/users/{id} (DELETE) Tests for deleting an existing user from the
   * repository.
   */
  @Test
  public void deleteUsersUsernameTest() {

    // Normal flow
    try {
      // Setup mock object dragonBallUserServiceMock
      when(dragonBallUserServiceMock.deleteDragonBallUser(dragonBallUsersList.get(0).getId()))
          .thenReturn(dragonBallUsersList.get(0));

      // Execute HTTP DELETE on the /dragonball/users/{id} endpoint
      ResultActions requestResult = mockMvc
          .perform(delete("/api/v1/dragonball/users/" + dragonBallUsersList.get(0).getId()))
          .andDo(print());
      requestResult.andExpect(status().isOk());
      requestResult.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
      requestResult
          .andExpect(content().bytes(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0))));

      verify(dragonBallUserServiceMock, times(1))
          .deleteDragonBallUser(dragonBallUsersList.get(0).getId());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * /dragonball/users/{id} (DELETE) Tests for deleting an user not found in the
   * repository.
   */
  @Test
  public void deleteUsersUsernameNotFoundExceptionTest() {

    // Exception flows
    try {
      // Setup mock object dragonBallUserServiceMock
      Mockito.doThrow(new KameHouseNotFoundException("User not found"))
          .when(dragonBallUserServiceMock).deleteDragonBallUser(dragonBallUsersList.get(0).getId());

      // Execute HTTP DELETE on the /dragonball/users/{id} endpoint
      ResultActions requestResult = mockMvc
          .perform(delete("/api/v1/dragonball/users/" + dragonBallUsersList.get(0).getId()))
          .andDo(print());
      requestResult.andExpect(status().is4xxClientError());
      verify(dragonBallUserServiceMock, times(1))
          .deleteDragonBallUser(dragonBallUsersList.get(0).getId());
    } catch (Exception e) {
      if (!(e.getCause() instanceof KameHouseNotFoundException)) {
        e.printStackTrace();
        fail("Unexpected exception thrown.");
      }
    }
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
   * assertSame([message,] expected, actual) assertNotSame([message,] // expected,
   * actual) }
   */
}
