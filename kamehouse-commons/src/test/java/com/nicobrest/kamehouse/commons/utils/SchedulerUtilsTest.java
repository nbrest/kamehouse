package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;

/**
 * SchedulerUtils tests.
 *
 * @author nbrest
 */
public class SchedulerUtilsTest {

  /** Tests getting a Trigger to schedule a job. */
  @Test
  public void getTriggerTest() {
    JobDetailImpl jobDetail = new JobDetailImpl();
    jobDetail.setJobClass(SampleTestJob.class);
    jobDetail.setName("sampleJob");

    Trigger trigger =
        SchedulerUtils.getTrigger(10, jobDetail, "sampleTrigger", "sampleTriggerDescription");

    assertEquals("sampleTriggerDescription", trigger.getDescription());
    assertEquals("sampleTrigger", trigger.getKey().getName());
    assertEquals("sampleJob", trigger.getJobKey().getName());
  }

  /** Dummy sample job test class for unit tests. */
  public class SampleTestJob implements Job {

    /** Dummy sample execute method for unit tests. */
    public void execute(JobExecutionContext context) {}
  }
}
