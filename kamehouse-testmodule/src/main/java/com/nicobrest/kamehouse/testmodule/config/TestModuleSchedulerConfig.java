package com.nicobrest.kamehouse.testmodule.config;

import com.nicobrest.kamehouse.testmodule.model.scheduler.job.SampleJob;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.PostConstruct;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration class to setup the scheduler beans in the test-module package.
 *
 * @author nbrest
 */
@Configuration
@EnableScheduling
public class TestModuleSchedulerConfig {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private Scheduler scheduler;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public TestModuleSchedulerConfig(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  /**
   * Init TestModuleSchedulerConfig.
   */
  @PostConstruct
  public void init() {
    logger.info("init TestModuleSchedulerConfig");
    try {
      scheduler.addJob(sampleJobDetail(), true);
    } catch (SchedulerException e) {
      logger.error("Error adding test-module jobs to the scheduler", e);
    }
  }

  /**
   * sampleJobDetail bean.
   */
  @Bean(name = "sampleJobDetail")
  public JobDetail sampleJobDetail() {
    return JobBuilder.newJob(SampleJob.class)
        .storeDurably()
        .withIdentity(JobKey.jobKey("sampleJobDetail"))
        .withDescription("Sample job")
        .build();
  }

  /**
   * sampleTrigger bean.
   */
  @Bean(name = "sampleTrigger")
  public Trigger sampleTrigger(JobDetail sampleJobDetail) {
    return TriggerBuilder.newTrigger()
        .forJob(sampleJobDetail)
        .withIdentity(TriggerKey.triggerKey("sampleJobTrigger"))
        .withDescription("Trigger to schedule a sample job")
        .withSchedule(
            SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(15).withRepeatCount(10))
        .build();
  }
}
