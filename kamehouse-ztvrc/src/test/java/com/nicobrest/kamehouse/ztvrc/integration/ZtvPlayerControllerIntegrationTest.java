package com.nicobrest.kamehouse.ztvrc.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.integration.AbstractCrudControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.ztvrc.model.ZtvPlayer;
import com.nicobrest.kamehouse.ztvrc.model.dto.ZtvPlayerDto;
import com.nicobrest.kamehouse.ztvrc.testutils.ZtvPlayerTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the ZtvPlayerController class.
 *
 * @author nbrest
 */
class ZtvPlayerControllerIntegrationTest
    extends AbstractCrudControllerIntegrationTest<ZtvPlayer, ZtvPlayerDto> {

  @Override
  public String getWebapp() {
    return "kame-house-ztvrc";
  }

  @Override
  public String getCrudUrlSuffix() {
    return ZtvPlayerTestUtils.API_V1_ZTVPLAYERS;
  }

  @Override
  public Class<ZtvPlayer> getEntityClass() {
    return ZtvPlayerTestUtils.getEntityClass();
  }

  @Override
  public TestUtils<ZtvPlayer, ZtvPlayerDto> getTestUtils() {
    return new ZtvPlayerTestUtils();
  }

  @Override
  public ZtvPlayerDto buildDto(ZtvPlayerDto dto) {
    String randomUsername = RandomStringUtils.randomAlphabetic(12);
    dto.setUsername(randomUsername);
    dto.setHostname(randomUsername);
    return dto;
  }

  @Override
  public void updateDto(ZtvPlayerDto dto) {
    String username = RandomStringUtils.randomAlphabetic(12);
    dto.setUsername(username);
    dto.setHostname(username);
  }

  /**
   * Gets a ZTV Player.
   */
  @Test
  @Order(5)
  void loadUserByHostnameTest() throws Exception {
    logger.info("Running loadUserByHostnameTest");
    String hostname = getDto().getHostname();

    HttpResponse response = get(getCrudUrl() + "/hostname/" + hostname);

    verifySuccessfulResponse(response, getEntityClass());
  }

  /**
   * Tests get ZTV Player not found exception.
   */
  @Test
  @Order(5)
  void loadUserByHostnameNotFoundExceptionTest() throws Exception {
    logger.info("Running loadUserByHostnameNotFoundExceptionTest");
    String invalidHostname = "invalid-" + getDto().getHostname();

    HttpResponse response = get(getCrudUrl() + "/hostname/" + invalidHostname);

    assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    logger.info("loadUserByHostnameNotFoundExceptionTest completed successfully");
  }
}
