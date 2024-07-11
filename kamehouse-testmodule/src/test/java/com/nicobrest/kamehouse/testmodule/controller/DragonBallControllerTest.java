package com.nicobrest.kamehouse.testmodule.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.controller.AbstractController;
import com.nicobrest.kamehouse.commons.controller.AbstractCrudControllerTest;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.service.DragonBallUserService;
import com.nicobrest.kamehouse.testmodule.testutils.DragonBallUserTestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import jakarta.servlet.ServletException;

/**
 * Unit tests for the DragonBallController class.
 *
 * @author nbrest
 */
class DragonBallControllerTest
    extends AbstractCrudControllerTest<DragonBallUser, DragonBallUserDto> {

  private static final String API_V1_DRAGONBALL_USERS =
      DragonBallUserTestUtils.API_V1_DRAGONBALL_USERS;

  @InjectMocks
  private DragonBallController dragonBallController;

  @Mock(name = "dragonBallUserService")
  private DragonBallUserService dragonBallUserServiceMock;

  @Override
  public String getCrudUrl() {
    return DragonBallUserTestUtils.API_V1_DRAGONBALL_USERS;
  }

  @Override
  public Class<DragonBallUser> getEntityClass() {
    return DragonBallUser.class;
  }

  @Override
  public CrudService<DragonBallUser, DragonBallUserDto> getCrudService() {
    return dragonBallUserServiceMock;
  }

  @Override
  public TestUtils<DragonBallUser, DragonBallUserDto> getTestUtils() {
    return new DragonBallUserTestUtils();
  }

  @Override
  public AbstractController getController() {
    return dragonBallController;
  }

  /**
   * Actions to perform once before all tests.
   */
  @BeforeAll
  static void beforeClassTest() {
    /* Initialization tasks that happen once for all tests. */
  }

  /**
   * Clean up after each test.
   */
  @AfterEach
  void afterTest() {
    /* Actions to perform after each test */

  }

  /**
   * Cleanup after all tests have executed.
   */
  @AfterAll
  static void afterClassTest() {
    /* Actions to perform ONCE after all tests in the class */
  }

  /**
   * Tests the rest web service on the endpoint /dragonball/users with the parameter to throw an
   * exception.
   */
  @ParameterizedTest
  @ValueSource(strings = {
      "KameHouseException",
      "KameHouseConflictException",
      "KameHouseNotFoundException",
      "NullPointerException",
      "IndexOutOfBoundsException"
  })
  void readAllKameHouseExceptionTest(String exception) {
    assertThrows(
        ServletException.class,
        () -> {
          doGet(API_V1_DRAGONBALL_USERS + "?action=" + exception);
        });
  }

  /**
   * Tests getting a specific user from the repository.
   */
  @Test
  void getByUsernameTest() throws Exception {
    DragonBallUser dragonBallUser = testUtils.getSingleTestData();
    when(dragonBallUserServiceMock.getByUsername(dragonBallUser.getUsername()))
        .thenReturn(dragonBallUser);

    MockHttpServletResponse response =
        doGet(API_V1_DRAGONBALL_USERS + "/username/" + dragonBallUser.getUsername());
    DragonBallUser responseBody = getResponseBody(response, DragonBallUser.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributes(dragonBallUser, responseBody);
  }

  /**
   * Tests user not found when getting a specific user from the repository.
   */
  @Test
  void getByUsernameNotFoundExceptionTest() {
    assertThrows(
        ServletException.class,
        () -> {
          Mockito.doThrow(new KameHouseNotFoundException(""))
              .when(dragonBallUserServiceMock)
              .getByUsername(DragonBallUserTestUtils.INVALID_USERNAME);

          doGet(API_V1_DRAGONBALL_USERS + "/username/" + DragonBallUserTestUtils.INVALID_USERNAME);
        });
  }

  /**
   * Tests getting a specific user from the repository by email.
   */
  @Test
  void getByEmailTest() throws Exception {
    DragonBallUser dragonBallUser = testUtils.getSingleTestData();
    when(dragonBallUserServiceMock.getByEmail(dragonBallUser.getEmail()))
        .thenReturn(dragonBallUser);

    MockHttpServletResponse response =
        doGet(API_V1_DRAGONBALL_USERS + "/emails?email=" + dragonBallUser.getEmail());
    DragonBallUser responseBody = getResponseBody(response, DragonBallUser.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributes(dragonBallUser, responseBody);
  }

  /*
   * @Ignore("Disabled test example")
   *
   * @Test void disabledTest() { // @Ignore disables the execution of the
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
