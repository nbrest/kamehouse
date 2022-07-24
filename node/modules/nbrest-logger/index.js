
/**
 * Log object to perform logging similar to my frontend and bash logging frameworks.
 * To modify the log level pass LOG=DEBUG or log=trace to npm start as command line arguments.
 * 
 * Dependencies: none.
 * 
 * @author nbrest
 */
module.exports = {
  error: (message) => { logger.error(message) },
  warn: (message) => { logger.warn(message) },
  info: (message) => { logger.info(message) },
  debug: (message) => { logger.debug(message) },
  trace: (message) => { logger.trace(message) }
};

function Logger() {

  this.init = init;
  this.error = error;
  this.warn = warn;
  this.info = info;
  this.debug = debug;
  this.trace = trace;

  /**
   * Log levels:
   * 
   * 0: ERROR
   * 1: WARN
   * 2: INFO
   * 3: DEBUG
   * 4: TRACE
   * 
   * Default log level: INFO (2)
   */
  let logLevelNumber = 2;

  /**
   * Override the default log level from url parameters.
   */
  function init() {
    trace("Initializing logger");
    const logLevel = getLogLevelFromCmdArgs();
    if (!isEmpty(logLevel)) {
      const logLevelNumberParam = getLogLevelNumber(logLevel);
      info("Overriding logLevel with url parameter logLevel: " + logLevel + " mapped to logLevelNumber: " + logLevelNumberParam);
      setLogLevel(logLevelNumberParam);
    }
  }

  /**
   * Get the log level number mapped to the specified log level string.
   */
  function getLogLevelNumber(logLevel) {
    if (isEmpty(logLevel)) {
      return 2;
    }
    const logLevelUpperCase = logLevel.toUpperCase();
    if (logLevelUpperCase == "ERROR") {
      return 0;
    }
    if (logLevelUpperCase == "WARN") {
      return 1;
    }
    if (logLevelUpperCase == "INFO") {
      return 2;
    }
    if (logLevelUpperCase == "DEBUG") {
      return 3;
    }
    if (logLevelUpperCase == "TRACE") {
      return 4;
    }
    // default INFO
    return 2;
  }

  /**
   * Set the log level for the console in numeric value, based on the mapping shown above.
   */
  function setLogLevel(levelNumber) {
    logLevelNumber = levelNumber;
  }

  /** Log a specified message with the specified logging level. */
  function log(logLevel, message) {
    if (isEmpty(logLevel)) {
      console.error("Invalid use of log(logLevel, message) function. LogLevel is missing.");
      return;
    }
    const logLevelUpperCase = logLevel.toUpperCase();
    const logEntry = getTimestamp() + " - [" + logLevelUpperCase + "] - " + message;
    if (logLevelUpperCase == "ERROR") {
      console.error(logEntry);
    }
    if (logLevelUpperCase == "WARN" && logLevelNumber >= 1) {
      console.warn(logEntry);
    }
    if (logLevelUpperCase == "INFO" && logLevelNumber >= 2) {
      console.info(logEntry);
    }
    if (logLevelUpperCase == "DEBUG" && logLevelNumber >= 3) {
      console.debug(logEntry);
    }
    if (logLevelUpperCase == "TRACE" && logLevelNumber >= 4) {
      console.info(logEntry);
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

  /** Get the current timestamp */
  function getTimestamp() {
    const date = new Date();
    const offsetTime = date.getTimezoneOffset() * -1 * 60 * 1000;
    return new Date(date.getTime() + offsetTime).toISOString().replace("T", " ").slice(0, 19);
  }  

  /** Checks if a variable is undefined or null. */
  function isEmpty(val) {
    return val === undefined || val == null;
  }

  /**
   * Get the log level passed as command line argument, if any.
   */
  function getLogLevelFromCmdArgs() {
    const cmdArgs = process.argv.slice(2);
    if (isEmpty(cmdArgs)) {
      return null;
    }
    let logLevel = null;
    cmdArgs.forEach((cmdArg) => {
      const cmdArgArray = cmdArg.split("=");
      const cmdArgKey = cmdArgArray[0];
      const cmdArgValue = cmdArgArray[1];
      if (!isEmpty(cmdArgKey) && cmdArgKey.toUpperCase().trim() == "LOG") {
        if (!isEmpty(cmdArgValue) && cmdArgValue.trim() != "") {
          logLevel = cmdArgValue;
        }
      }
    });
    return logLevel;
  }
}

const logger = new Logger();
logger.init();