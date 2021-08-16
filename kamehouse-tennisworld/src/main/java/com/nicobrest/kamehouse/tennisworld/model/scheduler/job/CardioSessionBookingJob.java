package com.nicobrest.kamehouse.tennisworld.model.scheduler.job;

import com.nicobrest.kamehouse.tennisworld.service.BookingService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * CardioSessionBookingJob to be scheduled.
 *
 * @author nbrest
 *
 */
public class CardioSessionBookingJob implements Job {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private BookingService bookingService;

  /**
   * Execute the CardioSessionBookingJob.
   */
  public void execute(JobExecutionContext context) {
    logger.info("Job {} fired @ {}", context.getJobDetail().getKey().getName(),
        context.getFireTime());
    bookingService.bookScheduledCardioSession();
    logger.info("Next job scheduled @ {}", context.getNextFireTime());
  }
}