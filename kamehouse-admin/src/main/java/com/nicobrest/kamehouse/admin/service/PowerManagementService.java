package com.nicobrest.kamehouse.admin.service;

import com.nicobrest.kamehouse.admin.model.systemcommand.WolSystemCommand;
import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.NetworkUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.commons.utils.SchedulerUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service to execute power management commands.
 *
 * @author nbrest
 */
@Service
public class PowerManagementService {

  public static final String TRIGGER_WONT_FIRE =
      "Based on configured schedule, the given trigger will never fire";
  private static final Logger logger = LoggerFactory.getLogger(PowerManagementService.class);
  private static final String SHUTDOWN_TRIGGER = "shutdownTrigger";
  private static final String SUSPEND_TRIGGER = "suspendTrigger";
  private static final String KAMEHOUSE_CMD_WIN = DockerUtils.getDockerHostUserHome()
      + "\\programs\\kamehouse-cmd\\bin\\kamehouse-cmd.bat";
  private static final String KAMEHOUSE_CMD_LIN = DockerUtils.getDockerHostUserHome()
      + "/programs/kamehouse-cmd/bin/kamehouse-cmd.sh";

  private Scheduler scheduler;
  private JobDetail shutdownJobDetail;
  private JobDetail suspendJobDetail;

  /**
   * Autowired Constructor.
   */
  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public PowerManagementService(Scheduler scheduler,
      @Qualifier("shutdownJobDetail") JobDetail shutdownJobDetail,
      @Qualifier("suspendJobDetail") JobDetail suspendJobDetail) {
    this.scheduler = scheduler;
    this.shutdownJobDetail = shutdownJobDetail;
    this.suspendJobDetail = suspendJobDetail;
  }

  /**
   * Getters and Setters.
   */
  public void setShutdownJobDetail(JobDetail shutdownJobDetail) {
    this.shutdownJobDetail = shutdownJobDetail;
  }

  /**
   * Getters and Setters.
   */
  public void setSuspendJobDetail(JobDetail suspendJobDetail) {
    this.suspendJobDetail = suspendJobDetail;
  }

  /**
   * Wake on lan the specified server. The server should be the base of the the admin.properties
   * [server].mac and [server].broadcast. For example: "media.server"
   */
  public void wakeOnLan(String server) {
    logger.trace("Waking up {}", server);
    String macAddress = PropertiesUtils.getProperty(server + ".mac");
    String broadcastAddress = PropertiesUtils.getProperty(server + ".broadcast");
    if (macAddress == null || broadcastAddress == null) {
      throw new KameHouseBadRequestException("Invalid server specified " + server);
    }
    wakeOnLan(macAddress, broadcastAddress);
  }

  /**
   * Wake on lan the specified MAC address in format FF:FF:FF:FF:FF:FF or FF-FF-FF-FF-FF-FF using
   * the specified broadcast address.
   */
  public void wakeOnLan(String macAddress, String broadcastAddress) {
    NetworkUtils.wakeOnLan(macAddress, broadcastAddress);
    if (DockerUtils.isDockerContainer() && DockerUtils.isDockerControlHostEnabled()) {
      WolSystemCommand wolCommand = new WolSystemCommand(macAddress, broadcastAddress);
      DockerUtils.executeOnDockerHost(wolCommand);
    }
  }

  /**
   * Schedule a server shutdown at the specified delay in seconds.
   */
  public void scheduleShutdown(Integer delay) {
    try {
      if (delay == null || delay < 60) {
        throw new KameHouseBadRequestException("Invalid delay specified");
      }
      Trigger shutdownTrigger = getShutdownTrigger(delay);
      if (scheduler.checkExists(shutdownTrigger.getKey())) {
        scheduler.rescheduleJob(shutdownTrigger.getKey(), shutdownTrigger);
      } else {
        scheduler.scheduleJob(shutdownTrigger);
      }
      logger.debug("Scheduling shutdown with a delay of {} seconds", delay);
    } catch (SchedulerException e) {
      if (e.getMessage() != null && e.getMessage().contains(TRIGGER_WONT_FIRE)) {
        logger.warn(e.getMessage());
      } else {
        throw new KameHouseServerErrorException(e.getMessage(), e);
      }
    }
  }

