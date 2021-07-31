var myScriptsManager;

function main() {
  bannerUtils.setRandomDragonBallBanner();
  moduleUtils.waitForModules(["logger", "httpClient", "grootHeader"], () => {
    myScriptsManager = new MyScriptsManager();
    myScriptsManager.handleSessionStatus();
    myScriptsManager.getMyScripts(myScriptsManager.populateMyScriptsTable, () => { logger.error("Error getting my.scripts csv"); });
  });
}

function MyScriptsManager() {
  let self = this;

  const EXEC_SCRIPT_PAGE = "/kame-house-groot/admin/my-scripts/exec-script.php";

  /** Populates all my-scripts table */
  this.populateMyScriptsTable = (myScriptsArray) => {
    let $allMyScriptsTableBody = $('#all-my-scripts-table-body');
    let tbody = self.getAllMyScriptsTableBody();
    for (let i = 0; i < myScriptsArray.length; i++) {
      let scriptName = myScriptsArray[i];
      tbody.append(self.createAllMyScriptsTableRow(scriptName));
    }
    $allMyScriptsTableBody.replaceWith(tbody);
  }
  
  /** Execute the clicked script from the table */
  this.clickEventOnAllMyScriptsRow = (event) => {
    let scriptName = event.data.scriptName;
    self.executeScript(scriptName, null);
  }
  
  /** Execute the specified script */
  this.executeScript = (scriptName, scriptArguments) => {
    logger.info("Executing script : " + scriptName + " with args: " + scriptArguments);
    if (scriptArguments) {
      let urlEncodedArgs = encodeURI(scriptArguments);
      window.location.href = EXEC_SCRIPT_PAGE + "?script=" + scriptName + "&args=" + urlEncodedArgs;
    } else {
      window.location.href = EXEC_SCRIPT_PAGE + "?script=" + scriptName;
    }
  }
  
  /** Filters rows for all my-scripts table */
  this.filterMyScriptsRows = (filterString) => {
    tableUtils.filterTableRows(filterString, 'all-my-scripts-table-body');
  }
  
  /** Handle Session Status */
  this.handleSessionStatus = () => {
    self.updateServerName(global.groot.session);
  }
  
  /** Update server name */
  this.updateServerName = (sessionStatus) => {
    if (!isNullOrUndefined(sessionStatus.server)) {
      $("#banner-server-name").text(sessionStatus.server);
    }
  }

  /** Get session status from the backend */
  this.getMyScripts = (successCallback, errorCallback) => {
    const MY_SCRIPTS_API = '/kame-house-groot/api/v1/admin/my-scripts/my-scripts.php';
    httpClient.get(MY_SCRIPTS_API, null,
      (responseBody, responseCode, responseDescription) => successCallback(responseBody, responseCode, responseDescription),
      (responseBody, responseCode, responseDescription) => errorCallback(responseBody, responseCode, responseDescription));
  }
  
  /** Dynamic DOM element generation ------------------------------------------ */
  this.createAllMyScriptsTableRow = (scriptName) => {
    let tableRow = $('<tr>');
    let tableRowData = $('<td>');
    tableRowData.append(self.createAllMyScriptsTableRowButton(scriptName));
    tableRow.append(tableRowData);
    return tableRow;
  }
  
  this.createAllMyScriptsTableRowButton = (scriptName) => {
    let button = $('<button>');
    button.addClass("my-scripts-table-btn");
    button.text(scriptName);
    button.click({
      scriptName: scriptName
    }, self.clickEventOnAllMyScriptsRow);
    return button;
  }
  
  this.getAllMyScriptsTableBody = () => {
    let tBody = $('<tbody>');
    tBody.attr("id", "all-my-scripts-table-body");
    return tBody;
  }
}

window.onload = () => {
  main();
}