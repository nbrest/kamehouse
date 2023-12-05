/**
 * Loader for kamehouse shell scripts.
 * 
 * @author nbrest
 */
class ExecScriptLoader {

  /**
   * Load the exec script loader extension.
   */
  load() {
    kameHouse.util.banner.setRandomAllBanner();
    this.#setScriptNameAndArgsFromUrlParams();
    kameHouse.util.module.waitForModules(["kameHouseGrootSession"], () => {
      this.#handleSessionStatus();
    });
  }

  /**
   * Execute script from url parameters.
   */
  executeFromUrlParams() {
    this.#setScriptInProgressView();
    const urlParams = new URLSearchParams(window.location.search);
    const scriptName = urlParams.get('script');
    const args = urlParams.get('args');
    const executeOnDockerHost = urlParams.get('executeOnDockerHost');
    const timeout = urlParams.get('timeout');
    kameHouse.util.module.waitForModules(["kameHouseShell"], () => {
      kameHouse.extension.kameHouseShell.execute(scriptName, args, executeOnDockerHost, timeout, () => {this.#successCallback()}, () => {this.#errorCallback()});
    }); 
  }

  /** Allow the user to download the full bash script output */
  downloadBashScriptOutput() {
    const clientDate = new Date();
    const clientMonth = clientDate.getMonth() + 1;
    const timestamp = clientDate.getDate() + "-" + clientMonth + "-" + clientDate.getFullYear() + "_" + clientDate.getHours() + "-" + clientDate.getMinutes() + "-" + clientDate.getSeconds();
    const downloadLink = this.#getDownloadLink(timestamp);
    kameHouse.util.dom.appendChild(document.body, downloadLink);
    downloadLink.click();
    kameHouse.util.dom.removeChild(document.body, downloadLink);
  }  

  /**
   * Execute script success callback.
   */
  #successCallback() {
    this.#scriptExecCallback();
  }

  /**
   * Execute script error callback.
   */
  #errorCallback() {
    this.#scriptExecCallback();
  }

  /**
   * Execute script global callback.
   */
  #scriptExecCallback() {
    this.#updateScriptExecutionEndDate();
    kameHouse.util.dom.removeClass($('#kamehouse-shell-output-header'), "hidden-kh");
    kameHouse.util.dom.removeClass($('#btn-execute-script'), "hidden-kh");
    kameHouse.util.dom.removeClass($('#btn-download-kamehouse-shell-output'), "hidden-kh");  
    this.#setBannerScriptStatus("finished!");
  }

  /**
   * Set script in progress view.
   */
  #setScriptInProgressView() {
    this.#updateScriptExecutionStartDate();
    kameHouse.util.dom.addClass($('#kamehouse-shell-output-header'), "hidden-kh");
    kameHouse.util.dom.addClass($('#btn-execute-script'), "hidden-kh");
    kameHouse.util.dom.addClass($('#btn-download-kamehouse-shell-output'), "hidden-kh");
    this.#setBannerScriptStatus("in progress...");
  }

  /**
   * Set banner script status.
   */
  #setBannerScriptStatus(status) {
    kameHouse.util.dom.setHtml($("#banner-script-status"), status);
  }

  /** Update script execution end date */
  #updateScriptExecutionEndDate() {
    const clientTimeAndDate = this.#getClientTimeAndDate();
    kameHouse.util.dom.setHtml($("#st-script-exec-end-date"), clientTimeAndDate);
  }

  /** Update script execution start date */
  #updateScriptExecutionStartDate() {
    const clientTimeAndDate = this.#getClientTimeAndDate();
    kameHouse.util.dom.setHtml($("#st-script-exec-start-date"), clientTimeAndDate);
    kameHouse.util.dom.setHtml($("#st-script-exec-end-date"), "");
  }

  /** Get the current time and date on the client */
  #getClientTimeAndDate() {
    const clientDate = new Date();
    const clientMonth = clientDate.getMonth() + 1;
    return clientDate.getDate() + "/" + clientMonth + "/" + clientDate.getFullYear() + " - " + clientDate.getHours() + ":" + clientDate.getMinutes() + ":" + clientDate.getSeconds();
  }

  /** Handle Session Status */
  #handleSessionStatus() {
    this.#updateServerName(kameHouse.extension.groot.session);
  }

  /** Update server name */
  #updateServerName(sessionStatus) {
    if (!kameHouse.core.isEmpty(sessionStatus.server)) {
      kameHouse.util.dom.setHtml($("#st-server-name"), sessionStatus.server);
      kameHouse.util.dom.setHtml($("#banner-server-name"), sessionStatus.server);
    }
  }

  /** Set script name and args */
  #setScriptNameAndArgsFromUrlParams() {
    const urlParams = new URLSearchParams(window.location.search);
    const scriptName = urlParams.get('script');
    const args = urlParams.get('args');
    const executeOnDockerHost = urlParams.get('executeOnDockerHost');
    kameHouse.util.dom.setHtml($("#st-script-name"), scriptName);
    kameHouse.util.dom.setHtml($("#st-script-args"), args);
    kameHouse.util.dom.setHtml($("#st-script-exec-docker-host"), executeOnDockerHost);
  }

  /**
   * Get download link.
   */
  #getDownloadLink(timestamp) {
    return kameHouse.util.dom.getDomNode(kameHouse.util.dom.getA({
      href: 'data:text/plain;charset=utf-8,' + encodeURIComponent(kameHouse.extension.kameHouseShell.getBashScriptOutput()),
      download:  "kamehouse-shell-output-" + timestamp + ".log",
      class: "hidden-kh"
    }, null));
  }
}

$(document).ready(() => {
  kameHouse.addExtension("execScriptLoader", new ExecScriptLoader());
});