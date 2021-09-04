package com.nicobrest.kamehouse.admin.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.admin.service.KameHouseUserService;
import com.nicobrest.kamehouse.commons.controller.AbstractController;
import com.nicobrest.kamehouse.commons.controller.AbstractCrudControllerTest;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.testutils.KameHouseUserTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.NestedServletException;

/**
 * Unit tests for the KameHouseUserController class.
 *
 * @author nbrest
 */
public class KameHouseUserControllerTest
    extends AbstractCrudControllerTest<KameHouseUser, KameHouseUserDto> {

  private static final String API_V1_ADMIN_KAMEHOUSE_USERS =
      KameHouseUserTestUtils.API_V1_ADMIN_KAMEHOUSE_USERS;

  @InjectMocks
  private KameHouseUserController kameHouseUserController;

  @Mock(name = "kameHouseUserService")
  private KameHouseUserService kameHouseUserServiceMock;

  @Override
  public String getCrudUrl() {
    return KameHouseUserTestUtils.API_V1_ADMIN_KAMEHOUSE_USERS;
  }

  @Override
  public Class<KameHouseUser> getEntityClass() {
    return KameHouseUser.class;
  }

  @Override
  public CrudService<KameHouseUser, KameHouseUserDto> getCrudService() {
    return kameHouseUserServiceMock;
  }

  @Override
  public TestUtils<KameHouseUser, KameHouseUserDto> getTestUtils() {
    return new KameHouseUserTestUtils();
  }

  @Override
  public AbstractController getController() {
    return kameHouseUserController;
  }

  /**
   * Gets an kamehouse user.
   */
  @Test
  public void loadUserByUsernameTest() throws Exception {
    KameHouseUser kameHouseUser = testUtils.getSingleTestData();
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
  public void loadUserByUsernameNotFoundExceptionTest() {
    assertThrows(
        NestedServletException.class,
        () -> {
          Mockito.doThrow(new KameHouseNotFoundException(""))
              .when(kameHouseUserServiceMock)
              .loadUserByUsername(KameHouseUserTestUtils.INVALID_USERNAME);

          doGet(
              API_V1_ADMIN_KAMEHOUSE_USERS + "username/" + KameHouseUserTestUtils.INVALID_USERNAME);
        });
  }
}
