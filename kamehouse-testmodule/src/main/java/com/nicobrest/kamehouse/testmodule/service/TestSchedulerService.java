package com.nicobrest.kamehouse.testmodule.service;

import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.utils.SchedulerUtils;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service to execute test scheduler commands.
 *
 * @author nbrest
 */
@Service
public class TestSchedulerService {

  private static final Logger logger = LoggerFactory.getLogger(TestSchedulerService.class);
  protected static final String TRIGGER_WONT_FIRE = "Based on configured schedule, the given "
      + "trigger will never fire";
  private static final String SAMPLE_JOB_TRIGGER = "sampleJobTrigger";

  @Autowired
  private Scheduler scheduler;

  @Autowired
  @Qualifier("sampleJobDetail")
  private JobDetail sampleJobDetail;

  @Autowired
  @Qualifier("sampleTrigger")
  private Trigger sampleTrigger;

  /**
   * Getters and Setters.
   */
  public void setScheduler(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  /**
   * Getters and Setters.
   */
  public void setSampleJobJobDetail(JobDetail sampleJobDetail) {
    this.sampleJobDetail = sampleJobDetail;
  }

  /**
   * Schedule a sample job at the specified delay in seconds.
   */
  public void scheduleSampleJob(Integer delay) {
    try {
      Trigger sampleJobTrigger = getSampleJobTrigger(delay);
      if (scheduler.checkExists(sampleJobTrigger.getKey())) {
        scheduler.rescheduleJob(sampleJobTrigger.getKey(), sampleJobTrigger);
      } else {
        scheduler.scheduleJob(sampleJobTrigger);
      }
      if (delay != null) {
        logger.info("Scheduling sample job with a delay of {} seconds", delay);
      } else {
        logger.info("Scheduling sample job at a fixed delay");
      }
    } catch (SchedulerException e) {
      if (e.getMessage() != null && e.getMessage().contains(TRIGGER_WONT_FIRE)) {
        logger.debug(e.getMessage());
      } else {
        throw new KameHouseServerErrorException(e.getMessage(), e);
      }
    }
  }

  /**
   * Get current sample job status.
   */
  public String getSampleJobStatus() {
    try {
      Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(SAMPLE_JOB_TRIGGER));
      if (trigger != null && trigger.getNextFireTime() != null) {
        return "Sample job scheduled at: " + trigger.getNextFireTime().toString();
      } else {
        return "Sample job not scheduled";
      }
    } catch (SchedulerException e) {
      throw new KameHouseServerErrorException(e.getMessage(), e);
    }
  }

  /**
   * Cancel a current scheduled sample job.
   */
  public String cancelScheduledSampleJob() {
    try {
      boolean cancelledSuspend = scheduler.unscheduleJob(TriggerKey.triggerKey(SAMPLE_JOB_TRIGGER));
      if (cancelledSuspend) {
        return "Sample job cancelled";
      } else {
        return "Sample job was not scheduled, so no need to cancel";
      }
    } catch (SchedulerException e) {
      throw new KameHouseServerErrorException(e.getMessage(), e);
    }
  }

  /**
   * Get the trigger to schedule the sample job at the specified delay in seconds, or a default
   * trigger.
   */
  private Trigger getSampleJobTrigger(Integer delay) {
    if (delay != null) {
      return SchedulerUtils.getTrigger(delay, sampleJobDetail, SAMPLE_JOB_TRIGGER, "Trigger to "
          + "schedule a sample job");
    } else {
      return sampleTrigger;
    }
  }
}
