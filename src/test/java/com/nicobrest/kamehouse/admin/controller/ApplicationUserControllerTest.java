package com.nicobrest.kamehouse.admin.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
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

import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.admin.service.ApplicationUserService;
import com.nicobrest.kamehouse.admin.service.dto.ApplicationUserDto;
import com.nicobrest.kamehouse.admin.testutils.ApplicationUserTestUtils;
import com.nicobrest.kamehouse.main.controller.AbstractControllerTest;
import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.main.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.utils.JsonUtils;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.List;

/**
 * Unit tests for the ApplicationUserController class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class ApplicationUserControllerTest extends AbstractControllerTest {

  public static final String API_V1_ADMIN_APPLICATION_USERS =
      ApplicationUserTestUtils.API_V1_ADMIN_APPLICATION_USERS;
  private static ApplicationUser applicationUser;
  private static List<ApplicationUser> applicationUsersList;
  private static ApplicationUserDto applicationUserDto;

  @InjectMocks
  private ApplicationUserController applicationUserController;

  @Mock(name = "applicationUserService")
  private ApplicationUserService applicationUserServiceMock;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Resets mock objects.
   */
  @Before
  public void beforeTest() {
    ApplicationUserTestUtils.initApplicationUserTestData();
    applicationUser = ApplicationUserTestUtils.getApplicationUser();
    applicationUsersList = ApplicationUserTestUtils.getApplicationUsersList();
    applicationUserDto = ApplicationUserTestUtils.getApplicationUserDto();

    MockitoAnnotations.initMocks(this);
    Mockito.reset(applicationUserServiceMock);
    mockMvc = MockMvcBuilders.standaloneSetup(applicationUserController).build();
  }

  /**
   * Test getting all ApplicationUsers.
   */
  @Test
  public void getUsersTest() throws Exception {
    when(applicationUserServiceMock.getAllUsers()).thenReturn(applicationUsersList);

    ResultActions requestResult =
        mockMvc.perform(get(API_V1_ADMIN_APPLICATION_USERS)).andDo(print());
    requestResult.andExpect(status().isOk());
    requestResult.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    requestResult.andExpect(jsonPath("$", hasSize(3)));
    requestResult.andExpect(jsonPath("$[0].id", equalTo(1001)));
    requestResult.andExpect(jsonPath("$[0].username", equalTo("goku")));
    requestResult.andExpect(jsonPath("$[0].email", equalTo("goku@dbz.com")));
    requestResult.andExpect(jsonPath("$[0].password", equalTo(null)));
    requestResult.andExpect(jsonPath("$[1].id", equalTo(1002)));
    requestResult.andExpect(jsonPath("$[1].username", equalTo("gohan")));
    requestResult.andExpect(jsonPath("$[1].email", equalTo("gohan@dbz.com")));
    requestResult.andExpect(jsonPath("$[1].password", equalTo(null)));
    requestResult.andExpect(jsonPath("$[2].id", equalTo(1003)));
    requestResult.andExpect(jsonPath("$[2].username", equalTo("goten")));
    requestResult.andExpect(jsonPath("$[2].email", equalTo("goten@dbz.com")));
    requestResult.andExpect(jsonPath("$[2].password", equalTo(null)));
    verify(applicationUserServiceMock, times(1)).getAllUsers();
    verifyNoMoreInteractions(applicationUserServiceMock);
  }

  /**
   * Create a user test.
   */
  @Test
  public void postUsersTest() throws Exception {
    Mockito.doReturn(applicationUser.getId()).when(applicationUserServiceMock)
        .createUser(applicationUserDto);
    when(applicationUserServiceMock.loadUserByUsername(applicationUser.getUsername()))
        .thenReturn(applicationUser);

    byte[] requestPayload = JsonUtils.toJsonByteArray(applicationUserDto);
    ResultActions requestResult =
        mockMvc
            .perform(post(API_V1_ADMIN_APPLICATION_USERS)
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(requestPayload))
            .andDo(print());

    requestResult.andExpect(status().isCreated());
    requestResult.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    requestResult.andExpect(content().bytes(JsonUtils.toJsonByteArray(applicationUserDto.getId())));
    requestResult.andExpect(content().string(applicationUserDto.getId().toString()));
    verify(applicationUserServiceMock, times(1)).createUser(applicationUserDto);
  }

  /**
   * Create an user conflict exception test.
   */
  @Test
  public void postUsersConflictExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable>instanceOf(KameHouseConflictException.class));
    Mockito.doThrow(new KameHouseConflictException("User already exists"))
        .when(applicationUserServiceMock).createUser(applicationUserDto);

    byte[] requestPayload = JsonUtils.toJsonByteArray(applicationUserDto);
    ResultActions requestResult =
        mockMvc
            .perform(post(API_V1_ADMIN_APPLICATION_USERS)
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(requestPayload))
            .andDo(print());

    requestResult.andExpect(status().is4xxClientError());
    verify(applicationUserServiceMock, times(1)).createUser(applicationUserDto);
  }

  /**
   * Get an application user test.
   */
  @Test
  public void getUserTest() throws Exception {
    when(applicationUserServiceMock.loadUserByUsername(applicationUser.getUsername()))
        .thenReturn(applicationUser);

    ResultActions requestResult =
        mockMvc.perform(get(API_V1_ADMIN_APPLICATION_USERS + applicationUser.getUsername()))
            .andDo(print());

    requestResult.andExpect(status().isOk());
    requestResult.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    requestResult.andExpect(jsonPath("$.id", equalTo(1001)));
    requestResult.andExpect(jsonPath("$.username", equalTo("goku")));
    requestResult.andExpect(jsonPath("$.email", equalTo("goku@dbz.com")));
    requestResult.andExpect(jsonPath("$.password", equalTo(null)));
  }

  /**
   * Test get user not found exception.
   */
  @Test
  public void getUserNotFoundExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable>instanceOf(KameHouseNotFoundException.class));

    Mockito
        .doThrow(new KameHouseNotFoundException(
            "User " + ApplicationUserTestUtils.INVALID_USERNAME + " not found"))
        .when(applicationUserServiceMock)
        .loadUserByUsername(ApplicationUserTestUtils.INVALID_USERNAME);

    ResultActions requestResult = mockMvc
        .perform(get(API_V1_ADMIN_APPLICATION_USERS + ApplicationUserTestUtils.INVALID_USERNAME))
        .andDo(print());

    requestResult.andExpect(status().is4xxClientError());
    verify(applicationUserServiceMock, times(1))
        .loadUserByUsername(ApplicationUserTestUtils.INVALID_USERNAME);
  }

  /**
   * Update an user test.
   */
  @Test
  public void putUsersTest() throws Exception {
    Mockito.doNothing().when(applicationUserServiceMock).updateUser(applicationUserDto);

    byte[] requestPayload = JsonUtils.toJsonByteArray(applicationUserDto);
    ResultActions requestResult =
        mockMvc
            .perform(put(API_V1_ADMIN_APPLICATION_USERS + applicationUserDto.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(requestPayload))
            .andDo(print());

    requestResult.andExpect(status().isOk());
    verify(applicationUserServiceMock, times(1)).updateUser(any());
  }

  /**
   * Update an user with invalid path id. Exception expected.
   */
  @Test
  public void putUsersInvalidPathId() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable>instanceOf(KameHouseBadRequestException.class));

    byte[] requestPayload = JsonUtils.toJsonByteArray(applicationUserDto);
    ResultActions requestResult =
        mockMvc
            .perform(put(API_V1_ADMIN_APPLICATION_USERS + ApplicationUserTestUtils.INVALID_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(requestPayload))
            .andDo(print());

    requestResult.andExpect(status().is4xxClientError());
    verify(applicationUserServiceMock, times(0)).updateUser(any());
  }

  /**
   * Update an user not found test.
   */
  @Test
  public void putUsersUsernameNotFoundExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable>instanceOf(KameHouseNotFoundException.class));
    Mockito.doThrow(new KameHouseNotFoundException("User not found"))
        .when(applicationUserServiceMock).updateUser(applicationUserDto);

    byte[] requestPayload = JsonUtils.toJsonByteArray(applicationUsersList.get(0));
    ResultActions requestResult =
        mockMvc
            .perform(put(API_V1_ADMIN_APPLICATION_USERS + applicationUserDto.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(requestPayload))
            .andDo(print());

    requestResult.andExpect(status().is4xxClientError());
    verify(applicationUserServiceMock, times(1)).updateUser(applicationUserDto);
  }

  /**
   * Delete an user test.
   */
  @Test
  public void deleteUserTest() throws Exception {
    when(applicationUserServiceMock.deleteUser(applicationUsersList.get(0).getId()))
        .thenReturn(applicationUsersList.get(0));

    ResultActions requestResult = mockMvc
        .perform(delete(API_V1_ADMIN_APPLICATION_USERS + applicationUsersList.get(0).getId()))
        .andDo(print());

    requestResult.andExpect(status().isOk());
    requestResult.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    requestResult.andExpect(jsonPath("$.id", equalTo(1001)));
    requestResult.andExpect(jsonPath("$.username", equalTo("goku")));
    requestResult.andExpect(jsonPath("$.email", equalTo("goku@dbz.com")));
    requestResult.andExpect(jsonPath("$.password", equalTo(null)));
    verify(applicationUserServiceMock, times(1)).deleteUser(applicationUsersList.get(0).getId());
  }

  /**
   * Delete an user not found test.
   */
  @Test
  public void deleteUserNotFoundExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable>instanceOf(KameHouseNotFoundException.class));
    Mockito.doThrow(new KameHouseNotFoundException("User not found"))
        .when(applicationUserServiceMock).deleteUser(applicationUsersList.get(0).getId());

    ResultActions requestResult = mockMvc
        .perform(delete(API_V1_ADMIN_APPLICATION_USERS + applicationUsersList.get(0).getId()))
        .andDo(print());

    requestResult.andExpect(status().is4xxClientError());
    verify(applicationUserServiceMock, times(1)).deleteUser(applicationUsersList.get(0).getId());
  }
}
