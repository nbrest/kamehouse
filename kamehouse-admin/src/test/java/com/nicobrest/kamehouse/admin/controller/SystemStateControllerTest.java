package com.nicobrest.kamehouse.admin.controller;

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
 * Unit tests for the SystemStateController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
class SystemStateControllerTest extends AbstractKameHouseCommandControllerTest {

  @InjectMocks
  private SystemStateController systemStateController;

  @BeforeEach
  void beforeTest() {
    kameHouseCommandControllerTestSetup();
    mockMvc = MockMvcBuilders.standaloneSetup(systemStateController).build();
  }

  /**
   * uptime successful test.
   */
  @Test
  void uptimeSuccessfulTest() throws Exception {
    execGetKameHouseCommandsTest("/api/v1/admin/system-state/uptime");
  }

  /**
   * free successful test.
   */
  @Test
  void freeSuccessfulTest() throws Exception {
    execGetKameHouseCommandsTest("/api/v1/admin/system-state/free");
  }

  /**
   * df successful test.
   */
  @Test
  void dfSuccessfulTest() throws Exception {
    execGetKameHouseCommandsTest("/api/v1/admin/system-state/df");
  }

  /**
   * top successful test.
   */
  @Test
  void topSuccessfulTest() throws Exception {
    execGetKameHouseCommandsTest("/api/v1/admin/system-state/top");
  }

  /**
   * httpd get status successful test.
   */
  @Test
  void httpdGetStatusSuccessfulTest() throws Exception {
    execGetKameHouseCommandsTest("/api/v1/admin/system-state/httpd");
  }

  /**
   * httpd restart successful test.
   */
  @Test
  void httpdRestartSuccessfulTest() throws Exception {
    execPostKameHouseCommandsTest("/api/v1/admin/system-state/httpd");
  }
}
