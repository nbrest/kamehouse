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
        $('#script-output-header').addClass("hidden-kh");
        $('#btn-execute-script').addClass("hidden-kh");
        $('#btn-download-script-output').addClass("hidden-kh");
        $('#script-output').addClass("hidden-kh");
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
    $("#st-script-name").text(scriptName);
    $("#st-script-args").text(args);
  }

  /** Set the script ouput to show that the script is currently executing */
  this.setScriptExecutingScriptOutput = (scriptName, args) => {
    $('#script-output-executing-wrapper').removeClass("hidden-kh");
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
            tbody.append(self.getScriptOutputTr(scriptOutputArray[i]));
          }
        }
      } else {
        // Show only the first x and last y lines
        for (let i = 0; i < 50; i++) {
          if (scriptOutputArray[i].trim().length > 0) {
            tbody.append(self.getScriptOutputTr(scriptOutputArray[i]));
          }
        }
         
        tbody.append(self.getScriptOutputTr(" "));
        tbody.append(self.getScriptOutputTr(" "));
        tbody.append(self.getScriptOutputTr(" "));
        tbody.append(self.getScriptOutputTr("... Script output is too long. Showing first and last lines. Total lines " + scriptOutputLength + " ..."));
        tbody.append(self.getScriptOutputTr(" "));
        tbody.append(self.getScriptOutputTr(" "));
        tbody.append(self.getScriptOutputTr(" "));
  
        for (let i = scriptOutputLength - 350; i < scriptOutputLength; i++) {
          if (scriptOutputArray[i].trim().length > 0) {
            tbody.append(self.getScriptOutputTr(scriptOutputArray[i]));
          }
        }
      }
  
      $scriptOutputTableBody.replaceWith(tbody);
  
      // Update the view
      $('#script-output-header').removeClass("hidden-kh");
      $('#btn-execute-script').removeClass("hidden-kh");
      $('#script-output').removeClass("hidden-kh");
      $('#script-output-executing-wrapper').addClass("hidden-kh");
      self.setBannerScriptStatus("finished!");
      $('#btn-download-script-output').removeClass("hidden-kh");  
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
      tbody.append(self.getScriptOutputErrorTr("Error response from the backend"));
      tbody.append(self.getScriptOutputErrorTr("responseBody : " + JSON.stringify(responseBody, null, 2)));
      tbody.append(self.getScriptOutputErrorTr("responseCode : " + responseCode));
      tbody.append(self.getScriptOutputErrorTr("responseDescription : " + responseDescription));
      $scriptOutputTableBody.replaceWith(tbody);
  
      // Update the view
      $('#script-output-header').removeClass("hidden-kh");
      $('#btn-execute-script').removeClass("hidden-kh");
      $('#script-output').removeClass("hidden-kh");
      $('#script-output-executing-wrapper').addClass("hidden-kh");
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
      $("#st-server-name").text(sessionStatus.server);
      $("#banner-server-name").text(sessionStatus.server);
    }
  }

  /** Update script execution start date */
  this.updateScriptExecutionStartDate = () => {
    let clientDate = new Date();
    let clientMonth = clientDate.getMonth() + 1;
    let clientTimeAndDate = clientDate.getDate() + "/" + clientMonth + "/" + clientDate.getFullYear() + " - " + clientDate.getHours() + ":" + clientDate.getMinutes() + ":" + clientDate.getSeconds();
    $("#st-script-exec-start-date").text(clientTimeAndDate);
    $("#st-script-exec-end-date").text("");
  }

  /** Update script execution end date */
  this.updateScriptExecutionEndDate = () => {
    let clientDate = new Date();
    let clientMonth = clientDate.getMonth() + 1;
    let clientTimeAndDate = clientDate.getDate() + "/" + clientMonth + "/" + clientDate.getFullYear() + " - " + clientDate.getHours() + ":" + clientDate.getMinutes() + ":" + clientDate.getSeconds();
    $("#st-script-exec-end-date").text(clientTimeAndDate);
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
    $("#banner-script-status").text(status);
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