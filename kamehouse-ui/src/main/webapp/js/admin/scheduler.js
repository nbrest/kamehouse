/** 
 * Functionality to list and manipulate scheduled jobs in the backend.
 */
var scheduler;

window.onload = () => {
  scheduler = new Scheduler();
  moduleUtils.waitForModules(["debuggerHttpClient", "kameHouseWebappTabsManager"], () => {
    logger.info("Started initializing scheduler");
    kameHouseWebappTabsManager.setCookiePrefix('kh-admin-scheduler');
    kameHouseWebappTabsManager.loadStateFromCookies();
    scheduler.init();
  });
  bannerUtils.setRandomAllBanner();
};

/**
 * Manager to get the scheduled jobs in the current server and cancel their scheduling.
 */
function Scheduler() {

  this.init = init;
  this.getAllJobs = getAllJobs;

  let jobs = [[]];
  let schedulerTableTemplate;

  /**
   * Loads initial data.
   */
  async function init() {
    await loadTableTemplate();
    getAllJobs('admin', false);
    getAllJobs('media', false);
    getAllJobs('tennisworld', false);
    getAllJobs('testmodule', false);
    getAllJobs('ui', false);
    getAllJobs('vlcrc', false);
  }

  /**
   * Loads the ehcache table html snippet into a variable to be reused as a template on render.
   */
  async function loadTableTemplate() {
    schedulerTableTemplate = await fetchUtils.loadHtmlSnippet('/kame-house/html-snippets/scheduler-table.html');
  }

  /**
   * Get scheduler api url for each webapp.
   */
  function getApiUrl(webapp) {
    if (webapp == "ui") {
      return '/kame-house/api/v1/commons/scheduler/jobs';
    } else {
      return '/kame-house-' + webapp + '/api/v1/commons/scheduler/jobs';
    }
  }

  /** Get all jobs */
  function getAllJobs(webapp, openModal) {
    if (openModal) {
      loadingWheelModal.open();
    }
    debuggerHttpClient.get(getApiUrl(webapp), processSuccess, processError, webapp);
  }

  /** Cancel job execution */
  function cancelJobExecution(jobKey, webapp) {
    loadingWheelModal.open();
    let urlParams = "?name=" + jobKey.name + "&group=" + jobKey.group;
    debuggerHttpClient.delete(getApiUrl(webapp) + urlParams, null, processSuccess, processError, webapp);
  }

  /** Update the jobs table content */
  function updateJobsTable(webapp) {
    let $jobsData = $("#jobs-data-" + webapp);
    domUtils.empty($jobsData);
    jobs.forEach((jobEntry) => {
      let tableIdKey = webapp + jobEntry.key.name;
      domUtils.append($jobsData, getTableFromTemplate(tableIdKey));
      domUtils.append($jobsData, domUtils.getBr());

      domUtils.setHtml($("#scheduler-table-" + tableIdKey + "-name-val"), jobEntry.key.name);
      domUtils.setHtml($("#scheduler-table-" + tableIdKey + "-key-val"), jobEntry.key.group + "." + jobEntry.key.name);
      domUtils.setHtml($("#scheduler-table-" + tableIdKey + "-description-val"), jobEntry.description);
      domUtils.setHtml($("#scheduler-table-" + tableIdKey + "-jobclass-val"), jobEntry.jobClass);
      domUtils.setHtml($("#scheduler-table-" + tableIdKey + "-schedule-val"), formatSchedule(jobEntry.schedules));

      domUtils.setClick($("#clear-scheduler-table-" + tableIdKey), null, () => {
        logger.debug("Clear schedule for " + JSON.stringify(jobEntry.key));
        cancelJobExecution(jobEntry.key, webapp);
      });
    });
  }

  /**
   * Get the table from the template.
   */
  function getTableFromTemplate(tableIdKey) {
    // Create a wrapper div to insert the table template
    let tableDiv = domUtils.getElementFromTemplate(schedulerTableTemplate);
    
    // Update the ids and classes on the table generated from the template
    domUtils.setId(tableDiv.querySelector('tr #scheduler-table-TEMPLATE-name-val'), "scheduler-table-" + tableIdKey + "-name-val");
    domUtils.setId(tableDiv.querySelector('tr #scheduler-table-TEMPLATE-key-val'), "scheduler-table-" + tableIdKey + "-key-val");
    domUtils.setId(tableDiv.querySelector('tr #scheduler-table-TEMPLATE-description-val'), "scheduler-table-" + tableIdKey + "-description-val");
    domUtils.setId(tableDiv.querySelector('tr #scheduler-table-TEMPLATE-jobclass-val'), "scheduler-table-" + tableIdKey + "-jobclass-val");
    domUtils.setId(tableDiv.querySelector('tr #scheduler-table-TEMPLATE-schedule-val'), "scheduler-table-" + tableIdKey + "-schedule-val");
    domUtils.setId(tableDiv.querySelector('tr #clear-scheduler-table-TEMPLATE'), "clear-scheduler-table-" + tableIdKey);

    return tableDiv;
  }

  /** Returns the schedule formated to display in the UI */
  function formatSchedule(schedules) {
    if (!isEmpty(schedules) && schedules.length != 0) {
      let scheduleFormattedArray = []
      schedules.forEach(schedule => {
        if (!isEmpty(schedule.nextRun)) {
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
  function updateJobsTableError(webapp) {
    let $jobsData = $('#jobs-data-' + webapp);
    domUtils.empty($jobsData);
    domUtils.append($jobsData, getErrorMessage());
  }

  /** Get the message for the error table */
  function getErrorMessage() {
    return domUtils.getP(null, "Error retrieving jobs from the backend");
  }
  
  /** Process success response */
  function processSuccess(responseBody, responseCode, responseDescription, webapp) {
    loadingWheelModal.close();
    jobs = responseBody;
    updateJobsTable(webapp);
  }

  /** Process error response */
  function processError(responseBody, responseCode, responseDescription, webapp) {
    loadingWheelModal.close();
    updateJobsTableError(webapp);
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
  }
}