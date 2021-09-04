package com.nicobrest.kamehouse.admin.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
  public String getCrudSuffix() {
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
  public KameHouseUser buildEntity(KameHouseUser entity) {
    String randomUsername = RandomStringUtils.randomAlphabetic(12);
    entity.setUsername(randomUsername);
    entity.setEmail(randomUsername + "@dbz.com");
    return entity;
  }

  @Override
  public void updateEntity(KameHouseUser entity) {
    entity.setFirstName(RandomStringUtils.randomAlphabetic(12));
  }

  /**
   * Gets a kamehouse user.
   */
  @Test
  @Order(5)
  public void loadUserByUsernameTest() throws Exception {
    logger.info("Running loadUserByUsernameTest");
    String username = getEntity().getUsername();
    HttpGet get = HttpClientUtils.httpGet(getCrudUrl() + "username/" + username);

    HttpResponse response = getHttpClient().execute(get);

    assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    KameHouseUser responseBody = getResponseBody(response, KameHouseUser.class);
    assertNotNull(responseBody);
    logger.info("Response body {}", responseBody);
  }

  /**
   * Tests get user not found exception.
   */
  @Test
  @Order(5)
  public void loadUserByUsernameNotFoundExceptionTest() throws Exception {
    logger.info("Running loadUserByUsernameNotFoundExceptionTest");
    String invalidUsername = "invalid-" + getEntity().getUsername();
    HttpGet get = HttpClientUtils.httpGet(getCrudUrl() + "username/" + invalidUsername);

    HttpResponse response = getHttpClient().execute(get);

    assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    logger.info("loadUserByUsernameNotFoundExceptionTest completed successfully");
  }
}
