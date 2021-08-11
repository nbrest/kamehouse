var myScriptsManager;

function main() {
  bannerUtils.setRandomDragonBallBanner();
  moduleUtils.waitForModules(["logger", "httpClient", "grootHeader"], () => {
    myScriptsManager = new MyScriptsManager();
    myScriptsManager.handleSessionStatus();
    myScriptsManager.getMyScripts(myScriptsManager.populateMyScriptsTable, () => { logger.error("Error getting my.scripts csv"); });
  });
}

/**
 * Manager to load and execute my.scripts.
 */
function MyScriptsManager() {

  this.populateMyScriptsTable = populateMyScriptsTable;
  this.executeScript = executeScript;
  this.filterMyScriptsRows = filterMyScriptsRows;
  this.handleSessionStatus = handleSessionStatus;
  this.getMyScripts = getMyScripts;

  const EXEC_SCRIPT_PAGE = "/kame-house-groot/admin/my-scripts/exec-script.php";

  /** Populates all my-scripts table */
  function populateMyScriptsTable(myScriptsArray) {
    let $allMyScriptsTableBody = $('#all-my-scripts-table-body');
    let tbody = getAllMyScriptsTbody();
    for (let i = 0; i < myScriptsArray.length; i++) {
      let scriptName = myScriptsArray[i];
      domUtils.append(tbody, getAllMyScriptsTr(scriptName));
    }
    $allMyScriptsTableBody.replaceWith(tbody);
  }
  
  /** Execute the clicked script from the table */
  function clickEventOnAllMyScriptsRow(event) {
    let scriptName = event.data.scriptName;
    executeScript(scriptName, null);
  }
  
  /** Execute the specified script */
  function executeScript(scriptName, scriptArguments) {
    logger.info("Executing script : " + scriptName + " with args: " + scriptArguments);
    if (scriptArguments) {
      let urlEncodedArgs = encodeURI(scriptArguments);
      window.location.href = EXEC_SCRIPT_PAGE + "?script=" + scriptName + "&args=" + urlEncodedArgs;
    } else {
      window.location.href = EXEC_SCRIPT_PAGE + "?script=" + scriptName;
    }
  }
  
  /** Filters rows for all my-scripts table */
  function filterMyScriptsRows(filterString) {
    tableUtils.filterTableRows(filterString, 'all-my-scripts-table-body');
  }
  
  /** Handle Session Status */
  function handleSessionStatus() {
    updateServerName(global.groot.session);
  }
  
  /** Update server name */
  function updateServerName(sessionStatus) {
    if (!isNullOrUndefined(sessionStatus.server)) {
      domUtils.setHtml($("#banner-server-name"), sessionStatus.server);
    }
  }

  /** Get session status from the backend */
  function getMyScripts(successCallback, errorCallback) {
    const MY_SCRIPTS_API = '/kame-house-groot/api/v1/admin/my-scripts/my-scripts.php';
    httpClient.get(MY_SCRIPTS_API, null,
      (responseBody, responseCode, responseDescription) => successCallback(responseBody, responseCode, responseDescription),
      (responseBody, responseCode, responseDescription) => errorCallback(responseBody, responseCode, responseDescription));
  }
  
  function getAllMyScriptsTbody() {
    return domUtils.getTbody({
      id: "all-my-scripts-table-body"
    }, null);
  }
  
  function getAllMyScriptsTr(scriptName) {
    return domUtils.getTrTd(getTrBtn(scriptName));
  }
  
  function getTrBtn(scriptName) {
    return domUtils.getButton({
      attr: {
        class: "my-scripts-table-btn",
      },
      html: scriptName,
      clickData: {
        scriptName: scriptName
      },
      click: clickEventOnAllMyScriptsRow
    });
  }
}

window.onload = () => {
  main();
}