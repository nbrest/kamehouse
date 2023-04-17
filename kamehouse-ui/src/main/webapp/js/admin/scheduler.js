/** 
 * Functionality to list and manipulate scheduled jobs in the backend.
 */
var scheduler;

window.onload = () => {
  scheduler = new Scheduler();
  kameHouse.util.module.waitForModules(["kameHouseDebugger", "webappTabsManager"], () => {
    kameHouse.logger.info("Started initializing scheduler");
    kameHouse.plugin.webappTabsManager.setCookiePrefix('kh-admin-scheduler');
    kameHouse.plugin.webappTabsManager.loadStateFromCookies();
    scheduler.init();
  });
  kameHouse.util.banner.setRandomAllBanner();
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
    schedulerTableTemplate = await kameHouse.util.fetch.loadHtmlSnippet('/kame-house/html-snippets/scheduler-table.html');
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
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    kameHouse.plugin.debugger.http.get(getApiUrl(webapp), null, null, processSuccess, processError, webapp);
  }

  /** Cancel job execution */
  function cancelJobExecution(jobKey, webapp) {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const params = {
      "name" : jobKey.name,
      "group" : jobKey.group
    };
    kameHouse.plugin.debugger.http.delete(getApiUrl(webapp), kameHouse.http.getUrlEncodedHeaders(), params, processSuccess, processError, webapp);
  }

  /** Update the jobs table content */
  function updateJobsTable(webapp) {
    const $jobsData = $("#jobs-data-" + webapp);
    kameHouse.util.dom.empty($jobsData);
    jobs.forEach((jobEntry) => {
      const tableIdKey = webapp + jobEntry.key.name;
      kameHouse.util.dom.append($jobsData, getTableFromTemplate(tableIdKey));
      kameHouse.util.dom.append($jobsData, kameHouse.util.dom.getBr());

      kameHouse.util.dom.setHtml($("#scheduler-table-" + tableIdKey + "-name-val"), jobEntry.key.name);
      kameHouse.util.dom.setHtml($("#scheduler-table-" + tableIdKey + "-key-val"), jobEntry.key.group + "." + jobEntry.key.name);
      kameHouse.util.dom.setHtml($("#scheduler-table-" + tableIdKey + "-description-val"), jobEntry.description);
      kameHouse.util.dom.setHtml($("#scheduler-table-" + tableIdKey + "-jobclass-val"), jobEntry.jobClass);
      kameHouse.util.dom.setHtml($("#scheduler-table-" + tableIdKey + "-schedule-val"), formatSchedule(jobEntry.schedules));

      kameHouse.util.dom.setClick($("#clear-scheduler-table-" + tableIdKey), null, () => {
        kameHouse.logger.debug("Clear schedule for " + JSON.stringify(jobEntry.key));
        cancelJobExecution(jobEntry.key, webapp);
      });
    });
  }

  /**
   * Get the table from the template.
   */
  function getTableFromTemplate(tableIdKey) {
    // Create a wrapper div to insert the table template
    const tableDiv = kameHouse.util.dom.getElementFromTemplate(schedulerTableTemplate);
    
    // Update the ids and classes on the table generated from the template
    kameHouse.util.dom.setId(tableDiv.querySelector('tr #scheduler-table-TEMPLATE-name-val'), "scheduler-table-" + tableIdKey + "-name-val");
    kameHouse.util.dom.setId(tableDiv.querySelector('tr #scheduler-table-TEMPLATE-key-val'), "scheduler-table-" + tableIdKey + "-key-val");
    kameHouse.util.dom.setId(tableDiv.querySelector('tr #scheduler-table-TEMPLATE-description-val'), "scheduler-table-" + tableIdKey + "-description-val");
    kameHouse.util.dom.setId(tableDiv.querySelector('tr #scheduler-table-TEMPLATE-jobclass-val'), "scheduler-table-" + tableIdKey + "-jobclass-val");
    kameHouse.util.dom.setId(tableDiv.querySelector('tr #scheduler-table-TEMPLATE-schedule-val'), "scheduler-table-" + tableIdKey + "-schedule-val");
    kameHouse.util.dom.setId(tableDiv.querySelector('tr #clear-scheduler-table-TEMPLATE'), "clear-scheduler-table-" + tableIdKey);

    return tableDiv;
  }

  /** Returns the schedule formated to display in the UI */
  function formatSchedule(schedules) {
    if (!kameHouse.core.isEmpty(schedules) && schedules.length != 0) {
      const scheduleFormattedArray = [];
      schedules.forEach((schedule) => {
        if (!kameHouse.core.isEmpty(schedule.nextRun)) {
          const date = new Date(parseInt(schedule.nextRun));
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
    const $jobsData = $('#jobs-data-' + webapp);
    kameHouse.util.dom.empty($jobsData);
    kameHouse.util.dom.append($jobsData, getErrorMessage());
  }

  /** Get the message for the error table */
  function getErrorMessage() {
    return kameHouse.util.dom.getP(null, "Error retrieving jobs from the backend");
  }
  
  /** Process success response */
  function processSuccess(responseBody, responseCode, responseDescription, webapp) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    jobs = responseBody;
    updateJobsTable(webapp);
  }

  /** Process error response */
  function processError(responseBody, responseCode, responseDescription, webapp) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    updateJobsTableError(webapp);
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription);
  }
}