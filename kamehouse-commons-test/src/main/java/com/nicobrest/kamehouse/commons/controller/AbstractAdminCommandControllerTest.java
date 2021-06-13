package com.nicobrest.kamehouse.commons.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.AdminCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.commons.service.SystemCommandService;
import com.nicobrest.kamehouse.commons.testutils.SystemCommandOutputTestUtils;

import org.hamcrest.core.IsInstanceOf;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.NestedServletException;

import java.util.List;

/**
 * Abstract class for common test functionality for AdminCommand controller
 * tests.
 * 
 * @author nbrest
 *
 */
public abstract class AbstractAdminCommandControllerTest extends
    AbstractControllerTest<SystemCommand.Output, Object> {

  protected List<SystemCommand.Output> systemCommandOutputList;

  @Mock
  protected SystemCommandService systemCommandService;

  public AbstractAdminCommandControllerTest() {
    adminCommandControllerTestSetup();
  }

  /**
   * Setup test data and mocks for AdminCommand controller tests.
   */
  protected void adminCommandControllerTestSetup() {
    testUtils = new SystemCommandOutputTestUtils();
    testUtils.initTestData();
    systemCommandOutputList = testUtils.getTestDataList();

    MockitoAnnotations.initMocks(this);
    Mockito.reset(systemCommandService);
    when(systemCommandService.execute(Mockito.any(AdminCommand.class))).thenReturn(
        systemCommandOutputList);
  }
  
  /**
   * Tests executing an AdminCommand through a get request.
   */
  protected void execGetAdminCommandTest(String url, Class<? extends AdminCommand> clazz)
      throws Exception {
    MockHttpServletResponse response = doGet(url);
    List<SystemCommand.Output> responseBody = getResponseBodyList(response,
        SystemCommand.Output.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributesList(systemCommandOutputList, responseBody);
    verify(systemCommandService, times(1)).execute(Mockito.any(clazz));
  }
  
  /**
   * Tests executing an AdminCommand through a post request without request body.
   */
  protected void execPostAdminCommandTest(String url, Class<? extends AdminCommand> clazz)
      throws Exception {
    MockHttpServletResponse response = doPost(url);
    List<SystemCommand.Output> responseBody = getResponseBodyList(response,
        SystemCommand.Output.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributesList(systemCommandOutputList, responseBody);
    verify(systemCommandService, times(1)).execute(Mockito.any(clazz));
  }

  /**
   * Tests executing an invalid AdminCommand through a post request without
   * request body.
   */
  protected void execPostInvalidAdminCommandTest(String url) throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable>instanceOf(
        KameHouseInvalidCommandException.class));

    doPost(url);
  }

  /**
   * Tests executing an AdminCommand through a delete request.
   */
  protected void execDeleteAdminCommandTest(String url, Class<? extends AdminCommand> clazz)
      throws Exception {
    MockHttpServletResponse response = doDelete(url);
    List<SystemCommand.Output> responseBody = getResponseBodyList(response,
        SystemCommand.Output.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributesList(systemCommandOutputList, responseBody);
    verify(systemCommandService, times(1)).execute(Mockito.any(clazz));
  }

  /**
   * Tests executing an AdminCommand through a delete request with a Server
   * Error.
   */
  protected void execDeleteServerErrorAdminCommandTest(String url,
      Class<? extends AdminCommand> clazz) throws Exception {
    systemCommandOutputList.get(0).setExitCode(1);

    MockHttpServletResponse response = doDelete(url);
    List<SystemCommand.Output> responseBody = getResponseBodyList(response,
        SystemCommand.Output.class);

    verifyResponseStatus(response, HttpStatus.INTERNAL_SERVER_ERROR);
    testUtils.assertEqualsAllAttributesList(systemCommandOutputList, responseBody);
    verify(systemCommandService, times(1)).execute(Mockito.any(clazz));
  }
}
