package com.nicobrest.kamehouse.commons.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.nicobrest.kamehouse.commons.model.KameHouseJob;
import com.nicobrest.kamehouse.commons.service.SchedulerService;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.quartz.JobKey;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Unit tests for SchedulerController class.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class SchedulerControllerTest extends AbstractControllerTest {

  private static final String SCHEDULER_JOBS_API = "/api/v1/commons/scheduler/jobs";
  @InjectMocks
  private SchedulerController schedulerController;

  @Mock
  protected SchedulerService schedulerService;

  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    Mockito.reset(schedulerService);
    mockMvc = MockMvcBuilders.standaloneSetup(schedulerController).build();
    when(schedulerService.getAllJobsStatus()).thenReturn(getMockedKameHouseJobs());
  }

  /**
   * Schedule job successful test.
   */
  @Test
  public void scheduleJobTest() throws Exception {
    MockHttpServletResponse response = doPost(SCHEDULER_JOBS_API + "?delay=5400&name=jobName" +
        "&group=DEFAULT");
    List<KameHouseJob> responseBody = getResponseBodyList(response,
        KameHouseJob.class);

    verifyResponseStatus(response, HttpStatus.CREATED);
    assertEquals(1, responseBody.size());
    verify(schedulerService, times(1)).scheduleJob(Mockito.any(JobKey.class), Mockito.any());
  }

  /**
   * Cancels job schedule successful test.
   */
  @Test
  public void cancelScheduledJobTest() throws Exception {
    MockHttpServletResponse response = doDelete(SCHEDULER_JOBS_API + "?name=jobName&group=DEFAULT");
    List<KameHouseJob> responseBody = getResponseBodyList(response,
        KameHouseJob.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals(1, responseBody.size());
    verify(schedulerService, times(1)).cancelScheduledJob(Mockito.any());
  }

  /**
   * Cancels scheduled job error test.
   */
  @Test
  public void cancelScheduledJobServerErrorTest() throws Exception {
    thrown.expect(NestedServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable> instanceOf(
        KameHouseServerErrorException.class));
    Mockito.doThrow(new KameHouseServerErrorException("")).when(schedulerService)
        .cancelScheduledJob(Mockito.any());

    doDelete(SCHEDULER_JOBS_API + "?name=jobName&group=DEFAULT");
  }

  /**
   * Get all jobs status successful test.
   */
  @Test
  public void getAllJobsStatusTest() throws Exception {
    MockHttpServletResponse response = doGet(SCHEDULER_JOBS_API);
    List<KameHouseJob> responseBody = getResponseBodyList(response,
        KameHouseJob.class);

    verifyResponseStatus(response, HttpStatus.OK);
    assertEquals(1, responseBody.size());
    verify(schedulerService, times(1)).getAllJobsStatus();
  }

  /**
   * Generate mock data.
   */
  private static List<KameHouseJob> getMockedKameHouseJobs() {
    List<KameHouseJob> kameHouseJobs = new ArrayList<>();
    KameHouseJob kameHouseJob = new KameHouseJob();
    kameHouseJob.setDescription("test job");
    kameHouseJob.setJobClass("com.mada.mada.dane.SampleJob");
    kameHouseJob.setKey(new KameHouseJob.Key("DEFAULT", "sampleJob"));
    List<KameHouseJob.Schedule> schedules = kameHouseJob.getSchedules();
    KameHouseJob.Schedule schedule = new KameHouseJob.Schedule();
    schedule.setDescription("schedule description");
    schedule.setKey(new KameHouseJob.Key("DEFAULT", "sampleJobSchedule"));
    schedule.setNextRun(new Date());
    schedule.setPriority(5);
    schedules.add(schedule);
    kameHouseJobs.add(kameHouseJob);
    return kameHouseJobs;
  }
}
