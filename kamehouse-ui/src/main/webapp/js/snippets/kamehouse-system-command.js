/** 
 * Handles the kamehouse system commands on the ui's side.
 * 
 * @author nbrest
 */
 var systemCommandManager;
 
function main() {
  logger.info("Started initializing systemCommandManager");
  systemCommandManager = new SystemCommandManager();
}

/**
 * Handles the functionality for the kamehouse system commands.
 */
function SystemCommandManager() {

  this.renderCommandOutput = renderCommandOutput;
  this.renderErrorExecutingCommand = renderErrorExecutingCommand;

  /**
   * Render the system command output.
   */
  function renderCommandOutput(systemCommandOutputArray, displayCommandLine, systemCommandOutputDivId) {
    let systemCommandOutputDivSelector;
    if (!isEmpty(systemCommandOutputDivId)) {
      systemCommandOutputDivSelector = "#" + systemCommandOutputDivId;
    } else {
      systemCommandOutputDivSelector = "#system-command-output";
    }
    const systemCommandOutputDiv = $(systemCommandOutputDivSelector);
    domUtils.empty(systemCommandOutputDiv);
    systemCommandOutputArray.forEach((systemCommandOutput) => {
      if (displayCommandLine) {
        domUtils.append(systemCommandOutputDiv, getCommandLine(systemCommandOutput.command));
      }
      if (!isEmpty(systemCommandOutput.standardOutput) && 
          systemCommandOutput.standardOutput.length > 0) {
        systemCommandOutput.standardOutput.forEach((standardOutputLine) => {
          domUtils.append(systemCommandOutputDiv, standardOutputLine);
          domUtils.append(systemCommandOutputDiv, domUtils.getBr());
        });
      }
      if (!isEmpty(systemCommandOutput.standardError) && 
          systemCommandOutput.standardError.length > 0) {
        domUtils.append(systemCommandOutputDiv, getCommandErrorHeaderLine());
        systemCommandOutput.standardError.forEach((standardErrorLine) => {
          domUtils.append(systemCommandOutputDiv, standardErrorLine);
          domUtils.append(systemCommandOutputDiv, domUtils.getBr());
        });
      }
      if (systemCommandOutput.status == "running") {
        domUtils.append(systemCommandOutputDiv, getDaemonRunningLine(systemCommandOutput.command));
      }
    });
    collapsibleDivUtils.refreshCollapsibleDiv();
  }
  
  /**
   * Display an error executing the system command.
   */
  function renderErrorExecutingCommand() {
    const systemCommandOutputDiv = $("#system-command-output");
    domUtils.empty(systemCommandOutputDiv);
    domUtils.append(systemCommandOutputDiv, "Error executing system command. Check the logs on the backend...");
    collapsibleDivUtils.refreshCollapsibleDiv();
  }
  
  function getCommandLine(command) {
    const message = domUtils.getSpan({}, domUtils.getSpan({
      class: "bold-kh"
    }, "command: " + command));
    domUtils.append(message, domUtils.getBr());
    domUtils.append(message, domUtils.getBr());
    return message;
  }

  function getDaemonRunningLine(command) {
    const message = domUtils.getSpan({}, domUtils.getSpan({
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
    const message = domUtils.getSpan({}, domUtils.getSpan({
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