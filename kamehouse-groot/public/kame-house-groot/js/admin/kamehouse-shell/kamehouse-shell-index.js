/**
 * UI Manager to load and execute kamehouse shell scripts.
 */
function KameHouseShellLoader() {

  this.load = load;
  this.filterKameHouseShellRows = filterKameHouseShellRows;

  const EXEC_SCRIPT_PAGE = "/kame-house-groot/admin/kamehouse-shell/exec-script";

  function load() {
    kameHouse.util.banner.setRandomAllBanner();
    kameHouse.util.module.waitForModules(["kameHouseGrootSession"], () => {
      handleSessionStatus();
    });
    getKameHouseShell(populateKameHouseShellTable, () => { 
      const message = "Error getting scripts csv";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
    });
  }

  /** Populates all kamehouse-shell table */
  function populateKameHouseShellTable(kameHouseShellArray) {
    const $allKameHouseShellTableBody = $('#all-kamehouse-shell-table-body');
    const tbody = getAllKameHouseShellTbody();
    for (let i = 0; i < kameHouseShellArray.length; i++) {
      const scriptName = kameHouseShellArray[i];
      kameHouse.util.dom.append(tbody, getAllKameHouseShellTr(scriptName));
    }
    kameHouse.util.dom.replaceWith($allKameHouseShellTableBody, tbody);
  }
  
  /** Execute the clicked script from the table */
  function clickEventOnAllKameHouseShellRow(event) {
    const scriptName = event.data.scriptName;
    executeScript(scriptName, null);
  }
  
  /** Execute the specified script */
  function executeScript(scriptName, scriptArguments) {
    kameHouse.logger.info("Executing script : " + scriptName + " with args: " + scriptArguments);
    if (!kameHouse.core.isEmpty(scriptArguments)) {
      const urlEncodedArgs = encodeURI(scriptArguments);
      kameHouse.extension.groot.windowLocation(EXEC_SCRIPT_PAGE, "?script=" + scriptName + "&args=" + urlEncodedArgs);
    } else {
      kameHouse.extension.groot.windowLocation(EXEC_SCRIPT_PAGE, "?script=" + scriptName);
    }
  }
  
  /** Filters rows for all kamehouse-shell table */
  function filterKameHouseShellRows(filterString) {
    kameHouse.util.table.filterTableRows(filterString, 'all-kamehouse-shell-table-body');
  }
  
  /** Handle Session Status */
  function handleSessionStatus() {
    updateServerName(kameHouse.extension.groot.session);
  }
  
  /** Update server name */
  function updateServerName(sessionStatus) {
    if (!kameHouse.core.isEmpty(sessionStatus.server)) {
      kameHouse.util.dom.setHtml($("#banner-server-name"), sessionStatus.server);
    }
  }

  /** Get session status from the backend */
  function getKameHouseShell(successCallback, errorCallback) {
    const KAMEHOUSE_SHELL_SCRIPTS_API = '/kame-house-groot/api/v1/admin/kamehouse-shell/scripts.php';
    const config = kameHouse.http.getConfig();
    kameHouse.http.get(config, KAMEHOUSE_SHELL_SCRIPTS_API, null, null,
      (responseBody, responseCode, responseDescription, responseHeaders) => successCallback(responseBody, responseCode, responseDescription, responseHeaders),
      (responseBody, responseCode, responseDescription, responseHeaders) => errorCallback(responseBody, responseCode, responseDescription, responseHeaders));
  }
  
  function getAllKameHouseShellTbody() {
    return kameHouse.util.dom.getTbody({
      id: "all-kamehouse-shell-table-body"
    }, null);
  }
  
  function getAllKameHouseShellTr(scriptName) {
    return kameHouse.util.dom.getTrTd(getTrBtn(scriptName));
  }
  
  function getTrBtn(scriptName) {
    return kameHouse.util.dom.getButton({
      attr: {
        class: "kamehouse-shell-table-btn",
      },
      html: scriptName,
      clickData: {
        scriptName: scriptName
      },
      click: clickEventOnAllKameHouseShellRow
    });
  }
}

$(document).ready(() => {
  kameHouse.addExtension("kameHouseShellLoader", new KameHouseShellLoader());
});