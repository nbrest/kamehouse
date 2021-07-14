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

  this.renderCommandOutput = (systemCommandOutputArray, displayCommandLine, systemCommandOutputDivId) => {
    let systemCommandOutputDiv = "#system-command-output";
    if (systemCommandOutputDivId) {
      systemCommandOutputDiv = "#" + systemCommandOutputDivId;
    }

    $(systemCommandOutputDiv).empty();
    systemCommandOutputArray.forEach((systemCommandOutput) => {
      if (displayCommandLine) {
        $(systemCommandOutputDiv).append(getCommandLine(systemCommandOutput.command));
      }
      if (!isNullOrUndefined(systemCommandOutput.standardOutput) && 
          systemCommandOutput.standardOutput.length > 0) {
        systemCommandOutput.standardOutput.forEach((standardOutputLine) => {
          $(systemCommandOutputDiv).append(standardOutputLine);
          $(systemCommandOutputDiv).append(getBr());
        });
      }
      if (!isNullOrUndefined(systemCommandOutput.standardError) && 
          systemCommandOutput.standardError.length > 0) {
        $(systemCommandOutputDiv).append(getCommandErrorHeaderLine());
        systemCommandOutput.standardError.forEach((standardErrorLine) => {
          $(systemCommandOutputDiv).append(standardErrorLine);
          $(systemCommandOutputDiv).append(getBr());
        });
      }
      if (systemCommandOutput.status == "running") {
        $(systemCommandOutputDiv).append(getDaemonRunningLine(systemCommandOutput.command));
      }
    });
    collapsibleDivUtils.refreshCollapsibleDiv();
  }
  
  this.renderErrorExecutingCommand = () => {
    let systemCommandOutputDiv = "#system-command-output";
    $(systemCommandOutputDiv).empty();
    $(systemCommandOutputDiv).append("Error executing system command. Check the logs on the backend...");
    collapsibleDivUtils.refreshCollapsibleDiv();
  }
  
  /** Dynamic DOM element generation ------------------------------------------ */
  function getCommandLine(command) {
    return "<span class='bold-kh'>command: " + command + "</span><br><br>";
  }

  function getDaemonRunningLine(command) {
    return "command: <span class='bold-kh'>" + command + "</span> is <span class='bold-kh'>running</span><br>";
  }

  function getCommandErrorHeaderLine() {
    return "<span class='bold-kh'>errors:</span><br><br>";
  }

  function getBr() {
    return "<br>";
  }
}

/**
 * Call main.
 */
 $(document).ready(main);