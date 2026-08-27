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
 * Unit tests for the KameHouseDesktopController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
class KameHouseDesktopControllerTest extends AbstractKameHouseCommandControllerTest {

  @InjectMocks
  private KameHouseDesktopController kameHouseDesktopController;

  @BeforeEach
  void beforeTest() {
    kameHouseCommandControllerTestSetup();
    mockMvc = MockMvcBuilders.standaloneSetup(kameHouseDesktopController).build();
  }

  /**
   * kamehouse-desktop stop successful test.
   */
  @Test
  void kameHouseDesktopStopSuccessfulTest() throws Exception {
    execDeleteKameHouseCommandsTest("/api/v1/admin/kamehouse-desktop");
  }

  /**
   * kamehouse-desktop startup successful test.
   */
  @Test
  void kameHouseDesktopStartupSuccessfulTest() throws Exception {
    execPostKameHouseCommandsTest("/api/v1/admin/kamehouse-desktop");
  }
}
