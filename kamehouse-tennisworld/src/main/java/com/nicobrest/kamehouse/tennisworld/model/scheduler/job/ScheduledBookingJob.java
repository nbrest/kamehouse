package com.nicobrest.kamehouse.tennisworld.model.scheduler.job;

import com.nicobrest.kamehouse.tennisworld.service.BookingService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * ScheduledBookingJob to be executed at the scheduled time.
 *
 * @author nbrest
 */
public class ScheduledBookingJob implements Job {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  @Qualifier("perfectGymBookingService")
  private BookingService bookingService;

  /**
   * Execute the ScheduledBookingJob.
   */
  public void execute(JobExecutionContext context) {
    logger.info("Job {} fired @ {}", context.getJobDetail().getKey().getName(),
        context.getFireTime());
    bookingService.bookScheduledSessions();
    logger.info("Next job scheduled @ {}", context.getNextFireTime());
  }
}