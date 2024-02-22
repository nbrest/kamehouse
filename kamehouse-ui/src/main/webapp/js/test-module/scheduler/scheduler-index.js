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
    kameHouse.plugin.debugger.http.post(config, TestScheduler.#TEST_MODULE_API_URL + TestScheduler.#SAMPLE_JOB_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessSampleJob(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorSampleJob(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /** Cancel a SampleJob command */
  cancelSampleJob() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, TestScheduler.#TEST_MODULE_API_URL + TestScheduler.#SAMPLE_JOB_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessSampleJob(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorSampleJob(responseBody, responseCode, responseDescription, responseHeaders)});
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
    this.getSampleJobStatus(false);
  }

  /** Process the error response of a SampleJob command (set/cancel) */
  #processErrorSampleJob(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    this.getSampleJobStatus(false);
  }

  /** Update the status of SampleJob command */
  #processSuccessSampleJobStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.util.dom.setHtml(document.getElementById("sample-job-status"), responseBody.message);
  }

  /** Update the status of SampleJob command with an error */
  #processErrorSampleJobStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    kameHouse.util.dom.setHtml(document.getElementById("sample-job-status"), "Error getting the status of SampleJob command");
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("testScheduler", new TestScheduler());
});
