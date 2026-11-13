/**
 * Manager to tail logs in the current server.
 * 
 * @author nbrest
 */
class TailLogManager {

  #KAMEHOUSE_SHELL_EXECUTE_API = '/kame-house-groot/api/v1/admin/kamehouse-shell/execute';
  #CAT_LOG_SCRIPT = 'cat/cat-log.sh';
  #TAIL_LOG_SCRIPT = 'tail/tail-log.sh';
  #GROOT_LOGS = ['groot', 'groot-access'];

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
    const isGrootLog = this.#GROOT_LOGS.includes(logFileName);
    const params = {
      script: this.getTailLogScript(logFileName),
      args: isGrootLog ? this.#getGrootLogArgs(logFileName, logLevel) : "-f "  + logFileName + " -l " + logLevel,
      executeOnDockerHost: executeOnDockerHost,
      isDaemon: isDaemon
    };   
    const config = kameHouse.http.getConfig();
    config.timeout = 45;
    kameHouse.http.get(config, url, kameHouse.http.getUrlEncodedHeaders(), params,
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#updateTailLogOutput(responseBody, responseCode, responseDescription, responseHeaders, numberOfLines, callback),
      (responseBody, responseCode, responseDescription, responseHeaders) => this.#updateTailLogOutputError(responseBody, responseCode, responseDescription, responseHeaders, callback));
  }  

  /** Get the script to execute to tail the specified log file */
  getTailLogScript(logFileName) {
    return this.#GROOT_LOGS.includes(logFileName) ? this.#TAIL_LOG_SCRIPT : this.#CAT_LOG_SCRIPT;
  }

  /** Get the tail log arguments for the groot logs */
  #getGrootLogArgs(logFileName, logLevel) {
    let args = "-f " + logFileName + " -q -n 2000";
    if (!kameHouse.core.isEmpty(logLevel) && logLevel != "ALL") {
      args += " -l " + logLevel;
    }
    return args;
  }

  /** Update the script tail log output with the result of the script */
  #updateTailLogOutput(responseBody, responseCode, responseDescription, responseHeaders, numberOfLines, callback) {
    const standardOutputHtml = responseBody.standardOutputHtml;
    if (kameHouse.core.isEmpty(standardOutputHtml)) {
      return;
    }
    const tailLogOutputTableBody = document.getElementById('tail-log-output-table-body');  
    const tbody = this.#getTailLogOutputTbody();
    const standardOutputHtmlLength = standardOutputHtml.length;
    if (standardOutputHtmlLength < numberOfLines) {
      // Show full output
      for (let i = 0; i < standardOutputHtmlLength; i++) {
        if (standardOutputHtml[i].trim().length > 0) {
          kameHouse.util.dom.append(tbody, this.#getTailLogOutputTr(standardOutputHtml[i]));
        }
      }
    } else {
      for (let i = standardOutputHtmlLength - numberOfLines; i < standardOutputHtmlLength; i++) {
        if (standardOutputHtml[i].trim().length > 0) {
          kameHouse.util.dom.append(tbody, this.#getTailLogOutputTr(standardOutputHtml[i]));
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