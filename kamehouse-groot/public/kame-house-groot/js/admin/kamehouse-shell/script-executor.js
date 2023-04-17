var scriptExecutor;

function loadScriptExecutor() {
  scriptExecutor = new ScriptExecutor();
  kameHouse.util.module.setModuleLoaded("scriptExecutor");
  kameHouse.logger.info("Initialized scriptExecutor");
}

/**
 * Execute a script and update the view.
 */
function ScriptExecutor() {

  this.executeFromUrlParams = executeFromUrlParams;
  this.execute = execute;
  this.setScriptNameAndArgsFromUrlParams = setScriptNameAndArgsFromUrlParams;
  this.handleSessionStatus = handleSessionStatus;
  this.downloadBashScriptOutput = downloadBashScriptOutput;

  const EXEC_SCRIPT_API = '/kame-house-groot/api/v1/admin/kamehouse-shell/exec-script.php';
  let bashScriptOutput = "Script output not set yet.";

  /** Execute the specified script in the url parameters*/
  function executeFromUrlParams() {
    const urlParams = new URLSearchParams(window.location.search);
    const scriptName = urlParams.get('script');
    const args = urlParams.get('args');
    const executeOnDockerHost = urlParams.get('executeOnDockerHost');
    execute(scriptName, args, executeOnDockerHost);
  }

  /** Execute the specified script*/
  function execute(scriptName, args, executeOnDockerHost, callback, skipUpdateView) {
    if (!kameHouse.core.isEmpty(scriptName)) {
      const params = {
        script: scriptName,
        args: args,
        executeOnDockerHost: executeOnDockerHost
      };
      kameHouse.logger.info("Executing script : " + scriptName + " with args : '" + args + "' executeOnDockerHost: " + executeOnDockerHost);
      if (!skipUpdateView) {
        updateScriptExecutionStartDate();
        kameHouse.util.dom.addClass($('#script-output-header'), "hidden-kh");
        kameHouse.util.dom.addClass($('#btn-execute-script'), "hidden-kh");
        kameHouse.util.dom.addClass($('#btn-download-script-output'), "hidden-kh");
        kameHouse.util.dom.addClass($('#script-output'), "hidden-kh");
        setScriptExecutingScriptOutput(scriptName, args, executeOnDockerHost);
        setBannerScriptStatus("in progress...");
      } else {
        kameHouse.logger.trace("Skipping view update");
      }
      kameHouse.http.get(EXEC_SCRIPT_API, kameHouse.http.getUrlEncodedHeaders(), params,
        (responseBody, responseCode, responseDescription) => updateScriptOutput(responseBody, responseCode, responseDescription, callback, skipUpdateView),
        (responseBody, responseCode, responseDescription) => updateScriptOutputError(responseBody, responseCode, responseDescription, callback, skipUpdateView));
    } else {
      kameHouse.logger.error("No script specified to execute");
    }
  }

  /** Set script name and args */
  function setScriptNameAndArgsFromUrlParams() {
    const urlParams = new URLSearchParams(window.location.search);
    const scriptName = urlParams.get('script');
    const args = urlParams.get('args');
    const executeOnDockerHost = urlParams.get('executeOnDockerHost');
    kameHouse.util.dom.setHtml($("#st-script-name"), scriptName);
    kameHouse.util.dom.setHtml($("#st-script-args"), args);
    kameHouse.util.dom.setHtml($("#st-script-exec-docker-host"), executeOnDockerHost);
  }

  /** Set the script ouput to show that the script is currently executing */
  function setScriptExecutingScriptOutput(scriptName, args, executeOnDockerHost) {
    kameHouse.util.dom.removeClass($('#script-output-executing-wrapper'), "hidden-kh");
    kameHouse.util.dom.setHtml($("#script-output-executing"), getScriptExecutingMessage(scriptName, args, executeOnDockerHost));
    kameHouse.util.collapsibleDiv.refreshCollapsibleDiv();
  }

  /** Update the script script output with the result of the script */
  function updateScriptOutput(responseBody, responseCode, responseDescription, callback, skipUpdateView) {
    if (!skipUpdateView) {
      updateScriptExecutionEndDate();
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
      kameHouse.util.dom.removeClass($('#script-output-header'), "hidden-kh");
      kameHouse.util.dom.removeClass($('#btn-execute-script'), "hidden-kh");
      kameHouse.util.dom.removeClass($('#script-output'), "hidden-kh");
      kameHouse.util.dom.addClass($('#script-output-executing-wrapper'), "hidden-kh");
      setBannerScriptStatus("finished!");
      kameHouse.util.dom.removeClass($('#btn-download-script-output'), "hidden-kh");  
    } else {
      kameHouse.logger.trace("Skipping view update");
    }
    if (kameHouse.core.isFunction(callback)) {
      callback(responseBody);
    }
  }

  /** Displays the error message in the script output */
  function updateScriptOutputError(responseBody, responseCode, responseDescription, callback, skipUpdateView) {
    if (!skipUpdateView) {
      updateScriptExecutionEndDate();
      const $scriptOutputTableBody = $('#script-output-table-body');
      kameHouse.util.dom.empty($scriptOutputTableBody);
      const tbody = getScriptOutputTbody();
      kameHouse.util.dom.append(tbody, getScriptOutputErrorTr("Error response from the backend"));
      kameHouse.util.dom.append(tbody, getScriptOutputErrorTr("responseBody : " + JSON.stringify(responseBody, null, 2)));
      kameHouse.util.dom.append(tbody, getScriptOutputErrorTr("responseCode : " + responseCode));
      kameHouse.util.dom.append(tbody, getScriptOutputErrorTr("responseDescription : " + responseDescription));
      kameHouse.util.dom.replaceWith($scriptOutputTableBody, tbody);
  
      // Update the view
      kameHouse.util.dom.removeClass($('#script-output-header'), "hidden-kh");
      kameHouse.util.dom.removeClass($('#btn-execute-script'), "hidden-kh");
      kameHouse.util.dom.removeClass($('#script-output'), "hidden-kh");
      kameHouse.util.dom.addClass($('#script-output-executing-wrapper'), "hidden-kh");
      setBannerScriptStatus("finished!");
    } else {
      kameHouse.logger.trace("Skipping view update");
    }
    if (kameHouse.core.isFunction(callback)) {
      callback(responseBody);
    }
  }

  /** Handle Session Status */
  function handleSessionStatus() {
    updateServerName(kameHouse.extension.groot.session);
  }

  /** Update server name */
  function updateServerName(sessionStatus) {
    if (!kameHouse.core.isEmpty(sessionStatus.server)) {
      kameHouse.util.dom.setHtml($("#st-server-name"), sessionStatus.server);
      kameHouse.util.dom.setHtml($("#banner-server-name"), sessionStatus.server);
    }
  }

  /** Update script execution start date */
  function updateScriptExecutionStartDate() {
    const clientTimeAndDate = getClientTimeAndDate();
    kameHouse.util.dom.setHtml($("#st-script-exec-start-date"), clientTimeAndDate);
    kameHouse.util.dom.setHtml($("#st-script-exec-end-date"), "");
  }

  /** Update script execution end date */
  function updateScriptExecutionEndDate() {
    const clientTimeAndDate = getClientTimeAndDate();
    kameHouse.util.dom.setHtml($("#st-script-exec-end-date"), clientTimeAndDate);
  }

  /** Get the current time and date on the client */
  function getClientTimeAndDate() {
    const clientDate = new Date();
    const clientMonth = clientDate.getMonth() + 1;
    return clientDate.getDate() + "/" + clientMonth + "/" + clientDate.getFullYear() + " - " + clientDate.getHours() + ":" + clientDate.getMinutes() + ":" + clientDate.getSeconds();
  }

  /** Allow the user to download the full bash script output */
  function downloadBashScriptOutput() {
    const clientDate = new Date();
    const clientMonth = clientDate.getMonth() + 1;
    const timestamp = clientDate.getDate() + "-" + clientMonth + "-" + clientDate.getFullYear() + "_" + clientDate.getHours() + "-" + clientDate.getMinutes() + "-" + clientDate.getSeconds();
    const downloadLink = getDownloadLink(timestamp);
    kameHouse.util.dom.appendChild(document.body, downloadLink);
    downloadLink.click();
    kameHouse.util.dom.removeChild(document.body, downloadLink);
  }

  function setBannerScriptStatus(status) {
    kameHouse.util.dom.setHtml($("#banner-script-status"), status);
  }

  function getDownloadLink(timestamp) {
    return kameHouse.util.dom.getDomNode(kameHouse.util.dom.getA({
      href: 'data:text/plain;charset=utf-8,' + encodeURIComponent(bashScriptOutput),
      download:  "script-output-" + timestamp + ".log",
      class: "hidden-kh"
    }, null));
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

$(document).ready(loadScriptExecutor);