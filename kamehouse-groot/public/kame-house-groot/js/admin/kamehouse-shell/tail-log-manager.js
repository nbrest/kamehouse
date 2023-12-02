/**
 * Manager to tail logs in the current server.
 */
function TailLogManager() {

  this.load = load;
  this.tailLog = tailLog;

  const KAMEHOUSE_SHELL_EXECUTE_API = '/kame-house-groot/api/v1/admin/kamehouse-shell/execute.php';

  /**
   * Load the extension.
   */
  function load() {
    kameHouse.logger.info("Initialized tailLogManager");
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
      const config = kameHouse.http.getConfig();
      config.timeout = 20;
      kameHouse.http.get(config, KAMEHOUSE_SHELL_EXECUTE_API, kameHouse.http.getUrlEncodedHeaders(), params,
        (responseBody, responseCode, responseDescription, responseHeaders) => updateTailLogOutput(responseBody, responseCode, responseDescription, responseHeaders, numberOfLines, callback),
        (responseBody, responseCode, responseDescription, responseHeaders) => updateTailLogOutputError(responseBody, responseCode, responseDescription, responseHeaders, callback));
    } else {
      const message = "Invalid or no script received as url parameter";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
      displayInvalidScript();
    }
  }

  /**
   * Check if it's a valid script.
   */
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

  /** Update the script tail log output with the result of the script */
  function updateTailLogOutput(responseBody, responseCode, responseDescription, responseHeaders, numberOfLines, callback) {
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
  function updateTailLogOutputError(responseBody, responseCode, responseDescription, responseHeaders, callback) {
    if ((responseCode == "0" && responseDescription == "timeout") || responseCode == "-4") {
      kameHouse.logger.warn("Tail log request timed out");
    } else {
      const tbody = $("#tail-log-output-table-body");
      const errorMessage = kameHouse.logger.getCyanText(kameHouse.util.time.getTimestamp()) + " - [" + kameHouse.logger.getRedText("ERROR") + "] - " + kameHouse.logger.getRedText("Error response from the backend. responseCode : '" + responseCode + "'. responseBody : '" + responseBody + "'. responseDescription : '" + responseDescription + "'");
      kameHouse.util.dom.append(tbody, getTailLogOutputErrorTr(errorMessage));
    }

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

  /**
   * Get tail log output table body.
   */
  function getTailLogOutputTbody() {
    return kameHouse.util.dom.getTbody({
      id: "tail-log-output-table-body"
    }, null);
  }

  /**
   * Get tail log output error table row.
   */
  function getTailLogOutputErrorTr(message) {
    return kameHouse.util.dom.getTrTd(message);
  }

  /**
   * Get tail log output table row.
   */
  function getTailLogOutputTr(htmlContent) {
    return kameHouse.util.dom.getTrTd(htmlContent);
  }
}

$(document).ready(() => {
  kameHouse.addExtension("tailLogManager", new TailLogManager());
});