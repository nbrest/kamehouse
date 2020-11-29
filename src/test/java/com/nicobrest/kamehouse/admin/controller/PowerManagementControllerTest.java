package com.nicobrest.kamehouse.admin.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import com.nicobrest.kamehouse.admin.model.admincommand.ShutdownAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.ShutdownCancelAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.ShutdownStatusAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.SuspendAdminCommand;

import com.nicobrest.kamehouse.admin.service.PowerManagementService;
import com.nicobrest.kamehouse.admin.service.SystemCommandService;
import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.main.exception.KameHouseInvalidCommandException;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

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

  @Mock
  protected PowerManagementService powerManagementService;

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
   * WOL server successful test.
   */
  @Test
  public void wolServerTest() throws Exception {
    doNothing().when(powerManagementService).wakeOnLan(anyString(), anyString());

    MockHttpServletResponse response = doPost("/api/v1/admin/power-management/wol"
        + "?server=media.server");

    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  /**
   * WOL mac and broadcast successful test.
   */
  @Test
  public void wolMacAndBroadcastTest() throws Exception {
    doNothing().when(powerManagementService).wakeOnLan(anyString(), anyString());

    MockHttpServletResponse response = doPost("/api/v1/admin/power-management/wol"
       + "?mac=AA:BB:CC:DD:EE:FF&broadcast=192.168.0.255");

    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  /**
   * WOL invalid request test.
   */
  @Test
  public void wolInvalidRequestTest() throws Exception {
    doNothing().when(powerManagementService).wakeOnLan(anyString());
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(
        KameHouseBadRequestException.class));
    thrown.expectMessage("server OR mac and broadcast parameters are required");

    doPost("/api/v1/admin/power-management/wol");
  }
}
