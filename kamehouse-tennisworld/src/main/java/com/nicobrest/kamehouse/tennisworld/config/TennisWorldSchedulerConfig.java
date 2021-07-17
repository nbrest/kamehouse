package com.nicobrest.kamehouse.tennisworld.config;

import com.nicobrest.kamehouse.tennisworld.model.scheduler.job.CardioSessionBookingJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

/**
 * Configuration class to setup the scheduler beans in the tennisworld module.
 * 
 * @author nbrest
 *
 */
@Configuration
@EnableScheduling
public class TennisWorldSchedulerConfig {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private Scheduler scheduler;

  /**
   * Init TennisWorldSchedulerConfig.
   */
  @PostConstruct
  public void init() {
    logger.info("init TennisWorldSchedulerConfig");
    try {
      JobDetail cardioSessionBookingJobDetail = cardioSessionBookingJobDetail();
      scheduler.addJob(cardioSessionBookingJobDetail, true);
      scheduler.scheduleJob(cardioSessionBookingTrigger(cardioSessionBookingJobDetail, 0, 2));
      scheduler.scheduleJob(cardioSessionBookingTrigger(cardioSessionBookingJobDetail, 0, 10));
    } catch (SchedulerException e) {
      logger.error("Error adding tennisworld jobs to the scheduler", e);
    }
  }

  /**
   * cardioSessionBookingJobDetail bean.
   */
  @Bean(name = "cardioSessionBookingJobDetail")
  public JobDetail cardioSessionBookingJobDetail() {
    return JobBuilder.newJob()
        .ofType(CardioSessionBookingJob.class)
        .storeDurably()
        .withIdentity(JobKey.jobKey("cardioSessionBookingJobDetail"))
        .withDescription("Cardio session booking job")
        .build();
  }

  /**
   * Trigger for the cardioSessionBookingJobDetail at the specified hour and minutes.
   */
  private static Trigger cardioSessionBookingTrigger(JobDetail cardioSessionBookingJobDetail,
                                                   int hour, int minute) {
    return TriggerBuilder.newTrigger()
        .forJob(cardioSessionBookingJobDetail)
        .withIdentity(TriggerKey.triggerKey("cardioSessionBookingTrigger_" + hour + "_" + minute))
        .withDescription("Trigger to schedule a cardio session booking job")
        .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(hour, minute))
        .build();
  }
}