  /**
   * Get current shutdown status.
   */
  public String getShutdownStatus() {
    try {
      Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(SHUTDOWN_TRIGGER));
      if (trigger != null && trigger.getNextFireTime() != null) {
        return "Shutdown scheduled at: " + trigger.getNextFireTime().toString();
      } else {
        return "Shutdown not scheduled";
      }
    } catch (SchedulerException e) {
      throw new KameHouseServerErrorException(e.getMessage(), e);
    }
  }

  /**
   * Cancel a current scheduled shutdown.
   */
  public String cancelScheduledShutdown() {
    try {
      boolean cancelledSuspend = scheduler.unscheduleJob(TriggerKey.triggerKey(SHUTDOWN_TRIGGER));
      if (cancelledSuspend) {
        return "Shutdown cancelled";
      } else {
        return "Shutdown was not scheduled, so no need to cancel";
      }
    } catch (SchedulerException e) {
      throw new KameHouseServerErrorException(e.getMessage(), e);
    }
  }

  /**
   * Schedule a server suspend at the specified delay in seconds.
   */
  public void scheduleSuspend(Integer delay) {
    try {
      if (delay == null || delay < 0) {
        throw new KameHouseBadRequestException("Invalid delay specified");
      }
      Trigger suspendTrigger = getSuspendTrigger(delay);
      if (scheduler.checkExists(suspendTrigger.getKey())) {
        scheduler.rescheduleJob(suspendTrigger.getKey(), suspendTrigger);
      } else {
        scheduler.scheduleJob(suspendTrigger);
      }
      logger.debug("Scheduling suspend with a delay of {} seconds", delay);
    } catch (SchedulerException e) {
      if (e.getMessage() != null && e.getMessage().contains(TRIGGER_WONT_FIRE)) {
        logger.warn(e.getMessage());
      } else {
        throw new KameHouseServerErrorException(e.getMessage(), e);
      }
    }
  }

  /**
   * Get current suspend status.
   */
  public String getSuspendStatus() {
    try {
      Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(SUSPEND_TRIGGER));
      if (trigger != null && trigger.getNextFireTime() != null) {
        return "Suspend scheduled at: " + trigger.getNextFireTime().toString();
      } else {
        return "Suspend not scheduled";
      }
    } catch (SchedulerException e) {
      throw new KameHouseServerErrorException(e.getMessage(), e);
    }
  }

  /**
   * Cancel a current scheduled suspend.
   */
  public String cancelScheduledSuspend() {
    try {
      boolean cancelledSuspend = scheduler.unscheduleJob(TriggerKey.triggerKey(SUSPEND_TRIGGER));
      if (cancelledSuspend) {
        return "Suspend cancelled";
      } else {
        return "Suspend was not scheduled, so no need to cancel";
      }
    } catch (SchedulerException e) {
      throw new KameHouseServerErrorException(e.getMessage(), e);
    }
  }

  /**
   * Get the trigger to schedule the job to shutdown the server at the specified delay in seconds.
   */
  private Trigger getShutdownTrigger(int delay) {
    return SchedulerUtils.getTrigger(
        delay, shutdownJobDetail, SHUTDOWN_TRIGGER, "Trigger to " + "schedule a server shutdown");
  }

  /**
   * Get the trigger to schedule the job to suspend the server at the specified delay in seconds.
   */
  private Trigger getSuspendTrigger(int delay) {
    return SchedulerUtils.getTrigger(
        delay, suspendJobDetail, SUSPEND_TRIGGER, "Trigger to " + "schedule a server suspend");
  }
}
