package com.nicobrest.kamehouse.commons.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.nicobrest.kamehouse.commons.service.KameHouseCommandService;
import com.nicobrest.kamehouse.commons.testutils.KameHouseCommandResultCoreTestUtils;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for the AbstractKameHouseCommandController through a TestEntity controller.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
class AbstractKameHouseCommandControllerUnitTest {

  private static final String API_TEST_ENTITY = "/api/v1/unit-tests/kamehouse-command";
  private MockMvc mockMvc;
  private KameHouseCommandResultCoreTestUtils testUtils = new KameHouseCommandResultCoreTestUtils();

  @InjectMocks
  private TestKameHouseCommandController testKameHouseCommandController;

  @Mock
  private KameHouseCommandService kameHouseCommandService;

  /**
   * Tests setup.
   */
  @BeforeEach
  void beforeTest() {
    testUtils.initTestData();
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(testKameHouseCommandController).build();
  }

  /**
   * execute kamehouse command test.
   */
  @Test
  void executeKameHouseCommandTest() throws Exception {
    Mockito.when(kameHouseCommandService.execute(Mockito.any(List.class)))
        .thenReturn(testUtils.getTestDataList());

    MockHttpServletResponse response = doPost(API_TEST_ENTITY, null);

    verifyResponseStatus(response, HttpStatus.OK);
  }

  /**
   * Executes a post request for the specified url and payload on the mock server.
   */
  private MockHttpServletResponse doPost(String url, byte[] requestPayload) throws Exception {
    return mockMvc
        .perform(post(url).contentType(MediaType.APPLICATION_JSON).content(requestPayload))
        .andDo(print())
        .andReturn()
        .getResponse();
  }

  /**
   * Verifies that the response's status code matches the expected one.
   */
  private static void verifyResponseStatus(
      MockHttpServletResponse response, HttpStatus expectedStatus) {
    assertEquals(expectedStatus.value(), response.getStatus());
  }
}
