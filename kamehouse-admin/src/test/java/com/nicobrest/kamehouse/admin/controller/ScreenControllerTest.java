package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractKameHouseCommandControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for the ScreenController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
class ScreenControllerTest extends AbstractKameHouseCommandControllerTest {

  @InjectMocks
  private ScreenController screenController;

  @BeforeEach
  void beforeTest() {
    kameHouseCommandControllerTestSetup();
    mockMvc = MockMvcBuilders.standaloneSetup(screenController).build();
  }

  /**
   * Locks screen successful test.
   */
  @Test
  void lockScreenSuccessfulTest() throws Exception {
    execPostKameHouseCommandsTest("/api/v1/admin/screen/lock");
  }

  /**
   * Unlocks screen successful test.
   */
  @Test
  void unlockScreenSuccessfulTest() throws Exception {
    execPostKameHouseCommandsTest("/api/v1/admin/screen/unlock");
  }

  /**
   * Wakes Up screen successful test.
   */
  @Test
  void wakeUpScreenSuccessfulTest() throws Exception {
    execPostKameHouseCommandsTest("/api/v1/admin/screen/wake-up");
  }

  /**
   * Key press successful test.
   */
  @ParameterizedTest
  @CsvSource({
      "ALT_F4, 1000",
      "ALT_TAB, 2",
      "ARROW_UP, 2",
      "ARROW_DOWN, 2",
      "ARROW_LEFT, 2",
      "ARROW_RIGHT, 2",
      "CTRL_F, 2",
      "ENTER, 2",
      "ESC, 2",
      "WIN, 2",
      "WIN_TAB, 2"
  })
  void keyPressSuccessfulTest(String key, Integer keyPresses) throws Exception {
    String apiUrl = "/api/v1/admin/screen/key-press?key=" + key + "&keyPresses=" + keyPresses;
    execPostKameHouseCommandsTest(apiUrl);
  }

  /**
   * Mouse click successful test.
   */
  @ParameterizedTest
  @CsvSource({
      "LEFT,500, 600, 1",
      "RIGHT,800, 600, 2"
  })
  void mouseClickSuccessfulTest(String mouseButton, Integer positionX, Integer positionY,
      Integer clickCount) throws Exception {
    String apiUrl =
        "/api/v1/admin/screen/mouse-click?mouseButton=" + mouseButton + "&positionX=" + positionX
            + "&positionY=" + positionY + "&clickCount=" + clickCount;
    execPostKameHouseCommandsTest(apiUrl);
  }

  /**
   * Mouse click error test.
   */
  @ParameterizedTest
  @CsvSource({
      "LEFT, -1, 600, 1",
      "LEFT, 800, 10000, 2",
      "RIGHT, 800, 1000, 900"
  })
  void mouseClickErrorTest(String mouseButton, Integer positionX, Integer positionY,
      Integer clickCount) {
    String apiUrl =
        "/api/v1/admin/screen/mouse-click?mouseButton=" + mouseButton + "&positionX=" + positionX
            + "&positionY=" + positionY + "&clickCount=" + clickCount;
    execPostInvalidKameHouseCommandsTest(apiUrl);
  }
}
