package com.nicobrest.kamehouse.admin.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.integration.AbstractCrudControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.testutils.KameHouseUserTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the KameHouseUserController class.
 *
 * @author nbrest
 */
class KameHouseUserControllerIntegrationTest
    extends AbstractCrudControllerIntegrationTest<KameHouseUser, KameHouseUserDto> {

  @Override
  public String getWebapp() {
    return "kame-house-admin";
  }

  @Override
  public String getCrudUrlSuffix() {
    return KameHouseUserTestUtils.API_V1_ADMIN_KAMEHOUSE_USERS;
  }

  @Override
  public Class<KameHouseUser> getEntityClass() {
    return KameHouseUser.class;
  }

  @Override
  public TestUtils<KameHouseUser, KameHouseUserDto> getTestUtils() {
    return new KameHouseUserTestUtils();
  }

  @Override
  public KameHouseUserDto buildDto(KameHouseUserDto dto) {
    String randomUsername = RandomStringUtils.randomAlphabetic(12);
    dto.setUsername(randomUsername);
    dto.setEmail(randomUsername + "@dbz.com");
    return dto;
  }

  @Override
  public void updateDto(KameHouseUserDto dto) {
    dto.setFirstName(RandomStringUtils.randomAlphabetic(12));
  }

  /**
   * Gets a kamehouse user.
   */
  @Test
  @Order(5)
  void loadUserByUsernameTest() throws Exception {
    logger.info("Running loadUserByUsernameTest");
    String username = getDto().getUsername();

    HttpResponse response = get(getCrudUrl() + "/username/" + username);

    verifySuccessfulResponse(response, KameHouseUser.class);
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
}
