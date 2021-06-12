package com.nicobrest.kamehouse.admin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.admin.model.KameHouseJob;
import com.nicobrest.kamehouse.commons.utils.SchedulerUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Unit tests for the SchedulerService class.
 * 
 * @author nbrest
 *
 */
public class SchedulerServiceTest {

  @InjectMocks
  private SchedulerService schedulerService;

  @Mock(name = "scheduler")
  private Scheduler scheduler;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void before()  {
    MockitoAnnotations.initMocks(this);
    try {
      Set<JobKey> jobKeySet = new HashSet<>();
      jobKeySet.add(JobKey.jobKey("sampleJob", "DEFAULT"));
      JobDetailImpl jobDetail = new JobDetailImpl();
      jobDetail.setKey(JobKey.jobKey("sampleJob", "DEFAULT"));
      jobDetail.setName("sampleJob");
      jobDetail.setJobClass(SampleTestJob.class);
      Trigger trigger = SchedulerUtils.getTrigger(1, jobDetail, "sampleTrigger", "");
      List triggers = new ArrayList<>();
      triggers.add(trigger);
      when(scheduler.getJobKeys(null)).thenReturn(jobKeySet);
      when(scheduler.getJobDetail(any())).thenReturn(jobDetail);
      when(scheduler.getTriggersOfJob(Mockito.any(JobKey.class))).thenReturn(triggers);
    } catch (SchedulerException e) {
      e.printStackTrace();
      fail("Error setting up the test data");
    }
  }

  /**
   * Get all jobs status successful test.
   */
  @Test
  public void getAllJobsStatusSuccessTest() {
    List<KameHouseJob> jobs = schedulerService.getAllJobsStatus();
    assertEquals(1, jobs.size());
    assertEquals("sampleJob", jobs.get(0).getKey().getName());
    assertEquals("sampleTrigger", jobs.get(0).getSchedules().get(0).getKey().getName());
  }

  /**
   * Cancel scheduled job successful test.
   */
  @Test
  public void cancelScheduledJobSuccessTest() throws SchedulerException {
    when(scheduler.unscheduleJob(any())).thenReturn(true);
    JobKey jobKey = new JobKey("sampleJob", "DEFAULT");

    schedulerService.cancelScheduledJob(jobKey);
    // no exception thrown
  }

  /**
   * Schedule job successful test.
   */
  @Test
  public void scheduleJobSuccessTest() {
    JobKey jobKey = new JobKey("sampleJob", "DEFAULT");
    JobDetailImpl jobDetail = new JobDetailImpl();
    jobDetail.setJobClass(SampleTestJob.class);
    jobDetail.setName("sampleJob");
    jobDetail.setKey(jobKey);

    schedulerService.scheduleJob(jobDetail, 2);
    // no exception thrown
  }

  /**
   * Dummy sample job test class for unit tests.
   */
  public class SampleTestJob implements Job {

    /**
     * Dummy sample execute method for unit tests.
     */
    public void execute(JobExecutionContext context) {

    }
  }
}
