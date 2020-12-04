package com.nicobrest.kamehouse.testmodule.service;

import static org.junit.Assert.assertEquals;
import com.nicobrest.kamehouse.testmodule.config.TestModuleSchedulerConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.Scheduler;

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
   * Get shutdown server status successful test.
   */
  @Test
  public void getShutdownStatusSuccessTest() {
    testSchedulerService.setSampleJobJobDetail(new TestModuleSchedulerConfig().sampleJobDetail());

    String status = testSchedulerService.getSampleJobStatus();
    assertEquals("Sample job not scheduled", status);
  }

  /**
   * Cancel shutdown server successful test.
   */
  @Test
  public void cancelScheduledShutdownSuccessTest() {
    testSchedulerService.setSampleJobJobDetail(new TestModuleSchedulerConfig().sampleJobDetail());

    String status = testSchedulerService.cancelScheduledSampleJob();
    assertEquals("Sample job was not scheduled, so no need to cancel", status);
  }
}
