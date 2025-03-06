package com.nicobrest.kamehouse.vlcrc.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractKameHouseCommandControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for the VlcController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
class VlcProcessControllerTest extends AbstractKameHouseCommandControllerTest {

  @InjectMocks
  private VlcProcessController vlcProcessController;

  @BeforeEach
  void beforeTest() {
    kameHouseCommandControllerTestSetup();
    mockMvc = MockMvcBuilders.standaloneSetup(vlcProcessController).build();
  }

  /**
   * Starts VLC player successful test.
   */
  @Test
  void startVlcPlayerTest() throws Exception {
    execPostKameHouseCommandsTest(
        "/api/v1/vlc-rc/vlc-process?file=src/test/resources/vlcrc/playlists/marvel.m3u");
  }

  /**
   * Starts vlc exception test.
   */
  @Test
  void startVlcExceptionTest() {
    execPostInvalidKameHouseCommandsTest("/api/v1/vlc-rc/vlc-process?file=invalid-file");
  }

  /**
   * Stops VLC player successful test.
   */
  @Test
  void stopVlcPlayerTest() throws Exception {
    execDeleteKameHouseCommandsTest("/api/v1/vlc-rc/vlc-process");
  }

  /**
   * Stops VLC server error test.
   */
  @Test
  void stopVlcPlayerServerErrorTest() throws Exception {
    execDeleteServerErrorKameHouseCommandsTest("/api/v1/vlc-rc/vlc-process");
  }

  /**
   * Gets the status of VLC successful test.
   */
  @Test
  void statusVlcPlayerTest() throws Exception {
    execGetKameHouseCommandsTest("/api/v1/vlc-rc/vlc-process");
  }
}
