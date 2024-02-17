package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.kamehousecommand.KeyPressKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.MouseClickKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenLockKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenUnlockKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenWakeUpKameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.controller.AbstractKameHouseSystemCommandControllerTest;
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
class ScreenControllerTest extends AbstractKameHouseSystemCommandControllerTest {

  @InjectMocks
  private ScreenController screenController;

  @BeforeEach
  void beforeTest() {
    kameHouseSystemCommandControllerTestSetup();
    mockMvc = MockMvcBuilders.standaloneSetup(screenController).build();
  }

  /**
   * Locks screen successful test.
   */
  @Test
  void lockScreenSuccessfulTest() throws Exception {
    execPostKameHouseSystemCommandTest(
        "/api/v1/admin/screen/lock", ScreenLockKameHouseSystemCommand.class);
  }

  /**
   * Unlocks screen successful test.
   */
  @Test
  void unlockScreenSuccessfulTest() throws Exception {
    execPostKameHouseSystemCommandTest(
        "/api/v1/admin/screen/unlock", ScreenUnlockKameHouseSystemCommand.class);
  }

  /**
   * Wakes Up screen successful test.
   */
  @Test
  void wakeUpScreenSuccessfulTest() throws Exception {
    execPostKameHouseSystemCommandTest(
        "/api/v1/admin/screen/wake-up", ScreenWakeUpKameHouseSystemCommand.class);
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
    execPostKameHouseSystemCommandTest(apiUrl, KeyPressKameHouseSystemCommand.class);
  }

  /**
   * Mouse click successful test.
   */
  @ParameterizedTest
  @CsvSource({
      "500, 600, 1",
      "800, 600, 2"
  })
  void mouseClickSuccessfulTest(Integer xPosition, Integer yPosition, Integer clickCount)
      throws Exception {
    String apiUrl =
        "/api/v1/admin/screen/mouse-click?xPosition=" + xPosition + "&yPosition=" + yPosition
            + "&clickCount=" + clickCount;
    execPostKameHouseSystemCommandTest(apiUrl, MouseClickKameHouseSystemCommand.class);
  }

  /**
   * Mouse click error test.
   */
  @ParameterizedTest
  @CsvSource({
      "-1, 600, 1",
      "800, 10000, 2",
      "800, 1000, 900"
  })
  void mouseClickErrorTest(Integer xPosition, Integer yPosition, Integer clickCount) {
    String apiUrl =
        "/api/v1/admin/screen/mouse-click?xPosition=" + xPosition + "&yPosition=" + yPosition
            + "&clickCount=" + clickCount;
    execPostInvalidKameHouseSystemCommandTest(apiUrl);
  }
}
