package com.nicobrest.kamehouse.vlcrc.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractKameHouseSystemCommandControllerTest;
import com.nicobrest.kamehouse.vlcrc.model.kamehousecommand.VlcStartKameHouseSystemCommand;
import com.nicobrest.kamehouse.vlcrc.model.kamehousecommand.VlcStatusKameHouseSystemCommand;
import com.nicobrest.kamehouse.vlcrc.model.kamehousecommand.VlcStopKameHouseSystemCommand;
import java.io.IOException;
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
class VlcProcessControllerTest extends AbstractKameHouseSystemCommandControllerTest {

  @InjectMocks private VlcProcessController vlcProcessController;

  @BeforeEach
  void beforeTest() {
    kameHouseSystemCommandControllerTestSetup();
    mockMvc = MockMvcBuilders.standaloneSetup(vlcProcessController).build();
  }

  /** Starts VLC player successful test. */
  @Test
  void startVlcPlayerTest() throws Exception {
    execPostKameHouseSystemCommandTest(
        "/api/v1/vlc-rc/vlc-process?file=src/test/resources/vlcrc/playlists/marvel.m3u",
        VlcStartKameHouseSystemCommand.class);
  }

  /** Starts vlc exception test. */
  @Test
  void startVlcExceptionTest() throws IOException, Exception {
    execPostInvalidKameHouseSystemCommandTest("/api/v1/vlc-rc/vlc-process?file=invalid-file");
  }

  /** Stops VLC player successful test. */
  @Test
  void stopVlcPlayerTest() throws Exception {
    execDeleteKameHouseSystemCommandTest(
        "/api/v1/vlc-rc/vlc-process", VlcStopKameHouseSystemCommand.class);
  }

  /** Stops VLC server error test. */
  @Test
  void stopVlcPlayerServerErrorTest() throws Exception {
    execDeleteServerErrorKameHouseSystemCommandTest(
        "/api/v1/vlc-rc/vlc-process", VlcStopKameHouseSystemCommand.class);
  }

  /** Gets the status of VLC successful test. */
  @Test
  void statusVlcPlayerTest() throws Exception {
    execGetKameHouseSystemCommandTest(
        "/api/v1/vlc-rc/vlc-process", VlcStatusKameHouseSystemCommand.class);
  }
}
