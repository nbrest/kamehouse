/**
 * KameHouse Shell javascript interface through groot's kamehouse-shell/execute.php api.
 */
function KameHouseShell() {

  this.load = load;
  this.execute = execute;
  this.getBashScriptOutput = getBashScriptOutput;

  const EXECUTE_API = '/kame-house-groot/api/v1/admin/kamehouse-shell/execute.php';
  let bashScriptOutput = "Script output not set yet.";

  function load() {
    kameHouse.logger.info("Initialized kameHouseShell");
    kameHouse.util.module.setModuleLoaded("kameHouseShell");
  }

  function getBashScriptOutput() {
    return bashScriptOutput;
  }

  /** Execute the specified script*/
  function execute(scriptName, args, executeOnDockerHost, successCallback, errorCallback) {
    if (!kameHouse.core.isEmpty(scriptName)) {
      const params = {
        script: scriptName,
        args: args,
        executeOnDockerHost: executeOnDockerHost
      };
      setScriptExecutingScriptOutput(scriptName, args, executeOnDockerHost);
      kameHouse.logger.info("Executing script : " + scriptName + " with args : '" + args + "' executeOnDockerHost: " + executeOnDockerHost);
      const config = kameHouse.http.getConfig();
      kameHouse.plugin.debugger.http.get(config, EXECUTE_API, kameHouse.http.getUrlEncodedHeaders(), params,
        (responseBody, responseCode, responseDescription, responseHeaders) => updateScriptOutput(responseBody, responseCode, responseDescription, responseHeaders, successCallback),
        (responseBody, responseCode, responseDescription, responseHeaders) => updateScriptOutputError(responseBody, responseCode, responseDescription, responseHeaders, errorCallback));
    } else {
      kameHouse.logger.error("No script specified to execute");
    }
  }

  /** Set the script ouput to show that the script is currently executing */
  function setScriptExecutingScriptOutput(scriptName, args, executeOnDockerHost) {
    kameHouse.util.dom.addClass($('#script-output'), "hidden-kh");
    kameHouse.util.dom.removeClass($('#script-output-executing-wrapper'), "hidden-kh");
    kameHouse.util.dom.setHtml($("#script-output-executing"), getScriptExecutingMessage(scriptName, args, executeOnDockerHost));
    kameHouse.util.collapsibleDiv.refreshCollapsibleDiv();
  }

  /** Update the script script output with the result of the script */
  function updateScriptOutput(responseBody, responseCode, responseDescription, responseHeaders, callback) {
    const scriptOutputArray = responseBody.htmlConsoleOutput;
    bashScriptOutput = responseBody.bashConsoleOutput;
    const $scriptOutputTableBody = $('#script-output-table-body');
    kameHouse.util.dom.empty($scriptOutputTableBody);
    const tbody = getScriptOutputTbody();

    const scriptOutputLength = scriptOutputArray.length;
    if (scriptOutputLength < 400) {
      // Show full output
      for (let i = 0; i < scriptOutputLength; i++) {
        if (scriptOutputArray[i].trim().length > 0) {
          kameHouse.util.dom.append(tbody, getScriptOutputTr(scriptOutputArray[i]));
        }
      }
    } else {
      // Show only the first x and last y lines
      for (let i = 0; i < 50; i++) {
        if (scriptOutputArray[i].trim().length > 0) {
          kameHouse.util.dom.append(tbody, getScriptOutputTr(scriptOutputArray[i]));
        }
      }
       
      kameHouse.util.dom.append(tbody, getScriptOutputTr(" "));
      kameHouse.util.dom.append(tbody, getScriptOutputTr(" "));
      kameHouse.util.dom.append(tbody, getScriptOutputTr(" "));
      kameHouse.util.dom.append(tbody, getScriptOutputTr("... Script output is too long. Showing first and last lines. Total lines " + scriptOutputLength + " ..."));
      kameHouse.util.dom.append(tbody, getScriptOutputTr(" "));
      kameHouse.util.dom.append(tbody, getScriptOutputTr(" "));
      kameHouse.util.dom.append(tbody, getScriptOutputTr(" "));

      for (let i = scriptOutputLength - 350; i < scriptOutputLength; i++) {
        if (scriptOutputArray[i].trim().length > 0) {
          kameHouse.util.dom.append(tbody, getScriptOutputTr(scriptOutputArray[i]));
        }
      }
    }

    kameHouse.util.dom.replaceWith($scriptOutputTableBody, tbody);

    // Update the view
    kameHouse.util.dom.removeClass($('#script-output'), "hidden-kh");
    kameHouse.util.dom.addClass($('#script-output-executing-wrapper'), "hidden-kh");

    if (kameHouse.core.isFunction(callback)) {
      callback(responseBody);
    }
  }

  /** Displays the error message in the script output */
  function updateScriptOutputError(responseBody, responseCode, responseDescription, responseHeaders, callback) {
    const $scriptOutputTableBody = $('#script-output-table-body');
    kameHouse.util.dom.empty($scriptOutputTableBody);
    const tbody = getScriptOutputTbody();
    kameHouse.util.dom.append(tbody, getScriptOutputErrorTr("Error response from the backend"));
    kameHouse.util.dom.append(tbody, getScriptOutputErrorTr("responseBody : " + JSON.stringify(responseBody, null, 2)));
    kameHouse.util.dom.append(tbody, getScriptOutputErrorTr("responseCode : " + responseCode));
    kameHouse.util.dom.append(tbody, getScriptOutputErrorTr("responseDescription : " + responseDescription));
    kameHouse.util.dom.replaceWith($scriptOutputTableBody, tbody);

    // Update the view
    kameHouse.util.dom.removeClass($('#script-output'), "hidden-kh");
    kameHouse.util.dom.addClass($('#script-output-executing-wrapper'), "hidden-kh");

    if (kameHouse.core.isFunction(callback)) {
      callback(responseBody);
    }
  }

  function getScriptOutputTbody() {
    return kameHouse.util.dom.getTbody({
      id: "script-output-table-body"
    }, null);
  }

  function getScriptOutputErrorTr(message) {
    return kameHouse.util.dom.getTrTd(message);
  }

  function getScriptOutputTr(htmlContent) {
    return kameHouse.util.dom.getTrTd(htmlContent);
  }

  function getScriptExecutingMessage(scriptName, args, executeOnDockerHost) {
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

$(document).ready(() => {
  kameHouse.addExtension("kameHouseShell", new KameHouseShell());
});