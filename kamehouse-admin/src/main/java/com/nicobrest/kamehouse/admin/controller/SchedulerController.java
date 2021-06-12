package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.KameHouseJob;
import com.nicobrest.kamehouse.admin.service.SchedulerService;
import com.nicobrest.kamehouse.commons.controller.AbstractController;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Controller class for the scheduler commands.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/admin/scheduler")
public class SchedulerController extends AbstractController {

  private static final String BASE_URL = "/api/v1/admin/scheduler";

  @Autowired
  SchedulerService schedulerService;

  /**
   * Gets the status of all jobs in the system.
   */
  @GetMapping(path = "/jobs")
  @ResponseBody
  public ResponseEntity<List<KameHouseJob>> getAllJobs() {
    logger.trace("{}/jobs (GET)", BASE_URL);
    List<KameHouseJob> jobs = schedulerService.getAllJobsStatus();
    return generateGetResponseEntity(jobs);
  }

  /**
   * Cancel the execution of the specified job.
   */
  @DeleteMapping(path = "/jobs")
  @ResponseBody
  public ResponseEntity<List<KameHouseJob>> cancelJob(
      @RequestParam(value = "name", required = true) String name,
      @RequestParam(value = "group", required = true) String group) {
    logger.trace("{}/jobs?name=[name]&group=[group] (DELETE)", BASE_URL);
    JobKey jobKey = new JobKey(name, group);
    schedulerService.cancelScheduledJob(jobKey);
    List<KameHouseJob> jobs = schedulerService.getAllJobsStatus();
    return generateGetResponseEntity(jobs);
  }

  /**
   * Executes the specified job with the specified delay.
   */
  @PostMapping(path = "/jobs")
  @ResponseBody
  public ResponseEntity<List<KameHouseJob>> scheduleJob(
      @RequestParam(value = "name", required = true) String name,
      @RequestParam(value = "group", required = true) String group,
      @RequestParam(value = "delay", required = true) Integer delay) {
    logger.trace("{}/jobs?name=[name]&group=[group]&delay=[delay] (POST)", BASE_URL);
    JobKey jobKey = new JobKey(name, group);
    schedulerService.scheduleJob(jobKey, delay);
    List<KameHouseJob> jobs = schedulerService.getAllJobsStatus();
    return generatePostResponseEntity(jobs);
  }
}
