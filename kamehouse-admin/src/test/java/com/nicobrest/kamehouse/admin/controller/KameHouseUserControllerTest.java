package com.nicobrest.kamehouse.admin.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.admin.service.KameHouseUserService;
import com.nicobrest.kamehouse.commons.testutils.KameHouseUserTestUtils;
import com.nicobrest.kamehouse.commons.controller.AbstractCrudControllerTest;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

/**
 * Unit tests for the KameHouseUserController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class KameHouseUserControllerTest
    extends AbstractCrudControllerTest<KameHouseUser, KameHouseUserDto> {

  private static final String API_V1_ADMIN_KAMEHOUSE_USERS =
      KameHouseUserTestUtils.API_V1_ADMIN_KAMEHOUSE_USERS;
  private KameHouseUser kameHouseUser;

  @InjectMocks
  private KameHouseUserController kameHouseUserController;

  @Mock(name = "kameHouseUserService")
  private KameHouseUserService kameHouseUserServiceMock;

  /**
   * Resets mock objects.
   */
  @BeforeEach
  public void beforeTest() {
    testUtils = new KameHouseUserTestUtils();
    testUtils.initTestData();
    testUtils.setIds();
    kameHouseUser = testUtils.getSingleTestData();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(kameHouseUserServiceMock);
    mockMvc = MockMvcBuilders.standaloneSetup(kameHouseUserController).build();
  }

  /**
   * Creates a user.
   */
  @Test
  public void createTest() throws Exception {
    createTest(API_V1_ADMIN_KAMEHOUSE_USERS, kameHouseUserServiceMock);
  }

  /**
   * Creates an user conflict exception.
   */
  @Test
  public void createConflictExceptionTest() throws Exception {
    createConflictExceptionTest(API_V1_ADMIN_KAMEHOUSE_USERS, kameHouseUserServiceMock);
  }

  /**
   * Gets a specific user from the repository.
   */
  @Test
  public void readTest() throws Exception {
    readTest(API_V1_ADMIN_KAMEHOUSE_USERS, kameHouseUserServiceMock, KameHouseUser.class);
  }

  /**
   * Gets all KameHouseUsers.
   */
  @Test
  public void readAllTest() throws Exception {
    readAllTest(API_V1_ADMIN_KAMEHOUSE_USERS, kameHouseUserServiceMock, KameHouseUser.class);
  }

  /**
   * Updates an user.
   */
  @Test
  public void updateTest() throws Exception {
    updateTest(API_V1_ADMIN_KAMEHOUSE_USERS, kameHouseUserServiceMock);
  }

  /**
   * Updates an user with invalid path id. Exception expected.
   */
  @Test
  public void updateInvalidPathId() throws Exception {
    updateInvalidPathId(API_V1_ADMIN_KAMEHOUSE_USERS);
  }

  /**
   * Updates an user not found.
   */
  @Test
  public void updateNotFoundExceptionTest() throws Exception {
    updateNotFoundExceptionTest(API_V1_ADMIN_KAMEHOUSE_USERS, kameHouseUserServiceMock);
  }

  /**
   * Deletes an user.
   */
  @Test
  public void deleteTest() throws Exception {
    deleteTest(API_V1_ADMIN_KAMEHOUSE_USERS, kameHouseUserServiceMock, KameHouseUser.class);
  }

  /**
   * Deletes an user not found.
   */
  @Test
  public void deleteNotFoundExceptionTest() throws Exception {
    deleteNotFoundExceptionTest(API_V1_ADMIN_KAMEHOUSE_USERS, kameHouseUserServiceMock);
  }

  /**
   * Gets an kamehouse user.
   */
  @Test
  public void loadUserByUsernameTest() throws Exception {
    when(kameHouseUserServiceMock.loadUserByUsername(kameHouseUser.getUsername()))
        .thenReturn(kameHouseUser);

    MockHttpServletResponse response =
        doGet(API_V1_ADMIN_KAMEHOUSE_USERS + "username/" + kameHouseUser.getUsername());
    KameHouseUser responseBody = getResponseBody(response, KameHouseUser.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributes(kameHouseUser, responseBody);
  }

  /**
   * Tests get user not found exception.
   */
  @Test
  public void loadUserByUsernameNotFoundExceptionTest() throws Exception {
    assertThrows(NestedServletException.class, () -> {
      Mockito.doThrow(new KameHouseNotFoundException("")).when(kameHouseUserServiceMock)
          .loadUserByUsername(KameHouseUserTestUtils.INVALID_USERNAME);

      doGet(
          API_V1_ADMIN_KAMEHOUSE_USERS + "username/" + KameHouseUserTestUtils.INVALID_USERNAME);
    });
  }
}
