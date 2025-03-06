package com.nicobrest.kamehouse.admin.model.scheduler.job;

import com.nicobrest.kamehouse.admin.model.kamehousecommand.SuspendKameHouseCommand;
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
 * Job to schedule a server suspend.
 *
 * @author nbrest
 */
public class SuspendJob implements Job {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private KameHouseCommandService kameHouseCommandService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public SuspendJob(KameHouseCommandService kameHouseCommandService) {
    this.kameHouseCommandService = kameHouseCommandService;
  }

  public SuspendJob() {
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
   * Execute suspend job.
   */
  public void execute(JobExecutionContext context) {
    logger.debug("Suspending the system now");
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new SuspendKameHouseCommand());
    kameHouseCommandService.execute(kameHouseCommands);
  }
}