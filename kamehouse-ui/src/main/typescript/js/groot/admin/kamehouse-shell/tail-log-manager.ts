/**
 * Manager to tail logs in the current server.
 * 
 * @author nbrest
 */
class TailLogManager {

  #KAMEHOUSE_SHELL_EXECUTE_API = '/kame-house-groot/api/v1/admin/kamehouse-shell/execute.php';

  /**
   * Load the extension.
   */
  load() {
    kameHouse.logger.info("Initialized tailLogManager", null);
  }

  /** Tails the log based on the script parameter and the number of lines to display */
  tailLog(logFileName, numberOfLines, logLevel, executeOnDockerHost, isDaemon, callback) {
    return this.executeTailLog(this.#KAMEHOUSE_SHELL_EXECUTE_API, logFileName, numberOfLines, logLevel, executeOnDockerHost, isDaemon, callback);  
  }

  /**
   * Execute the tail on the specified url.
   */
  executeTailLog(url, logFileName, numberOfLines, logLevel, executeOnDockerHost, isDaemon, callback) {
    kameHouse.logger.trace("Tailing log: " + logFileName, null);
    if (kameHouse.core.isEmpty(logLevel)) {
      logLevel = "";
    }    
    const params = {
      script: "common/logs/cat-log.sh",
      args: "-f "  + logFileName + " -l " + logLevel,
      executeOnDockerHost: executeOnDockerHost,
      isDaemon: isDaemon
    };   
    const config = kameHouse.http.getConfig();
    config.timeout = 45;
    kameHouse.http.get(config, url, kameHouse.http.getUrlEncodedHeaders(), params,
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#updateTailLogOutput(responseBody, responseCode, responseDescription, responseHeaders, numberOfLines, callback),
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#updateTailLogOutputError(responseBody, responseCode, responseDescription, responseHeaders, callback));
  }  

  /** Update the script tail log output with the result of the script */
  #updateTailLogOutput(responseBody, responseCode, responseDescription, responseHeaders, numberOfLines, callback) {
    const tailLogStandardOutputHtml = responseBody.standardOutputHtml;
    const tailLogOutputTableBody = document.getElementById('tail-log-output-table-body');  
    const tbody = this.#getTailLogOutputTbody();
    const tailLogStandardOutputHtmlLength = tailLogStandardOutputHtml.length;
    if (tailLogStandardOutputHtmlLength < numberOfLines) {
      // Show full output
      for (let i = 0; i < tailLogStandardOutputHtmlLength; i++) {
        if (tailLogStandardOutputHtml[i].trim().length > 0) {
          kameHouse.util.dom.append(tbody, this.#getTailLogOutputTr(tailLogStandardOutputHtml[i]));
        }
      }
    } else {
      for (let i = tailLogStandardOutputHtmlLength - numberOfLines; i < tailLogStandardOutputHtmlLength; i++) {
        if (tailLogStandardOutputHtml[i].trim().length > 0) {
          kameHouse.util.dom.append(tbody, this.#getTailLogOutputTr(tailLogStandardOutputHtml[i]));
        }
      }
    }
    kameHouse.util.dom.empty(tailLogOutputTableBody);
    kameHouse.util.dom.replaceWith(tailLogOutputTableBody, tbody);
    kameHouse.util.collapsibleDiv.resize("tail-log-output-wrapper");

    if (kameHouse.core.isFunction(callback)) {
      callback(responseBody);
    }
  }

  /** Displays the error message in the tail log output */
  #updateTailLogOutputError(responseBody, responseCode, responseDescription, responseHeaders, callback) {
    if ((responseCode == "0" && responseDescription == "timeout") || responseCode == "-4") {
      kameHouse.logger.warn("Tail log request timed out", null);
    } else {
      const tbody = document.getElementById("tail-log-output-table-body");
      const errorMessage = kameHouse.logger.getCyanText(kameHouse.util.time.getTimestamp(null)) + " - [" + kameHouse.logger.getRedText("ERROR") + "] - " + kameHouse.logger.getRedText("Error response from the backend. responseCode : '" + responseCode + "'. responseBody : '" + responseBody + "'. responseDescription : '" + responseDescription + "'");
      kameHouse.util.dom.append(tbody, this.#getTailLogOutputErrorTr(errorMessage));
    }
    kameHouse.util.collapsibleDiv.resize("tail-log-output-wrapper");

    if (kameHouse.core.isFunction(callback)) {
      callback(responseBody);
    }
  }

  /**
   * Get tail log output table body.
   */
  #getTailLogOutputTbody() {
    return kameHouse.util.dom.getTbody({
      id: "tail-log-output-table-body"
    }, null);
  }

  /**
   * Get tail log output error table row.
   */
  #getTailLogOutputErrorTr(message) {
    return kameHouse.util.dom.getTrTd(message);
  }

  /**
   * Get tail log output table row.
   */
  #getTailLogOutputTr(htmlContent) {
    return kameHouse.util.dom.getTrTd(htmlContent);
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("tailLogManager", new TailLogManager());
});