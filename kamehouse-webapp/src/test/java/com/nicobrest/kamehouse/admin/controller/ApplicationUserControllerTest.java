package com.nicobrest.kamehouse.admin.controller;

import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.model.ApplicationUser;
import com.nicobrest.kamehouse.commons.model.dto.ApplicationUserDto;
import com.nicobrest.kamehouse.admin.service.ApplicationUserService;
import com.nicobrest.kamehouse.commons.testutils.ApplicationUserTestUtils;
import com.nicobrest.kamehouse.commons.controller.AbstractCrudControllerTest;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
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

/**
 * Unit tests for the ApplicationUserController class.
 *
 * @author nbrest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class ApplicationUserControllerTest
    extends AbstractCrudControllerTest<ApplicationUser, ApplicationUserDto> {

  private static final String API_V1_ADMIN_APPLICATION_USERS =
      ApplicationUserTestUtils.API_V1_ADMIN_APPLICATION_USERS;
  private ApplicationUser applicationUser;

  @InjectMocks
  private ApplicationUserController applicationUserController;

  @Mock(name = "applicationUserService")
  private ApplicationUserService applicationUserServiceMock;

  /**
   * Resets mock objects.
   */
  @Before
  public void beforeTest() {
    testUtils = new ApplicationUserTestUtils();
    testUtils.initTestData();
    testUtils.setIds();
    applicationUser = testUtils.getSingleTestData();

    MockitoAnnotations.initMocks(this);
    Mockito.reset(applicationUserServiceMock);
    mockMvc = MockMvcBuilders.standaloneSetup(applicationUserController).build();
  }

  /**
   * Creates a user.
   */
  @Test
  public void createTest() throws Exception {
    createTest(API_V1_ADMIN_APPLICATION_USERS, applicationUserServiceMock);
  }

  /**
   * Creates an user conflict exception.
   */
  @Test
  public void createConflictExceptionTest() throws Exception {
    createConflictExceptionTest(API_V1_ADMIN_APPLICATION_USERS, applicationUserServiceMock);
  }

  /**
   * Gets a specific user from the repository.
   */
  @Test
  public void readTest() throws Exception {
    readTest(API_V1_ADMIN_APPLICATION_USERS, applicationUserServiceMock, ApplicationUser.class);
  }

  /**
   * Gets all ApplicationUsers.
   */
  @Test
  public void readAllTest() throws Exception {
    readAllTest(API_V1_ADMIN_APPLICATION_USERS, applicationUserServiceMock, ApplicationUser.class);
  }

  /**
   * Updates an user.
   */
  @Test
  public void updateTest() throws Exception {
    updateTest(API_V1_ADMIN_APPLICATION_USERS, applicationUserServiceMock);
  }

  /**
   * Updates an user with invalid path id. Exception expected.
   */
  @Test
  public void updateInvalidPathId() throws Exception {
    updateInvalidPathId(API_V1_ADMIN_APPLICATION_USERS);
  }

  /**
   * Updates an user not found.
   */
  @Test
  public void updateNotFoundExceptionTest() throws Exception {
    updateNotFoundExceptionTest(API_V1_ADMIN_APPLICATION_USERS, applicationUserServiceMock);
  }

  /**
   * Deletes an user.
   */
  @Test
  public void deleteTest() throws Exception {
    deleteTest(API_V1_ADMIN_APPLICATION_USERS, applicationUserServiceMock, ApplicationUser.class);
  }

  /**
   * Deletes an user not found.
   */
  @Test
  public void deleteNotFoundExceptionTest() throws Exception {
    deleteNotFoundExceptionTest(API_V1_ADMIN_APPLICATION_USERS, applicationUserServiceMock);
  }

  /**
   * Gets an application user.
   */
  @Test
  public void loadUserByUsernameTest() throws Exception {
    when(applicationUserServiceMock.loadUserByUsername(applicationUser.getUsername()))
        .thenReturn(applicationUser);

    MockHttpServletResponse response =
        doGet(API_V1_ADMIN_APPLICATION_USERS + "username/" + applicationUser.getUsername());
    ApplicationUser responseBody = getResponseBody(response, ApplicationUser.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributes(applicationUser, responseBody);
  }

  /**
   * Tests get user not found exception.
   */
  @Test
  public void loadUserByUsernameNotFoundExceptionTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable>instanceOf(KameHouseNotFoundException.class));
    Mockito.doThrow(new KameHouseNotFoundException("")).when(applicationUserServiceMock)
        .loadUserByUsername(ApplicationUserTestUtils.INVALID_USERNAME);

    doGet(
        API_V1_ADMIN_APPLICATION_USERS + "username/" + ApplicationUserTestUtils.INVALID_USERNAME);
  }
}
