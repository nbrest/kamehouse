/**
 * Log object to perform logging to the console on the frontend side.
 * 
 * Dependencies: timeUtils.
 * 
 * @author nbrest
 */
function Logger() {
  let self = this;
  /**
   * Log levels:
   * 0: ERROR
   * 1: WARN
   * 2: INFO
   * 3: DEBUG
   * 4: TRACE
   */
  //Defaults log level to INFO (2)
  this.logLevel = 2;

  /**
   * Set the log level for the console in numeric value, based on the mapping shown above.
   */
  this.setLogLevel = (levelNumber) => {
    self.logLevel = levelNumber;
  }

  /** Log a specified message with the specified logging level. */
  this.log = (logLevel, message) => {
    if (isNullOrUndefined(logLevel)) {
      console.error("Invalid use of log(logLevel, message) function. LogLevel is missing.");
      return;
    }
    if (!message) {
      console.error("Invalid use of log(logLevel, message) function. Message is empty");
      return;
    }
    let logLevelUpperCase = logLevel.toUpperCase();
    let logEntry = "";
    logEntry = timeUtils.getTimestamp() + " - [" + logLevelUpperCase + "] - " + message;
    if (logLevelUpperCase == "ERROR") {
      console.error(logEntry);
      self.logToDebugMode(logEntry);
    }
    if (logLevelUpperCase == "WARN" && self.logLevel >= 1) {
      console.warn(logEntry);
      self.logToDebugMode(logEntry);
    }
    if (logLevelUpperCase == "INFO" && self.logLevel >= 2) {
      console.info(logEntry);
      self.logToDebugMode(logEntry);
    }
    if (logLevelUpperCase == "DEBUG" && self.logLevel >= 3) {
      // Use debug to log behavior, such as executing x method, selected x playlist, etc.
      console.debug(logEntry);
      self.logToDebugMode(logEntry);
    }
    if (logLevelUpperCase == "TRACE" && self.logLevel >= 4) {
      // Use trace to log content such as responses from api calls. But use debug or info logger. trace prints a useless stack trace in the console that doesn't help.
      console.info(logEntry);
      self.logToDebugMode(logEntry);
    }
  }

  /** Log an error message */
  this.error = (message) => self.log("ERROR", message);

  /** Log a warn message */
  this.warn = (message) => self.log("WARN", message);

  /** Log an info message */
  this.info = (message) => self.log("INFO", message);

  /** Log a debug message */
  this.debug = (message) => self.log("DEBUG", message);

  /** Log a trace message */
  this.trace = (message) => self.log("TRACE", message);

  /**
   * Log the entry into the debug mode console log table.
   */
  this.logToDebugMode = (logEntry) => {
    const DEBUG_MODE_LOG_SIZE = 20;
    let debugModeConsoleLog = document.getElementById("debug-mode-console-log-entries");
    if (!isNullOrUndefined(debugModeConsoleLog)) {
      // Remove first log N entries
      let logEntriesSize = debugModeConsoleLog.childElementCount;
      while (logEntriesSize > DEBUG_MODE_LOG_SIZE) {
        debugModeConsoleLog.removeChild(debugModeConsoleLog.firstChild);
        logEntriesSize = debugModeConsoleLog.childElementCount;
      }
      // Add new log entry
      $("#debug-mode-console-log-entries").append(self.getLogEntryListItem(logEntry));
      // Scroll down log div
      self.debugModeLogScroll();
    }
  }

  /**
   * Scroll to the last entries of the console log.
   */
  this.debugModeLogScroll = () => {
    let height = $("#debug-mode-console-log-entries").get(0).scrollHeight;
    $("#debug-mode-console-log-entries").animate({
      scrollTop: height
    }, 100);
  }
  
  /** Dynamic DOM element generation ------------------------------------------ */
  this.getLogEntryListItem = (logEntry) => {
    let listItem = $('<li>');
    listItem.text(logEntry);
    return listItem;
  }
}
