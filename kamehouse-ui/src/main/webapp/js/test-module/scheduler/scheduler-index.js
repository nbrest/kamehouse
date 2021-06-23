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
  importCss();
  moduleUtils.waitForModules(["logger", "debuggerHttpClient"], () => {
    logger.info("Started initializing scheduler");
    scheduler = new Scheduler();
    scheduler.getSampleJobStatus(false);
  });
};

function importCss() {
  $('head').append('<link rel="stylesheet" type="text/css" href="/kame-house/css/test-module/scheduler/scheduler.css">');
}

function Scheduler() {
  let self = this;
  const TEST_MODULE_API_URL = "/kame-house-testmodule/api/v1/test-module";
  const SAMPLE_JOB_URL = '/test-scheduler/sample-job';

  /**
   * --------------------------------------------------------------------------
   * Sample Job functions
   */
  /** Set a sample job command */
  this.setSampleJob = () => {
    let delay = document.getElementById("sample-job-delay-dropdown").value;
    logger.trace("Sample job delay: " + delay);
    let requestParam = "delay=" + delay;
    loadingWheelModal.open();
    debuggerHttpClient.postUrlEncoded(TEST_MODULE_API_URL + SAMPLE_JOB_URL, requestParam, processSuccessSampleJob, processErrorSampleJob);
  }

  /** Cancel a SampleJob command */
  this.cancelSampleJob = () => {
    loadingWheelModal.open();
    debuggerHttpClient.delete(TEST_MODULE_API_URL + SAMPLE_JOB_URL, null, processSuccessSampleJob, processErrorSampleJob);
  }

  /** Get the SampleJob command status */
  this.getSampleJobStatus = (openModal) => {
    if (openModal) {
      loadingWheelModal.open();
    }
    debuggerHttpClient.get(TEST_MODULE_API_URL + SAMPLE_JOB_URL, processSuccessSampleJobStatus, processErrorSampleJobStatus);
  }

  /** Process the success response of a SampleJob command (set/cancel) */
  function processSuccessSampleJob(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    self.getSampleJobStatus();
  }

  /** Process the error response of a SampleJob command (set/cancel) */
  function processErrorSampleJob(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
    self.getSampleJobStatus();
  }

  /** Update the status of SampleJob command */
  function processSuccessSampleJobStatus(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    $("#sample-job-status").text(responseBody.message);
  }

  /** Update the status of SampleJob command with an error */
  function processErrorSampleJobStatus(responseBody, responseCode, responseDescription) {
    loadingWheelModal.close();
    basicKamehouseModal.openApiError(responseBody, responseCode, responseDescription);
    $("#sample-job-status").text("Error getting the status of SampleJob command");
  }
}

/**
 * Call main.
 */
$(document).ready(main);
