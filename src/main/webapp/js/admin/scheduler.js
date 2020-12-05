/** 
 * Functionality to list and manipulate scheduled jobs in the backend.
 */
var scheduler;
var kamehouseDebugger;

window.onload = () => {
  scheduler = new Scheduler();
  moduleUtils.waitForModules(["logger", "httpClient"], () => {
    logger.info("Started initializing scheduler");
    scheduler.getAllJobs();
  });
  kamehouseDebugger = new KamehouseDebugger();
  bannerUtils.setRandomAllBanner();
};

function Scheduler() {
  let self = this;
  const SCHEDULER_JOBS_API_URL = "/kame-house/api/v1/admin/scheduler/jobs";
  this.jobs = [];

  /** Get all jobs */
  this.getAllJobs = () => {
    loadingWheelModal.open();
    apiCallTable.get(SCHEDULER_JOBS_API_URL, processSuccess, processError);
  }

  /** Cancel job execution */
  this.cancelJobExecution = (jobKey) => {
    loadingWheelModal.open();
    let urlParams = "?name=" + jobKey.name + "&group=" + jobKey.group;
    apiCallTable.delete(SCHEDULER_JOBS_API_URL + urlParams, null, processSuccess, processError);
  }

  /** Update the jobs table content */
  this.updateJobsTable = () => {
    let $jobsData = $("#jobs-data");
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
      $jobTableRowContent.append("<img id='clear-" + jobEntry.key.name + "' class='btn-scheduler scheduler-status-buttons' src='/kame-house/img/other/cancel.png' alt='Clear Schedule' title='Clear Schedule' />");
      $jobTableRow.append($jobTableRowContent);
      $jobTable.append($jobTableRow);

      $jobsData.append($jobTable);
      $jobsData.append("<br>");

      $("#clear-" + jobEntry.key.name).click(() => {
        logger.debug("Clear schedule for " + JSON.stringify(jobEntry.key));
        self.cancelJobExecution(jobEntry.key);
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

  /** Add jobs table header */
  this.addJobsTableHeader = () => {
    let $tableBody = $('#log-level-tbody');
    $tableBody.empty();
    let tableRow = $('<tr id="log-level-thead">');
    tableRow.append($('<td>').text("Job Key"));
    tableRow.append($('<td>').text("Schedule"));
    $tableBody.append(tableRow);
  }
  
  /** Set jobs table to error */
  this.updateJobsTableError = () => {
    let $tableBody = $('#log-level-tbody');
    $tableBody.empty();
    let tableRow = $('<tr>');
    tableRow.append($('<td>').text("Error retrieving jobs from the backend"));
    $tableBody.append(tableRow);
  }

  /** Process success response */
  function processSuccess(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    self.jobs = responseBody;
    self.updateJobsTable();
  }

  /** Process error response */
  function processError(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    self.updateJobsTableError();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
  }
}

/** 
 * Handles the debugger functionality.
 * 
 * @author nbrest
 */
function KamehouseDebugger() {

  /** Toggle debug mode. */
  this.toggleDebugMode = () => {
    logger.debug("Toggled debug mode")
    let debugModeDiv = document.getElementById("debug-mode");
    debugModeDiv.classList.toggle("hidden-kh");
  }
}
