var scriptExecutor;

function loadScriptExecutor() {
  scriptExecutor = new ScriptExecutor();
  moduleUtils.setModuleLoaded("scriptExecutor");
  logger.info("Initialized scriptExecutor");
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
    if (!isEmpty(scriptName)) {
      const params = new URLSearchParams({
        script: scriptName,
        args: args,
        executeOnDockerHost: executeOnDockerHost
      });
      const getUrl = EXEC_SCRIPT_API + "?" + params;
      logger.info("Executing script : " + scriptName + " with args : '" + args + "' executeOnDockerHost: " + executeOnDockerHost);
      if (!skipUpdateView) {
        updateScriptExecutionStartDate();
        domUtils.addClass($('#script-output-header'), "hidden-kh");
        domUtils.addClass($('#btn-execute-script'), "hidden-kh");
        domUtils.addClass($('#btn-download-script-output'), "hidden-kh");
        domUtils.addClass($('#script-output'), "hidden-kh");
        setScriptExecutingScriptOutput(scriptName, args, executeOnDockerHost);
        setBannerScriptStatus("in progress...");
      } else {
        logger.trace("Skipping view update");
      }
      httpClient.get(getUrl, null,
        (responseBody, responseCode, responseDescription) => updateScriptOutput(responseBody, responseCode, responseDescription, callback, skipUpdateView),
        (responseBody, responseCode, responseDescription) => updateScriptOutputError(responseBody, responseCode, responseDescription, callback, skipUpdateView));
    } else {
      logger.error("No script specified to execute");
    }
  }

  /** Set script name and args */
  function setScriptNameAndArgsFromUrlParams() {
    const urlParams = new URLSearchParams(window.location.search);
    const scriptName = urlParams.get('script');
    const args = urlParams.get('args');
    const executeOnDockerHost = urlParams.get('executeOnDockerHost');
    domUtils.setHtml($("#st-script-name"), scriptName);
    domUtils.setHtml($("#st-script-args"), args);
    domUtils.setHtml($("#st-script-exec-docker-host"), executeOnDockerHost);
  }

  /** Set the script ouput to show that the script is currently executing */
  function setScriptExecutingScriptOutput(scriptName, args, executeOnDockerHost) {
    domUtils.removeClass($('#script-output-executing-wrapper'), "hidden-kh");
    domUtils.setHtml($("#script-output-executing"), getScriptExecutingMessage(scriptName, args, executeOnDockerHost));
    collapsibleDivUtils.refreshCollapsibleDiv();
  }

  /** Update the script script output with the result of the script */
  function updateScriptOutput(responseBody, responseCode, responseDescription, callback, skipUpdateView) {
    if (!skipUpdateView) {
      updateScriptExecutionEndDate();
      const scriptOutputArray = responseBody.htmlConsoleOutput;
      bashScriptOutput = responseBody.bashConsoleOutput;
      const $scriptOutputTableBody = $('#script-output-table-body');
      domUtils.empty($scriptOutputTableBody);
      const tbody = getScriptOutputTbody();
  
      const scriptOutputLength = scriptOutputArray.length;
      if (scriptOutputLength < 400) {
        // Show full output
        for (let i = 0; i < scriptOutputLength; i++) {
          if (scriptOutputArray[i].trim().length > 0) {
            domUtils.append(tbody, getScriptOutputTr(scriptOutputArray[i]));
          }
        }
      } else {
        // Show only the first x and last y lines
        for (let i = 0; i < 50; i++) {
          if (scriptOutputArray[i].trim().length > 0) {
            domUtils.append(tbody, getScriptOutputTr(scriptOutputArray[i]));
          }
        }
         
        domUtils.append(tbody, getScriptOutputTr(" "));
        domUtils.append(tbody, getScriptOutputTr(" "));
        domUtils.append(tbody, getScriptOutputTr(" "));
        domUtils.append(tbody, getScriptOutputTr("... Script output is too long. Showing first and last lines. Total lines " + scriptOutputLength + " ..."));
        domUtils.append(tbody, getScriptOutputTr(" "));
        domUtils.append(tbody, getScriptOutputTr(" "));
        domUtils.append(tbody, getScriptOutputTr(" "));
  
        for (let i = scriptOutputLength - 350; i < scriptOutputLength; i++) {
          if (scriptOutputArray[i].trim().length > 0) {
            domUtils.append(tbody, getScriptOutputTr(scriptOutputArray[i]));
          }
        }
      }
  
      domUtils.replaceWith($scriptOutputTableBody, tbody);
  
      // Update the view
      domUtils.removeClass($('#script-output-header'), "hidden-kh");
      domUtils.removeClass($('#btn-execute-script'), "hidden-kh");
      domUtils.removeClass($('#script-output'), "hidden-kh");
      domUtils.addClass($('#script-output-executing-wrapper'), "hidden-kh");
      setBannerScriptStatus("finished!");
      domUtils.removeClass($('#btn-download-script-output'), "hidden-kh");  
    } else {
      logger.trace("Skipping view update");
    }
    if (isFunction(callback)) {
      callback(responseBody);
    }
  }

  /** Displays the error message in the script output */
  function updateScriptOutputError(responseBody, responseCode, responseDescription, callback, skipUpdateView) {
    if (!skipUpdateView) {
      updateScriptExecutionEndDate();
      const $scriptOutputTableBody = $('#script-output-table-body');
      domUtils.empty($scriptOutputTableBody);
      const tbody = getScriptOutputTbody();
      domUtils.append(tbody, getScriptOutputErrorTr("Error response from the backend"));
      domUtils.append(tbody, getScriptOutputErrorTr("responseBody : " + JSON.stringify(responseBody, null, 2)));
      domUtils.append(tbody, getScriptOutputErrorTr("responseCode : " + responseCode));
      domUtils.append(tbody, getScriptOutputErrorTr("responseDescription : " + responseDescription));
      domUtils.replaceWith($scriptOutputTableBody, tbody);
  
      // Update the view
      domUtils.removeClass($('#script-output-header'), "hidden-kh");
      domUtils.removeClass($('#btn-execute-script'), "hidden-kh");
      domUtils.removeClass($('#script-output'), "hidden-kh");
      domUtils.addClass($('#script-output-executing-wrapper'), "hidden-kh");
      setBannerScriptStatus("finished!");
    } else {
      logger.trace("Skipping view update");
    }
    if (isFunction(callback)) {
      callback(responseBody);
    }
  }

  /** Handle Session Status */
  function handleSessionStatus() {
    updateServerName(global.groot.session);
  }

  /** Update server name */
  function updateServerName(sessionStatus) {
    if (!isEmpty(sessionStatus.server)) {
      domUtils.setHtml($("#st-server-name"), sessionStatus.server);
      domUtils.setHtml($("#banner-server-name"), sessionStatus.server);
    }
  }

  /** Update script execution start date */
  function updateScriptExecutionStartDate() {
    const clientTimeAndDate = getClientTimeAndDate();
    domUtils.setHtml($("#st-script-exec-start-date"), clientTimeAndDate);
    domUtils.setHtml($("#st-script-exec-end-date"), "");
  }

  /** Update script execution end date */
  function updateScriptExecutionEndDate() {
    const clientTimeAndDate = getClientTimeAndDate();
    domUtils.setHtml($("#st-script-exec-end-date"), clientTimeAndDate);
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
    domUtils.appendChild(document.body, downloadLink);
    downloadLink.click();
    domUtils.removeChild(document.body, downloadLink);
  }

  function setBannerScriptStatus(status) {
    domUtils.setHtml($("#banner-script-status"), status);
  }

  function getDownloadLink(timestamp) {
    return domUtils.getDomNode(domUtils.getA({
      href: 'data:text/plain;charset=utf-8,' + encodeURIComponent(bashScriptOutput),
      download:  "script-output-" + timestamp + ".log",
      class: "hidden-kh"
    }, null));
  }

  function getScriptOutputTbody() {
    return domUtils.getTbody({
      id: "script-output-table-body"
    }, null);
  }

  function getScriptOutputErrorTr(message) {
    return domUtils.getTrTd(message);
  }

  function getScriptOutputTr(htmlContent) {
    return domUtils.getTrTd(htmlContent);
  }

  function getScriptExecutingMessage(scriptName, args, executeOnDockerHost) {
    const executingMessageSpan = domUtils.getSpan({}, "Executing script : ");
    const scriptNameSpan = domUtils.getSpan({
      class: "bold-kh"
    }, scriptName);
    domUtils.append(executingMessageSpan, scriptNameSpan);
    if (!isEmpty(args)) {
      domUtils.append(executingMessageSpan, domUtils.getBr());
      domUtils.append(executingMessageSpan, domUtils.getBr());
      domUtils.append(executingMessageSpan, "with args : ");
      const argsSpan = domUtils.getSpan({
        class: "bold-kh"
      }, args);
      domUtils.append(executingMessageSpan, argsSpan);
    } else {
      domUtils.append(executingMessageSpan, " without args");
    }
    domUtils.append(executingMessageSpan, domUtils.getBr());
    domUtils.append(executingMessageSpan, domUtils.getBr());
    domUtils.append(executingMessageSpan, "executeOnDockerHost: " + executeOnDockerHost);
    return executingMessageSpan;
  }
}

$(document).ready(loadScriptExecutor);