package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.model.KameHouseJob;
import com.nicobrest.kamehouse.commons.service.SchedulerService;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.commons.validator.InputValidator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for the scheduler commands.
 *
 * @author nbrest
 */
@RestController
@RequestMapping(value = "/api/v1/commons/scheduler")
public class SchedulerController extends AbstractController {

  private SchedulerService schedulerService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public SchedulerController(SchedulerService schedulerService) {
    this.schedulerService = schedulerService;
  }

  /**
   * Gets the status of all jobs in the system.
   */
  @GetMapping(path = "/jobs")
  public ResponseEntity<List<KameHouseJob>> getAllJobs() {
    List<KameHouseJob> jobs = schedulerService.getAllJobsStatus();
    return generateGetResponseEntity(jobs);
  }

  /**
   * Cancel the execution of the specified job.
   */
  @DeleteMapping(path = "/jobs")
  public ResponseEntity<List<KameHouseJob>> cancelJob(
      @RequestParam(value = "name", required = true) String name,
      @RequestParam(value = "group", required = true) String group) {
    InputValidator.validateForbiddenCharsForShell(name);
    InputValidator.validateForbiddenCharsForShell(group);
    String nameSanitized = StringUtils.sanitize(name);
    String groupSanitized = StringUtils.sanitize(group);
    JobKey jobKey = new JobKey(nameSanitized, groupSanitized);
    schedulerService.cancelScheduledJob(jobKey);
    List<KameHouseJob> jobs = schedulerService.getAllJobsStatus();
    return generateGetResponseEntity(jobs);
  }

  /**
   * Executes the specified job with the specified delay.
   */
  @PostMapping(path = "/jobs")
  public ResponseEntity<List<KameHouseJob>> scheduleJob(
      @RequestParam(value = "name", required = true) String name,
      @RequestParam(value = "group", required = true) String group,
      @RequestParam(value = "delay", required = true) Integer delay) {
    InputValidator.validateForbiddenCharsForShell(name);
    InputValidator.validateForbiddenCharsForShell(group);
    String nameSanitized = StringUtils.sanitize(name);
    String groupSanitized = StringUtils.sanitize(group);
    JobKey jobKey = new JobKey(nameSanitized, groupSanitized);
    schedulerService.scheduleJob(jobKey, delay);
    List<KameHouseJob> jobs = schedulerService.getAllJobsStatus();
    return generatePostResponseEntity(jobs);
  }
}
