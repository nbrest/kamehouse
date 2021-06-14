package com.nicobrest.kamehouse.admin.model.scheduler.job;

import com.nicobrest.kamehouse.admin.model.kamehousecommand.SuspendKameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.service.SystemCommandService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Job to schedule a server suspend.
 *
 * @author nbrest
 *
 */
public class SuspendJob implements Job {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private SystemCommandService systemCommandService;

  public void execute(JobExecutionContext context) {
    logger.debug("Suspending the system now");
    systemCommandService.execute(new SuspendKameHouseSystemCommand());
  }
}