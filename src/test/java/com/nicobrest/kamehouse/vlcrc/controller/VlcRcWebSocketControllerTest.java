package com.nicobrest.kamehouse.vlcrc.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import com.nicobrest.kamehouse.vlcrc.service.VlcRcService;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcStatusTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for the VlcRcWebSocketController.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class VlcRcWebSocketControllerTest {

  private VlcRcStatusTestUtils vlcRcStatusTestUtils = new VlcRcStatusTestUtils();
  private VlcRcStatus vlcRcStatus;

  @InjectMocks
  private VlcRcWebSocketController vlcRcWebSocketController;

  @Mock(name = "vlcRcService")
  private VlcRcService vlcRcServiceMock;

  @Before
  public void beforeTest() {
    vlcRcStatusTestUtils.initTestData();
    vlcRcStatus = vlcRcStatusTestUtils.getSingleTestData();

    MockitoAnnotations.initMocks(this);
    Mockito.reset(vlcRcServiceMock);
  }

  /**
   * Tests getting VlcRcStatus.
   */
  @Test
  public void getVlcRcStatusTest() {
    when(vlcRcServiceMock.getVlcRcStatus("localhost")).thenReturn(vlcRcStatus);

    VlcRcStatus returnedVlcRcStatus = vlcRcWebSocketController.getVlcRcStatus();

    verify(vlcRcServiceMock, times(1)).getVlcRcStatus("localhost");
    vlcRcStatusTestUtils.assertEqualsAllAttributes(vlcRcStatus, returnedVlcRcStatus);
  }

  /**
   * Tests getting VlcRcStatus when VlcRcService returns null.
   */
  @Test
  public void getVlcRcStatusNullTest() {
    when(vlcRcServiceMock.getVlcRcStatus("localhost")).thenReturn(null);
    VlcRcStatus emptyVlcRcStatus = new VlcRcStatus();

    VlcRcStatus returnedVlcRcStatus = vlcRcWebSocketController.getVlcRcStatus();

    verify(vlcRcServiceMock, times(1)).getVlcRcStatus("localhost");
    vlcRcStatusTestUtils.assertEqualsAllAttributes(emptyVlcRcStatus, returnedVlcRcStatus);
  }

  /**
   * Tests getting VlcRcStatus when VlcRcService throws KameHouseNotFoundException.
   */
  @Test
  public void getVlcRcStatusKameHouseNotFoundExceptionTest() {
    Mockito.doThrow(new KameHouseNotFoundException("Entity not found"))
        .when(vlcRcServiceMock).getVlcRcStatus("localhost");
    VlcRcStatus emptyVlcRcStatus = new VlcRcStatus();

    VlcRcStatus returnedVlcRcStatus = vlcRcWebSocketController.getVlcRcStatus();

    verify(vlcRcServiceMock, times(1)).getVlcRcStatus("localhost");
    vlcRcStatusTestUtils.assertEqualsAllAttributes(emptyVlcRcStatus, returnedVlcRcStatus);
  }

  //TODO: ADD UNIT TESTS FOR PLAYLIST WS ENDPOINT
}
