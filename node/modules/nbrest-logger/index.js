
/**
 * Basic logger module to perform logging similar to my frontend and bash logging frameworks.
 * To modify the log level pass LOG=DEBUG or log=trace to npm start as command line arguments.
 * The default log level is INFO.
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
  trace: (message) => { logger.trace(message) },
  setLogLevel: (logLevel) => { logger.setLogLevel(logLevel) }
};

class Logger {

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
  #logLevelNumber = 2;

  /**
   * Override the default log level from cmd arguments.
   */
  constructor() {
    const logLevel = this.#getLogLevelFromCmdArgs();
    if (!this.#isEmpty(logLevel)) {
      const logLevelNumber = this.#getLogLevelNumber(logLevel);
      this.info("Overriding logLevel with command line parameter log: " + logLevel + " mapped to logLevelNumber: " + logLevelNumber);
      this.#setLogLevelNumber(logLevelNumber);
      
    }
    this.trace("Finished initializing nbrest-logger");
  }

  /** Log an error message */
  error(message) { this.#log("ERROR", message); }

  /** Log a warn message */
  warn(message) { this.#log("WARN", message); }

  /** Log an info message */
  info(message) { this.#log("INFO", message); }

  /** Log a debug message */
  debug(message) { this.#log("DEBUG", message); }

  /** Log a trace message */
  trace(message) { this.#log("TRACE", message); }

  /**
   * Set the log level.
   */
  setLogLevel(logLevel) {
    const logLevelNumber = this.#getLogLevelNumber(logLevel);
    this.info("Updating log level to : " + logLevel + " mapped to logLevelNumber: " + logLevelNumber);
    this.#setLogLevelNumber(logLevelNumber);
  }

  /**
   * Get the log level number mapped to the specified log level string.
   */
  #getLogLevelNumber(logLevel) {
    if (this.#isEmpty(logLevel)) {
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
  #setLogLevelNumber(levelNumber) {
    this.#logLevelNumber = levelNumber;
  }

  /** Log a specified message with the specified logging level. */
  #log(logLevel, message) {
    if (this.#isEmpty(logLevel)) {
      console.error("Invalid use of log(logLevel, message) function. LogLevel is missing.");
      return;
    }
    const logLevelUpperCase = logLevel.toUpperCase();
    const logEntry = this.#getTimestamp() + " - [" + logLevelUpperCase + "] - " + message;
    if (logLevelUpperCase == "ERROR") {
      console.error(logEntry);
    }
    if (logLevelUpperCase == "WARN" && this.#logLevelNumber >= 1) {
      console.warn(logEntry);
    }
    if (logLevelUpperCase == "INFO" && this.#logLevelNumber >= 2) {
      console.info(logEntry);
    }
    if (logLevelUpperCase == "DEBUG" && this.#logLevelNumber >= 3) {
      console.debug(logEntry);
    }
    if (logLevelUpperCase == "TRACE" && this.#logLevelNumber >= 4) {
      console.info(logEntry);
    }
  }

  /** Get the current timestamp */
  #getTimestamp() {
    const date = new Date();
    const offsetTime = date.getTimezoneOffset() * -1 * 60 * 1000;
    return new Date(date.getTime() + offsetTime).toISOString().replace("T", " ").slice(0, 19);
  }  

  /** Checks if a variable is undefined or null. */
  #isEmpty(val) {
    return val === undefined || val == null;
  }

  /**
   * Get the log level passed as command line argument, if any.
   */
  #getLogLevelFromCmdArgs() {
    const cmdArgs = process.argv.slice(2);
    if (this.#isEmpty(cmdArgs)) {
      return null;
    }
    let logLevel = null;
    cmdArgs.forEach((cmdArg) => {
      const cmdArgArray = cmdArg.split("=");
      const cmdArgKey = cmdArgArray[0];
      const cmdArgValue = cmdArgArray[1];
      if (!this.#isEmpty(cmdArgKey) && cmdArgKey.toUpperCase().trim() == "LOG") {
        if (!this.#isEmpty(cmdArgValue) && cmdArgValue.trim() != "") {
          logLevel = cmdArgValue;
        }
      }
    });
    return logLevel;
  }
}

const logger = new Logger();