/** 
 * Handles the kamehouse system commands on the ui's side.
 * 
 * @author nbrest
 */
/**
 * Handles the functionality for the kamehouse system commands.
 */
function SystemCommandManager() {

  this.load = load;
  this.renderCommandOutput = renderCommandOutput;
  this.renderErrorExecutingCommand = renderErrorExecutingCommand;

  function load() {
    kameHouse.logger.info("Started initializing systemCommandManager");
  }

  /**
   * Render the system command output.
   */
  function renderCommandOutput(systemCommandOutputArray, displayCommandLine, systemCommandOutputDivId) {
    let systemCommandOutputDivSelector;
    if (!kameHouse.core.isEmpty(systemCommandOutputDivId)) {
      systemCommandOutputDivSelector = "#" + systemCommandOutputDivId;
    } else {
      systemCommandOutputDivSelector = "#system-command-output";
    }
    const systemCommandOutputDiv = $(systemCommandOutputDivSelector);
    kameHouse.util.dom.empty(systemCommandOutputDiv);
    systemCommandOutputArray.forEach((systemCommandOutput) => {
      if (displayCommandLine) {
        kameHouse.util.dom.append(systemCommandOutputDiv, getCommandLine(systemCommandOutput.command));
      }
      if (!kameHouse.core.isEmpty(systemCommandOutput.standardOutput) && 
          systemCommandOutput.standardOutput.length > 0) {
        systemCommandOutput.standardOutput.forEach((standardOutputLine) => {
          kameHouse.util.dom.append(systemCommandOutputDiv, standardOutputLine);
          kameHouse.util.dom.append(systemCommandOutputDiv, kameHouse.util.dom.getBr());
        });
      }
      if (!kameHouse.core.isEmpty(systemCommandOutput.standardError) && 
          systemCommandOutput.standardError.length > 0) {
        kameHouse.util.dom.append(systemCommandOutputDiv, getCommandErrorHeaderLine());
        systemCommandOutput.standardError.forEach((standardErrorLine) => {
          kameHouse.util.dom.append(systemCommandOutputDiv, standardErrorLine);
          kameHouse.util.dom.append(systemCommandOutputDiv, kameHouse.util.dom.getBr());
        });
      }
      if (systemCommandOutput.status == "running") {
        kameHouse.util.dom.append(systemCommandOutputDiv, getDaemonRunningLine(systemCommandOutput.command));
      }
    });
    kameHouse.util.collapsibleDiv.refreshCollapsibleDiv();
  }
  
  /**
   * Display an error executing the system command.
   */
  function renderErrorExecutingCommand() {
    const systemCommandOutputDiv = $("#system-command-output");
    kameHouse.util.dom.empty(systemCommandOutputDiv);
    kameHouse.util.dom.append(systemCommandOutputDiv, "Error executing system command. Check the logs on the backend...");
    kameHouse.util.collapsibleDiv.refreshCollapsibleDiv();
  }
  
  function getCommandLine(command) {
    const message = kameHouse.util.dom.getSpan({}, kameHouse.util.dom.getSpan({
      class: "bold-kh"
    }, "command: " + command));
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    return message;
  }

  function getDaemonRunningLine(command) {
    const message = kameHouse.util.dom.getSpan({}, kameHouse.util.dom.getSpan({
      class: "bold-kh"
    }, command));
    kameHouse.util.dom.append(message, " is ");
    kameHouse.util.dom.append(message, kameHouse.util.dom.getSpan({
      class: "bold-kh"
    }, "running"));
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    return message;
  }

  function getCommandErrorHeaderLine() {
    const message = kameHouse.util.dom.getSpan({}, kameHouse.util.dom.getSpan({
      class: "bold-kh"
    }, "errors:"));
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    return message;
  }
}

/**
 * Call main.
 */
 $(document).ready(() => {kameHouse.addPlugin("systemCommandManager", new SystemCommandManager());});