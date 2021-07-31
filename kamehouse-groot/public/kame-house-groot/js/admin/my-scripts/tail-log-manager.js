var tailLogManager;

function loadTailLogManager() {
  moduleUtils.waitForModules(["logger", "httpClient"], () => {
    tailLogManager = new TailLogManager();
    moduleUtils.setModuleLoaded("tailLogManager");
    logger.info("Initialized tailLogManager");
  });
}

/**
 * Manager to tail logs in the current server.
 */
function TailLogManager() {
  let self = this;
  const EXEC_SCRIPT_API = '/kame-house-groot/api/v1/admin/my-scripts/exec-script.php';

  /** Tails the log based on the script parameter */
  this.tailLogFromUrlParams = () => {
    const urlParams = new URLSearchParams(window.location.search);
    const scriptName = urlParams.get('script');
    self.tailLog(scriptName, 150, null);
  }

  /** Tails the log based on the script parameter and the number of lines to display */
  this.tailLog = (scriptName, numberOfLines, callback) => {
    if (self.isValidScript(scriptName)) {
      logger.trace("Executing script : " + scriptName);
      const params = new URLSearchParams({
        script: scriptName
      });
      let getUrl = EXEC_SCRIPT_API + "?" + params;
      httpClient.get(getUrl, null,
        (responseBody, responseCode, responseDescription) => self.updateTailLogOutput(responseBody, responseCode, responseDescription, numberOfLines, callback),
        (responseBody, responseCode, responseDescription) => self.updateTailLogOutputError(responseBody, responseCode, responseDescription, callback));
    } else {
      logger.error("Invalid or no script received as url parameter");
      self.displayInvalidScript();
    }
  }

  this.isValidScript = (scriptName) => {
    if (!isNullOrUndefined(scriptName)) { 
      if (scriptName.startsWith("common/logs/cat-") && scriptName.endsWith("-log.sh")) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  /** Set script name and args */
  this.setScriptName = () => {
    const urlParams = new URLSearchParams(window.location.search);
    const scriptName = urlParams.get('script');
    $("#st-script-name").text(scriptName);
  }

  /** Update the script tail log output with the result of the script */
  this.updateTailLogOutput = (responseBody, responseCode, responseDescription, numberOfLines, callback) => {
    let tailLogOutputArray = responseBody.htmlConsoleOutput;
    let $tailLogOutputTableBody = $('#tail-log-output-table-body');  
    let tbody = self.getTailLogOutputTableBody();
    let tailLogOutputLength = tailLogOutputArray.length;
    if (tailLogOutputLength < numberOfLines) {
      // Show full output
      for (let i = 0; i < tailLogOutputLength; i++) {
        if (tailLogOutputArray[i].trim().length > 0) {
          tbody.append(self.getTailLogOutputTableRow(tailLogOutputArray[i]));
        }
      }
    } else {
      for (let i = tailLogOutputLength - numberOfLines; i < tailLogOutputLength; i++) {
        if (tailLogOutputArray[i].trim().length > 0) {
          tbody.append(self.getTailLogOutputTableRow(tailLogOutputArray[i]));
        }
      }
    }
    $tailLogOutputTableBody.empty();
    $tailLogOutputTableBody.replaceWith(tbody);

    if (isFunction(callback)) {
      callback();
    }
  }

  /** Displays the error message in the tail log output */
  this.updateTailLogOutputError = (responseBody, responseCode, responseDescription, callback) => {
    let $tailLogOutputTableBody = $('#tail-log-output-table-body');
    let tbody = self.getTailLogOutputTableBody();
    tbody.append(self.getTailLogOutputErrorTableRow("Error response from the backend"));
    tbody.append(self.getTailLogOutputErrorTableRow("responseBody : " + responseBody));
    tbody.append(self.getTailLogOutputErrorTableRow("responseCode : " + responseCode));
    tbody.append(self.getTailLogOutputErrorTableRow("responseDescription : " + responseDescription));
    $tailLogOutputTableBody.empty();
    $tailLogOutputTableBody.replaceWith(tbody);

    if (isFunction(callback)) {
      callback();
    }
  }

  /** Displays the error message in the tail log output from an invalid script */
  this.displayInvalidScript = () => {
    let $tailLogOutputTableBody = $('#tail-log-output-table-body');
    let tbody = self.getTailLogOutputTableBody();
    tbody.append(self.getTailLogOutputErrorTableRow("Invalid script sent as parameter"));
    $tailLogOutputTableBody.empty();
    $tailLogOutputTableBody.replaceWith(tbody);
  }

  /** Handle Session Status */
  this.handleSessionStatus = (sessionStatus) => {
    self.updateServerName(sessionStatus);
  }

  /** Update server name */
  this.updateServerName = (sessionStatus) => {
    if (!isNullOrUndefined(sessionStatus.server)) {
      $("#st-server-name").text(sessionStatus.server);
      $("#banner-server-name").text(sessionStatus.server);
    }
  }

  /** Dynamic DOM element generation ------------------------------------------ */
  this.getTailLogOutputTableBody = () => {
    let tBody = $('<tbody>');
    tBody.attr("id", "tail-log-output-table-body");
    return tBody;
  }

  this.getTailLogOutputErrorTableRow = (message) => {
    let tableRow = $('<tr>');
    let tableRowData = $('<td>');
    tableRowData.text(message);
    tableRow.append(tableRowData);
    return tableRow;
  }

  this.getTailLogOutputTableRow = (htmlContent) => {
    let tableRow = $('<tr>');
    let tableRowData = $('<td>');
    tableRowData.html(htmlContent);
    tableRow.append(tableRowData);
    return tableRow;
  }
}

$(document).ready(loadTailLogManager);