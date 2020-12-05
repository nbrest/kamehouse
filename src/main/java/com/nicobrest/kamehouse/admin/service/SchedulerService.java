package com.nicobrest.kamehouse.admin.service;

import com.nicobrest.kamehouse.admin.model.JobSchedule;
import com.nicobrest.kamehouse.main.exception.KameHouseServerErrorException;
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

  @Autowired
  private Scheduler scheduler;

  /**
   * Getters and Setters.
   */
  public void setScheduler(Scheduler scheduler) {
    this.scheduler = scheduler;
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
  public List<JobSchedule> getAllJobsStatus() {
    try {
      List<JobSchedule> jobs = new ArrayList<>();
      Set<JobKey> jobKeySet = scheduler.getJobKeys(null);
      for (JobKey jobKey: jobKeySet) {
        JobSchedule jobSchedule = new JobSchedule();
        JobSchedule.Job job = jobSchedule.getJob();
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        job.setKey(new JobSchedule.Key(jobKey.getGroup(), jobKey.getName()));
        job.setDescription(jobDetail.getDescription());
        job.setJobClass(jobDetail.getJobClass().getCanonicalName());

        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
        if (triggers != null) {
          List<JobSchedule.Schedule> schedules = jobSchedule.getSchedules();
          for (Trigger trigger : triggers) {
            JobSchedule.Schedule schedule = new JobSchedule.Schedule();
            JobSchedule.Key triggerKey = new JobSchedule.Key(trigger.getKey().getGroup(),
                trigger.getKey().getName());
            schedule.setKey(triggerKey);
            schedule.setDescription(trigger.getDescription());
            schedule.setNextRun(trigger.getNextFireTime());
            schedule.setPriority(trigger.getPriority());

            schedules.add(schedule);
          }
        }
        jobs.add(jobSchedule);
      }
      return jobs;
    } catch (SchedulerException e) {
      throw new KameHouseServerErrorException(e.getMessage(), e);
    }
  }

  /**
   * Cancel a current scheduled job based on it's trigger.
   */
  public String cancelScheduledJob(Trigger trigger) {
    try {
      boolean cancelledSuspend = scheduler.unscheduleJob(trigger.getKey());
      if (cancelledSuspend) {
        return trigger.getJobKey() + " cancelled";
      } else {
        return trigger.getJobKey() + " was not scheduled, so no need to cancel";
      }
    } catch (SchedulerException e) {
      throw new KameHouseServerErrorException(e.getMessage(), e);
    }
  }
}
