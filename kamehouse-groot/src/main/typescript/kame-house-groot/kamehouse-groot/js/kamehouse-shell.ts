/**
 * KameHouse Shell javascript interface through groot's kamehouse-shell/execute.php api.
 * 
 * @author nbrest
 */
class KameHouseShell {

  #executeAPI = '/kame-house-groot/api/v1/admin/kamehouse-shell/execute.php';
  #standardOutput = "Script output not set yet.";

  /**
   * Load the kamehouse shell extension.
   */
  load() {
    kameHouse.logger.info("Initialized kameHouseShell", null);
    kameHouse.util.module.setModuleLoaded("kameHouseShell");
  }

  /** Execute the specified script*/
  execute(scriptName, args, executeOnDockerHost, timeout, successCallback, errorCallback) {
    if (!kameHouse.core.isEmpty(scriptName)) {
      const params = {
        script: scriptName,
        args: args,
        executeOnDockerHost: executeOnDockerHost
      };
      this.#setScriptExecutingScriptOutput(scriptName, args, executeOnDockerHost);
      kameHouse.logger.info("Executing script : " + scriptName + " with args : '" + args + "' executeOnDockerHost: " + executeOnDockerHost + " and timeout " + timeout, null);
      const config = kameHouse.http.getConfig();
      if (!kameHouse.core.isEmpty(timeout)) {
        config.timeout = timeout;
      }
      kameHouse.plugin.debugger.http.get(config, this.#executeAPI, kameHouse.http.getUrlEncodedHeaders(), params,
        (responseBody, responseCode, responseDescription, responseHeaders) => this.#updateScriptOutput(responseBody, responseCode, responseDescription, responseHeaders, successCallback),
        (responseBody, responseCode, responseDescription, responseHeaders) => this.#updateScriptOutputError(responseBody, responseCode, responseDescription, responseHeaders, errorCallback));
    } else {
      const message = "No script specified to execute";
      kameHouse.logger.error(message, kameHouse.logger.getRedText(message));
    }
  }

  /**
   * Get bash script output.
   */
  getBashScriptStandardOutput() {
    return this.#standardOutput;
  }

  /**
   * Override default execute api endpoint.
   */
  setExecuteApi(executeApi) {
    this.#executeAPI = executeApi;
  }

  /** Set the script ouput to show that the script is currently executing */
  #setScriptExecutingScriptOutput(scriptName, args, executeOnDockerHost) {
    kameHouse.util.dom.classListAddById('kamehouse-shell-output', "hidden-kh");
    kameHouse.util.dom.classListRemoveById('kamehouse-shell-output-executing-wrapper', "hidden-kh");
    kameHouse.util.dom.setHtmlById("kamehouse-shell-output-executing", this.#getScriptExecutingMessage(scriptName, args, executeOnDockerHost));
  }

  /** Update the script script output with the result of the script */
  #updateScriptOutput(responseBody, responseCode, responseDescription, responseHeaders, callback) {
    const standardOutputHtml = responseBody.standardOutputHtml;
    this.#standardOutput = responseBody.standardOutput;
    const scriptOutputTableBody = document.getElementById('kamehouse-shell-output-table-body');
    kameHouse.util.dom.empty(scriptOutputTableBody);
    const tbody = this.#getScriptOutputTbody();

    const standardOutputHtmlLength = standardOutputHtml.length;
    if (standardOutputHtmlLength < 400) {
      // Show full output
      for (let i = 0; i < standardOutputHtmlLength; i++) {
        if (standardOutputHtml[i].trim().length > 0) {
          kameHouse.util.dom.append(tbody, this.#getScriptOutputTr(standardOutputHtml[i]));
        }
      }
    } else {
      // Show only the first x and last y lines
      for (let i = 0; i < 50; i++) {
        if (standardOutputHtml[i].trim().length > 0) {
          kameHouse.util.dom.append(tbody, this.#getScriptOutputTr(standardOutputHtml[i]));
        }
      }
       
      kameHouse.util.dom.append(tbody, this.#getScriptOutputTr(" "));
      kameHouse.util.dom.append(tbody, this.#getScriptOutputTr(" "));
      kameHouse.util.dom.append(tbody, this.#getScriptOutputTr(" "));
      kameHouse.util.dom.append(tbody, this.#getScriptOutputTr("... Script output is too long. Showing first and last lines. Total lines " + standardOutputHtmlLength + " ..."));
      kameHouse.util.dom.append(tbody, this.#getScriptOutputTr(" "));
      kameHouse.util.dom.append(tbody, this.#getScriptOutputTr(" "));
      kameHouse.util.dom.append(tbody, this.#getScriptOutputTr(" "));

      for (let i = standardOutputHtmlLength - 350; i < standardOutputHtmlLength; i++) {
        if (standardOutputHtml[i].trim().length > 0) {
          kameHouse.util.dom.append(tbody, this.#getScriptOutputTr(standardOutputHtml[i]));
        }
      }
    }

    kameHouse.util.dom.replaceWith(scriptOutputTableBody, tbody);

    // Update the view
    kameHouse.util.dom.classListRemoveById('kamehouse-shell-output', "hidden-kh");
    kameHouse.util.dom.classListAddById('kamehouse-shell-output-executing-wrapper', "hidden-kh");

    if (kameHouse.core.isFunction(callback)) {
      callback(responseBody);
    }
  }

  /** Displays the error message in the script output */
  #updateScriptOutputError(responseBody, responseCode, responseDescription, responseHeaders, callback) {
    const scriptOutputTableBody = document.getElementById('kamehouse-shell-output-table-body');
    kameHouse.util.dom.empty(scriptOutputTableBody);
    const tbody = this.#getScriptOutputTbody();
    kameHouse.util.dom.append(tbody, this.#getScriptOutputErrorTr("Error response from the backend"));
    kameHouse.util.dom.append(tbody, this.#getScriptOutputErrorTr("responseBody : " + kameHouse.json.stringify(responseBody, null, 2)));
    kameHouse.util.dom.append(tbody, this.#getScriptOutputErrorTr("responseCode : " + responseCode));
    kameHouse.util.dom.append(tbody, this.#getScriptOutputErrorTr("responseDescription : " + responseDescription));
    kameHouse.util.dom.replaceWith(scriptOutputTableBody, tbody);

    // Update the view
    kameHouse.util.dom.classListRemoveById('kamehouse-shell-output', "hidden-kh");
    kameHouse.util.dom.classListAddById('kamehouse-shell-output-executing-wrapper', "hidden-kh");

    if (kameHouse.core.isFunction(callback)) {
      callback(responseBody);
    }
  }

  /**
   * Get script output table body.
   */
  #getScriptOutputTbody() {
    return kameHouse.util.dom.getTbody({
      id: "kamehouse-shell-output-table-body"
    }, null);
  }

  /**
   * Get script output errot table row.
   */
  #getScriptOutputErrorTr(message) {
    return kameHouse.util.dom.getTrTd(message);
  }

  /**
   * Get script output table row.
   */
  #getScriptOutputTr(htmlContent) {
    return kameHouse.util.dom.getTrTd(htmlContent);
  }

  /**
   * Get script is executing message.
   */
  #getScriptExecutingMessage(scriptName, args, executeOnDockerHost) {
    const executingMessageSpan = kameHouse.util.dom.getSpan({}, "Executing script : ");
    const scriptNameSpan = kameHouse.util.dom.getSpan({
      class: "bold-kh"
    }, scriptName);
    kameHouse.util.dom.append(executingMessageSpan, scriptNameSpan);
    if (!kameHouse.core.isEmpty(args)) {
      kameHouse.util.dom.append(executingMessageSpan, kameHouse.util.dom.getBr());
      kameHouse.util.dom.append(executingMessageSpan, kameHouse.util.dom.getBr());
      kameHouse.util.dom.append(executingMessageSpan, "with args : ");
      const argsSpan = kameHouse.util.dom.getSpan({
        class: "bold-kh"
      }, args);
      kameHouse.util.dom.append(executingMessageSpan, argsSpan);
    } else {
      kameHouse.util.dom.append(executingMessageSpan, " without args");
    }
    kameHouse.util.dom.append(executingMessageSpan, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(executingMessageSpan, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(executingMessageSpan, "executeOnDockerHost: " + executeOnDockerHost);
    return executingMessageSpan;
  }
}

kameHouse.ready(() => {
  kameHouse.addExtension("kameHouseShell", new KameHouseShell());
});