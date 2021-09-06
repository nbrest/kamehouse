package com.nicobrest.kamehouse.vlcrc.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.integration.AbstractCrudControllerIntegrationTest;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.model.dto.VlcPlayerDto;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcPlayerTestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the VlcPlayerController class.
 *
 * @author nbrest
 */
public class VlcPlayerControllerIntegrationTest
    extends AbstractCrudControllerIntegrationTest<VlcPlayer, VlcPlayerDto> {

  @Override
  public String getWebapp() {
    return "kame-house-vlcrc";
  }

  @Override
  public String getCrudUrlSuffix() {
    return VlcPlayerTestUtils.API_V1_VLCPLAYERS;
  }

  @Override
  public Class<VlcPlayer> getEntityClass() {
    return VlcPlayer.class;
  }

  @Override
  public TestUtils<VlcPlayer, VlcPlayerDto> getTestUtils() {
    return new VlcPlayerTestUtils();
  }

  @Override
  public VlcPlayerDto buildDto(VlcPlayerDto dto) {
    String randomUsername = RandomStringUtils.randomAlphabetic(12);
    dto.setUsername(randomUsername);
    dto.setHostname(randomUsername);
    return dto;
  }

  @Override
  public void updateDto(VlcPlayerDto dto) {
    String username = RandomStringUtils.randomAlphabetic(12);
    dto.setUsername(username);
    dto.setHostname(username);
  }

  /**
   * Gets a vlc player.
   */
  @Test
  @Order(5)
  public void loadUserByHostnameTest() throws Exception {
    logger.info("Running loadUserByHostnameTest");
    String hostname = getDto().getHostname();

    HttpResponse response = get(getCrudUrl() + "hostname/" + hostname);

    verifySuccessfulResponse(response, VlcPlayer.class);
  }

  /**
   * Tests get vlc player not found exception.
   */
  @Test
  @Order(5)
  public void loadUserByHostnameNotFoundExceptionTest() throws Exception {
    logger.info("Running loadUserByHostnameNotFoundExceptionTest");
    String invalidHostname = "invalid-" + getDto().getHostname();

    HttpResponse response = get(getCrudUrl() + "hostname/" + invalidHostname);

    assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    logger.info("loadUserByHostnameNotFoundExceptionTest completed successfully");
  }
}
