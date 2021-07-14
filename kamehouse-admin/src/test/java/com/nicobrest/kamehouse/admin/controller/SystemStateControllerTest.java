package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.kamehousecommand.DfKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.FreeKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.HttpdRestartKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.HttpdStatusKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.UptimeKameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.controller.AbstractKameHouseSystemCommandControllerTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for the SystemStateController class.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class SystemStateControllerTest extends AbstractKameHouseSystemCommandControllerTest {

  @InjectMocks
  private SystemStateController systemStateController;

  @Before
  public void beforeTest() {
    kameHouseSystemCommandControllerTestSetup();
    mockMvc = MockMvcBuilders.standaloneSetup(systemStateController).build();
  }

  /**
   * uptime successful test.
   */
  @Test
  public void uptimeSuccessfulTest() throws Exception {
    execGetKameHouseSystemCommandTest("/api/v1/admin/system-state/uptime",
        UptimeKameHouseSystemCommand.class);
  }

  /**
   * free successful test.
   */
  @Test
  public void freeSuccessfulTest() throws Exception {
    execGetKameHouseSystemCommandTest("/api/v1/admin/system-state/free",
        FreeKameHouseSystemCommand.class);
  }

  /**
   * df successful test.
   */
  @Test
  public void dfSuccessfulTest() throws Exception {
    execGetKameHouseSystemCommandTest("/api/v1/admin/system-state/df",
        DfKameHouseSystemCommand.class);
  }

  /**
   * httpd get status successful test.
   */
  @Test
  public void httpdGetStatusSuccessfulTest() throws Exception {
    execGetKameHouseSystemCommandTest("/api/v1/admin/system-state/httpd",
        HttpdStatusKameHouseSystemCommand.class);
  }

  /**
   * httpd restart successful test.
   */
  @Test
  public void httpdRestartSuccessfulTest() throws Exception {
    execPostKameHouseSystemCommandTest("/api/v1/admin/system-state/httpd",
        HttpdRestartKameHouseSystemCommand.class);
  }
}