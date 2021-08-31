package com.nicobrest.kamehouse.commons.utils;

import java.util.Date;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

/**
 * Utility class for Scheduler functionality.
 *
 * @author nbrest
 */
public class SchedulerUtils {

  private SchedulerUtils() {
    throw new IllegalStateException("Utility class");
  }

  /** Creates a trigger for the specified delay in seconds and JobDetail. */
  public static Trigger getTrigger(
      int delay, JobDetail jobDetail, String triggerName, String triggerDescription) {
    Date currentDate = DateUtils.getCurrentDate();
    Date scheduleDate = DateUtils.addSeconds(currentDate, delay);
    String cronExpression = DateUtils.toCronExpression(scheduleDate);
    ScheduleBuilder<?> scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
    return TriggerBuilder.newTrigger()
        .forJob(jobDetail)
        .withIdentity(TriggerKey.triggerKey(triggerName))
        .withDescription(triggerDescription)
        .withSchedule(scheduleBuilder)
        .build();
  }
}
