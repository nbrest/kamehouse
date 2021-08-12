/**
 * Test module scheduler functions.
 * 
 * Dependencies: logger, debuggerHttpClient.
 * 
 * @author nbrest
 */
var scheduler;

var main = () => {
  bannerUtils.setRandomAllBanner();
  moduleUtils.waitForModules(["debuggerHttpClient"], () => {
    logger.info("Started initializing scheduler");
    scheduler = new Scheduler();
    scheduler.getSampleJobStatus(false);
  });
};

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
    logger.trace("Sample job delay: " + delay);
    const requestParam = "delay=" + delay;
    loadingWheelModal.open();
    debuggerHttpClient.postUrlEncoded(TEST_MODULE_API_URL + SAMPLE_JOB_URL, requestParam, processSuccessSampleJob, processErrorSampleJob);
  }

  /** Cancel a SampleJob command */
  function cancelSampleJob() {
    loadingWheelModal.open();
    debuggerHttpClient.delete(TEST_MODULE_API_URL + SAMPLE_JOB_URL, null, processSuccessSampleJob, processErrorSampleJob);
  }

  /** Get the SampleJob command status */
  function getSampleJobStatus(openModal) {
    if (openModal) {
      loadingWheelModal.open();
    }
    debuggerHttpClient.get(TEST_MODULE_API_URL + SAMPLE_JOB_URL, processSuccessSampleJobStatus, processErrorSampleJobStatus);
  }

  /** Process the success response of a SampleJob command (set/cancel) */
  function processSuccessSampleJob(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    getSampleJobStatus();
  }

  /** Process the error response of a SampleJob command (set/cancel) */
  function processErrorSampleJob(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
    getSampleJobStatus();
  }

  /** Update the status of SampleJob command */
  function processSuccessSampleJobStatus(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    domUtils.setHtml($("#sample-job-status"), responseBody.message);
  }

  /** Update the status of SampleJob command with an error */
  function processErrorSampleJobStatus(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
    domUtils.setHtml($("#sample-job-status"), "Error getting the status of SampleJob command");
  }
}

/**
 * Call main.
 */
$(document).ready(main);
