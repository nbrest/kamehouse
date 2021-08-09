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
  let self = this;

  const EXEC_SCRIPT_PAGE = "/kame-house-groot/admin/my-scripts/exec-script.php";

  /** Populates all my-scripts table */
  this.populateMyScriptsTable = (myScriptsArray) => {
    let $allMyScriptsTableBody = $('#all-my-scripts-table-body');
    let tbody = self.getAllMyScriptsTbody();
    for (let i = 0; i < myScriptsArray.length; i++) {
      let scriptName = myScriptsArray[i];
      tbody.append(self.getAllMyScriptsTr(scriptName));
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
  
  this.getAllMyScriptsTbody = () => {
    return domUtils.getTbody({
      id: "all-my-scripts-table-body"
    }, null);
  }
  
  this.getAllMyScriptsTr = (scriptName) => {
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
      click: self.clickEventOnAllMyScriptsRow
    });
  }
}

window.onload = () => {
  main();
}