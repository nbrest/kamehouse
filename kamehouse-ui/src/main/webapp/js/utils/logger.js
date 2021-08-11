/**
 * Log object to perform logging to the console on the frontend side.
 * 
 * Dependencies: timeUtils.
 * 
 * @author nbrest
 */
function Logger() {

  this.setLogLevel = setLogLevel;
  this.getLogLevel = getLogLevel;
  this.error = error;
  this.warn = warn;
  this.info = info;
  this.debug = debug;
  this.trace = trace;

  /**
   * Log levels:
   * 0: ERROR
   * 1: WARN
   * 2: INFO
   * 3: DEBUG
   * 4: TRACE
   */
  //Defaults log level to INFO (2)
  let logLevelNumber = 2;

  /**
   * Set the log level for the console in numeric value, based on the mapping shown above.
   */
  function setLogLevel(levelNumber) {
    logLevelNumber = levelNumber;
  }

  /**
   * Get the log level for the console in numeric value, based on the mapping shown above.
   */
  function getLogLevel() {
    return logLevelNumber;
  }

  /** Log a specified message with the specified logging level. */
  function log(logLevel, message) {
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
      logToDebugMode(logEntry);
    }
    if (logLevelUpperCase == "WARN" && logLevelNumber >= 1) {
      console.warn(logEntry);
      logToDebugMode(logEntry);
    }
    if (logLevelUpperCase == "INFO" && logLevelNumber >= 2) {
      console.info(logEntry);
      logToDebugMode(logEntry);
    }
    if (logLevelUpperCase == "DEBUG" && logLevelNumber >= 3) {
      // Use debug to log behavior, such as executing x method, selected x playlist, etc.
      console.debug(logEntry);
      logToDebugMode(logEntry);
    }
    if (logLevelUpperCase == "TRACE" && logLevelNumber >= 4) {
      // Use trace to log content such as responses from api calls. But use debug or info logger. trace prints a useless stack trace in the console that doesn't help.
      console.info(logEntry);
      logToDebugMode(logEntry);
    }
  }

  /** Log an error message */
  function error(message) { log("ERROR", message); }

  /** Log a warn message */
  function warn(message) { log("WARN", message); }

  /** Log an info message */
  function info(message) { log("INFO", message); }

  /** Log a debug message */
  function debug(message) { log("DEBUG", message); }

  /** Log a trace message */
  function trace(message) { log("TRACE", message); }

  /**
   * Log the entry into the debug mode console log table.
   */
  function logToDebugMode(logEntry) {
    const DEBUG_MODE_LOG_SIZE = 20;
    let debugModeConsoleLog = document.getElementById("debug-mode-console-log-entries");
    if (!isNullOrUndefined(debugModeConsoleLog)) {
      // Remove first log N entries
      let logEntriesSize = debugModeConsoleLog.childElementCount;
      while (logEntriesSize > DEBUG_MODE_LOG_SIZE) {
        domUtils.removeChild(debugModeConsoleLog, debugModeConsoleLog.firstChild);
        logEntriesSize = debugModeConsoleLog.childElementCount;
      }
      // Add new log entry
      domUtils.append($("#debug-mode-console-log-entries"), getLogEntryListItem(logEntry));
      // Scroll down log div
      debugModeLogScroll();
    }
  }

  /**
   * Scroll to the last entries of the console log.
   */
  function debugModeLogScroll() {
    let height = $("#debug-mode-console-log-entries").get(0).scrollHeight;
    $("#debug-mode-console-log-entries").animate({
      scrollTop: height
    }, 100);
  }
  
  function getLogEntryListItem(logEntry) {
    return domUtils.getLi({}, logEntry);
  }
}
