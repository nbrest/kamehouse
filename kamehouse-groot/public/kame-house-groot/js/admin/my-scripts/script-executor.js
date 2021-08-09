var scriptExecutor;

function loadScriptExecutor() {
  moduleUtils.waitForModules(["logger", "httpClient"], () => {
    scriptExecutor = new ScriptExecutor();
    moduleUtils.setModuleLoaded("scriptExecutor");
    logger.info("Initialized scriptExecutor");
  });
}

/**
 * Execute a script and update the view.
 */
function ScriptExecutor() {
  let self = this;
  const EXEC_SCRIPT_API = '/kame-house-groot/api/v1/admin/my-scripts/exec-script.php';
  this.bashScriptOutput = "Script output not set yet.";

  /** Execute the specified script in the url parameters*/
  this.executeFromUrlParams = () => {
    const urlParams = new URLSearchParams(window.location.search);
    const scriptName = urlParams.get('script');
    const args = urlParams.get('args');
    self.execute(scriptName, args);
  }

  /** Execute the specified script*/
  this.execute = (scriptName, args, callback, skipUpdateView) => {
    if (!isNullOrUndefined(scriptName)) {
      const params = new URLSearchParams({
        script: scriptName,
        args: args
      });
      let getUrl = EXEC_SCRIPT_API + "?" + params;
      logger.info("Executing script : " + scriptName + " with args : " + args);
      if (!skipUpdateView) {
        self.updateScriptExecutionStartDate();
        domUtils.addClass($('#script-output-header'), "hidden-kh");
        domUtils.addClass($('#btn-execute-script'), "hidden-kh");
        domUtils.addClass($('#btn-download-script-output'), "hidden-kh");
        domUtils.addClass($('#script-output'), "hidden-kh");
        self.setScriptExecutingScriptOutput(scriptName, args);
        self.setBannerScriptStatus("in progress...");
      } else {
        logger.trace("Skipping view update");
      }
      httpClient.get(getUrl, null,
        (responseBody, responseCode, responseDescription) => self.updateScriptOutput(responseBody, responseCode, responseDescription, callback, skipUpdateView),
        (responseBody, responseCode, responseDescription) => self.updateScriptOutputError(responseBody, responseCode, responseDescription, callback, skipUpdateView));
    } else {
      logger.error("No script specified to execute");
    }
  }

  /** Set script name and args */
  this.setScriptNameAndArgsFromUrlParams = () => {
    const urlParams = new URLSearchParams(window.location.search);
    const scriptName = urlParams.get('script');
    const args = urlParams.get('args');
    domUtils.setHtml($("#st-script-name"), scriptName);
    domUtils.setHtml($("#st-script-args"), args);
  }

  /** Set the script ouput to show that the script is currently executing */
  this.setScriptExecutingScriptOutput = (scriptName, args) => {
    domUtils.removeClass($('#script-output-executing-wrapper'), "hidden-kh");
    $("#script-output-executing").html(self.getScriptExecutingMessage(scriptName, args));
    collapsibleDivUtils.refreshCollapsibleDiv();
  }

  /** Update the script script output with the result of the script */
  this.updateScriptOutput = (responseBody, responseCode, responseDescription, callback, skipUpdateView) => {
    if (!skipUpdateView) {
      self.updateScriptExecutionEndDate();
      let scriptOutputArray = responseBody.htmlConsoleOutput;
      self.bashScriptOutput = responseBody.bashConsoleOutput;
      let $scriptOutputTableBody = $('#script-output-table-body');
      domUtils.empty($scriptOutputTableBody);
      let tbody = self.getScriptOutputTbody();
  
      let scriptOutputLength = scriptOutputArray.length;
      if (scriptOutputLength < 400) {
        // Show full output
        for (let i = 0; i < scriptOutputLength; i++) {
          if (scriptOutputArray[i].trim().length > 0) {
            domUtils.append(tbody, self.getScriptOutputTr(scriptOutputArray[i]));
          }
        }
      } else {
        // Show only the first x and last y lines
        for (let i = 0; i < 50; i++) {
          if (scriptOutputArray[i].trim().length > 0) {
            domUtils.append(tbody, self.getScriptOutputTr(scriptOutputArray[i]));
          }
        }
         
        domUtils.append(tbody, self.getScriptOutputTr(" "));
        domUtils.append(tbody, self.getScriptOutputTr(" "));
        domUtils.append(tbody, self.getScriptOutputTr(" "));
        domUtils.append(tbody, self.getScriptOutputTr("... Script output is too long. Showing first and last lines. Total lines " + scriptOutputLength + " ..."));
        domUtils.append(tbody, self.getScriptOutputTr(" "));
        domUtils.append(tbody, self.getScriptOutputTr(" "));
        domUtils.append(tbody, self.getScriptOutputTr(" "));
  
        for (let i = scriptOutputLength - 350; i < scriptOutputLength; i++) {
          if (scriptOutputArray[i].trim().length > 0) {
            domUtils.append(tbody, self.getScriptOutputTr(scriptOutputArray[i]));
          }
        }
      }
  
      $scriptOutputTableBody.replaceWith(tbody);
  
      // Update the view
      domUtils.removeClass($('#script-output-header'), "hidden-kh");
      domUtils.removeClass($('#btn-execute-script'), "hidden-kh");
      domUtils.removeClass($('#script-output'), "hidden-kh");
      domUtils.addClass($('#script-output-executing-wrapper'), "hidden-kh");
      self.setBannerScriptStatus("finished!");
      domUtils.removeClass($('#btn-download-script-output'), "hidden-kh");  
    } else {
      logger.trace("Skipping view update");
    }
    if (isFunction(callback)) {
      callback(responseBody);
    }
  }

  /** Displays the error message in the script output */
  this.updateScriptOutputError = (responseBody, responseCode, responseDescription, callback, skipUpdateView) => {
    if (!skipUpdateView) {
      self.updateScriptExecutionEndDate();
      let $scriptOutputTableBody = $('#script-output-table-body');
      domUtils.empty($scriptOutputTableBody);
      let tbody = self.getScriptOutputTbody();
      domUtils.append(tbody, self.getScriptOutputErrorTr("Error response from the backend"));
      domUtils.append(tbody, self.getScriptOutputErrorTr("responseBody : " + JSON.stringify(responseBody, null, 2)));
      domUtils.append(tbody, self.getScriptOutputErrorTr("responseCode : " + responseCode));
      domUtils.append(tbody, self.getScriptOutputErrorTr("responseDescription : " + responseDescription));
      $scriptOutputTableBody.replaceWith(tbody);
  
      // Update the view
      domUtils.removeClass($('#script-output-header'), "hidden-kh");
      domUtils.removeClass($('#btn-execute-script'), "hidden-kh");
      domUtils.removeClass($('#script-output'), "hidden-kh");
      domUtils.addClass($('#script-output-executing-wrapper'), "hidden-kh");
      self.setBannerScriptStatus("finished!");
    } else {
      logger.trace("Skipping view update");
    }
    if (isFunction(callback)) {
      callback(responseBody);
    }
  }

  /** Handle Session Status */
  this.handleSessionStatus = () => {
    self.updateServerName(global.groot.session);
  }

  /** Update server name */
  this.updateServerName = (sessionStatus) => {
    if (!isNullOrUndefined(sessionStatus.server)) {
      domUtils.setHtml($("#st-server-name"), sessionStatus.server);
      domUtils.setHtml($("#banner-server-name"), sessionStatus.server);
    }
  }

  /** Update script execution start date */
  this.updateScriptExecutionStartDate = () => {
    let clientDate = new Date();
    let clientMonth = clientDate.getMonth() + 1;
    let clientTimeAndDate = clientDate.getDate() + "/" + clientMonth + "/" + clientDate.getFullYear() + " - " + clientDate.getHours() + ":" + clientDate.getMinutes() + ":" + clientDate.getSeconds();
    domUtils.setHtml($("#st-script-exec-start-date"), clientTimeAndDate);
    domUtils.setHtml($("#st-script-exec-end-date"), "");
  }

  /** Update script execution end date */
  this.updateScriptExecutionEndDate = () => {
    let clientDate = new Date();
    let clientMonth = clientDate.getMonth() + 1;
    let clientTimeAndDate = clientDate.getDate() + "/" + clientMonth + "/" + clientDate.getFullYear() + " - " + clientDate.getHours() + ":" + clientDate.getMinutes() + ":" + clientDate.getSeconds();
    domUtils.setHtml($("#st-script-exec-end-date"), clientTimeAndDate);
  }

  /** Allow the user to download the full bash script output */
  this.downloadBashScriptOutput = () => {
    let clientDate = new Date();
    let clientMonth = clientDate.getMonth() + 1;
    let timestamp = clientDate.getDate() + "-" + clientMonth + "-" + clientDate.getFullYear() + "_" + clientDate.getHours() + "-" + clientDate.getMinutes() + "-" + clientDate.getSeconds();
    let downloadLink = self.getDownloadLink(timestamp);
    document.body.appendChild(downloadLink);
    downloadLink.click();
    document.body.removeChild(downloadLink);
  }

  this.setBannerScriptStatus = (status) => {
    domUtils.setHtml($("#banner-script-status"), status);
  }

  this.getDownloadLink = (timestamp) => {
    return domUtils.getDomNode(domUtils.getA({
      href: 'data:text/plain;charset=utf-8,' + encodeURIComponent(self.bashScriptOutput),
      download:  "script-output-" + timestamp + ".log",
      class: "hidden-kh"
    }, null));
  }

  this.getScriptOutputTbody = () => {
    return domUtils.getTbody({
      id: "script-output-table-body"
    }, null);
  }

  this.getScriptOutputErrorTr = (message) => {
    return domUtils.getTrTd(message);
  }

  this.getScriptOutputTr = (htmlContent) => {
    return domUtils.getTrTd(htmlContent);
  }

  this.getScriptExecutingMessage = (scriptName, args) => {
    let executingMessageSpan = domUtils.getSpan({}, "Executing script : ");
    let scriptNameSpan = domUtils.getSpan({
      class: "bold-kh"
    }, scriptName);
    domUtils.append(executingMessageSpan, scriptNameSpan);
    if (args) {
      domUtils.append(executingMessageSpan, domUtils.getBr());
      domUtils.append(executingMessageSpan, domUtils.getBr());
      domUtils.append(executingMessageSpan, "with args : ");
      let argsSpan = domUtils.getSpan({
        class: "bold-kh"
      }, args);
      domUtils.append(executingMessageSpan, argsSpan);
    } else {
      domUtils.append(executingMessageSpan, " without args");
    }
    return executingMessageSpan;
  }
}

$(document).ready(loadScriptExecutor);