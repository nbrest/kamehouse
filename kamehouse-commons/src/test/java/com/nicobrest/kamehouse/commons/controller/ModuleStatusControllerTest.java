package com.nicobrest.kamehouse.commons.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for ModuleStatusController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
public class ModuleStatusControllerTest extends AbstractControllerTest<Object, Object> {

  private static final String MODULE_STATUS_API = "/api/v1/commons/module/status";
  private static final String BUILD_VERSION_PROP = "kamehouse.build.version";
  private static final String BUILD_DATE_PROP = "kamehouse.build.date";
  private static final String BUILD_VERSION_MOCK = "2.00.1-a1b2c3d4";
  private static final String BUILD_DATE_MOCK = "Sat Jun 26 11:05:18 AEST 2021";

  private MockedStatic<PropertiesUtils> propertiesUtils;

  @InjectMocks private ModuleStatusController moduleStatusController;

  /**
   * Tests setup.
   */
  @BeforeEach
  void beforeTest() {
    MockitoAnnotations.openMocks(this);
    propertiesUtils = Mockito.mockStatic(PropertiesUtils.class);

    when(PropertiesUtils.isWindowsHost()).thenCallRealMethod();
    when(PropertiesUtils.getHostname()).thenReturn("kamehouse-ultimate-server");
    when(PropertiesUtils.getModuleName()).thenReturn("kamehouse-ultimate-module");
    when(PropertiesUtils.getProperty(BUILD_VERSION_PROP)).thenReturn(BUILD_VERSION_MOCK);
    when(PropertiesUtils.getProperty(BUILD_DATE_PROP)).thenReturn(BUILD_DATE_MOCK);
    mockMvc = MockMvcBuilders.standaloneSetup(moduleStatusController).build();
  }

  @AfterEach
  public void close() {
    propertiesUtils.close();
  }

  /** Get module status test. */
  @Test
  void getModuleStatusTest() throws Exception {
    MockHttpServletResponse response = doGet(MODULE_STATUS_API);
    Map<String, String> responseBody = getResponseBody(response, Map.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals(4, responseBody.size());
    assertEquals(BUILD_VERSION_MOCK, responseBody.get("buildVersion"));
    assertEquals(BUILD_DATE_MOCK, responseBody.get("buildDate"));
    assertEquals("kamehouse-ultimate-server", responseBody.get("server"));
    assertEquals("kamehouse-ultimate-module", responseBody.get("module"));
  }
}
