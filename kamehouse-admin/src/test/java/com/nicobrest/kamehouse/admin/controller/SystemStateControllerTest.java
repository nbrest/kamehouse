package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.kamehousecommand.DfKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.FreeKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.HttpdRestartKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.HttpdStatusKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.TopKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.UptimeKameHouseSystemCommand;
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
 * Unit tests for the SystemStateController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
class SystemStateControllerTest extends AbstractKameHouseSystemCommandControllerTest {

  @InjectMocks
  private SystemStateController systemStateController;

  @BeforeEach
  void beforeTest() {
    kameHouseSystemCommandControllerTestSetup();
    mockMvc = MockMvcBuilders.standaloneSetup(systemStateController).build();
  }

  /**
   * uptime successful test.
   */
  @Test
  void uptimeSuccessfulTest() throws Exception {
    execGetKameHouseSystemCommandTest(
        "/api/v1/admin/system-state/uptime", UptimeKameHouseSystemCommand.class);
  }

  /**
   * free successful test.
   */
  @Test
  void freeSuccessfulTest() throws Exception {
    execGetKameHouseSystemCommandTest(
        "/api/v1/admin/system-state/free", FreeKameHouseSystemCommand.class);
  }

  /**
   * df successful test.
   */
  @Test
  void dfSuccessfulTest() throws Exception {
    execGetKameHouseSystemCommandTest(
        "/api/v1/admin/system-state/df", DfKameHouseSystemCommand.class);
  }

  /**
   * top successful test.
   */
  @Test
  void topSuccessfulTest() throws Exception {
    execGetKameHouseSystemCommandTest(
        "/api/v1/admin/system-state/top", TopKameHouseSystemCommand.class);
  }

  /**
   * httpd get status successful test.
   */
  @Test
  void httpdGetStatusSuccessfulTest() throws Exception {
    execGetKameHouseSystemCommandTest(
        "/api/v1/admin/system-state/httpd", HttpdStatusKameHouseSystemCommand.class);
  }

  /**
   * httpd restart successful test.
   */
  @Test
  void httpdRestartSuccessfulTest() throws Exception {
    execPostKameHouseSystemCommandTest(
        "/api/v1/admin/system-state/httpd", HttpdRestartKameHouseSystemCommand.class);
  }
}
