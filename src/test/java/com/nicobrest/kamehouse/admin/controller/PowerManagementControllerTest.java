package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.admincommand.ShutdownAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.ShutdownCancelAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.ShutdownStatusAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.SuspendAdminCommand;

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
 * Unit tests for PowerManagementController class.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class PowerManagementControllerTest extends AbstractAdminCommandControllerTest {

  @InjectMocks
  private PowerManagementController adminPowerManagementController;

  @Before
  public void beforeTest() {
    adminCommandControllerTestSetup();
    mockMvc = MockMvcBuilders.standaloneSetup(adminPowerManagementController).build();
  }

  /**
   * Sets shutdown successful test.
   */
  @Test
  public void setShutdownTest() throws Exception {
    execPostAdminCommandTest("/api/v1/admin/power-management/shutdown?delay=5400",
        ShutdownAdminCommand.class);
  }

  /**
   * Sets shutdown exception test.
   */
  @Test
  public void setShutdownExceptionTest() throws IOException, Exception {
    execPostInvalidAdminCommandTest("/api/v1/admin/power-management/shutdown?delay=0");
  }

  /**
   * Cancels shutdown successful test.
   */
  @Test
  public void cancelShutdownTest() throws Exception {
    execDeleteAdminCommandTest("/api/v1/admin/power-management/shutdown",
        ShutdownCancelAdminCommand.class);
  }

  /**
   * Cancels shutdown server error test.
   */
  @Test
  public void cancelShutdownServerErrorTest() throws Exception {
    execDeleteServerErrorAdminCommandTest("/api/v1/admin/power-management/shutdown",
        ShutdownCancelAdminCommand.class);
  }

  /**
   * Shutdowns status successful test.
   */
  @Test
  public void statusShutdownTest() throws Exception {
    execGetAdminCommandTest("/api/v1/admin/power-management/shutdown",
        ShutdownStatusAdminCommand.class);
  }

  /**
   * Suspends server successful test.
   */
  @Test
  public void suspendTest() throws Exception {
    execPostAdminCommandTest("/api/v1/admin/power-management/suspend", SuspendAdminCommand.class);
  }
}
