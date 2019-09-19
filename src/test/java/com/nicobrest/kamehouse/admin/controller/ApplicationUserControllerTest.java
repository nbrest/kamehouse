package com.nicobrest.kamehouse.admin.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import java.util.ArrayList;
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

    MockHttpServletResponse response = executeGet(API_V1_ADMIN_APPLICATION_USERS);
    List<ApplicationUser> l = new ArrayList<ApplicationUser>();
    
    List<ApplicationUser> responseBody = getResponseBodyList(response, ApplicationUser.class);

    verifyResponseStatus(response, HttpStatus.OK.value());
    verifyContentType(response, MediaType.APPLICATION_JSON_UTF8.toString());
    assertEquals(3, responseBody.size());
    assertEquals(applicationUsersList, responseBody);
    verify(applicationUserServiceMock, times(1)).getAllUsers();
  }

  /**
   * Create a user test.
   */
  @Test
  public void postUsersTest() throws Exception {
    Mockito.doReturn(applicationUser.getId()).when(applicationUserServiceMock).createUser(
        applicationUserDto);
    when(applicationUserServiceMock.loadUserByUsername(applicationUser.getUsername())).thenReturn(
        applicationUser);
    byte[] requestPayload = JsonUtils.toJsonByteArray(applicationUserDto);

    MockHttpServletResponse response = executePost(API_V1_ADMIN_APPLICATION_USERS, requestPayload);
    Long responseBody = getResponseBody(response, Long.class);

    verifyResponseStatus(response, HttpStatus.CREATED.value());
    assertEquals(applicationUserDto.getId(), responseBody);
    verify(applicationUserServiceMock, times(1)).createUser(applicationUserDto);
  }

  /**
   * Create an user conflict exception test.
   */
  @Test
  public void postUsersConflictExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseConflictException.class));
    Mockito.doThrow(new KameHouseConflictException("")).when(applicationUserServiceMock)
        .createUser(applicationUserDto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(applicationUserDto);

    executePost(API_V1_ADMIN_APPLICATION_USERS, requestPayload);
  }

  /**
   * Get an application user test.
   */
  @Test
  public void getUserTest() throws Exception {
    when(applicationUserServiceMock.loadUserByUsername(applicationUser.getUsername())).thenReturn(
        applicationUser);

    MockHttpServletResponse response = executeGet(API_V1_ADMIN_APPLICATION_USERS + applicationUser
        .getUsername());
    ApplicationUser responseBody = getResponseBody(response, ApplicationUser.class);

    verifyResponseStatus(response, HttpStatus.OK.value());
    assertEquals(applicationUser, responseBody);
  }

  /**
   * Test get user not found exception.
   */
  @Test
  public void getUserNotFoundExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseNotFoundException.class));
    Mockito.doThrow(new KameHouseNotFoundException("")).when(applicationUserServiceMock)
        .loadUserByUsername(ApplicationUserTestUtils.INVALID_USERNAME);

    executeGet(API_V1_ADMIN_APPLICATION_USERS + ApplicationUserTestUtils.INVALID_USERNAME);
  }

  /**
   * Update an user test.
   */
  @Test
  public void putUsersTest() throws Exception {
    Mockito.doNothing().when(applicationUserServiceMock).updateUser(applicationUserDto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(applicationUserDto);

    MockHttpServletResponse response = executePut(API_V1_ADMIN_APPLICATION_USERS
        + applicationUserDto.getId(), requestPayload);

    verifyResponseStatus(response, HttpStatus.OK.value());
    verify(applicationUserServiceMock, times(1)).updateUser(any());
  }

  /**
   * Update an user with invalid path id. Exception expected.
   */
  @Test
  public void putUsersInvalidPathId() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseBadRequestException.class));
    byte[] requestPayload = JsonUtils.toJsonByteArray(applicationUserDto);

    executePut(API_V1_ADMIN_APPLICATION_USERS + ApplicationUserTestUtils.INVALID_ID,
        requestPayload);
  }

  /**
   * Update an user not found test.
   */
  @Test
  public void putUsersUsernameNotFoundExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseNotFoundException.class));
    Mockito.doThrow(new KameHouseNotFoundException("")).when(applicationUserServiceMock)
        .updateUser(applicationUserDto);
    byte[] requestPayload = JsonUtils.toJsonByteArray(applicationUserDto);

    executePut(API_V1_ADMIN_APPLICATION_USERS + applicationUserDto.getId(), requestPayload);
  }

  /**
   * Delete an user test.
   */
  @Test
  public void deleteUserTest() throws Exception {
    when(applicationUserServiceMock.deleteUser(applicationUser.getId())).thenReturn(
        applicationUser);

    MockHttpServletResponse response = executeDelete(API_V1_ADMIN_APPLICATION_USERS
        + applicationUser.getId());
    ApplicationUser responseBody = getResponseBody(response, ApplicationUser.class);

    verifyResponseStatus(response, HttpStatus.OK.value());
    assertEquals(applicationUser, responseBody);
    verify(applicationUserServiceMock, times(1)).deleteUser(applicationUser.getId());
  }

  /**
   * Delete an user not found test.
   */
  @Test
  public void deleteUserNotFoundExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(KameHouseNotFoundException.class));
    Mockito.doThrow(new KameHouseNotFoundException("")).when(applicationUserServiceMock)
        .deleteUser(applicationUser.getId());

    executeDelete(API_V1_ADMIN_APPLICATION_USERS + applicationUser.getId());
  }
}
