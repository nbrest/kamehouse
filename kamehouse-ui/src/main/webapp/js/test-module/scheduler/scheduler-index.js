/**
 * Test module scheduler functions.
 * 
 * Dependencies: logger, kameHouse.plugin.debugger.http.
 * 
 * @author nbrest
 */
var scheduler;

function mainScheduler() {
  kameHouse.util.banner.setRandomAllBanner();
  kameHouse.util.module.waitForModules(["kameHouseDebugger"], () => {
    kameHouse.logger.info("Started initializing scheduler");
    scheduler = new Scheduler();
    scheduler.getSampleJobStatus(false);
  });
}

/**
 * Manager to handle the scheduling of the sample job.
 */
function Scheduler() {

  this.setSampleJob = setSampleJob;
  this.cancelSampleJob = cancelSampleJob;
  this.getSampleJobStatus = getSampleJobStatus;

  const TEST_MODULE_API_URL = "/kame-house-testmodule/api/v1/test-module";
  const SAMPLE_JOB_URL = '/test-scheduler/sample-job';

  /**
   * --------------------------------------------------------------------------
   * Sample Job functions
   */
  /** Set a sample job command */
  function setSampleJob() {
    const delay = document.getElementById("sample-job-delay-dropdown").value;
    kameHouse.logger.trace("Sample job delay: " + delay);
    const requestParam =  {
      "delay" : delay
    };
    kameHouse.plugin.modal.loadingWheelModal.open();
    kameHouse.plugin.debugger.http.post(TEST_MODULE_API_URL + SAMPLE_JOB_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam, processSuccessSampleJob, processErrorSampleJob);
  }

  /** Cancel a SampleJob command */
  function cancelSampleJob() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    kameHouse.plugin.debugger.http.delete(TEST_MODULE_API_URL + SAMPLE_JOB_URL, null, null, processSuccessSampleJob, processErrorSampleJob);
  }

  /** Get the SampleJob command status */
  function getSampleJobStatus(openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    kameHouse.plugin.debugger.http.get(TEST_MODULE_API_URL + SAMPLE_JOB_URL, null, null, processSuccessSampleJobStatus, processErrorSampleJobStatus);
  }

  /** Process the success response of a SampleJob command (set/cancel) */
  function processSuccessSampleJob(responseBody, responseCode, responseDescription) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    getSampleJobStatus();
  }

  /** Process the error response of a SampleJob command (set/cancel) */
  function processErrorSampleJob(responseBody, responseCode, responseDescription) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription);
    getSampleJobStatus();
  }

  /** Update the status of SampleJob command */
  function processSuccessSampleJobStatus(responseBody, responseCode, responseDescription) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.util.dom.setHtml($("#sample-job-status"), responseBody.message);
  }

  /** Update the status of SampleJob command with an error */
  function processErrorSampleJobStatus(responseBody, responseCode, responseDescription) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription);
    kameHouse.util.dom.setHtml($("#sample-job-status"), "Error getting the status of SampleJob command");
  }
}

/**
 * Call main.
 */
$(document).ready(mainScheduler);
