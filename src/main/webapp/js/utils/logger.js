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
  let stripCommentsRegex = /((\/\/.*$)|(\/\*[\s\S]*?\*\/))/mg;
  let argumentNamesRegex = /([^\s,]+)/g;

  /** Log a specified message with the specified logging level. */
  this.log = (logLevel, message) => {
    if (isNullOrUndefined(logLevel)) {
      console.error("Invalid use of log(logLevel, message) function. LogLevel is missing.");
      return;
    }
    let logLevelUpperCase = logLevel.toUpperCase();
    let callerFunction = self.getCallerFunctionName();
    let logEntry = "";
    if (isNullOrUndefined(callerFunction)) {
      logEntry = timeUtils.getTimestamp() + " - [" + logLevelUpperCase + "] - () - " + message;
    } else {
      logEntry = timeUtils.getTimestamp() + " - [" + logLevelUpperCase + "] - (" + callerFunction + ") - " + message;
    }
    if (logLevelUpperCase == "ERROR") {
      console.error(logEntry)
    }
    if (logLevelUpperCase == "WARN" && self.logLevel >= 1) {
      console.warn(logEntry);
    }
    if (logLevelUpperCase == "INFO" && self.logLevel >= 2) {
      console.info(logEntry);
    }
    if (logLevelUpperCase == "DEBUG" && self.logLevel >= 3) {
      // Use debug to log behavior, such as executing x method, selected x playlist, etc.
      console.debug(logEntry);
    }
    if (logLevelUpperCase == "TRACE" && self.logLevel >= 4) {
      // Use trace to log content such as responses from api calls. But use debug logger. trace prints a useless stack trace in the console that doesn't help.
      console.info(logEntry);
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

  /** Log a debug message of the function call with it's parameters*/
  this.debugFunctionCall = () => {
    let message = self.getMessageForTraceFunctionCall();
    if (!isNullOrUndefined(message)) {
      self.debug(message);
    }
  }

  /** Log a trace message of the function call with it's parameters*/
  this.traceFunctionCall = () => { 
    let message = self.getMessageForTraceFunctionCall();
    if (!isNullOrUndefined(message)) {
      self.trace(message);
    }
  }

  /** Get the caller name of the function generating the log entry. 
   * This is meant to be called from log() function which would be indirectly
   * called from log.error, lot warn, log.info, etc, so it goes 3 levels up. 
   * There are some cases like blacklisted functions or annonymous functions
   * that return an empty callerFunction. */
  this.getCallerFunctionName = function getCallerFunctionName() {
    let blacklistedFunctions = ["debugFunctionCall", "traceFunctionCall", "success", "error"];
    let callerFunction = "";
    try {
      callerFunction = getCallerFunctionName.caller.caller.caller.name;
      if (blacklistedFunctions.includes(callerFunction)) {
        callerFunction = "";
      }
    } catch (error) {
      callerFunction = "";
    }
    return callerFunction;
  }

  /** Get the message to log the start of a function. This is meant to be called only 
   * from logDebugFunctionCall and logTraceFunctionCall 
   * as it goes 2 levels up to get the caller function details. 
   * Async functions throw an error. */
  this.getMessageForTraceFunctionCall = function getMessageForFunctionCall() {
    let message = "";
    try {
      let callerFunction = getMessageForFunctionCall.caller.caller;
      message = "Started " + callerFunction.name;
      let argumentsString = "";
      if (!isNullOrUndefined(callerFunction.arguments) && !isNullOrUndefined(callerFunction.arguments.length) 
        && callerFunction.arguments.length > 0) {
        argumentsString = " with arguments";
        let callerFunctionArgumentNames = self.getFunctionArgumentNames(callerFunction);
        for (let i = 0; i < callerFunction.arguments.length; i++) {
          if (i < callerFunctionArgumentNames.length) {
            argumentsString = argumentsString + " " + callerFunctionArgumentNames[i] + ":" + JSON.stringify(callerFunction.arguments[i]);
          } else {
            argumentsString = argumentsString + " arg[" + i + "]:" + JSON.stringify(callerFunction.arguments[i]);
          }
        }
      } else {
        argumentsString = " without arguments"
      }
      message = message + argumentsString;
    } catch (error) {
      message = "";
    }
    return message;
  }

  /** Get the function argument names of the specified function */
  this.getFunctionArgumentNames = (functionToParse) => {
    let fuctionString = functionToParse.toString().replace(stripCommentsRegex, '');
    let argumentNames = fuctionString.slice(fuctionString.indexOf('(') + 1, fuctionString.indexOf(')')).match(argumentNamesRegex);
    if (argumentNames === null)
      argumentNames = [];
    return argumentNames;
  }  
}
