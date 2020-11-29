package com.nicobrest.kamehouse.admin.scheduler.job;

import com.nicobrest.kamehouse.admin.model.admincommand.SuspendAdminCommand;
import com.nicobrest.kamehouse.admin.service.SystemCommandService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Job to schedule a server suspend.
 *
 * @author nbrest
 *
 */
@Component
public class SuspendJob implements Job {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private SystemCommandService systemCommandService;

  public void execute(JobExecutionContext context) {
    logger.debug("Suspending the system now");
    systemCommandService.execute(new SuspendAdminCommand());
  }
}