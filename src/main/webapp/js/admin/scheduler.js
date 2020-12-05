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

  /** Update the jobs table content */
  this.updateJobsTable = () => {
    let $tableBody = $('#log-level-tbody');
    self.addJobsTableHeader();
    self.jobs.forEach((jobEntry) => {
      let tableRow = $('<tr>');
      tableRow.append($('<td>').text(jobEntry.job.key.name));
      let scheduleFormatted = self.formatSchedule(jobEntry.schedules);
      tableRow.append($('<td>').text(scheduleFormatted));
      $tableBody.append(tableRow);
    });
  }

  this.formatSchedule = (schedules) => {
    if (!isNullOrUndefined(schedules) && schedules.length != 0) {
      let scheduleFormattedArray = []
      schedules.forEach(schedule => {
        let date = new Date(parseInt(schedule.nextRun));
        scheduleFormattedArray.push(date.toLocaleString());
      });
      return JSON.stringify(scheduleFormattedArray);
    } else {
      return "No schedule for this job";
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
