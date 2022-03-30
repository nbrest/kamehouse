var kameHouseShellManager;

function mainKameHouseShell() {
  bannerUtils.setRandomDragonBallBanner();
  moduleUtils.waitForModules(["grootHeader"], () => {
    kameHouseShellManager = new KameHouseShellManager();
    kameHouseShellManager.handleSessionStatus();
    kameHouseShellManager.getKameHouseShell(kameHouseShellManager.populateKameHouseShellTable, () => { logger.error("Error getting scripts csv"); });
  });
}

/**
 * Manager to load and execute scripts.
 */
function KameHouseShellManager() {

  this.populateKameHouseShellTable = populateKameHouseShellTable;
  this.executeScript = executeScript;
  this.filterKameHouseShellRows = filterKameHouseShellRows;
  this.handleSessionStatus = handleSessionStatus;
  this.getKameHouseShell = getKameHouseShell;

  const EXEC_SCRIPT_PAGE = "/kame-house-groot/admin/kamehouse-shell/exec-script.php";

  /** Populates all kamehouse-shell table */
  function populateKameHouseShellTable(kameHouseShellArray) {
    const $allKameHouseShellTableBody = $('#all-kamehouse-shell-table-body');
    const tbody = getAllKameHouseShellTbody();
    for (let i = 0; i < kameHouseShellArray.length; i++) {
      const scriptName = kameHouseShellArray[i];
      domUtils.append(tbody, getAllKameHouseShellTr(scriptName));
    }
    domUtils.replaceWith($allKameHouseShellTableBody, tbody);
  }
  
  /** Execute the clicked script from the table */
  function clickEventOnAllKameHouseShellRow(event) {
    const scriptName = event.data.scriptName;
    executeScript(scriptName, null);
  }
  
  /** Execute the specified script */
  function executeScript(scriptName, scriptArguments) {
    logger.info("Executing script : " + scriptName + " with args: " + scriptArguments);
    if (!isEmpty(scriptArguments)) {
      const urlEncodedArgs = encodeURI(scriptArguments);
      window.location.href = EXEC_SCRIPT_PAGE + "?script=" + scriptName + "&args=" + urlEncodedArgs;
    } else {
      window.location.href = EXEC_SCRIPT_PAGE + "?script=" + scriptName;
    }
  }
  
  /** Filters rows for all kamehouse-shell table */
  function filterKameHouseShellRows(filterString) {
    tableUtils.filterTableRows(filterString, 'all-kamehouse-shell-table-body');
  }
  
  /** Handle Session Status */
  function handleSessionStatus() {
    updateServerName(global.groot.session);
  }
  
  /** Update server name */
  function updateServerName(sessionStatus) {
    if (!isEmpty(sessionStatus.server)) {
      domUtils.setHtml($("#banner-server-name"), sessionStatus.server);
    }
  }

  /** Get session status from the backend */
  function getKameHouseShell(successCallback, errorCallback) {
    const MY_SCRIPTS_API = '/kame-house-groot/api/v1/admin/kamehouse-shell/kamehouse-shell.php';
    httpClient.get(MY_SCRIPTS_API, null,
      (responseBody, responseCode, responseDescription) => successCallback(responseBody, responseCode, responseDescription),
      (responseBody, responseCode, responseDescription) => errorCallback(responseBody, responseCode, responseDescription));
  }
  
  function getAllKameHouseShellTbody() {
    return domUtils.getTbody({
      id: "all-kamehouse-shell-table-body"
    }, null);
  }
  
  function getAllKameHouseShellTr(scriptName) {
    return domUtils.getTrTd(getTrBtn(scriptName));
  }
  
  function getTrBtn(scriptName) {
    return domUtils.getButton({
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

window.onload = () => {
  mainKameHouseShell();
}