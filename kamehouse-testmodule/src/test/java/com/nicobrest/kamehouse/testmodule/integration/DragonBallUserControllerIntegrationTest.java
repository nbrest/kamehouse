package com.nicobrest.kamehouse.testmodule.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.integration.AbstractCrudControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.testutils.DragonBallUserTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the DragonBallUserController class.
 *
 * @author nbrest
 */
class DragonBallUserControllerIntegrationTest
    extends AbstractCrudControllerIntegrationTest<DragonBallUser, DragonBallUserDto> {

  @Override
  public String getWebapp() {
    return "kame-house-testmodule";
  }

  @Override
  public String getCrudUrlSuffix() {
    return DragonBallUserTestUtils.API_V1_DRAGONBALL_USERS;
  }

  @Override
  public Class<DragonBallUser> getEntityClass() {
    return DragonBallUser.class;
  }

  @Override
  public TestUtils<DragonBallUser, DragonBallUserDto> getTestUtils() {
    return new DragonBallUserTestUtils();
  }

  @Override
  public DragonBallUserDto buildDto(DragonBallUserDto dto) {
    String randomUsername = RandomStringUtils.randomAlphabetic(12);
    dto.setUsername(randomUsername);
    dto.setEmail(randomUsername + "@dbz.com");
    return dto;
  }

  @Override
  public void updateDto(DragonBallUserDto dto) {
    String username = RandomStringUtils.randomAlphabetic(12);
    dto.setUsername(username);
    dto.setEmail(username + "@dbz.com");
  }

  /**
   * Gets a user.
   */
  @Test
  @Order(5)
  void loadUserByUsernameTest() throws Exception {
    logger.info("Running loadUserByUsernameTest");
    String username = getDto().getUsername();

    HttpResponse response = get(getCrudUrl() + "/username/" + username);

    verifySuccessfulResponse(response, DragonBallUser.class);
  }

  /**
   * Tests get user not found exception.
   */
  @Test
  @Order(5)
  void loadUserByUsernameNotFoundExceptionTest() throws Exception {
    logger.info("Running loadUserByUsernameNotFoundExceptionTest");
    String invalidUsername = "invalid-" + getDto().getUsername();

    HttpResponse response = get(getCrudUrl() + "/username/" + invalidUsername);

    assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    logger.info("loadUserByUsernameNotFoundExceptionTest completed successfully");
  }

  /**
   * Gets a user.
   */
  @Test
  @Order(5)
  void loadUserByEmailTest() throws Exception {
    logger.info("Running loadUserByEmailTest");
    String email = getDto().getEmail();

    HttpResponse response = get(getCrudUrl() + "/emails?email=" + email);

    verifySuccessfulResponse(response, DragonBallUser.class);
  }

  /**
   * Tests get user not found exception.
   */
  @Test
  @Order(5)
  void loadUserByEmailNotFoundExceptionTest() throws Exception {
    logger.info("Running loadUserByEmailNotFoundExceptionTest");
    String invalidEmail = "invalid-" + getDto().getEmail();

    HttpResponse response = get(getCrudUrl() + "/emails?email=" + invalidEmail);

    assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    logger.info("loadUserByEmailNotFoundExceptionTest completed successfully");
  }
}
