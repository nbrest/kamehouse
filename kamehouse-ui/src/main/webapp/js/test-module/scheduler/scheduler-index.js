/**
 * Test module scheduler functions.
 * 
 * Dependencies: logger, kameHouse.plugin.debugger.http.
 * 
 * Manager to handle the scheduling of the sample job.
 * 
 * @author nbrest
 */
class TestScheduler {

  static #TEST_MODULE_API_URL = "/kame-house-testmodule/api/v1/test-module";
  static #SAMPLE_JOB_URL = '/test-scheduler/sample-job';

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Loading TestScheduler");
    kameHouse.util.banner.setRandomAllBanner();
    kameHouse.util.module.waitForModules(["kameHouseModal", "kameHouseDebugger"], () => {
      this.getSampleJobStatus(false);
    });
  }

  /**
   * --------------------------------------------------------------------------
   * Sample Job functions
   */
  /** Set a sample job command */
  setSampleJob() {
    const delay = document.getElementById("sample-job-delay-dropdown").value;
    kameHouse.logger.trace("Sample job delay: " + delay);
    const requestParam =  {
      "delay" : delay
    };
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, TestScheduler.#TEST_MODULE_API_URL + TestScheduler.#SAMPLE_JOB_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam, this.#processSuccessSampleJob, this.#processErrorSampleJob);
  }

  /** Cancel a SampleJob command */
  cancelSampleJob() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, TestScheduler.#TEST_MODULE_API_URL + TestScheduler.#SAMPLE_JOB_URL, null, null, this.#processSuccessSampleJob, this.#processErrorSampleJob);
  }

  /** Get the SampleJob command status */
  getSampleJobStatus(openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, TestScheduler.#TEST_MODULE_API_URL + TestScheduler.#SAMPLE_JOB_URL, null, null, this.#processSuccessSampleJobStatus, this.#processErrorSampleJobStatus);
  }

  /** Process the success response of a SampleJob command (set/cancel) */
  #processSuccessSampleJob(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    // Can't use 'this' here because it's out of scope in this callback from an http request.
    // Another way to do it would be to use kameHouse.extension.testScheduler to call getSampleJobStatus
    new TestScheduler().getSampleJobStatus(false);
  }

  /** Process the error response of a SampleJob command (set/cancel) */
  #processErrorSampleJob(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    // Can't use 'this' here because it's out of scope in this callback from an http request.
    new TestScheduler().getSampleJobStatus(false);
  }

  /** Update the status of SampleJob command */
  #processSuccessSampleJobStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.util.dom.setHtml($("#sample-job-status"), responseBody.message);
  }

  /** Update the status of SampleJob command with an error */
  #processErrorSampleJobStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    kameHouse.util.dom.setHtml($("#sample-job-status"), "Error getting the status of SampleJob command");
  }
}

$(document).ready(() => {
  kameHouse.addExtension("testScheduler", new TestScheduler());
});
