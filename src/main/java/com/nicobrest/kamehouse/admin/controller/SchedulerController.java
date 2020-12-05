package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.JobSchedule;
import com.nicobrest.kamehouse.admin.service.SchedulerService;
import com.nicobrest.kamehouse.main.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

  private static final String BASE_URL = "/api/v1/test-module/test-scheduler";

  @Autowired
  SchedulerService schedulerService;

  /**
   * Gets the status of all jobs in the system.
   */
  @GetMapping(path = "/jobs")
  @ResponseBody
  public ResponseEntity<List<JobSchedule>> statusShutdown() {
    logger.trace("{}/jobs (GET)", BASE_URL);
    List<JobSchedule> jobs = schedulerService.getAllJobsStatus();
    return generateGetResponseEntity(jobs);
  }
}
