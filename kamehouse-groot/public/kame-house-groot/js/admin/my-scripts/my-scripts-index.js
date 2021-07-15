var myScriptsManager;

function main() {
  bannerUtils.setRandomDragonBallBanner();
  renderRootMenu();
  moduleUtils.waitForModules(["logger", "httpClient"], () => {
    myScriptsManager = new MyScriptsManager();
    myScriptsManager.populateTailLogsTable();
    getSessionStatus(myScriptsManager.handleSessionStatus, () => { logger.error("Error getting session status"); });
    myScriptsManager.getMyScripts(myScriptsManager.populateMyScriptsTable, () => { logger.error("Error getting my.scripts csv"); });
  });
}

function MyScriptsManager() {
  let self = this;

  const EXEC_SCRIPT_PAGE = "/kame-house-groot/admin/my-scripts/exec-script.html";

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
  
  /** Execute the specified script */
  this.executeDeployment = (scriptName) => {
    let module = document.getElementById("deploy-modules-dropdown").value;
    let scriptArgs = null;
    if (module) {
      logger.info("Selected module for deployment: '" + module + "'");
      scriptArgs = "-m" + module;
    }
    self.executeScript(scriptName, scriptArgs);
  }
  
  /** Tails log for the specified script */
  this.tailLog = (scriptName) => {
    const TAIL_LOG_PAGE = "/kame-house-groot/admin/my-scripts/tail-log.html";
    logger.info("Tailing log for script : " + scriptName);
    window.location.href = TAIL_LOG_PAGE + "?script=" + scriptName;
  }
  
  /** Filters rows for all my-scripts table */
  this.filterMyScriptsRows = (filterString) => {
    tableUtils.filterTableRows(filterString, 'all-my-scripts-table-body');
  }
  
  /** Handle Session Status */
  this.handleSessionStatus = (sessionStatus) => {
    self.updateServerName(sessionStatus);
    self.populateFrequentScriptsTable(sessionStatus.isLinuxHost);
  }
  
  /** Update server name */
  this.updateServerName = (sessionStatus) => {
    if (!isNullOrUndefined(sessionStatus.server)) {
      $("#banner-server-name").text(sessionStatus.server);
    }
  }
  
  /** Populate frequent scripts table */
  this.populateFrequentScriptsTable = (isLinuxHost) => {
    if (isLinuxHost) {
      $("#frequent-scripts-table-body").load("/kame-house-groot/html-snippets/admin/my-scripts/frequent-scripts-linux.html", () => {
        self.populateTorrents();
      });
    } else {
      $("#frequent-scripts-table-body").load("/kame-house-groot/html-snippets/admin/my-scripts/frequent-scripts-windows.html");
    }
  }
  
  /** Populate tail logs table */
  this.populateTailLogsTable = () => {
    $("#tail-logs-table-body").load("/kame-house-groot/html-snippets/admin/my-scripts/tail-logs.html");
  }
  
  /** Get session status from the backend */
  this.getMyScripts = (successCallback, errorCallback) => {
    const MY_SCRIPTS_API = '/kame-house-groot/api/v1/admin/my-scripts/my-scripts.php';
    httpClient.get(MY_SCRIPTS_API, null,
      (responseBody, responseCode, responseDescription) => successCallback(responseBody, responseCode, responseDescription),
      (responseBody, responseCode, responseDescription) => errorCallback(responseBody, responseCode, responseDescription));
  }
  
  /** Populate torrents dropdown for scp */
  this.populateTorrents = () => {
    const TORRENTS_LIST_API = '/kame-house-groot/api/v1/admin/my-scripts/torrents.php';
    let scpTorrentsDropdown = $('#scp-torrents-dropdown');
    scpTorrentsDropdown.empty();
    scpTorrentsDropdown.append(self.getInitialDropdownOption('Select Torrent to SCP'));
    httpClient.get(TORRENTS_LIST_API, null,
      (responseBody, responseCode, responseDescription) => {
        let torrents = responseBody;
        $.each(torrents, function (index, torrent) {
          if (!isNullOrUndefined(torrent) && torrent.trim().length != 0 ) {
            scpTorrentsDropdown.append(self.getTorrentDropdownOption(torrent));
          }
        });
      },
      (responseBody, responseCode, responseDescription) => logger.error("Error populating torrents " + responseCode)
    );
  }
  
  /** Execute the specified script */
  this.executeScpTorrent = () => {
    logger.info("Executing script : lin/transmission/scp-torrent.sh");
    let torrent = document.getElementById("scp-torrents-dropdown").value;
    logger.info("Sending torrent '" + torrent + "'");
    let urlEncodedArgs = encodeURI("-f " + torrent);
    window.location.href = EXEC_SCRIPT_PAGE + "?script=lin/transmission/scp-torrent.sh&args=" + urlEncodedArgs;
  }
  
  /** Dynamic DOM element generation ------------------------------------------ */
  this.getInitialDropdownOption = (optionText) => {
    let option = $('<option>');
    option.prop("disabled", true);
    option.prop("selected", true);
    option.text(optionText);
    return option;
  }

  this.getTorrentDropdownOption = (torrent) => {
    let option = $('<option>');
    option.attr('value', torrent);
    option.text(torrent);
    return option;
  }
  
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
    }, () => self.clickEventOnAllMyScriptsRow());
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