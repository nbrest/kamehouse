package com.nicobrest.kamehouse.testmodule.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.controller.AbstractControllerTest;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.model.KameHouseGenericResponse;
import com.nicobrest.kamehouse.testmodule.service.TestSchedulerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import jakarta.servlet.ServletException;

/**
 * Unit tests for TestSchedulerController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
class TestSchedulerControllerTest
    extends AbstractControllerTest<KameHouseGenericResponse, Object> {

  @InjectMocks private TestSchedulerController testSchedulerController;

  @Mock protected TestSchedulerService testSchedulerService;

  @BeforeEach
  void beforeTest() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(testSchedulerController).build();
  }

  /** Set sample job schedule successful test. */
  @Test
  void setSampleJobTest() throws Exception {
    MockHttpServletResponse response =
        doPost("/api/v1/test-module/test-scheduler/sample-job" + "?delay=5400");
    KameHouseGenericResponse responseBody =
        getResponseBody(response, KameHouseGenericResponse.class);

    verifyResponseStatus(response, HttpStatus.CREATED);
    assertEquals(
        "Scheduled sample job at the specified delay of 5400 seconds", responseBody.getMessage());
    verify(testSchedulerService, times(1)).scheduleSampleJob(Mockito.anyInt());
  }

  /** Cancels sample job successful test. */
  @Test
  void cancelSampleJobTest() throws Exception {
    when(testSchedulerService.cancelScheduledSampleJob()).thenReturn("Sample job cancelled");

    MockHttpServletResponse response = doDelete("/api/v1/test-module/test-scheduler/sample-job");
    KameHouseGenericResponse responseBody =
        getResponseBody(response, KameHouseGenericResponse.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals("Sample job cancelled", responseBody.getMessage());
    verify(testSchedulerService, times(1)).cancelScheduledSampleJob();
  }

  /** Cancels sample job error test. */
  @Test
  void cancelSampleJobServerErrorTest() {
    assertThrows(
        ServletException.class,
        () -> {
          Mockito.doThrow(new KameHouseServerErrorException(""))
              .when(testSchedulerService)
              .cancelScheduledSampleJob();

          doDelete("/api/v1/test-module/test-scheduler/sample-job");
        });
  }

  /** Sample job status successful test. */
  @Test
  void statusSampleJobTest() throws Exception {
    when(testSchedulerService.getSampleJobStatus()).thenReturn("Sample job not scheduled");

    MockHttpServletResponse response = doGet("/api/v1/test-module/test-scheduler/sample-job");
    KameHouseGenericResponse responseBody =
        getResponseBody(response, KameHouseGenericResponse.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals("Sample job not scheduled", responseBody.getMessage());
    verify(testSchedulerService, times(1)).getSampleJobStatus();
  }
}
