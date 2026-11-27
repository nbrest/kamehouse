package com.nicobrest.kamehouse.ztvrc.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcPlaylistItem;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcStatus;
import com.nicobrest.kamehouse.ztvrc.service.ZtvRcService;
import com.nicobrest.kamehouse.ztvrc.testutils.ZtvRcPlaylistTestUtils;
import com.nicobrest.kamehouse.ztvrc.testutils.ZtvRcStatusTestUtils;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Test class for the ZtvRcWebSocketController.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
class ZtvRcWebSocketControllerTest {

  private ZtvRcStatusTestUtils ztvRcStatusTestUtils = new ZtvRcStatusTestUtils();
  private ZtvRcStatus ztvRcStatus;
  private ZtvRcPlaylistTestUtils ztvRcPlaylistTestUtils = new ZtvRcPlaylistTestUtils();
  private List<ZtvRcPlaylistItem> ztvRcPlaylist;

  @InjectMocks
  private ZtvRcWebSocketController ztvRcWebSocketController;

  @Mock(name = "ztvRcService")
  private ZtvRcService ztvRcServiceMock;

  /**
   * Tests setup.
   */
  @BeforeEach
  void beforeTest() {
    ztvRcStatusTestUtils.initTestData();
    ztvRcStatus = ztvRcStatusTestUtils.getSingleTestData();
    ztvRcPlaylistTestUtils.initTestData();
    ztvRcPlaylist = ztvRcPlaylistTestUtils.getSingleTestData();
    MockitoAnnotations.openMocks(this);
    Mockito.reset(ztvRcServiceMock);
  }

  /**
   * Tests getting ZtvRcStatus.
   */
  @Test
  void getZtvRcStatusTest() {
    when(ztvRcServiceMock.getZtvRcStatus("localhost")).thenReturn(ztvRcStatus);

    ZtvRcStatus returnedZtvRcStatus = ztvRcWebSocketController.getZtvRcStatus();

    verify(ztvRcServiceMock, times(1)).getZtvRcStatus("localhost");
    ztvRcStatusTestUtils.assertEqualsAllAttributes(ztvRcStatus, returnedZtvRcStatus);
  }

  /**
   * Tests getting ZtvRcStatus when ZtvRcService returns null.
   */
  @Test
  void getZtvRcStatusNullTest() {
    when(ztvRcServiceMock.getZtvRcStatus("localhost")).thenReturn(null);
    ZtvRcStatus emptyZtvRcStatus = new ZtvRcStatus();

    ZtvRcStatus returnedZtvRcStatus = ztvRcWebSocketController.getZtvRcStatus();

    verify(ztvRcServiceMock, times(1)).getZtvRcStatus("localhost");
    ztvRcStatusTestUtils.assertEqualsAllAttributes(emptyZtvRcStatus, returnedZtvRcStatus);
  }

  /**
   * Tests getting ZtvRcStatus when ZtvRcService throws KameHouseNotFoundException.
   */
  @Test
  void getZtvRcStatusKameHouseNotFoundExceptionTest() {
    doThrow(new KameHouseNotFoundException("Entity not found"))
        .when(ztvRcServiceMock)
        .getZtvRcStatus("localhost");
    ZtvRcStatus emptyZtvRcStatus = new ZtvRcStatus();

    ZtvRcStatus returnedZtvRcStatus = ztvRcWebSocketController.getZtvRcStatus();

    verify(ztvRcServiceMock, times(1)).getZtvRcStatus("localhost");
    ztvRcStatusTestUtils.assertEqualsAllAttributes(emptyZtvRcStatus, returnedZtvRcStatus);
  }

  /**
   * Tests getting ZtvRcPlaylist.
   */
  @Test
  void getZtvRcPlaylistTest() {
    when(ztvRcServiceMock.getPlaylist("localhost")).thenReturn(ztvRcPlaylist);

    List<ZtvRcPlaylistItem> returnedPlaylist = ztvRcWebSocketController.getPlaylist();

    verify(ztvRcServiceMock, times(1)).getPlaylist("localhost");
    ztvRcPlaylistTestUtils.assertEqualsAllAttributes(ztvRcPlaylist, returnedPlaylist);
  }

  /**
   * Tests getting ZtvRcPlaylist when ZtvRcService returns an empty list.
   */
  @Test
  void getZtvRcPlaylistEmptyListTest() {
    List<ZtvRcPlaylistItem> emptyList = new ArrayList<>();
    when(ztvRcServiceMock.getPlaylist("localhost")).thenReturn(emptyList);

    List<ZtvRcPlaylistItem> returnedPlaylist = ztvRcWebSocketController.getPlaylist();

    verify(ztvRcServiceMock, times(1)).getPlaylist("localhost");
    ztvRcPlaylistTestUtils.assertEqualsAllAttributes(emptyList, returnedPlaylist);
  }

  /**
   * Tests getting ZtvRcPlaylist when ZtvRcService throws KameHouseNotFoundException.
   */
  @Test
  void getZtvRcPlaylistKameHouseNotFoundExceptionTest() {
    doThrow(new KameHouseNotFoundException("Entity not found"))
        .when(ztvRcServiceMock)
        .getZtvRcStatus("localhost");
    List<ZtvRcPlaylistItem> emptyList = new ArrayList<>();

    List<ZtvRcPlaylistItem> returnedPlaylist = ztvRcWebSocketController.getPlaylist();

    verify(ztvRcServiceMock, times(1)).getPlaylist("localhost");
    ztvRcPlaylistTestUtils.assertEqualsAllAttributes(emptyList, returnedPlaylist);
  }
}
