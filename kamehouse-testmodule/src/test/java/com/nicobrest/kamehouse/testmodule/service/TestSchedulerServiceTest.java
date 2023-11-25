package com.nicobrest.kamehouse.testmodule.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.testmodule.config.TestModuleSchedulerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * Unit tests for the TestSchedulerService class.
 *
 * @author nbrest
 */
class TestSchedulerServiceTest {

  @InjectMocks private TestSchedulerService testSchedulerService;

  @Mock(name = "scheduler")
  private Scheduler scheduler;

  @BeforeEach
  public void before() {
    MockitoAnnotations.openMocks(this);
  }

  /** Sample job successful test. */
  @Test
  void scheduleSampleJobSuccessTest() {
    testSchedulerService.setSampleJobJobDetail(new TestModuleSchedulerConfig().sampleJobDetail());

    testSchedulerService.scheduleSampleJob(5400);
    // no exception thrown expected
  }

  /** Sample job exception test. */
  @Test
  void scheduleSampleJobExceptionTest() throws SchedulerException {
    assertThrows(
        KameHouseServerErrorException.class,
        () -> {
          when(scheduler.scheduleJob(any())).thenThrow(new SchedulerException("mada mada dane"));
          testSchedulerService.setSampleJobJobDetail(
              new TestModuleSchedulerConfig().sampleJobDetail());

          testSchedulerService.scheduleSampleJob(5400);
        });
  }

  /** Sample job exception trigger won't fire test. */
  @Test
  void scheduleSampleJobExceptionTriggerWontFireTest() throws SchedulerException {
    when(scheduler.scheduleJob(any()))
        .thenThrow(new SchedulerException(TestSchedulerService.TRIGGER_WONT_FIRE));
    testSchedulerService.setSampleJobJobDetail(new TestModuleSchedulerConfig().sampleJobDetail());

    testSchedulerService.scheduleSampleJob(5400);
    // No exception thrown from the service
  }

  /** Get job status successful test. */
  @Test
  void getJobStatusSuccessTest() {
    testSchedulerService.setSampleJobJobDetail(new TestModuleSchedulerConfig().sampleJobDetail());

    String status = testSchedulerService.getSampleJobStatus();
    assertEquals("Sample job not scheduled", status);
  }

  /** Cancel job successful test. */
  @Test
  void cancelJobSuccessTest() {
    testSchedulerService.setSampleJobJobDetail(new TestModuleSchedulerConfig().sampleJobDetail());

    String status = testSchedulerService.cancelScheduledSampleJob();
    assertEquals("Sample job was not scheduled, so no need to cancel", status);
  }
}
