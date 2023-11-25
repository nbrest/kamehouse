package com.nicobrest.kamehouse.vlcrc.controller;

import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.controller.AbstractController;
import com.nicobrest.kamehouse.commons.controller.AbstractCrudControllerTest;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.model.dto.VlcPlayerDto;
import com.nicobrest.kamehouse.vlcrc.service.VlcPlayerService;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcPlayerTestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Test class for the VlcRcController.
 *
 * @author nbrest
 */
public class VlcPlayerControllerTest extends AbstractCrudControllerTest<VlcPlayer, VlcPlayerDto> {

  @InjectMocks
  private VlcPlayerController vlcPlayerController;

  @Mock(name = "vlcPlayerService")
  private VlcPlayerService vlcPlayerServiceMock;

  @Override
  public String getCrudUrl() {
    return VlcPlayerTestUtils.API_V1_VLCPLAYERS;
  }

  @Override
  public Class<VlcPlayer> getEntityClass() {
    return VlcPlayer.class;
  }

  @Override
  public CrudService<VlcPlayer, VlcPlayerDto> getCrudService() {
    return vlcPlayerServiceMock;
  }

  @Override
  public TestUtils<VlcPlayer, VlcPlayerDto> getTestUtils() {
    return new VlcPlayerTestUtils();
  }

  @Override
  public AbstractController getController() {
    return vlcPlayerController;
  }

  /**
   * Tests getting a specific VLC Player.
   */
  @Test
  void getByHostnameTest() throws Exception {
    VlcPlayer vlcPlayer = testUtils.getSingleTestData();
    when(vlcPlayerServiceMock.getByHostname(vlcPlayer.getHostname())).thenReturn(vlcPlayer);

    MockHttpServletResponse response =
        doGet(VlcPlayerTestUtils.API_V1_VLCPLAYERS + "/hostname/" + vlcPlayer.getHostname());
    VlcPlayer responseBody = getResponseBody(response, VlcPlayer.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributes(vlcPlayer, responseBody);
  }
}
