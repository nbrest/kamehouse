package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.model.KameHouseJob;
import com.nicobrest.kamehouse.commons.service.SchedulerService;
import java.util.List;
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

/**
 * Controller class for the scheduler commands.
 *
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/api/v1/commons/scheduler")
public class SchedulerController extends AbstractController {

  @Autowired SchedulerService schedulerService;

  /** Gets the status of all jobs in the system. */
  @GetMapping(path = "/jobs")
  @ResponseBody
  public ResponseEntity<List<KameHouseJob>> getAllJobs() {
    List<KameHouseJob> jobs = schedulerService.getAllJobsStatus();
    return generateGetResponseEntity(jobs);
  }

  /** Cancel the execution of the specified job. */
  @DeleteMapping(path = "/jobs")
  @ResponseBody
  public ResponseEntity<List<KameHouseJob>> cancelJob(
      @RequestParam(value = "name", required = true) String name,
      @RequestParam(value = "group", required = true) String group) {
    JobKey jobKey = new JobKey(name, group);
    schedulerService.cancelScheduledJob(jobKey);
    List<KameHouseJob> jobs = schedulerService.getAllJobsStatus();
    return generateGetResponseEntity(jobs);
  }

  /** Executes the specified job with the specified delay. */
  @PostMapping(path = "/jobs")
  @ResponseBody
  public ResponseEntity<List<KameHouseJob>> scheduleJob(
      @RequestParam(value = "name", required = true) String name,
      @RequestParam(value = "group", required = true) String group,
      @RequestParam(value = "delay", required = true) Integer delay) {
    JobKey jobKey = new JobKey(name, group);
    schedulerService.scheduleJob(jobKey, delay);
    List<KameHouseJob> jobs = schedulerService.getAllJobsStatus();
    return generatePostResponseEntity(jobs);
  }
}
