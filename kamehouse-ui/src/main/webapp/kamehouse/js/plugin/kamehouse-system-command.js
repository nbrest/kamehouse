/** 
 * Handles the kamehouse system commands on the ui's side.
 * 
 * Handles the functionality for the kamehouse system commands.
 * 
 * @author nbrest
 */
class SystemCommandManager {

  /**
   * Load kamehouse system command manager plugin.
   */
  load() {
    kameHouse.logger.info("Started initializing systemCommandManager");
  }

  /**
   * Render the system command output.
   */
  renderCommandOutput(systemCommandOutputArray, displayCommandLine, systemCommandOutputDivId) {
    let systemCommandOutputDivSelector;
    if (!kameHouse.core.isEmpty(systemCommandOutputDivId)) {
      systemCommandOutputDivSelector = systemCommandOutputDivId;
    } else {
      systemCommandOutputDivSelector = "system-command-output";
    }
    const systemCommandOutputDiv = document.getElementById(systemCommandOutputDivSelector);
    kameHouse.util.dom.empty(systemCommandOutputDiv);
    systemCommandOutputArray.forEach((systemCommandOutput) => {
      if (displayCommandLine) {
        kameHouse.util.dom.append(systemCommandOutputDiv, this.#getCommandLine(systemCommandOutput.command));
      }
      if (!kameHouse.core.isEmpty(systemCommandOutput.standardOutput) && 
          systemCommandOutput.standardOutput.length > 0) {
        systemCommandOutput.standardOutput.forEach((standardOutputLine) => {
          kameHouse.util.dom.append(systemCommandOutputDiv, kameHouse.core.convertBashColorsToHtml(standardOutputLine));
          kameHouse.util.dom.append(systemCommandOutputDiv, kameHouse.util.dom.getBr());
        });
      }
      if (!kameHouse.core.isEmpty(systemCommandOutput.standardError) && 
          systemCommandOutput.standardError.length > 0) {
        kameHouse.util.dom.append(systemCommandOutputDiv, this.#getCommandErrorHeaderLine());
        systemCommandOutput.standardError.forEach((standardErrorLine) => {
          kameHouse.util.dom.append(systemCommandOutputDiv, standardErrorLine);
          kameHouse.util.dom.append(systemCommandOutputDiv, kameHouse.util.dom.getBr());
        });
      }
      if (systemCommandOutput.status == "running") {
        kameHouse.util.dom.append(systemCommandOutputDiv, this.#getDaemonRunningLine(systemCommandOutput.command));
      }
    });
    kameHouse.util.collapsibleDiv.refreshCollapsibleDiv();
  }
  
  /**
   * Display an error executing the system command.
   */
  renderErrorExecutingCommand() {
    const systemCommandOutputDiv = document.getElementById("system-command-output");
    kameHouse.util.dom.empty(systemCommandOutputDiv);
    kameHouse.util.dom.append(systemCommandOutputDiv, "Error executing system command. Check the logs on the backend...");
    kameHouse.util.collapsibleDiv.refreshCollapsibleDiv();
  }
  
  /**
   * Get command line.
   */
  #getCommandLine(command) {
    const message = kameHouse.util.dom.getSpan({}, kameHouse.util.dom.getSpan({
      class: "bold-kh"
    }, "command: " + command));
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    return message;
  }

  /**
   * Get daemon running line.
   */
  #getDaemonRunningLine(command) {
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

  /**
   * Get command error header line.
   */
  #getCommandErrorHeaderLine() {
    const message = kameHouse.util.dom.getSpan({}, kameHouse.util.dom.getSpan({
      class: "bold-kh"
    }, "errors:"));
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    kameHouse.util.dom.append(message, kameHouse.util.dom.getBr());
    return message;
  }
}

 kameHouse.ready(() => {kameHouse.addPlugin("systemCommandManager", new SystemCommandManager());});