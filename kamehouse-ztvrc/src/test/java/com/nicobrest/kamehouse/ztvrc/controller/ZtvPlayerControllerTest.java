package com.nicobrest.kamehouse.ztvrc.controller;

import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.controller.AbstractController;
import com.nicobrest.kamehouse.commons.controller.AbstractCrudControllerTest;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.ztvrc.model.ZtvPlayer;
import com.nicobrest.kamehouse.ztvrc.model.dto.ZtvPlayerDto;
import com.nicobrest.kamehouse.ztvrc.service.ZtvPlayerService;
import com.nicobrest.kamehouse.ztvrc.testutils.ZtvPlayerTestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Test class for the ZtvRcController.
 *
 * @author nbrest
 */
class ZtvPlayerControllerTest extends AbstractCrudControllerTest<ZtvPlayer, ZtvPlayerDto> {

  @Mock(name = "ztvPlayerService")
  private ZtvPlayerService ztvPlayerServiceMock;

  @InjectMocks
  private ZtvPlayerController ztvPlayerController;

  @Override
  public String getCrudUrl() {
    return ZtvPlayerTestUtils.API_V1_ZTVPLAYERS;
  }

  @Override
  public Class<ZtvPlayer> getEntityClass() {
    return ZtvPlayerTestUtils.getEntityClass();
  }

  @Override
  public CrudService<ZtvPlayer, ZtvPlayerDto> getCrudService() {
    return ztvPlayerServiceMock;
  }

  @Override
  public TestUtils<ZtvPlayer, ZtvPlayerDto> getTestUtils() {
    return new ZtvPlayerTestUtils();
  }

  @Override
  public AbstractController getController() {
    return ztvPlayerController;
  }

  /**
   * Tests getting a specific ZTV Player.
   */
  @Test
  void getByHostnameTest() throws Exception {
    ZtvPlayer ztvPlayer = testUtils.getSingleTestData();
    when(ztvPlayerServiceMock.getByHostname(ztvPlayer.getHostname())).thenReturn(ztvPlayer);

    MockHttpServletResponse response =
        doGet(ZtvPlayerTestUtils.API_V1_ZTVPLAYERS + "/hostname/" + ztvPlayer.getHostname());
    ZtvPlayer responseBody = getResponseBody(response, getEntityClass());

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributes(ztvPlayer, responseBody);
  }
}
