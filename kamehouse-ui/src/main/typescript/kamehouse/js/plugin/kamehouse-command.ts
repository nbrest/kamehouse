/** 
 * Handles the kamehouse commands on the ui's side.
 * 
 * Handles the functionality for the kamehouse commands.
 * 
 * @author nbrest
 */
class KameHouseCommandManager {

  /**
   * Load kamehouse command manager plugin.
   */
  load() {
    kameHouse.logger.info("Started initializing kameHouseCommandManager", null);
  }

  /**
   * Render the kamehouse command kameHouseCommandResult.
   */
  renderKameHouseCommandResult(kameHouseCommandResultArray, displayCommandLine, kameHouseCommandResultDivId, collapsibleDivBtnId) {
    let kameHouseCommandResultDivSelector;
    if (!kameHouse.core.isEmpty(kameHouseCommandResultDivId)) {
      kameHouseCommandResultDivSelector = kameHouseCommandResultDivId;
    } else {
      kameHouseCommandResultDivSelector = "kamehouse-command-result";
    }
    const kameHouseCommandResultDiv = document.getElementById(kameHouseCommandResultDivSelector);
    kameHouse.util.dom.empty(kameHouseCommandResultDiv);
    kameHouseCommandResultArray.forEach((kameHouseCommandResult) => {
      if (displayCommandLine) {
        kameHouse.util.dom.append(kameHouseCommandResultDiv, this.#getCommandLine(kameHouseCommandResult.command));
      }
      const standardOutputHtml = kameHouseCommandResult.standardOutputHtml;
      if (!kameHouse.core.isEmpty(standardOutputHtml) && standardOutputHtml.length > 0) {
        standardOutputHtml.forEach((line) => {
          kameHouse.util.dom.append(kameHouseCommandResultDiv, line);
          kameHouse.util.dom.append(kameHouseCommandResultDiv, kameHouse.util.dom.getBr());
        });
      }
      const standardErrorHtml = kameHouseCommandResult.standardErrorHtml;
      if (!kameHouse.core.isEmpty(standardErrorHtml) && standardErrorHtml.length > 0) {
        kameHouse.util.dom.append(kameHouseCommandResultDiv, this.#getCommandErrorHeaderLine());
        standardErrorHtml.forEach((line) => {
          kameHouse.util.dom.append(kameHouseCommandResultDiv, line);
          kameHouse.util.dom.append(kameHouseCommandResultDiv, kameHouse.util.dom.getBr());
        });
      }
      if (kameHouseCommandResult.status == "running") {
        kameHouse.util.dom.append(kameHouseCommandResultDiv, this.#getDaemonRunningLine(kameHouseCommandResult.command));
      }
    });
    kameHouse.util.collapsibleDiv.resize(collapsibleDivBtnId);
  }
  
  /**
   * Display an error executing the kamehouse command.
   */
  renderErrorExecutingCommand(collapsibleDivBtnId) {
    const kameHouseCommandResultDiv = document.getElementById("kamehouse-command-result");
    kameHouse.util.dom.empty(kameHouseCommandResultDiv);
    kameHouse.util.dom.append(kameHouseCommandResultDiv, "Error executing kamehouse command. Check the logs on the backend...");
    kameHouse.util.collapsibleDiv.resize(collapsibleDivBtnId);
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

 kameHouse.ready(() => {kameHouse.addPlugin("kameHouseCommandManager", new KameHouseCommandManager());});