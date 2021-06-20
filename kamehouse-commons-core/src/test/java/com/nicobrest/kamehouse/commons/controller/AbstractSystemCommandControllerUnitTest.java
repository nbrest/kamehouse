package com.nicobrest.kamehouse.commons.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.service.SystemCommandService;
import com.nicobrest.kamehouse.commons.testutils.SystemCommandOutputTestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for the AbstractSystemCommandController through a TestEntity controller.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class AbstractSystemCommandControllerUnitTest {

  private static final String API_TEST_ENTITY = "/api/v1/unit-tests/system-command";
  private MockMvc mockMvc;
  private SystemCommandOutputTestUtils testUtils = new SystemCommandOutputTestUtils();

  @InjectMocks
  private TestSystemCommandController testSystemCommandController;

  @Mock
  private SystemCommandService systemCommandService;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void beforeTest() {
    testUtils.initTestData();
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(testSystemCommandController).build();
  }

  /**
   * execute system command test.
   */
  @Test
  public void executeSystemCommandTest() throws Exception {
    Mockito.when(systemCommandService.execute(Mockito.any(KameHouseSystemCommand.class)))
        .thenReturn(testUtils.getTestDataList());

    MockHttpServletResponse response = doPost(API_TEST_ENTITY, null);

    verifyResponseStatus(response, HttpStatus.OK);
  }

  /**
   * Executes a post request for the specified url and payload on the mock server.
   */
  private MockHttpServletResponse doPost(String url, byte[] requestPayload)
      throws Exception {
    return mockMvc
        .perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8).content(requestPayload))
        .andDo(print()).andReturn().getResponse();
  }

  /**
   * Verifies that the response's status code matches the expected one.
   */
  private static void verifyResponseStatus(MockHttpServletResponse response,
                                             HttpStatus expectedStatus) {
    assertEquals(expectedStatus.value(), response.getStatus());
  }
}
