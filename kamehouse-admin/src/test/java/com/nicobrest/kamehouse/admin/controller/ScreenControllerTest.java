package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenLockKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenUnlockKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.ScreenWakeUpKameHouseSystemCommand;

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
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class ScreenControllerTest extends AbstractKameHouseSystemCommandControllerTest {

  @InjectMocks
  private ScreenController screenController;

  @BeforeEach
  public void beforeTest() {
    kameHouseSystemCommandControllerTestSetup();
    mockMvc = MockMvcBuilders.standaloneSetup(screenController).build();
  }

  /**
   * Locks screen successful test.
   */
  @Test
  public void lockScreenSuccessfulTest() throws Exception {
    execPostKameHouseSystemCommandTest("/api/v1/admin/screen/lock", ScreenLockKameHouseSystemCommand.class);
  }

  /**
   * Unlocks screen successful test.
   */
  @Test
  public void unlockScreenSuccessfulTest() throws Exception {
    execPostKameHouseSystemCommandTest("/api/v1/admin/screen/unlock", ScreenUnlockKameHouseSystemCommand.class);
  }

  /**
   * Wakes Up screen successful test.
   */
  @Test
  public void wakeUpScreenSuccessfulTest() throws Exception {
    execPostKameHouseSystemCommandTest("/api/v1/admin/screen/wake-up", ScreenWakeUpKameHouseSystemCommand.class);
  }
}