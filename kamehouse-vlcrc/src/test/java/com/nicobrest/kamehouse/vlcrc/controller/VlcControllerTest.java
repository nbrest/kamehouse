package com.nicobrest.kamehouse.vlcrc.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractKameHouseSystemCommandControllerTest;
import com.nicobrest.kamehouse.vlcrc.model.kamehousecommand.VlcStartKameHouseSystemCommand;
import com.nicobrest.kamehouse.vlcrc.model.kamehousecommand.VlcStatusKameHouseSystemCommand;
import com.nicobrest.kamehouse.vlcrc.model.kamehousecommand.VlcStopKameHouseSystemCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

/**
 * Unit tests for the VlcController class.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class VlcControllerTest extends AbstractKameHouseSystemCommandControllerTest {

  @InjectMocks
  private VlcController adminVlcController;

  @Before
  public void beforeTest() {
    kameHouseSystemCommandControllerTestSetup();
    mockMvc = MockMvcBuilders.standaloneSetup(adminVlcController).build();
  }

  /**
   * Starts VLC player successful test.
   */
  @Test
  public void startVlcPlayerTest() throws Exception {
    execPostKameHouseSystemCommandTest(
        "/api/v1/vlc-rc/vlc?file=src/test/resources/vlcrc/playlists/marvel.m3u",
        VlcStartKameHouseSystemCommand.class);
  }

  /**
   * Starts vlc exception test.
   */
  @Test
  public void startVlcExceptionTest() throws IOException, Exception {
    execPostInvalidKameHouseSystemCommandTest("/api/v1/vlc-rc/vlc?file=invalid-file");
  }

  /**
   * Stops VLC player successful test.
   */
  @Test
  public void stopVlcPlayerTest() throws Exception {
    execDeleteKameHouseSystemCommandTest("/api/v1/vlc-rc/vlc", VlcStopKameHouseSystemCommand.class);
  }

  /**
   * Stops VLC server error test.
   */
  @Test
  public void stopVlcPlayerServerErrorTest() throws Exception {
    execDeleteServerErrorKameHouseSystemCommandTest("/api/v1/vlc-rc/vlc",
        VlcStopKameHouseSystemCommand.class);
  }

  /**
   * Gets the status of VLC successful test.
   */
  @Test
  public void statusVlcPlayerTest() throws Exception {
    execGetKameHouseSystemCommandTest("/api/v1/vlc-rc/vlc", VlcStatusKameHouseSystemCommand.class);
  }
}