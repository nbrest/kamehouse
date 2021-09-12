package com.nicobrest.kamehouse.commons.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.commons.service.SystemCommandService;
import com.nicobrest.kamehouse.commons.testutils.SystemCommandOutputTestUtils;
import java.util.List;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.NestedServletException;

/**
 * Abstract class for common test functionality for KameHouseSystemCommand controller tests.
 *
 * @author nbrest
 */
public abstract class AbstractKameHouseSystemCommandControllerTest
    extends AbstractControllerTest<SystemCommand.Output, Object> {

  protected List<SystemCommand.Output> systemCommandOutputList;

  @Mock
  protected SystemCommandService systemCommandService;

  protected AbstractKameHouseSystemCommandControllerTest() {
    kameHouseSystemCommandControllerTestSetup();
  }

  /**
   * Setup test data and mocks for KameHouseSystemCommand controller tests.
   */
  protected void kameHouseSystemCommandControllerTestSetup() {
    testUtils = new SystemCommandOutputTestUtils();
    testUtils.initTestData();
    systemCommandOutputList = testUtils.getTestDataList();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(systemCommandService);
    when(systemCommandService.execute(any(KameHouseSystemCommand.class)))
        .thenReturn(systemCommandOutputList);
  }

  /**
   * Tests executing an KameHouseSystemCommand through a get request.
   */
  protected void execGetKameHouseSystemCommandTest(
      String url, Class<? extends KameHouseSystemCommand> clazz) throws Exception {
    MockHttpServletResponse response = doGet(url);
    List<SystemCommand.Output> responseBody =
        getResponseBodyList(response, SystemCommand.Output.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributesList(systemCommandOutputList, responseBody);
    verify(systemCommandService, times(1)).execute(any(clazz));
  }

  /**
   * Tests executing an KameHouseSystemCommand through a post request without request body.
   */
  protected void execPostKameHouseSystemCommandTest(
      String url, Class<? extends KameHouseSystemCommand> clazz) throws Exception {
    MockHttpServletResponse response = doPost(url);
    List<SystemCommand.Output> responseBody =
        getResponseBodyList(response, SystemCommand.Output.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributesList(systemCommandOutputList, responseBody);
    verify(systemCommandService, times(1)).execute(any(clazz));
  }

  /**
   * Tests executing an invalid KameHouseSystemCommand through a post request without request body.
   */
  protected void execPostInvalidKameHouseSystemCommandTest(String url) {
    assertThrows(
        NestedServletException.class,
        () -> {
          doPost(url);
        });
  }

  /**
   * Tests executing an KameHouseSystemCommand through a delete request.
   */
  protected void execDeleteKameHouseSystemCommandTest(
      String url, Class<? extends KameHouseSystemCommand> clazz) throws Exception {
    MockHttpServletResponse response = doDelete(url);
    List<SystemCommand.Output> responseBody =
        getResponseBodyList(response, SystemCommand.Output.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributesList(systemCommandOutputList, responseBody);
    verify(systemCommandService, times(1)).execute(any(clazz));
  }

  /**
   * Tests executing an KameHouseSystemCommand through a delete request with a Server Error.
   */
  protected void execDeleteServerErrorKameHouseSystemCommandTest(
      String url, Class<? extends KameHouseSystemCommand> clazz) throws Exception {
    systemCommandOutputList.get(0).setExitCode(1);

    MockHttpServletResponse response = doDelete(url);
    List<SystemCommand.Output> responseBody =
        getResponseBodyList(response, SystemCommand.Output.class);

    verifyResponseStatus(response, HttpStatus.INTERNAL_SERVER_ERROR);
    testUtils.assertEqualsAllAttributesList(systemCommandOutputList, responseBody);
    verify(systemCommandService, times(1)).execute(any(clazz));
  }
}
