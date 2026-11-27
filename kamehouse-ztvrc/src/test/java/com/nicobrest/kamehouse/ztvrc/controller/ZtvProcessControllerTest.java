package com.nicobrest.kamehouse.ztvrc.controller;

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
 * Unit tests for the ZtvController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
class ZtvProcessControllerTest extends AbstractKameHouseCommandControllerTest {

  @InjectMocks
  private ZtvProcessController ztvProcessController;

  @BeforeEach
  void beforeTest() {
    kameHouseCommandControllerTestSetup();
    mockMvc = MockMvcBuilders.standaloneSetup(ztvProcessController).build();
  }

  /**
   * Starts ZTV Player successful test.
   */
  @Test
  void startZtvPlayerTest() throws Exception {
    execPostKameHouseCommandsTest(
        "/api/v1/ztv-rc/ztv-process?file=src/test/resources/ztvrc/playlists/marvel.m3u");
  }

  /**
   * Starts ZTV exception test.
   */
  @Test
  void startZtvExceptionTest() {
    execPostInvalidKameHouseCommandsTest("/api/v1/ztv-rc/ztv-process?file=invalid..-file");
  }

  /**
   * Stops ZTV Player successful test.
   */
  @Test
  void stopZtvPlayerTest() throws Exception {
    execDeleteKameHouseCommandsTest("/api/v1/ztv-rc/ztv-process");
  }

  /**
   * Stops ZTV server error test.
   */
  @Test
  void stopZtvPlayerServerErrorTest() throws Exception {
    execDeleteServerErrorKameHouseCommandsTest("/api/v1/ztv-rc/ztv-process");
  }

  /**
   * Gets the status of ZTV successful test.
   */
  @Test
  void statusZtvPlayerTest() throws Exception {
    execGetKameHouseCommandsTest("/api/v1/ztv-rc/ztv-process");
  }
}
