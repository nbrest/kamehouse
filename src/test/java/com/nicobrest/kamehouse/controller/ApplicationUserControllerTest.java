package com.nicobrest.kamehouse.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nicobrest.kamehouse.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.model.ApplicationUser;
import com.nicobrest.kamehouse.service.ApplicationUserService;
import com.nicobrest.kamehouse.utils.JsonUtils;

import org.junit.Before;
import org.junit.BeforeClass;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.LinkedList;
import java.util.List;

/**
 * Unit tests for the ApplicationUserController class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class ApplicationUserControllerTest {

  private MockMvc mockMvc;

  private static ApplicationUser applicationUserMock;
  private static List<ApplicationUser> applicationUsersList;

  @InjectMocks
  private ApplicationUserController applicationUserController;

  @Mock(name = "applicationUserService")
  private ApplicationUserService applicationUserServiceMock;

  /**
   * Initializes test repositories.
   */
  @BeforeClass
  public static void beforeClassTest() {
    applicationUserMock = new ApplicationUser();
    applicationUserMock.setId(1001L);
    applicationUserMock.setEmail("goku@dbz.com");
    applicationUserMock.setUsername("goku");
    applicationUserMock.setPassword("goku");

    ApplicationUser applicationUserMock2 = new ApplicationUser();
    applicationUserMock2.setId(1002L);
    applicationUserMock2.setEmail("gohan@dbz.com");
    applicationUserMock2.setUsername("gohan");
    applicationUserMock2.setPassword("gohan");

    ApplicationUser applicationUserMock3 = new ApplicationUser();
    applicationUserMock3.setId(1003L);
    applicationUserMock3.setEmail("goten@dbz.com");
    applicationUserMock3.setUsername("goten");
    applicationUserMock3.setPassword("goten");

    applicationUsersList = new LinkedList<ApplicationUser>();
    applicationUsersList.add(applicationUserMock);
    applicationUsersList.add(applicationUserMock2);
    applicationUsersList.add(applicationUserMock3);
  }

  /**
   * Resets mock objects.
   */
  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    Mockito.reset(applicationUserServiceMock);
    mockMvc = MockMvcBuilders.standaloneSetup(applicationUserController).build();
  }

  /**
   * Test getting all ApplicationUsers.
   */
  @Test
  public void getUsersTest() {
    when(applicationUserServiceMock.getAllUsers()).thenReturn(applicationUsersList);

    try {
      mockMvc.perform(get("/api/v1/admin/application/users")).andDo(print()).andExpect(status().isOk())
          .andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(jsonPath(
              "$", hasSize(3))).andExpect(jsonPath("$[0].id", equalTo(1001))).andExpect(jsonPath(
                  "$[0].username", equalTo("goku"))).andExpect(jsonPath("$[0].email", equalTo(
                      "goku@dbz.com"))).andExpect(jsonPath("$[0].password", equalTo(null)))

          .andExpect(jsonPath("$[1].id", equalTo(1002))).andExpect(jsonPath("$[1].username",
              equalTo("gohan"))).andExpect(jsonPath("$[1].email", equalTo("gohan@dbz.com")))
          .andExpect(jsonPath("$[1].password", equalTo(null)))

          .andExpect(jsonPath("$[2].id", equalTo(1003))).andExpect(jsonPath("$[2].username",
              equalTo("goten"))).andExpect(jsonPath("$[2].email", equalTo("goten@dbz.com")))
          .andExpect(jsonPath("$[2].password", equalTo(null)));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
    verify(applicationUserServiceMock, times(1)).getAllUsers();
    verifyNoMoreInteractions(applicationUserServiceMock);
  }

  /**
   * Create a user test.
   */
  @Test
  public void postUsersTest() {
    try {
      Mockito.doReturn(applicationUsersList.get(0).getId()).when(applicationUserServiceMock)
          .createUser(applicationUsersList.get(0));
      when(applicationUserServiceMock.loadUserByUsername(applicationUsersList.get(0)
          .getUsername())).thenReturn(applicationUsersList.get(0));

      mockMvc.perform(post("/api/v1/admin/application/users").contentType(
          MediaType.APPLICATION_JSON_UTF8).content(JsonUtils.convertToJsonBytes(
              applicationUsersList.get(0)))).andDo(print()).andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(content()
              .bytes(JsonUtils.convertToJsonBytes(applicationUsersList.get(0).getId()))).andExpect(
                  content().string(applicationUsersList.get(0).getId().toString()));

      verify(applicationUserServiceMock, times(1)).createUser(applicationUsersList.get(0));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * Create an user conflict exception test.
   */
  @Test
  public void postUsersConflictExceptionTest() {

    // TODO: Check which exception the application user service returns and update it here.
    try {
      Mockito.doThrow(new KameHouseConflictException("User already exists")).when(
          applicationUserServiceMock).createUser(applicationUsersList.get(0));

      mockMvc.perform(post("/api/v1/admin/application/users").contentType(
          MediaType.APPLICATION_JSON_UTF8).content(JsonUtils.convertToJsonBytes(
              applicationUsersList.get(0)))).andDo(print()).andExpect(status().is4xxClientError());

      verify(applicationUserServiceMock, times(1)).createUser(applicationUsersList.get(0));
    } catch (Exception e) {
      if (!(e.getCause() instanceof KameHouseConflictException)) {
        e.printStackTrace();
        fail("Unexpected exception thrown.");
      }
    }
  }

  /**
   * Get an application user test.
   */
  @Test
  public void getUserTest() {

    try {
      when(applicationUserServiceMock.loadUserByUsername("goku")).thenReturn(applicationUsersList
          .get(0));

      mockMvc.perform(get("/api/v1/admin/application/users/goku")).andDo(print()).andExpect(status()
          .isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(
              jsonPath("$.id", equalTo(1001))).andExpect(jsonPath("$.username", equalTo("goku")))
          .andExpect(jsonPath("$.email", equalTo("goku@dbz.com"))).andExpect(jsonPath("$.password",
              equalTo(null)));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * Test get user not found exception.
   */
  @Test
  public void getUserNotFoundExceptionTest() {

    // TODO: Check which exception the application user service returns and update it here.
    try {
      Mockito.doThrow(new KameHouseNotFoundException("User trunks not found")).when(
          applicationUserServiceMock).loadUserByUsername("trunks");

      mockMvc.perform(get("/api/v1/admin/application/users/trunks")).andDo(print()).andExpect(status()
          .is4xxClientError());
      verify(applicationUserServiceMock, times(1)).loadUserByUsername("trunks");
    } catch (Exception e) {
      if (!(e.getCause() instanceof KameHouseNotFoundException)) {
        e.printStackTrace();
        fail("Unexpected exception thrown.");
      }
    }
  }

  /**
   * Update an user test.
   */
  @Test
  public void putUsersTest() {

    try {
      Mockito.doNothing().when(applicationUserServiceMock).updateUser(applicationUsersList.get(0));

      mockMvc.perform(put("/api/v1/admin/application/users/" + applicationUsersList.get(0).getId())
          .contentType(MediaType.APPLICATION_JSON_UTF8).content(JsonUtils.convertToJsonBytes(
              applicationUsersList.get(0)))).andDo(print()).andExpect(status().isOk());
      verify(applicationUserServiceMock, times(1)).updateUser(any());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * Update an user not found test.
   */
  @Test
  public void putUsersUsernameNotFoundExceptionTest() {

    // TODO: Check which exception the application user service returns and update it here.
    try {
      Mockito.doThrow(new KameHouseNotFoundException("User not found")).when(
          applicationUserServiceMock).updateUser(applicationUsersList.get(0));

      mockMvc.perform(put("/api/v1/admin/application/users/" + applicationUsersList.get(0).getId())
          .contentType(MediaType.APPLICATION_JSON_UTF8).content(JsonUtils.convertToJsonBytes(
              applicationUsersList.get(0)))).andDo(print()).andExpect(status().is4xxClientError());
      verify(applicationUserServiceMock, times(1)).updateUser(applicationUsersList.get(0));
    } catch (Exception e) {
      if (!(e.getCause() instanceof KameHouseNotFoundException)) {
        e.printStackTrace();
        fail("Unexpected exception thrown.");
      }
    }
  }

  /**
   * Delete an user test.
   */
  @Test
  public void deleteUserTest() {

    try {
      when(applicationUserServiceMock.deleteUser(applicationUsersList.get(0).getId())).thenReturn(
          applicationUsersList.get(0));

      mockMvc.perform(delete("/api/v1/admin/application/users/" + applicationUsersList.get(0).getId()))
          .andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(
              MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$.id", equalTo(1001)))
          .andExpect(jsonPath("$.username", equalTo("goku"))).andExpect(jsonPath("$.email",
              equalTo("goku@dbz.com"))).andExpect(jsonPath("$.password", equalTo(null)));

      verify(applicationUserServiceMock, times(1)).deleteUser(applicationUsersList.get(0).getId());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * Delete an user not found test.
   */
  @Test
  public void deleteUserNotFoundExceptionTest() {

    // TODO: Check which exception the application user service returns and update it here.
    try {
      Mockito.doThrow(new KameHouseNotFoundException("User not found")).when(
          applicationUserServiceMock).deleteUser(applicationUsersList.get(0).getId());

      mockMvc.perform(delete("/api/v1/admin/application/users/" + applicationUsersList.get(0).getId()))
          .andDo(print()).andExpect(status().is4xxClientError());
      verify(applicationUserServiceMock, times(1)).deleteUser(applicationUsersList.get(0).getId());
    } catch (Exception e) {
      if (!(e.getCause() instanceof KameHouseNotFoundException)) {
        e.printStackTrace();
        fail("Unexpected exception thrown.");
      }
    }
  }
}
