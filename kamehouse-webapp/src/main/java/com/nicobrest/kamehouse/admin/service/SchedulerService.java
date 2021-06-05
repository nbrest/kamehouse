package com.nicobrest.kamehouse.admin.service;

import com.nicobrest.kamehouse.admin.model.KameHouseJob;
import com.nicobrest.kamehouse.main.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.main.utils.SchedulerUtils;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Service to execute scheduler commands.
 *
 * @author nbrest
 */
@Service
public class SchedulerService {

  private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);
  private static final String TRIGGER_WONT_FIRE = "Based on configured schedule, the given "
      + "trigger will never fire";
  private static final String TRIGGER = "-trigger";

  @Autowired
  private Scheduler scheduler;

  /**
   * Getters and Setters.
   */
  public void setScheduler(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  /**
   * Schedule a job based on the supplied delay.
   */
  public void scheduleJob(JobKey jobKey, Integer delay) {
    try {
      JobDetail jobDetail = scheduler.getJobDetail(jobKey);
      Trigger trigger = SchedulerUtils.getTrigger(delay, jobDetail, jobKey.getName() + TRIGGER,
          jobKey.getName() + TRIGGER);
      scheduleJob(trigger);
    } catch (SchedulerException e) {
      throw new KameHouseServerErrorException(e.getMessage(), e);
    }
  }

  /**
   * Schedule a job based on the supplied delay.
   */
  public void scheduleJob(JobDetail jobDetail, Integer delay) {
    JobKey jobKey = jobDetail.getKey();
    Trigger trigger = SchedulerUtils.getTrigger(delay, jobDetail, jobKey.getName() + TRIGGER,
        jobKey.getName() + TRIGGER);
    scheduleJob(trigger);
  }

  /**
   * Schedule a job based on it's supplied trigger.
   */
  public void scheduleJob(Trigger trigger) {
    try {
      if (scheduler.checkExists(trigger.getKey())) {
        scheduler.rescheduleJob(trigger.getKey(), trigger);
      } else {
        scheduler.scheduleJob(trigger);
      }
      logger.debug("Scheduling the job {} based on the trigger {}. Next run at {}",
          trigger.getJobKey(), trigger.getKey(), trigger.getNextFireTime());
    } catch (SchedulerException e) {
      if (e.getMessage() != null && e.getMessage().contains(TRIGGER_WONT_FIRE)) {
        logger.debug(e.getMessage());
      } else {
        throw new KameHouseServerErrorException(e.getMessage(), e);
      }
    }
  }

  /**
   * Get the status of all jobs in the system with their triggers.
   */
  public List<KameHouseJob> getAllJobsStatus() {
    try {
      List<KameHouseJob> jobs = new ArrayList<>();
      Set<JobKey> jobKeySet = scheduler.getJobKeys(null);
      for (JobKey jobKey: jobKeySet) {
        KameHouseJob kamehouseJob = new KameHouseJob();
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        kamehouseJob.setKey(new KameHouseJob.Key(jobKey.getGroup(), jobKey.getName()));
        kamehouseJob.setDescription(jobDetail.getDescription());
        kamehouseJob.setJobClass(jobDetail.getJobClass().getCanonicalName());

        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
        if (triggers != null) {
          List<KameHouseJob.Schedule> schedules = kamehouseJob.getSchedules();
          for (Trigger trigger : triggers) {
            KameHouseJob.Schedule schedule = new KameHouseJob.Schedule();
            KameHouseJob.Key triggerKey = new KameHouseJob.Key(trigger.getKey().getGroup(),
                trigger.getKey().getName());
            schedule.setKey(triggerKey);
            schedule.setDescription(trigger.getDescription());
            schedule.setNextRun(trigger.getNextFireTime());
            schedule.setPriority(trigger.getPriority());

            schedules.add(schedule);
          }
        }
        jobs.add(kamehouseJob);
      }
      return jobs;
    } catch (SchedulerException e) {
      throw new KameHouseServerErrorException(e.getMessage(), e);
    }
  }

  /**
   * Cancel a current scheduled job.
   */
  public void cancelScheduledJob(JobKey jobKey) {
    try {
      List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
      if (triggers != null) {
        for (Trigger trigger : triggers) {
          boolean cancelledJob = scheduler.unscheduleJob(trigger.getKey());
          if (cancelledJob) {
            logger.debug("{} execution cancelled", trigger.getJobKey());
          }
        }
      }
    } catch (SchedulerException e) {
      throw new KameHouseServerErrorException(e.getMessage(), e);
    }
  }
}
