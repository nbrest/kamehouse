package com.nicobrest.kamehouse.commons.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.model.KameHouseJob;
import com.nicobrest.kamehouse.commons.service.SchedulerService;
import com.nicobrest.kamehouse.commons.utils.DateUtils;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.quartz.JobKey;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

/**
 * Unit tests for SchedulerController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
public class SchedulerControllerTest extends AbstractControllerTest {

  private static final String SCHEDULER_JOBS_API = "/api/v1/commons/scheduler/jobs";

  @InjectMocks private SchedulerController schedulerController;

  @Mock protected SchedulerService schedulerService;

  @BeforeEach
  public void beforeTest() {
    MockitoAnnotations.openMocks(this);
    Mockito.reset(schedulerService);
    mockMvc = MockMvcBuilders.standaloneSetup(schedulerController).build();
    when(schedulerService.getAllJobsStatus()).thenReturn(getMockedKameHouseJobs());
  }

  /** Schedule job successful test. */
  @Test
  public void scheduleJobTest() throws Exception {
    MockHttpServletResponse response =
        doPost(SCHEDULER_JOBS_API + "?delay=5400&name=jobName" + "&group=DEFAULT");
    List<KameHouseJob> responseBody = getResponseBodyList(response, KameHouseJob.class);

    verifyResponseStatus(response, HttpStatus.CREATED);
    assertEquals(1, responseBody.size());
    verify(schedulerService, times(1)).scheduleJob(Mockito.any(JobKey.class), Mockito.any());
  }

  /** Cancels job schedule successful test. */
  @Test
  public void cancelScheduledJobTest() throws Exception {
    MockHttpServletResponse response = doDelete(SCHEDULER_JOBS_API + "?name=jobName&group=DEFAULT");
    List<KameHouseJob> responseBody = getResponseBodyList(response, KameHouseJob.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals(1, responseBody.size());
    verify(schedulerService, times(1)).cancelScheduledJob(Mockito.any());
  }

  /** Cancels scheduled job error test. */
  @Test
  public void cancelScheduledJobServerErrorTest() throws Exception {
    assertThrows(
        NestedServletException.class,
        () -> {
          Mockito.doThrow(new KameHouseServerErrorException(""))
              .when(schedulerService)
              .cancelScheduledJob(Mockito.any());

          doDelete(SCHEDULER_JOBS_API + "?name=jobName&group=DEFAULT");
        });
  }

  /** Get all jobs status successful test. */
  @Test
  public void getAllJobsStatusTest() throws Exception {
    MockHttpServletResponse response = doGet(SCHEDULER_JOBS_API);
    List<KameHouseJob> responseBody = getResponseBodyList(response, KameHouseJob.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals(1, responseBody.size());
    verify(schedulerService, times(1)).getAllJobsStatus();
  }

  /** Generate mock data. */
  private static List<KameHouseJob> getMockedKameHouseJobs() {
    KameHouseJob kameHouseJob = new KameHouseJob();
    kameHouseJob.setDescription("test job");
    kameHouseJob.setJobClass("com.mada.mada.dane.SampleJob");
    kameHouseJob.setKey(new KameHouseJob.Key("DEFAULT", "sampleJob"));
    KameHouseJob.Schedule schedule = new KameHouseJob.Schedule();
    schedule.setDescription("schedule description");
    schedule.setKey(new KameHouseJob.Key("DEFAULT", "sampleJobSchedule"));
    schedule.setNextRun(DateUtils.getCurrentDate());
    schedule.setPriority(5);
    List<KameHouseJob.Schedule> schedules = kameHouseJob.getSchedules();
    schedules.add(schedule);
    List<KameHouseJob> kameHouseJobs = new ArrayList<>();
    kameHouseJobs.add(kameHouseJob);
    return kameHouseJobs;
  }
}
