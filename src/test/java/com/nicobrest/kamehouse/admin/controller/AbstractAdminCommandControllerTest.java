package com.nicobrest.kamehouse.admin.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.admin.model.SystemCommandOutput;
import com.nicobrest.kamehouse.admin.model.admincommand.AdminCommand;
import com.nicobrest.kamehouse.admin.service.SystemCommandService;
import com.nicobrest.kamehouse.admin.testutils.SystemCommandOutputTestUtils;
import com.nicobrest.kamehouse.main.controller.AbstractControllerTest;
import com.nicobrest.kamehouse.main.exception.KameHouseInvalidCommandException;

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
    AbstractControllerTest<SystemCommandOutput, Object> {

  protected List<SystemCommandOutput> systemCommandOutputList;

  @Mock
  protected SystemCommandService systemCommandService;

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
   * Test executing an AdminCommand through a get request.
   */
  protected void executeGetAdminCommandTest(String url, Class<? extends AdminCommand> clazz)
      throws Exception {
    MockHttpServletResponse response = executeGet(url);
    List<SystemCommandOutput> responseBody = getResponseBodyList(response,
        SystemCommandOutput.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributesList(systemCommandOutputList, responseBody);
    verify(systemCommandService, times(1)).execute(Mockito.any(clazz));
  }
  
  /**
   * Test executing an AdminCommand through a post request without request body.
   */
  protected void executePostAdminCommandTest(String url, Class<? extends AdminCommand> clazz)
      throws Exception {
    MockHttpServletResponse response = executePost(url);
    List<SystemCommandOutput> responseBody = getResponseBodyList(response,
        SystemCommandOutput.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributesList(systemCommandOutputList, responseBody);
    verify(systemCommandService, times(1)).execute(Mockito.any(clazz));
  }

  /**
   * Test executing an invalid AdminCommand through a post request without
   * request body.
   */
  protected void executePostInvalidAdminCommandTest(String url) throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(
        KameHouseInvalidCommandException.class));

    executePost(url);
  }

  /**
   * Test executing an AdminCommand through a delete request.
   */
  protected void executeDeleteAdminCommandTest(String url, Class<? extends AdminCommand> clazz)
      throws Exception {
    MockHttpServletResponse response = executeDelete(url);
    List<SystemCommandOutput> responseBody = getResponseBodyList(response,
        SystemCommandOutput.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributesList(systemCommandOutputList, responseBody);
    verify(systemCommandService, times(1)).execute(Mockito.any(clazz));
  }

  /**
   * Test executing an AdminCommand through a delete request with a Server
   * Error.
   */
  protected void executeDeleteServerErrorAdminCommandTest(String url,
      Class<? extends AdminCommand> clazz) throws Exception {
    systemCommandOutputList.get(0).setExitCode(1);

    MockHttpServletResponse response = executeDelete(url);
    List<SystemCommandOutput> responseBody = getResponseBodyList(response,
        SystemCommandOutput.class);

    verifyResponseStatus(response, HttpStatus.INTERNAL_SERVER_ERROR);
    testUtils.assertEqualsAllAttributesList(systemCommandOutputList, responseBody);
    verify(systemCommandService, times(1)).execute(Mockito.any(clazz));
  }
}
