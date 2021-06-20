/** 
 * Functionality to list and manipulate scheduled jobs in the backend.
 */
var scheduler;

window.onload = () => {
  scheduler = new Scheduler();
  moduleUtils.waitForModules(["logger", "apiCallTable", "kameHouseWebappTabsManager"], () => {
    logger.info("Started initializing scheduler");
    kameHouseWebappTabsManager.openTab('tab-admin');
    scheduler.getAllJobs('admin');
    scheduler.getAllJobs('media');
    scheduler.getAllJobs('tennisworld');
    scheduler.getAllJobs('testmodule');
    scheduler.getAllJobs('ui');
    scheduler.getAllJobs('vlcrc');
  });
  bannerUtils.setRandomAllBanner();
};

function Scheduler() {
  let self = this;
  this.jobs = [[]];

  /**
   * Get scheduler api url for each webapp.
   */
  this.getApiUrl = (webapp) => {
    if (webapp == "ui") {
      return '/kame-house/api/v1/commons/scheduler/jobs';
    } else {
      return '/kame-house-' + webapp + '/api/v1/commons/scheduler/jobs';
    }
  }

  /** Get all jobs */
  this.getAllJobs = (webapp) => {
    loadingWheelModal.open();
    apiCallTable.get(self.getApiUrl(webapp), processSuccess, processError, webapp);
  }

  /** Cancel job execution */
  this.cancelJobExecution = (jobKey, webapp) => {
    loadingWheelModal.open();
    let urlParams = "?name=" + jobKey.name + "&group=" + jobKey.group;
    apiCallTable.delete(self.getApiUrl(webapp) + urlParams, null, processSuccess, processError, webapp);
  }

  /** Update the jobs table content */
  this.updateJobsTable = (webapp) => {
    let $jobsData = $("#jobs-data-" + webapp);
    $jobsData.empty();
    self.jobs.forEach((jobEntry) => {
      let $jobTable = $('<table class="table table-bordered table-scheduler table-bordered-kh table-responsive-kh table-responsive">');
      let $jobTableRow;

      $jobTableRow = $("<tr>");
      $jobTableRow.append($('<td class="td-scheduler-header">').append($('<div>').text("name")));
      let $jobTableRowContent = $('<td class="td-scheduler-header">');
      $jobTableRowContent.append($('<div>').text(jobEntry.key.name));
      $jobTableRow.append($jobTableRowContent);
      $jobTable.append($jobTableRow);

      $jobTableRow = $('<tr>');
      $jobTableRow.append($('<td class="td-scheduler-header">').text("key"));
      $jobTableRow.append($("<td>").text(jobEntry.key.group + "." + jobEntry.key.name));
      $jobTable.append($jobTableRow);

      $jobTableRow = $('<tr>');
      $jobTableRow.append($('<td class="td-scheduler-header">').text("description"));
      $jobTableRow.append($("<td>").text(jobEntry.description));
      $jobTable.append($jobTableRow);

      $jobTableRow = $('<tr>');
      $jobTableRow.append($('<td class="td-scheduler-header">').text("jobClass"));
      $jobTableRow.append($("<td>").text(jobEntry.jobClass));
      $jobTable.append($jobTableRow);

      $jobTableRow = $('<tr>');
      $jobTableRow.append($('<td class="td-scheduler-header">').text("schedule"));
      $jobTableRowContent = $("<td>");
      $jobTableRowContent.append($('<span>').text(self.formatSchedule(jobEntry.schedules)));
      $jobTableRowContent.append("<img id='clear-" + jobEntry.key.name + "-"  + webapp + "' class='btn-scheduler scheduler-status-buttons' src='/kame-house/img/other/cancel.png' alt='Clear Schedule' title='Clear Schedule' />");
      $jobTableRow.append($jobTableRowContent);
      $jobTable.append($jobTableRow);

      $jobsData.append($jobTable);
      $jobsData.append("<br>");

      $("#clear-" + jobEntry.key.name + "-" + webapp).click(() => {
        logger.debug("Clear schedule for " + JSON.stringify(jobEntry.key));
        self.cancelJobExecution(jobEntry.key, webapp);
      });
    });
  }

  /** Returns the schedule formated to display in the UI */
  this.formatSchedule = (schedules) => {
    if (!isNullOrUndefined(schedules) && schedules.length != 0) {
      let scheduleFormattedArray = []
      schedules.forEach(schedule => {
        if (!isNullOrUndefined(schedule.nextRun)) {
          let date = new Date(parseInt(schedule.nextRun));
          scheduleFormattedArray.push(date.toLocaleString());
        }
      });
      return JSON.stringify(scheduleFormattedArray);
    } else {
      return "Job not scheduled";
    }
  }
  
  /** Set jobs table to error */
  this.updateJobsTableError = (webapp) => {
    let $jobsData = $('#jobs-data-' + webapp);
    $jobsData.empty();
    let errorMessage = $('<p>').text("Error retrieving jobs from the backend");
    $jobsData.append(errorMessage);
  }

  /** Process success response */
  function processSuccess(responseBody, responseCode, responseDescription, webapp) {
    loadingWheelModal.close();
    self.jobs = responseBody;
    self.updateJobsTable(webapp);
  }

  /** Process error response */
  function processError(responseBody, responseCode, responseDescription, webapp) {
    loadingWheelModal.close();
    self.updateJobsTableError(webapp);
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
  }
}