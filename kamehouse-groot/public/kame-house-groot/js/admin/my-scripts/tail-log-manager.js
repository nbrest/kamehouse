var tailLogManager;

function loadTailLogManager() {
  tailLogManager = new TailLogManager();
  moduleUtils.setModuleLoaded("tailLogManager");
  logger.info("Initialized tailLogManager");
}

/**
 * Manager to tail logs in the current server.
 */
function TailLogManager() {

  this.tailLogFromUrlParams = tailLogFromUrlParams;
  this.tailLog = tailLog;
  this.setScriptName = setScriptName;
  this.handleSessionStatus = handleSessionStatus;

  const EXEC_SCRIPT_API = '/kame-house-groot/api/v1/admin/my-scripts/exec-script.php';

  /** Tails the log based on the script parameter */
  function tailLogFromUrlParams() {
    const urlParams = new URLSearchParams(window.location.search);
    const scriptName = urlParams.get('script');
    tailLog(scriptName, 150, null);
  }

  /** Tails the log based on the script parameter and the number of lines to display */
  function tailLog(scriptName, numberOfLines, callback) {
    if (isValidScript(scriptName)) {
      logger.trace("Executing script : " + scriptName);
      const params = new URLSearchParams({
        script: scriptName
      });
      const getUrl = EXEC_SCRIPT_API + "?" + params;
      httpClient.get(getUrl, null,
        (responseBody, responseCode, responseDescription) => updateTailLogOutput(responseBody, responseCode, responseDescription, numberOfLines, callback),
        (responseBody, responseCode, responseDescription) => updateTailLogOutputError(responseBody, responseCode, responseDescription, callback));
    } else {
      logger.error("Invalid or no script received as url parameter");
      displayInvalidScript();
    }
  }

  function isValidScript(scriptName) {
    if (!isEmpty(scriptName)) { 
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
  function setScriptName() {
    const urlParams = new URLSearchParams(window.location.search);
    const scriptName = urlParams.get('script');
    domUtils.setHtml($("#st-script-name"), scriptName);
  }

  /** Update the script tail log output with the result of the script */
  function updateTailLogOutput(responseBody, responseCode, responseDescription, numberOfLines, callback) {
    const tailLogOutputArray = responseBody.htmlConsoleOutput;
    const $tailLogOutputTableBody = $('#tail-log-output-table-body');  
    const tbody = getTailLogOutputTbody();
    const tailLogOutputLength = tailLogOutputArray.length;
    if (tailLogOutputLength < numberOfLines) {
      // Show full output
      for (let i = 0; i < tailLogOutputLength; i++) {
        if (tailLogOutputArray[i].trim().length > 0) {
          domUtils.append(tbody, getTailLogOutputTr(tailLogOutputArray[i]));
        }
      }
    } else {
      for (let i = tailLogOutputLength - numberOfLines; i < tailLogOutputLength; i++) {
        if (tailLogOutputArray[i].trim().length > 0) {
          domUtils.append(tbody, getTailLogOutputTr(tailLogOutputArray[i]));
        }
      }
    }
    domUtils.empty($tailLogOutputTableBody);
    domUtils.replaceWith($tailLogOutputTableBody, tbody);

    if (isFunction(callback)) {
      callback();
    }
  }

  /** Displays the error message in the tail log output */
  function updateTailLogOutputError(responseBody, responseCode, responseDescription, callback) {
    const $tailLogOutputTableBody = $('#tail-log-output-table-body');
    const tbody = getTailLogOutputTbody();
    domUtils.append(tbody, getTailLogOutputErrorTr("Error response from the backend"));
    domUtils.append(tbody, getTailLogOutputErrorTr("responseBody : " + responseBody));
    domUtils.append(tbody, getTailLogOutputErrorTr("responseCode : " + responseCode));
    domUtils.append(tbody, getTailLogOutputErrorTr("responseDescription : " + responseDescription));
    domUtils.empty($tailLogOutputTableBody);
    domUtils.replaceWith($tailLogOutputTableBody, tbody);

    if (isFunction(callback)) {
      callback();
    }
  }

  /** Displays the error message in the tail log output from an invalid script */
  function displayInvalidScript() {
    const $tailLogOutputTableBody = $('#tail-log-output-table-body');
    const tbody = getTailLogOutputTbody();
    domUtils.append(tbody, getTailLogOutputErrorTr("Invalid script sent as parameter"));
    domUtils.empty($tailLogOutputTableBody);
    domUtils.replaceWith($tailLogOutputTableBody, tbody);
  }

  /** Handle Session Status */
  function handleSessionStatus(sessionStatus) {
    updateServerName(sessionStatus);
  }

  /** Update server name */
  function updateServerName(sessionStatus) {
    if (!isEmpty(sessionStatus.server)) {
      domUtils.setHtml($("#st-server-name"), sessionStatus.server);
      domUtils.setHtml($("#banner-server-name"), sessionStatus.server);
    }
  }

  function getTailLogOutputTbody() {
    return domUtils.getTbody({
      id: "tail-log-output-table-body"
    }, null);
  }

  function getTailLogOutputErrorTr(message) {
    return domUtils.getTrTd(message);
  }

  function getTailLogOutputTr(htmlContent) {
    return domUtils.getTrTd(htmlContent);
  }
}

$(document).ready(loadTailLogManager);