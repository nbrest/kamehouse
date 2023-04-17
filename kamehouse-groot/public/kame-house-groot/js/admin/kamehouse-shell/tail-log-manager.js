var tailLogManager;

function loadTailLogManager() {
  tailLogManager = new TailLogManager();
  kameHouse.util.module.setModuleLoaded("tailLogManager");
  kameHouse.logger.info("Initialized tailLogManager");
}

/**
 * Manager to tail logs in the current server.
 */
function TailLogManager() {

  this.tailLogFromUrlParams = tailLogFromUrlParams;
  this.tailLog = tailLog;
  this.setScriptName = setScriptName;
  this.handleSessionStatus = handleSessionStatus;

  const EXEC_SCRIPT_API = '/kame-house-groot/api/v1/admin/kamehouse-shell/exec-script.php';

  /** Tails the log based on the script parameter */
  function tailLogFromUrlParams() {
    const urlParams = new URLSearchParams(window.location.search);
    const scriptName = urlParams.get('script');
    const executeOnDockerHost = urlParams.get('executeOnDockerHost');
    tailLog(scriptName, 150, executeOnDockerHost, null);
  }

  /** Tails the log based on the script parameter and the number of lines to display */
  function tailLog(scriptName, numberOfLines, logLevel, executeOnDockerHost, callback) {
    if (isValidScript(scriptName)) {
      kameHouse.logger.trace("Executing script : " + scriptName);
      if (kameHouse.core.isEmpty(logLevel)) {
        logLevel = "";
      }
      const params = {
        script: scriptName,
        args: "-l " + logLevel,
        executeOnDockerHost: executeOnDockerHost
      };
      kameHouse.http.get(EXEC_SCRIPT_API, kameHouse.http.getUrlEncodedHeaders(), params,
        (responseBody, responseCode, responseDescription) => updateTailLogOutput(responseBody, responseCode, responseDescription, numberOfLines, callback),
        (responseBody, responseCode, responseDescription) => updateTailLogOutputError(responseBody, responseCode, responseDescription, callback));
    } else {
      kameHouse.logger.error("Invalid or no script received as url parameter");
      displayInvalidScript();
    }
  }

  function isValidScript(scriptName) {
    if (!kameHouse.core.isEmpty(scriptName)) { 
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
    kameHouse.util.dom.setHtml($("#st-script-name"), scriptName);
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
          kameHouse.util.dom.append(tbody, getTailLogOutputTr(tailLogOutputArray[i]));
        }
      }
    } else {
      for (let i = tailLogOutputLength - numberOfLines; i < tailLogOutputLength; i++) {
        if (tailLogOutputArray[i].trim().length > 0) {
          kameHouse.util.dom.append(tbody, getTailLogOutputTr(tailLogOutputArray[i]));
        }
      }
    }
    kameHouse.util.dom.empty($tailLogOutputTableBody);
    kameHouse.util.dom.replaceWith($tailLogOutputTableBody, tbody);

    if (kameHouse.core.isFunction(callback)) {
      callback();
    }
  }

  /** Displays the error message in the tail log output */
  function updateTailLogOutputError(responseBody, responseCode, responseDescription, callback) {
    const $tailLogOutputTableBody = $('#tail-log-output-table-body');
    const tbody = getTailLogOutputTbody();
    kameHouse.util.dom.append(tbody, getTailLogOutputErrorTr("Error response from the backend"));
    kameHouse.util.dom.append(tbody, getTailLogOutputErrorTr("responseBody : " + responseBody));
    kameHouse.util.dom.append(tbody, getTailLogOutputErrorTr("responseCode : " + responseCode));
    kameHouse.util.dom.append(tbody, getTailLogOutputErrorTr("responseDescription : " + responseDescription));
    kameHouse.util.dom.empty($tailLogOutputTableBody);
    kameHouse.util.dom.replaceWith($tailLogOutputTableBody, tbody);

    if (kameHouse.core.isFunction(callback)) {
      callback();
    }
  }

  /** Displays the error message in the tail log output from an invalid script */
  function displayInvalidScript() {
    const $tailLogOutputTableBody = $('#tail-log-output-table-body');
    const tbody = getTailLogOutputTbody();
    kameHouse.util.dom.append(tbody, getTailLogOutputErrorTr("Invalid script sent as parameter"));
    kameHouse.util.dom.empty($tailLogOutputTableBody);
    kameHouse.util.dom.replaceWith($tailLogOutputTableBody, tbody);
  }

  /** Handle Session Status */
  function handleSessionStatus(sessionStatus) {
    updateServerName(sessionStatus);
  }

  /** Update server name */
  function updateServerName(sessionStatus) {
    if (!kameHouse.core.isEmpty(sessionStatus.server)) {
      kameHouse.util.dom.setHtml($("#st-server-name"), sessionStatus.server);
      kameHouse.util.dom.setHtml($("#banner-server-name"), sessionStatus.server);
    }
  }

  function getTailLogOutputTbody() {
    return kameHouse.util.dom.getTbody({
      id: "tail-log-output-table-body"
    }, null);
  }

  function getTailLogOutputErrorTr(message) {
    return kameHouse.util.dom.getTrTd(message);
  }

  function getTailLogOutputTr(htmlContent) {
    return kameHouse.util.dom.getTrTd(htmlContent);
  }
}

$(document).ready(loadTailLogManager);