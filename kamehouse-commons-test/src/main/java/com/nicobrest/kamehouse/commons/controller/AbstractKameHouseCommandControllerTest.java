package com.nicobrest.kamehouse.commons.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import com.nicobrest.kamehouse.commons.service.KameHouseCommandService;
import com.nicobrest.kamehouse.commons.testutils.KameHouseCommandResultTestUtils;
import jakarta.servlet.ServletException;
import java.util.List;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Abstract class for common test functionality for KameHouseCommand controller tests.
 *
 * @author nbrest
 */
public abstract class AbstractKameHouseCommandControllerTest
    extends AbstractControllerTest<KameHouseCommandResult, Object> {

  protected List<KameHouseCommandResult> kameHouseCommandResults;

  @Mock
  protected KameHouseCommandService kameHouseCommandService;

  protected AbstractKameHouseCommandControllerTest() {
    kameHouseCommandControllerTestSetup();
  }

  /**
   * Setup test data and mocks for KameHouseCommand controller tests.
   */
  protected void kameHouseCommandControllerTestSetup() {
    testUtils = new KameHouseCommandResultTestUtils();
    testUtils.initTestData();
    kameHouseCommandResults = testUtils.getTestDataList();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(kameHouseCommandService);
    when(kameHouseCommandService.execute(any(List.class)))
        .thenReturn(kameHouseCommandResults);
  }

  /**
   * Tests executing an KameHouseCommand through a get request.
   */
  protected void execGetKameHouseCommandsTest(String url) throws Exception {
    MockHttpServletResponse response = doGet(url);
    List<KameHouseCommandResult> responseBody =
        getResponseBodyList(response, KameHouseCommandResult.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributesList(kameHouseCommandResults, responseBody);
    verify(kameHouseCommandService, times(1)).execute(any(List.class));
  }

  /**
   * Tests executing an KameHouseCommand through a post request without request body.
   */
  protected void execPostKameHouseCommandsTest(String url) throws Exception {
    MockHttpServletResponse response = doPost(url);
    List<KameHouseCommandResult> responseBody =
        getResponseBodyList(response, KameHouseCommandResult.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributesList(kameHouseCommandResults, responseBody);
    verify(kameHouseCommandService, times(1)).execute(any(List.class));
  }

  /**
   * Tests executing an invalid KameHouseCommand through a post request without request body.
   */
  protected void execPostInvalidKameHouseCommandsTest(String url) {
    assertThrows(
        ServletException.class,
        () -> doPost(url)
    );
  }

  /**
   * Tests executing an KameHouseCommand through a delete request.
   */
  protected void execDeleteKameHouseCommandsTest(String url) throws Exception {
    MockHttpServletResponse response = doDelete(url);
    List<KameHouseCommandResult> responseBody =
        getResponseBodyList(response, KameHouseCommandResult.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributesList(kameHouseCommandResults, responseBody);
    verify(kameHouseCommandService, times(1)).execute(any(List.class));
  }

  /**
   * Tests executing an KameHouseCommand through a delete request with a Server Error.
   */
  protected void execDeleteServerErrorKameHouseCommandsTest(String url) throws Exception {
    kameHouseCommandResults.get(0).setExitCode(1);

    MockHttpServletResponse response = doDelete(url);
    List<KameHouseCommandResult> responseBody =
        getResponseBodyList(response, KameHouseCommandResult.class);

    verifyResponseStatus(response, HttpStatus.INTERNAL_SERVER_ERROR);
    testUtils.assertEqualsAllAttributesList(kameHouseCommandResults, responseBody);
    verify(kameHouseCommandService, times(1)).execute(any(List.class));
  }
}
