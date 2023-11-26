package com.nicobrest.kamehouse.tennisworld.config;

import com.nicobrest.kamehouse.tennisworld.model.scheduler.job.ScheduledBookingJob;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.PostConstruct;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.simpl.SimpleJobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration class to setup the scheduler beans in the tennisworld module.
 *
 * @author nbrest
 */
@Configuration
@EnableScheduling
public class TennisWorldSchedulerConfig {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private Scheduler scheduler;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public TennisWorldSchedulerConfig(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  /**
   * Init TennisWorldSchedulerConfig.
   */
  @PostConstruct
  public void init() {
    logger.info("init TennisWorldSchedulerConfig");
    try {
      JobDetail scheduledBookingJobDetail = scheduledBookingJobDetail();
      scheduler.addJob(scheduledBookingJobDetail, true);
      int[] bookingMinutes = {0, 15, 30, 45};
      for (int bookingHour = 0; bookingHour < 24; bookingHour++) {
        for (int bookingMinute : bookingMinutes) {
          scheduler.scheduleJob(
              scheduledBookingTrigger(scheduledBookingJobDetail, bookingHour, bookingMinute));
        }
      }
    } catch (SchedulerException e) {
      logger.error("Error adding tennisworld jobs to the scheduler", e);
    }
  }

  /**
   * scheduledBookingJobDetail bean.
   */
  @Bean(name = "scheduledBookingJobDetail")
  public JobDetail scheduledBookingJobDetail() {
    return JobBuilder.newJob(ScheduledBookingJob.class)
        .storeDurably()
        .withIdentity(JobKey.jobKey("scheduledBookingJobDetail"))
        .withDescription("Scheduled booking job")
        .build();
  }

  /**
   * Trigger for the scheduledBookingJobDetail at the specified hour and minutes at 1 second of
   * the minute.
   */
  private static Trigger scheduledBookingTrigger(
      JobDetail scheduledBookingJobDetail, int hour, int minute) {
    String cronExpression = String.format("1 %d %d ? * *", minute, hour);
    return TriggerBuilder.newTrigger()
        .forJob(scheduledBookingJobDetail)
        .withIdentity(TriggerKey.triggerKey("scheduledBookingTrigger_" + hour + "_" + minute))
        .withDescription("Trigger to schedule a booking job at " + hour + ":" + minute)
        .withSchedule(
            CronScheduleBuilder.cronSchedule(cronExpression)
                .withMisfireHandlingInstructionDoNothing())
        .build();
  }
}
