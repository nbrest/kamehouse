/** 
 * Functionality to list and manipulate scheduled jobs in the backend.
 */
var scheduler;

window.onload = () => {
  scheduler = new Scheduler();
  moduleUtils.waitForModules(["logger", "debuggerHttpClient", "kameHouseWebappTabsManager"], () => {
    logger.info("Started initializing scheduler");
    kameHouseWebappTabsManager.setCookiePrefix('kh-admin-scheduler');
    kameHouseWebappTabsManager.loadStateFromCookies();
    scheduler.init();
  });
  bannerUtils.setRandomAllBanner();
};

function Scheduler() {
  let self = this;
  this.jobs = [[]];
  this.schedulerTableTemplate;

  /**
   * Loads initial data.
   */
  this.init = async () => {
    await self.loadTableTemplate();
    self.getAllJobs('admin', false);
    self.getAllJobs('media', false);
    self.getAllJobs('tennisworld', false);
    self.getAllJobs('testmodule', false);
    self.getAllJobs('ui', false);
    self.getAllJobs('vlcrc', false);
  }

  /**
   * Loads the ehcache table html snippet into a variable to be reused as a template on render.
   */
  this.loadTableTemplate = async () => {
    const response = await fetch('/kame-house/html-snippets/scheduler-table.html');
    self.schedulerTableTemplate = await response.text();
  }

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
  this.getAllJobs = (webapp, openModal) => {
    if (openModal) {
      loadingWheelModal.open();
    }
    debuggerHttpClient.get(self.getApiUrl(webapp), processSuccess, processError, webapp);
  }

  /** Cancel job execution */
  this.cancelJobExecution = (jobKey, webapp) => {
    loadingWheelModal.open();
    let urlParams = "?name=" + jobKey.name + "&group=" + jobKey.group;
    debuggerHttpClient.delete(self.getApiUrl(webapp) + urlParams, null, processSuccess, processError, webapp);
  }

  /** Update the jobs table content */
  this.updateJobsTable = (webapp) => {
    let $jobsData = $("#jobs-data-" + webapp);
    $jobsData.empty();
    self.jobs.forEach((jobEntry) => {
      let tableIdKey = webapp + jobEntry.key.name;
      $jobsData.append(self.getTableFromTemplate(tableIdKey));
      $jobsData.append(self.getBr());

      $("#scheduler-table-" + tableIdKey + "-name-val").text(jobEntry.key.name);
      $("#scheduler-table-" + tableIdKey + "-key-val").text(jobEntry.key.group + "." + jobEntry.key.name);
      $("#scheduler-table-" + tableIdKey + "-description-val").text(jobEntry.description);
      $("#scheduler-table-" + tableIdKey + "-jobclass-val").text(jobEntry.jobClass);
      $("#scheduler-table-" + tableIdKey + "-schedule-val").text(self.formatSchedule(jobEntry.schedules));

      $("#clear-scheduler-table-" + tableIdKey).click(() => {
        logger.debug("Clear schedule for " + JSON.stringify(jobEntry.key));
        self.cancelJobExecution(jobEntry.key, webapp);
      });
    });
  }

  /**
   * Get the table from the template.
   */
  this.getTableFromTemplate = (tableIdKey) => {
    // Create a wrapper div to insert the table template
    let tableDiv = self.getSchedulerTableDivInstance();
    
    // Update the ids and classes on the table generated from the template
    tableDiv.querySelector('tr #scheduler-table-TEMPLATE-name-val').id = "scheduler-table-" + tableIdKey + "-name-val";
    tableDiv.querySelector('tr #scheduler-table-TEMPLATE-key-val').id = "scheduler-table-" + tableIdKey + "-key-val";
    tableDiv.querySelector('tr #scheduler-table-TEMPLATE-description-val').id = "scheduler-table-" + tableIdKey + "-description-val";
    tableDiv.querySelector('tr #scheduler-table-TEMPLATE-jobclass-val').id = "scheduler-table-" + tableIdKey + "-jobclass-val";
    tableDiv.querySelector('tr #scheduler-table-TEMPLATE-schedule-val').id = "scheduler-table-" + tableIdKey + "-schedule-val";
    tableDiv.querySelector('tr #clear-scheduler-table-TEMPLATE').id = "clear-scheduler-table-" + tableIdKey;

    return tableDiv;
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
    $jobsData.append(self.getErrorMessage());
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

  /** Dynamic DOM element generation ------------------------------------------ */
  this.getSchedulerTableDivInstance = () => {
    let tableDivWrapper = document.createElement('div');
    tableDivWrapper.innerHTML = self.schedulerTableTemplate;
    return tableDivWrapper.firstChild;
  }

  this.getErrorMessage = () => {
    let errorMessage = $('<p>');
    errorMessage.text("Error retrieving jobs from the backend");
    return errorMessage;
  }

  this.getBr = () => {
    return $('<br>');
  }
}