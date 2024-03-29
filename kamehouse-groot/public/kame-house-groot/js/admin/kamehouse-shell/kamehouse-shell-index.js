/**
 * UI Manager to load and execute kamehouse shell scripts.
 * 
 * @author nbrest
 */
class KameHouseShellLoader {

  static #EXEC_SCRIPT_PAGE = "/kame-house-groot/admin/kamehouse-shell/exec-script";

  /**
   * Load the extension.
   */
  load() {
    kameHouse.util.banner.setRandomAllBanner();
    kameHouse.util.module.waitForModules(["kameHouseGrootSession"], () => {
      this.#handleSessionStatus();
    });
    this.#getKameHouseShell((array) => {this.#populateKameHouseShellTable(array)}, 
    () => { 
      const message = "Error getting scripts csv";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
    });
  }

  /** Filters rows for all kamehouse-shell table */
  filterKameHouseShellRows(filterString) {
    kameHouse.util.table.filterTableRows(filterString, 'all-kamehouse-shell-table-body');
  }  

  /** Populates all kamehouse-shell table */
  #populateKameHouseShellTable(kameHouseShellArray) {
    const $allKameHouseShellTableBody = $('#all-kamehouse-shell-table-body');
    const tbody = this.#getAllKameHouseShellTbody();
    for (let i = 0; i < kameHouseShellArray.length; i++) {
      const scriptName = kameHouseShellArray[i];
      kameHouse.util.dom.append(tbody, this.#getAllKameHouseShellTr(scriptName));
    }
    kameHouse.util.dom.replaceWith($allKameHouseShellTableBody, tbody);
  }
  
  /** Execute the clicked script from the table */
  #clickEventOnAllKameHouseShellRow(event) {
    const scriptName = event.data.scriptName;
    this.#executeScript(scriptName, null);
  }
  
  /** Execute the specified script */
  #executeScript(scriptName, scriptArguments) {
    kameHouse.logger.info("Executing script : " + scriptName + " with args: " + scriptArguments);
    if (!kameHouse.core.isEmpty(scriptArguments)) {
      const urlEncodedArgs = encodeURI(scriptArguments);
      kameHouse.extension.groot.windowLocation(KameHouseShellLoader.#EXEC_SCRIPT_PAGE, "?script=" + scriptName + "&args=" + urlEncodedArgs);
    } else {
      kameHouse.extension.groot.windowLocation(KameHouseShellLoader.#EXEC_SCRIPT_PAGE, "?script=" + scriptName);
    }
  }
  
  /** Handle Session Status */
  #handleSessionStatus() {
    this.#updateServerName(kameHouse.extension.groot.session);
  }
  
  /** Update server name */
  #updateServerName(sessionStatus) {
    if (!kameHouse.core.isEmpty(sessionStatus.server)) {
      kameHouse.util.dom.setHtml($("#banner-server-name"), sessionStatus.server);
    }
  }

  /** Get kamehouse shell scripts from the backend */
  #getKameHouseShell(successCallback, errorCallback) {
    const KAMEHOUSE_SHELL_SCRIPTS_API = '/kame-house-groot/api/v1/admin/kamehouse-shell/scripts.php';
    const config = kameHouse.http.getConfig();
    config.timeout = 15;
    kameHouse.http.get(config, KAMEHOUSE_SHELL_SCRIPTS_API, null, null,
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
      clickData: {
        scriptName: scriptName
      },
      click: (event) => this.#clickEventOnAllKameHouseShellRow(event)
    });
  }
}

$(document).ready(() => {
  kameHouse.addExtension("kameHouseShellLoader", new KameHouseShellLoader());
});