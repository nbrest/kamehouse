package com.nicobrest.kamehouse.testmodule.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.when;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.testmodule.config.TestModuleSchedulerConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * Unit tests for the TestSchedulerService class.
 * 
 * @author nbrest
 *
 */
public class TestSchedulerServiceTest {

  @InjectMocks
  private TestSchedulerService testSchedulerService;

  @Mock(name = "scheduler")
  private Scheduler scheduler;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Sample job successful test.
   */
  @Test
  public void scheduleSampleJobSuccessTest() {
    testSchedulerService.setSampleJobJobDetail(new TestModuleSchedulerConfig().sampleJobDetail());

    testSchedulerService.scheduleSampleJob(5400);
    // no exception thrown expected
  }

  /**
   * Sample job exception test.
   */
  @Test
  public void scheduleSampleJobExceptionTest() throws SchedulerException {
    thrown.expect(KameHouseServerErrorException.class);
    thrown.expectMessage("mada mada dane");
    when(scheduler.scheduleJob(any())).thenThrow(new SchedulerException("mada mada dane"));
    testSchedulerService.setSampleJobJobDetail(new TestModuleSchedulerConfig().sampleJobDetail());

    testSchedulerService.scheduleSampleJob(5400);
  }

  /**
   * Sample job exception trigger won't fire test.
   */
  @Test
  public void scheduleSampleJobExceptionTriggerWontFireTest() throws SchedulerException {
    when(scheduler.scheduleJob(any()))
        .thenThrow(new SchedulerException(TestSchedulerService.TRIGGER_WONT_FIRE));
    testSchedulerService.setSampleJobJobDetail(new TestModuleSchedulerConfig().sampleJobDetail());

    testSchedulerService.scheduleSampleJob(5400);
    // No exception thrown from the service
  }

  /**
   * Get job status successful test.
   */
  @Test
  public void getJobStatusSuccessTest() {
    testSchedulerService.setSampleJobJobDetail(new TestModuleSchedulerConfig().sampleJobDetail());

    String status = testSchedulerService.getSampleJobStatus();
    assertEquals("Sample job not scheduled", status);
  }

  /**
   * Cancel job successful test.
   */
  @Test
  public void cancelJobSuccessTest() {
    testSchedulerService.setSampleJobJobDetail(new TestModuleSchedulerConfig().sampleJobDetail());

    String status = testSchedulerService.cancelScheduledSampleJob();
    assertEquals("Sample job was not scheduled, so no need to cancel", status);
  }
}
