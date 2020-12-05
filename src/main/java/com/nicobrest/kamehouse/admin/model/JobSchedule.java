package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.main.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Internal kamehouse representation of a job that can be scheduled.
 *
 * @author nbrest
 */
public class JobSchedule {

  private Job job;
  private List<Schedule> schedules;

  public JobSchedule() {
    this.job = new Job();
    this.schedules = new ArrayList<>();
  }

  public Job getJob() {
    return job;
  }

  public void setJob(Job job) {
    this.job = job;
  }

  public List<Schedule> getSchedules() {
    return schedules;
  }

  public void setSchedules(List<Schedule> schedules) {
    this.schedules = schedules;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    JobSchedule that = (JobSchedule) obj;
    return job.equals(that.job)
       && Objects.equals(schedules, that.schedules);
  }

  @Override
  public int hashCode() {
    return Objects.hash(job, schedules);
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }

  /**
   * Key used to identify a job or a schedule.
   * Similar to a JobKey or TriggerKey on quartz.
   *
   * @author nbrest
   */
  public static class Key {
    private String group;
    private String name;

    public Key(String group, String name) {
      this.group = group;
      this.name = name;
    }

    public String getGroup() {
      return group;
    }

    public void setGroup(String group) {
      this.group = group;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      Key key = (Key) obj;
      return Objects.equals(group, key.group)
          && Objects.equals(name, key.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(group, name);
    }

    @Override
    public String toString() {
      return JsonUtils.toJsonString(this, super.toString());
    }
  }

  /**
   * The job class represents an instance of a job that can be scheduled.
   * Similar to a JobDetail class in quartz framework.
   *
   * @author nbrest
   */
  public static class Job {
    private Key key;
    private String description;
    private String jobClass;

    public Key getKey() {
      return key;
    }

    public void setKey(Key key) {
      this.key = key;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getJobClass() {
      return jobClass;
    }

    public void setJobClass(String jobClass) {
      this.jobClass = jobClass;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      Job job = (Job) obj;
      return Objects.equals(key, job.key)
          && Objects.equals(description, job.description)
          && Objects.equals(jobClass, job.jobClass);
    }

    @Override
    public int hashCode() {
      return Objects.hash(key, description, jobClass);
    }

    @Override
    public String toString() {
      return JsonUtils.toJsonString(this, super.toString());
    }
  }

  /**
   * The schedule class represents a scheduling of a job. A job can have multiple schedules.
   * Similar to what a Trigger class represents on quartz framework.
   *
   * @author nbrest
   */
  public static class Schedule {
    private Key key;
    private String description;
    private Date nextRun;
    private int priority;

    public Key getKey() {
      return key;
    }

    public void setKey(Key key) {
      this.key = key;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public Date getNextRun() {
      return (nextRun != null ? (Date) nextRun.clone() : null);
    }

    public void setNextRun(Date nextRun) {
      this.nextRun = (nextRun != null ? (Date) nextRun.clone() : null);
    }

    public int getPriority() {
      return priority;
    }

    public void setPriority(int priority) {
      this.priority = priority;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      Schedule schedule = (Schedule) obj;
      return priority == schedule.priority
          && Objects.equals(key, schedule.key)
          && Objects.equals(description, schedule.description)
          && Objects.equals(nextRun, schedule.nextRun);
    }

    @Override
    public int hashCode() {
      return Objects.hash(key, description, nextRun, priority);
    }

    @Override
    public String toString() {
      return JsonUtils.toJsonString(this, super.toString());
    }
  }
}
