package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.kamehousecommand.AltTabKeyKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.EnterKeyKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.EscKeyKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.RightKeyKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenLockKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenUnlockKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenWakeUpKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.WinTabKeyKameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.controller.AbstractKameHouseSystemCommandControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
   * ESC key press successful test.
   */
  @Test
  void escKeySuccessfulTest() throws Exception {
    execPostKameHouseSystemCommandTest(
        "/api/v1/admin/screen/esc-key-press", EscKeyKameHouseSystemCommand.class);
  }

  /**
   * ENTER key press successful test.
   */
  @Test
  void enterKeySuccessfulTest() throws Exception {
    execPostKameHouseSystemCommandTest(
        "/api/v1/admin/screen/enter-key-press", EnterKeyKameHouseSystemCommand.class);
  }

  /**
   * ALT+TAB key press successful test.
   */
  @Test
  void altTabKeySuccessfulTest() throws Exception {
    execPostKameHouseSystemCommandTest(
        "/api/v1/admin/screen/alt-tab-key-press", AltTabKeyKameHouseSystemCommand.class);
  }

  /**
   * WIN+TAB key press successful test.
   */
  @Test
  void winTabKeySuccessfulTest() throws Exception {
    execPostKameHouseSystemCommandTest(
        "/api/v1/admin/screen/win-tab-key-press", WinTabKeyKameHouseSystemCommand.class);
  }

  /**
   * Right arrow key press successful test.
   */
  @Test
  void rightKeySuccessfulTest() throws Exception {
    execPostKameHouseSystemCommandTest(
        "/api/v1/admin/screen/right-key-press", RightKeyKameHouseSystemCommand.class);
  }
}
