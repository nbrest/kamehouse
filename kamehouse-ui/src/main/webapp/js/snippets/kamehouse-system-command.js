/** 
 * Handles the kamehouse system commands on the ui's side.
 * 
 * @author nbrest
 */
 var systemCommandManager;
 
function main() {
  moduleUtils.waitForModules(["logger"], () => {
    logger.info("Started initializing systemCommandManager");
    systemCommandManager = new SystemCommandManager();
  });
}

/**
 * Handles the functionality for the kamehouse system commands.
 */
function SystemCommandManager() {

  /**
   * Render the system command output.
   */
  this.renderCommandOutput = (systemCommandOutputArray, displayCommandLine, systemCommandOutputDivId) => {
    let systemCommandOutputDiv = "#system-command-output";
    if (systemCommandOutputDivId) {
      systemCommandOutputDiv = "#" + systemCommandOutputDivId;
    }

    domUtils.empty($(systemCommandOutputDiv));
    systemCommandOutputArray.forEach((systemCommandOutput) => {
      if (displayCommandLine) {
        $(systemCommandOutputDiv).append(getCommandLine(systemCommandOutput.command));
      }
      if (!isNullOrUndefined(systemCommandOutput.standardOutput) && 
          systemCommandOutput.standardOutput.length > 0) {
        systemCommandOutput.standardOutput.forEach((standardOutputLine) => {
          $(systemCommandOutputDiv).append(standardOutputLine);
          $(systemCommandOutputDiv).append(domUtils.getBr());
        });
      }
      if (!isNullOrUndefined(systemCommandOutput.standardError) && 
          systemCommandOutput.standardError.length > 0) {
        $(systemCommandOutputDiv).append(getCommandErrorHeaderLine());
        systemCommandOutput.standardError.forEach((standardErrorLine) => {
          $(systemCommandOutputDiv).append(standardErrorLine);
          $(systemCommandOutputDiv).append(domUtils.getBr());
        });
      }
      if (systemCommandOutput.status == "running") {
        $(systemCommandOutputDiv).append(getDaemonRunningLine(systemCommandOutput.command));
      }
    });
    collapsibleDivUtils.refreshCollapsibleDiv();
  }
  
  /**
   * Display an error executing the system command.
   */
  this.renderErrorExecutingCommand = () => {
    let systemCommandOutputDiv = "#system-command-output";
    domUtils.empty($(systemCommandOutputDiv));
    $(systemCommandOutputDiv).append("Error executing system command. Check the logs on the backend...");
    collapsibleDivUtils.refreshCollapsibleDiv();
  }
  
  function getCommandLine(command) {
    let message = domUtils.getSpan({}, domUtils.getSpan({
      class: "bold-kh"
    }, "command: " + command));
    domUtils.append(message, domUtils.getBr());
    domUtils.append(message, domUtils.getBr());
    return message;
  }

  function getDaemonRunningLine(command) {
    let message = domUtils.getSpan({}, domUtils.getSpan({
      class: "bold-kh"
    }, command));
    domUtils.append(message, " is ");
    domUtils.append(message, domUtils.getSpan({
      class: "bold-kh"
    }, "running"));
    domUtils.append(message, domUtils.getBr());
    return message;
  }

  function getCommandErrorHeaderLine() {
    let message = domUtils.getSpan({}, domUtils.getSpan({
      class: "bold-kh"
    }, "errors:"));
    domUtils.append(message, domUtils.getBr());
    domUtils.append(message, domUtils.getBr());
    return message;
  }
}

/**
 * Call main.
 */
 $(document).ready(main);