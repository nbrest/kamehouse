package com.nicobrest.kamehouse.admin.model.scheduler.job;

import com.nicobrest.kamehouse.admin.model.admincommand.ShutdownAdminCommand;
import com.nicobrest.kamehouse.commons.service.SystemCommandService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Job to schedule a server shutdown.
 *
 * @author nbrest
 *
 */
public class ShutdownJob implements Job {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private SystemCommandService systemCommandService;

  public void execute(JobExecutionContext context) {
    logger.debug("Shutting down the system now");
    systemCommandService.execute(new ShutdownAdminCommand(0));
  }
}