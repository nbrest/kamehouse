/** 
 * Functionality to list and manipulate scheduled jobs in the backend.
 * Manager to get the scheduled jobs in the current server and cancel their scheduling.
 * 
 * @author nbrest
 */
class Scheduler {

  #jobs = [[]];
  #schedulerTableTemplate;

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Started initializing scheduler");
    kameHouse.util.banner.setRandomAllBanner();
    kameHouse.util.module.waitForModules(["webappTabsManager"], () => {
      kameHouse.plugin.webappTabsManager.cookiePrefix('kh-admin-scheduler');
      kameHouse.plugin.webappTabsManager.loadStateFromCookies();
    });
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger", "webappTabsManager"], () => {
      this.#init();
    });
  }

  /** Get all jobs */
  getAllJobs(webapp, openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, this.#getApiUrl(webapp), null, null, 
    (responseBody, responseCode, responseDescription, responseHeaders) => { this.#processSuccess(responseBody, responseCode, responseDescription, responseHeaders, webapp); },
    (responseBody, responseCode, responseDescription, responseHeaders) => { this.#processError(responseBody, responseCode, responseDescription, responseHeaders, webapp); });
  }

  /**
   * Loads initial data.
   */
  async #init() {
    await this.#loadTableTemplate();
    this.getAllJobs('admin', false);
    this.getAllJobs('media', false);
    this.getAllJobs('tennisworld', false);
    this.getAllJobs('testmodule', false);
    this.getAllJobs('ui', false);
    this.getAllJobs('vlcrc', false);
  }

  /**
   * Loads the ehcache table html snippet into a variable to be reused as a template on render.
   */
  async #loadTableTemplate() {
    this.#schedulerTableTemplate = await kameHouse.util.fetch.loadHtmlSnippet('/kame-house/html-snippets/scheduler-table.html');
  }

  /**
   * Get scheduler api url for each webapp.
   */
  #getApiUrl(webapp) {
    if (webapp == "ui") {
      return '/kame-house/api/v1/commons/scheduler/jobs';
    } else {
      return '/kame-house-' + webapp + '/api/v1/commons/scheduler/jobs';
    }
  }

  /** Cancel job execution */
  #cancelJobExecution(jobKey, webapp) {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const params = {
      "name" : jobKey.name,
      "group" : jobKey.group
    };
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, this.#getApiUrl(webapp), kameHouse.http.getUrlEncodedHeaders(), params, 
    (responseBody, responseCode, responseDescription, responseHeaders) => { this.#processSuccess(responseBody, responseCode, responseDescription, responseHeaders, webapp); },
    (responseBody, responseCode, responseDescription, responseHeaders) => { this.#processError(responseBody, responseCode, responseDescription, responseHeaders, webapp); });
  }

  /** Update the jobs table content */
  #updateJobsTable(webapp) {
    const jobsData = document.getElementById("jobs-data-" + webapp);
    kameHouse.util.dom.empty(jobsData);
    if (this.#jobs.length == 0 || this.#jobs.length == null || this.#jobs.length == undefined) {
      const message = kameHouse.util.dom.getSpan({
        class: "bold-kh"
      }, "No jobs configured for " + webapp);
      const noJobsTd = kameHouse.util.dom.getTrTd(message);
      kameHouse.util.dom.append(jobsData, noJobsTd);
    } else {
      this.#jobs.forEach((jobEntry) => {
        const tableIdKey = webapp + jobEntry.key.name;
        kameHouse.util.dom.append(jobsData, this.#getTableFromTemplate(tableIdKey));
        kameHouse.util.dom.append(jobsData, kameHouse.util.dom.getBr());
  
        kameHouse.util.dom.setHtmlById("scheduler-table-" + tableIdKey + "-name-val", jobEntry.key.name);
        kameHouse.util.dom.setHtmlById("scheduler-table-" + tableIdKey + "-key-val", jobEntry.key.group + "." + jobEntry.key.name);
        kameHouse.util.dom.setHtmlById("scheduler-table-" + tableIdKey + "-description-val", jobEntry.description);
        kameHouse.util.dom.setHtmlById("scheduler-table-" + tableIdKey + "-jobclass-val", jobEntry.jobClass);
        kameHouse.util.dom.setHtmlById("scheduler-table-" + tableIdKey + "-schedule-val", this.#formatSchedule(jobEntry.schedules));
  
        kameHouse.util.dom.setClickById("clear-scheduler-table-" + tableIdKey, null, () => {
          kameHouse.logger.debug("Clear schedule for " + kameHouse.json.stringify(jobEntry.key));
          this.#cancelJobExecution(jobEntry.key, webapp);
        });
      });
      kameHouse.core.configDynamicHtml();
    }
  }

  /**
   * Get the table from the template.
   */
  #getTableFromTemplate(tableIdKey) {
    // Create a wrapper div to insert the table template
    const tableDiv = kameHouse.util.dom.getElementFromTemplate(this.#schedulerTableTemplate);
    
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
  #formatSchedule(schedules) {
    if (!kameHouse.core.isEmpty(schedules) && schedules.length != 0) {
      const scheduleFormattedArray = [];
      schedules.forEach((schedule) => {
        if (!kameHouse.core.isEmpty(schedule.nextRun)) {
          const date = new Date(parseInt(schedule.nextRun));
          scheduleFormattedArray.push(date.toLocaleString());
        }
      });
      return kameHouse.json.stringify(scheduleFormattedArray);
    } else {
      return "Job not scheduled";
    }
  }
  
  /** Set jobs table to error */
  #updateJobsTableError(webapp) {
    const jobsData = document.getElementById('jobs-data-' + webapp);
    kameHouse.util.dom.empty(jobsData);
    kameHouse.util.dom.append(jobsData, this.#getErrorMessage());
  }

  /** Get the message for the error table */
  #getErrorMessage() {
    return kameHouse.util.dom.getP(null, "Error retrieving jobs from the backend");
  }
  
  /** Process success response */
  #processSuccess(responseBody, responseCode, responseDescription, responseHeaders, webapp) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    this.#jobs = responseBody;
    this.#updateJobsTable(webapp);
  }

  /** Process error response */
  #processError(responseBody, responseCode, responseDescription, responseHeaders, webapp) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    this.#updateJobsTableError(webapp);
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("scheduler", new Scheduler());
});