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

  #TEST_MODULE_API_URL = "/kame-house-testmodule/api/v1/test-module";
  #SAMPLE_JOB_URL = '/test-scheduler/sample-job';

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Loading TestScheduler", null);
    kameHouse.util.banner.setRandomAllBanner(null);
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
    const delay = (document.getElementById("sample-job-delay-dropdown") as HTMLSelectElement).value;
    kameHouse.logger.trace("Sample job delay: " + delay, null);
    const requestParam =  {
      "delay" : delay
    };
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.post(config, this.#TEST_MODULE_API_URL + this.#SAMPLE_JOB_URL, kameHouse.http.getUrlEncodedHeaders(), requestParam, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessSampleJob(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorSampleJob(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /** Cancel a SampleJob command */
  cancelSampleJob() {
    kameHouse.plugin.modal.loadingWheelModal.open();
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.delete(config, this.#TEST_MODULE_API_URL + this.#SAMPLE_JOB_URL, null, null, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processSuccessSampleJob(responseBody, responseCode, responseDescription, responseHeaders)}, 
      (responseBody, responseCode, responseDescription, responseHeaders) => {this.#processErrorSampleJob(responseBody, responseCode, responseDescription, responseHeaders)});
  }

  /** Get the SampleJob command status */
  getSampleJobStatus(openModal) {
    if (openModal) {
      kameHouse.plugin.modal.loadingWheelModal.open();
    }
    const config = kameHouse.http.getConfig();
    kameHouse.plugin.debugger.http.get(config, this.#TEST_MODULE_API_URL + this.#SAMPLE_JOB_URL, null, null, this.#processSuccessSampleJobStatus, this.#processErrorSampleJobStatus);
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
    kameHouse.util.dom.setHtmlById("sample-job-status", responseBody.message);
  }

  /** Update the status of SampleJob command with an error */
  #processErrorSampleJobStatus(responseBody, responseCode, responseDescription, responseHeaders) {
    kameHouse.plugin.modal.loadingWheelModal.close();
    kameHouse.plugin.modal.basicModal.openApiError(responseBody, responseCode, responseDescription, responseHeaders);
    kameHouse.util.dom.setHtmlById("sample-job-status", "Error getting the status of SampleJob command");
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("testScheduler", new TestScheduler());
});
