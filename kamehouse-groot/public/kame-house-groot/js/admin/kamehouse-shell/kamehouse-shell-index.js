/**
 * UI Manager to load and execute kamehouse shell scripts.
 * 
 * @author nbrest
 */
class KameHouseShellLoader {

  #execScriptPageUrl = "/kame-house-groot/admin/kamehouse-shell/exec-script.html";
  #getScriptsApiUrl = '/kame-house-groot/api/v1/admin/kamehouse-shell/scripts.php';

  /**
   * Set exec-script page url.
   */
  setExecScriptPageUrl(execScriptPageUrl) {
    this.#execScriptPageUrl = execScriptPageUrl;
  }

  /**
   * Set the get scripts api url.
   */
  setGetScriptsApiUrl(getScriptsApiUrl) {
    this.#getScriptsApiUrl = getScriptsApiUrl;
  }

  /**
   * Load the extension.
   */
  load() {
    this.setBanners();
    kameHouse.util.module.waitForModules(["kameHouseGrootSession"], () => {
      this.#handleSessionStatus();
    });
    this.#getKameHouseShell((array) => {this.#populateKameHouseShellTable(array)}, 
    () => { 
      const message = "Error getting scripts csv";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
    });
  }
  
  /**
   * Set random banners.
   */
  setBanners() {
    kameHouse.util.banner.setRandomAllBanner();
  }

  /** Filters rows for all kamehouse-shell table */
  filterKameHouseShellRows(filterString) {
    kameHouse.util.table.filterTableRows(filterString, 'all-kamehouse-shell-table-body');
  }  

  /** Populates all kamehouse-shell table */
  #populateKameHouseShellTable(kameHouseShellArray) {
    const allKameHouseShellTableBody = document.getElementById('all-kamehouse-shell-table-body');
    const tbody = this.#getAllKameHouseShellTbody();
    for (let i = 0; i < kameHouseShellArray.length; i++) {
      const scriptName = kameHouseShellArray[i];
      if (!scriptName.includes("Started executing") && !scriptName.includes("Finished executing")) {
        kameHouse.util.dom.append(tbody, this.#getAllKameHouseShellTr(scriptName));
      }
    }
    kameHouse.util.dom.replaceWith(allKameHouseShellTableBody, tbody);
  }
  
  /** Execute the clicked script from the table */
  #clickEventOnAllKameHouseShellRow(event, data) {
    const scriptName = data.scriptName;
    this.#executeScript(scriptName, null);
  }
  
  /** Execute the specified script */
  #executeScript(scriptName, scriptArguments) {
    kameHouse.logger.info("Executing script : " + scriptName + " with args: " + scriptArguments);
    let url = this.#execScriptPageUrl + "?script=" + scriptName;
    if (!kameHouse.core.isEmpty(scriptArguments)) {
      url = url + "&args=" + urlEncodedArgs;
    }
    kameHouse.core.windowLocation(url);
  }
  
  /** Handle Session Status */
  #handleSessionStatus() {
    this.#updateServerName(kameHouse.extension.groot.session);
  }
  
  /** Update server name */
  #updateServerName(sessionStatus) {
    if (!kameHouse.core.isEmpty(sessionStatus.server)) {
      kameHouse.util.dom.setHtml(document.getElementById("banner-server-name"), sessionStatus.server);
    }
  }

  /** Get kamehouse shell scripts from the backend */
  #getKameHouseShell(successCallback, errorCallback) {
    const config = kameHouse.http.getConfig();
    config.timeout = 15;
    kameHouse.http.get(config, this.#getScriptsApiUrl, null, null,
      (responseBody, responseCode, responseDescription, responseHeaders) => successCallback(responseBody, responseCode, responseDescription, responseHeaders),
      (responseBody, responseCode, responseDescription, responseHeaders) => errorCallback(responseBody, responseCode, responseDescription, responseHeaders));
  }
  
  /**
   * Get all kamehouse shell scripts table body.
   */
  #getAllKameHouseShellTbody() {
    return kameHouse.util.dom.getTbody({
      id: "all-kamehouse-shell-table-body"
    }, null);
  }
  
  /**
   * Get all kamehouse shell scripts table row.
   */
  #getAllKameHouseShellTr(scriptName) {
    return kameHouse.util.dom.getTrTd(this.#getTrBtn(scriptName));
  }
  
  /**
   * Get kamehouse shell script table row button.
   */
  #getTrBtn(scriptName) {
    return kameHouse.util.dom.getButton({
      attr: {
        class: "kamehouse-shell-table-btn",
      },
      html: scriptName,
      data: {
        scriptName: scriptName
      },
      click: (event, data) => this.#clickEventOnAllKameHouseShellRow(event, data)
    });
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("kameHouseShellLoader", new KameHouseShellLoader());
});