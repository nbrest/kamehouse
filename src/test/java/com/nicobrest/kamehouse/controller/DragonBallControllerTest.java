package com.nicobrest.kamehouse.controller;

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

import com.nicobrest.kamehouse.controller.DragonBallController;
import com.nicobrest.kamehouse.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.model.DragonBallUser;
import com.nicobrest.kamehouse.service.DragonBallUserService;
import com.nicobrest.kamehouse.utils.JsonUtils;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
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

  private static final Logger LOGGER = LoggerFactory.getLogger(DragonBallControllerTest.class);

  private MockMvc mockMvc;

  private static List<DragonBallUser> dragonBallUsersList;

  @InjectMocks
  private DragonBallController dragonBallController;

  @Mock(name = "dragonBallUserService")
  private DragonBallUserService dragonBallUserServiceMock;

  /**
   * Initializes test repositories.
   *
   * @author nbrest
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
   *
   * @author nbrest
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
   *
   * @author nbrest
   */
  @After
  public void afterTest() {
    /* Actions to perform after each test */

  }

  /**
   * Cleanup after all tests have executed.
   *
   * @author nbrest
   */
  @AfterClass
  public static void afterClassTest() {
    /* Actions to perform ONCE after all tests in the class */

  }

  /**
   * /dragonball/model-and-view (GET) Test the endpoint /dragonball/model-and-view
   * with the HTTP method GET. The service should respond with HTTP status 200
   * OK and a view defined in dragonball/modelAndView.jsp.
   *
   * @author nbrest
   */
  @Test
  public void getModelAndViewTest() {
    LOGGER.info("***** Executing getModelAndViewTest");

    try {
      mockMvc.perform(get("/api/v1/dragonball/model-and-view")).andDo(print()).andExpect(status().isOk())
          .andExpect(view().name("jsp/dragonball/model-and-view"))
          .andExpect(forwardedUrl("jsp/dragonball/model-and-view"))
          .andExpect(model().attribute("name", isA(String.class)))
          .andExpect(model().attribute("name", equalTo("Goku")))
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
   *
   * @author nbrest
   */
  @Test
  public void getUsersTest() {
    LOGGER.info("***** Executing getUsersTest");

    // Setup mock object dragonBallUserServiceMock
    when(dragonBallUserServiceMock.getAllDragonBallUsers()).thenReturn(dragonBallUsersList);

    // Execute HTTP GET on the /dragonball/users endpoint
    try {
      mockMvc.perform(get("/api/v1/dragonball/users")).andDo(print()).andExpect(status().isOk())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$", hasSize(3))).andExpect(jsonPath("$[0].id", equalTo(101)))
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
   *
   * @author nbrest
   */
  @Test
  public void getUsersExceptionTest() {
    LOGGER.info("***** Executing getUsersExceptionTest");

    try {
      mockMvc.perform(get("/api/v1/dragonball/users?action=Exception")).andDo(print())
          .andExpect(status().isInternalServerError()).andExpect(view().name("error/error"))
          .andExpect(forwardedUrl("/WEB-INF/jsp/error/error.jsp"));
      fail("Expected an exception to be thrown.");
    } catch (Exception e) {
      //Do nothing. An exception was expected.
    }
    // Verify gotenDragonBallUserMock invocations
    verifyZeroInteractions(dragonBallUserServiceMock);
  }

  /**
   * /dragonball/users (GET) Test the rest web service on the endpoint
   * /dragonball/users with the parameter to throw an exception.
   *
   * @author nbrest
   */
  @Test
  public void getUsersRuntimeExceptionTest() {
    LOGGER.info("***** Executing getUsersRuntimeExceptionTest");

    try {
      mockMvc.perform(get("/api/v1/dragonball/users?action=RuntimeException")).andDo(print())
          .andExpect(status().isInternalServerError()).andExpect(view().name("error/error"))
          .andExpect(forwardedUrl("/WEB-INF/jsp/error/error.jsp"));
      fail("Expected an exception to be thrown.");
    } catch (Exception e) {
      //Do nothing. Expected an exception
    }
    // Verify gotenDragonBallUserMock invocations
    verifyZeroInteractions(dragonBallUserServiceMock);
  }

  /**
   * /dragonball/users (GET) Test the rest web service on the endpoint
   * /dragonball/users with the parameter to throw an exception.
   *
   * @author nbrest
   */
  @Test
  public void getUsersNotFoundExceptionTest() {
    LOGGER.info("***** Executing getUsersNotFoundExceptionTest");

    try {
      mockMvc.perform(get("/api/v1/dragonball/users?action=KameHouseNotFoundException"))
          .andDo(print()).andExpect(status().isNotFound());
      fail("Expected an exception to be thrown.");
    } catch (Exception e) {
      //Do nothing. Expected an exception
    }
    // Verify gotenDragonBallUserMock invocations
    verifyZeroInteractions(dragonBallUserServiceMock);
  }

  /**
   * /dragonball/users (POST) Test creating a new DragonBallUser in the
   * repository.
   *
   * @author nbrest
   */
  @Test
  public void postUsersTest() {
    LOGGER.info("***** Executing postUsersTest");

    // Normal flow
    try {
      // Setup mock object dragonBallUserServiceMock
      Mockito.doReturn(dragonBallUsersList.get(0).getId()).when(dragonBallUserServiceMock)
          .createDragonBallUser(dragonBallUsersList.get(0));
      when(dragonBallUserServiceMock.getDragonBallUser(dragonBallUsersList.get(0).getUsername()))
          .thenReturn(dragonBallUsersList.get(0));

      // Execute HTTP POST on the /dragonball/users endpoint
      mockMvc
          .perform(post("/api/v1/dragonball/users").contentType(MediaType.APPLICATION_JSON_UTF8)
              .content(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0))))
          .andDo(print()).andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
          .andExpect(
              content().bytes(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0).getId())))
          .andExpect(content().string(dragonBallUsersList.get(0).getId().toString()));

      verify(dragonBallUserServiceMock, times(1)).createDragonBallUser(dragonBallUsersList.get(0));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * /dragonball/users (POST) Test creating a new DragonBallUser in the
   * repository that already exists.
   *
   * @author nbrest
   */
  @Test
  public void postUsersConflictExceptionTest() {
    LOGGER.info("***** Executing postUsersConflictExceptionTest");

    // Exception flows
    try {
      // Setup mock object dragonBallUserServiceMock
      Mockito.doThrow(new KameHouseConflictException("User already exists"))
          .when(dragonBallUserServiceMock).createDragonBallUser(dragonBallUsersList.get(0));

      // Execute HTTP POST on the /dragonball/users endpoint
      mockMvc
          .perform(post("/api/v1/dragonball/users").contentType(MediaType.APPLICATION_JSON_UTF8)
              .content(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0))))
          .andDo(print()).andExpect(status().is4xxClientError());

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
   *
   * @author nbrest
   */
  @Test
  public void getUsersIdTest() {
    LOGGER.info("***** Executing getUsersIdTest");

    try {
      when(dragonBallUserServiceMock.getDragonBallUser(101L))
          .thenReturn(dragonBallUsersList.get(0));

      mockMvc.perform(get("/api/v1/dragonball/users/101")).andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.id", equalTo(101)))
          .andExpect(jsonPath("$.username", equalTo("gokuTestMock")))
          .andExpect(jsonPath("$.email", equalTo("gokuTestMock@dbz.com")))
          .andExpect(jsonPath("$.age", equalTo(49)))
          .andExpect(jsonPath("$.powerLevel", equalTo(30)))
          .andExpect(jsonPath("$.stamina", equalTo(1000)));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * /dragonball/users/username/{username} (GET) Tests getting a specific user from the
   * repository.
   *
   * @author nbrest
   */
  @Test
  public void getUsersUsernameTest() {
    LOGGER.info("***** Executing getUsersUsernameTest");

    try {
      when(dragonBallUserServiceMock.getDragonBallUser("gokuTestMock"))
          .thenReturn(dragonBallUsersList.get(0));

      mockMvc.perform(get("/api/v1/dragonball/users/username/gokuTestMock")).andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.id", equalTo(101)))
          .andExpect(jsonPath("$.username", equalTo("gokuTestMock")))
          .andExpect(jsonPath("$.email", equalTo("gokuTestMock@dbz.com")))
          .andExpect(jsonPath("$.age", equalTo(49)))
          .andExpect(jsonPath("$.powerLevel", equalTo(30)))
          .andExpect(jsonPath("$.stamina", equalTo(1000)));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }

    // Exception flows
    try {
      Mockito.reset(dragonBallUserServiceMock);
      Mockito.doThrow(new KameHouseNotFoundException("User trunks not found"))
          .when(dragonBallUserServiceMock).getDragonBallUser("trunks");

      mockMvc.perform(get("/api/v1/dragonball/users/username/trunks")).andDo(print())
          .andExpect(status().is4xxClientError());
      verify(dragonBallUserServiceMock, times(1)).getDragonBallUser("trunks");
    } catch (Exception e) {
      if (!(e.getCause() instanceof KameHouseNotFoundException)) {
        e.printStackTrace();
        fail("Unexpected exception thrown.");
      }
    }
  }

  /**
   * /dragonball/users/username/{username} (GET) Tests user not found when getting a
   * specific user from the repository.
   *
   * @author nbrest
   */
  @Test
  public void getUsersUsernameNotFoundExceptionTest() {
    LOGGER.info("***** Executing getUsersUsernameNotFoundExceptionTest");

    // Exception flows
    try {
      Mockito.doThrow(new KameHouseNotFoundException("User trunks not found"))
          .when(dragonBallUserServiceMock).getDragonBallUser("trunks");

      mockMvc.perform(get("/api/v1/dragonball/users/username/trunks")).andDo(print())
          .andExpect(status().is4xxClientError());
      verify(dragonBallUserServiceMock, times(1)).getDragonBallUser("trunks");
    } catch (Exception e) {
      if (!(e.getCause() instanceof KameHouseNotFoundException)) {
        e.printStackTrace();
        fail("Unexpected exception thrown.");
      }
    }
  }

  /**
   * /dragonball/users/emails/{email} (GET) Tests getting a specific user from
   * the repository by email.
   *
   * @author nbrest
   */
  @Test
  public void getUsersUsernameByEmailTest() {
    LOGGER.info("***** Executing getUsersUsernameByEmailTest");

    try {
      // Setup mock object dragonBallUserServiceMock
      when(dragonBallUserServiceMock.getDragonBallUserByEmail("gokuTestMock@dbz.com"))
          .thenReturn(dragonBallUsersList.get(0));

      // Execute HTTP GET on the /dragonball/users/{username} endpoint
      mockMvc.perform(get("/api/v1/dragonball/users/emails/gokuTestMock@dbz.com")).andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.id", equalTo(101)))
          .andExpect(jsonPath("$.username", equalTo("gokuTestMock")))
          .andExpect(jsonPath("$.email", equalTo("gokuTestMock@dbz.com")))
          .andExpect(jsonPath("$.age", equalTo(49)))
          .andExpect(jsonPath("$.powerLevel", equalTo(30)))
          .andExpect(jsonPath("$.stamina", equalTo(1000)));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * /dragonball/users/{id} (PUT) Tests updating an existing user in the
   * repository.
   *
   * @author nbrest
   */
  @Test
  public void putUsersUsernameTest() {
    LOGGER.info("***** Executing putUsersUsernameTest");

    // Normal flow
    try {
      // Setup mock object dragonBallUserServiceMock
      Mockito.doNothing().when(dragonBallUserServiceMock)
          .updateDragonBallUser(dragonBallUsersList.get(0));
      when(dragonBallUserServiceMock.getDragonBallUser(dragonBallUsersList.get(0).getUsername()))
          .thenReturn(dragonBallUsersList.get(0));

      // Execute HTTP PUT on the /dragonball/users/{id} endpoint
      mockMvc
          .perform(put("/api/v1/dragonball/users/" + dragonBallUsersList.get(0).getId())
              .contentType(MediaType.APPLICATION_JSON_UTF8)
              .content(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0))))
          .andDo(print()).andExpect(status().isOk());

      verify(dragonBallUserServiceMock, times(1)).updateDragonBallUser(dragonBallUsersList.get(0));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * /dragonball/users/{id} (PUT) Tests trying to update a non existing user in
   * the repository.
   *
   * @author nbrest
   */
  @Test
  public void putUsersUsernameNotFoundExceptionTest() {
    LOGGER.info("***** Executing putUsersUsernameNotFoundExceptionTest");

    // Exception flows
    try {
      // Setup mock object dragonBallUserServiceMock
      Mockito.doThrow(new KameHouseNotFoundException("User not found"))
          .when(dragonBallUserServiceMock).updateDragonBallUser(dragonBallUsersList.get(0));

      // Execute HTTP PUT on the /dragonball/users/{id} endpoint
      mockMvc
          .perform(put("/api/v1/dragonball/users/" + dragonBallUsersList.get(0).getId())
              .contentType(MediaType.APPLICATION_JSON_UTF8)
              .content(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0))))
          .andDo(print()).andExpect(status().is4xxClientError());
      verify(dragonBallUserServiceMock, times(1)).updateDragonBallUser(dragonBallUsersList.get(0));
    } catch (Exception e) {
      if (!(e.getCause() instanceof KameHouseNotFoundException)) {
        e.printStackTrace();
        fail("Unexpected exception thrown.");
      }
    }
  }

  /**
   * /dragonball/users/{id} (PUT) Tests failing to update an existing user in
   * the repository with forbidden access.
   *
   * @author nbrest
   */
  @Test
  public void putUsersUsernameForbiddenExceptionTest() {
    LOGGER.info("***** Executing putUsersUsernameForbiddenExceptionTest");

    // Exception flows
    try {
      // Setup mock object dragonBallUserServiceMock
      Mockito.doNothing().when(dragonBallUserServiceMock)
          .updateDragonBallUser(dragonBallUsersList.get(0));
      when(dragonBallUserServiceMock.getDragonBallUser(dragonBallUsersList.get(0).getUsername()))
          .thenReturn(dragonBallUsersList.get(0));

      // Execute HTTP PUT on the /dragonball/users/{id} endpoint
      mockMvc
          .perform(put("/dragonball/users/987").contentType(MediaType.APPLICATION_JSON_UTF8)
              .content(JsonUtils.convertToJsonBytes(dragonBallUsersList.get(0))))
          .andDo(print()).andExpect(status().is4xxClientError());
      verify(dragonBallUserServiceMock, times(0)).updateDragonBallUser(dragonBallUsersList.get(0));
      verify(dragonBallUserServiceMock, times(0))
          .getDragonBallUser(dragonBallUsersList.get(0).getUsername());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * /dragonball/users/{id} (DELETE) Tests for deleting an existing user from
   * the repository.
   *
   * @author nbrest
   */
  @Test
  public void deleteUsersUsernameTest() {
    LOGGER.info("***** Executing deleteUsersUsernameTest");

    // Normal flow
    try {
      // Setup mock object dragonBallUserServiceMock
      when(dragonBallUserServiceMock.deleteDragonBallUser(dragonBallUsersList.get(0).getId()))
          .thenReturn(dragonBallUsersList.get(0));

      // Execute HTTP DELETE on the /dragonball/users/{id} endpoint
      mockMvc.perform(delete("/api/v1/dragonball/users/" + dragonBallUsersList.get(0).getId()))
          .andDo(print()).andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
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
   *
   * @author nbrest
   */
  @Test
  public void deleteUsersUsernameNotFoundExceptionTest() {
    LOGGER.info("***** Executing deleteUsersUsernameNotFoundExceptionTest");

    // Exception flows
    try {
      // Setup mock object dragonBallUserServiceMock
      Mockito.doThrow(new KameHouseNotFoundException("User not found"))
          .when(dragonBallUserServiceMock)
          .deleteDragonBallUser(dragonBallUsersList.get(0).getId());

      // Execute HTTP DELETE on the /dragonball/users/{id} endpoint
      mockMvc.perform(delete("/api/v1/dragonball/users/" + dragonBallUsersList.get(0).getId()))
          .andDo(print()).andExpect(status().is4xxClientError());
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
   * assertSame([message,] expected, actual) assertNotSame([message,] //
   * expected, actual) }
   */
}
