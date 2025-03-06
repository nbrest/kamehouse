package com.nicobrest.kamehouse.admin.model.scheduler.job;

import com.nicobrest.kamehouse.admin.model.kamehousecommand.ShutdownKameHouseCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;
import com.nicobrest.kamehouse.commons.service.KameHouseCommandService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Job to schedule a server shutdown.
 *
 * @author nbrest
 */
public class ShutdownJob implements Job {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private KameHouseCommandService kameHouseCommandService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public ShutdownJob(KameHouseCommandService kameHouseCommandService) {
    this.kameHouseCommandService = kameHouseCommandService;
  }

  public ShutdownJob() {
    // empty constructor
  }

  public KameHouseCommandService getKameHouseCommandService() {
    return kameHouseCommandService;
  }

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public void setKameHouseCommandService(KameHouseCommandService kameHouseCommandService) {
    this.kameHouseCommandService = kameHouseCommandService;
  }

  /**
   * Execute shutdown job.
   */
  public void execute(JobExecutionContext context) {
    logger.debug("Shutting down the system now");
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new ShutdownKameHouseCommand(0));
    kameHouseCommandService.execute(kameHouseCommands);
  }
}