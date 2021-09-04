package com.nicobrest.kamehouse.admin.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.integration.AbstractCrudControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.testutils.KameHouseUserTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the KameHouseUserController class.
 *
 * @author nbrest
 */
public class KameHouseUserControllerIntegrationTest
    extends AbstractCrudControllerIntegrationTest<KameHouseUser, KameHouseUserDto> {

  @Override
  public String getWebapp() {
    return "/kame-house-admin";
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
  public void loadUserByUsernameTest() throws Exception {
    logger.info("Running loadUserByUsernameTest");
    String username = getDto().getUsername();
    HttpGet get = HttpClientUtils.httpGet(getCrudUrl() + "username/" + username);

    HttpResponse response = getHttpClient().execute(get);

    verifySuccessfulOkResponse(response);
  }

  /**
   * Tests get user not found exception.
   */
  @Test
  @Order(5)
  public void loadUserByUsernameNotFoundExceptionTest() throws Exception {
    logger.info("Running loadUserByUsernameNotFoundExceptionTest");
    String invalidUsername = "invalid-" + getDto().getUsername();
    HttpGet get = HttpClientUtils.httpGet(getCrudUrl() + "username/" + invalidUsername);

    HttpResponse response = getHttpClient().execute(get);

    assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    logger.info("loadUserByUsernameNotFoundExceptionTest completed successfully");
  }
}
