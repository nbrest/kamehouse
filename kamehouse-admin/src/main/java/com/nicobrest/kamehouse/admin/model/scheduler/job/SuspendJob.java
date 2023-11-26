package com.nicobrest.kamehouse.admin.model.scheduler.job;

import com.nicobrest.kamehouse.admin.model.kamehousecommand.SuspendKameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.service.SystemCommandService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Job to schedule a server suspend.
 *
 * @author nbrest
 */
public class SuspendJob implements Job {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private SystemCommandService systemCommandService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public SuspendJob(SystemCommandService systemCommandService) {
    this.systemCommandService = systemCommandService;
  }

  public SuspendJob() {
    // empty constructor
  }

  public SystemCommandService getSystemCommandService() {
    return systemCommandService;
  }

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public void setSystemCommandService(SystemCommandService systemCommandService) {
    this.systemCommandService = systemCommandService;
  }

  public void execute(JobExecutionContext context) {
    logger.debug("Suspending the system now");
    systemCommandService.execute(new SuspendKameHouseSystemCommand());
  }
}