package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.admincommand.ScreenLockAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.ScreenUnlockAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.ScreenWakeUpAdminCommand;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for the ScreenController class.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class ScreenControllerTest extends AbstractAdminCommandControllerTest {

  @InjectMocks
  private ScreenController screenController;

  @Before
  public void beforeTest() {
    adminCommandControllerTestSetup();
    mockMvc = MockMvcBuilders.standaloneSetup(screenController).build();
  }

  /**
   * Lock screen successful test.
   */
  @Test
  public void lockScreenSuccessfulTest() throws Exception {
    executePostAdminCommandTest("/api/v1/admin/screen/lock", ScreenLockAdminCommand.class);
  }

  /**
   * Unlock screen successful test.
   */
  @Test
  public void unlockScreenSuccessfulTest() throws Exception {
    executePostAdminCommandTest("/api/v1/admin/screen/unlock", ScreenUnlockAdminCommand.class);
  }

  /**
   * Wake Up screen successful test.
   */
  @Test
  public void wakeUpScreenSuccessfulTest() throws Exception {
    executePostAdminCommandTest("/api/v1/admin/screen/wake-up", ScreenWakeUpAdminCommand.class);
  }
}