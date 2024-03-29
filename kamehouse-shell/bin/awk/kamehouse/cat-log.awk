#!/usr/bin/awk -f 

# Block of code called only once before processing any input lines
BEGIN {
  DEFAULT_LOG_LEVEL_NUM = 0;
  LOG_LEVEL_NUM_TO_PRINT = DEFAULT_LOG_LEVEL_NUM;

  parseArguments();
}

# Main function called to process each line of input
function main() {
  filterByLogLevel();
  printCurrentLine();
}

# Parse the command line arguments
function parseArguments() {
  # Script arguments
  # logLevel=[trace|debug|info|warn|error]
  LOG_LEVEL_NUM_TO_PRINT = getLogLevelNumber(logLevel);
  if (LOG_LEVEL_NUM_TO_PRINT == DEFAULT_LOG_LEVEL_NUM) {
    # logLevel parameter not set. Set to print everything by default.
    LOG_LEVEL_NUM_TO_PRINT = 5;
  }
  if (LOG_LEVEL_NUM_TO_PRINT > 5) {
    # logLevel parameter not set. Set to print everything by default.
    LOG_LEVEL_NUM_TO_PRINT = 5;
  }
}

# Filter lines that don't match the log level to print
function filterByLogLevel() {
  logLevel_loc_ = getCurrentLineLogLevel();
  logLevelNumber_loc_ = getLogLevelNumber(logLevel_loc_);
  checkLogLevelToPrint(logLevelNumber_loc_);
}

# Print line
function printCurrentLine() {
  print $0;
  next;
}

# Get the log level of the current line
function getCurrentLineLogLevel(lineUpperCase_loc, trace_rx_loc_, debug_rx_loc_, info_rx_loc_, warn_rx_loc_, error_rx_loc_) {
  lineUpperCase_loc = toupper($0);
  trace_rx_loc_ = ".*\\[.*(TRACE|FINER|FINEST).*\\].*";
  if (lineUpperCase_loc ~ trace_rx_loc_) {
    return "TRACE";
  }

  debug_rx_loc_ = ".*\\[.*(DEBUG|FINE).*\\].*";
  if (lineUpperCase_loc ~ debug_rx_loc_) {
    return "DEBUG";
  }

  info_rx_loc_ = ".*\\[.*(INFO|NOTICE).*\\].*";
  if (lineUpperCase_loc ~ info_rx_loc_) {
    return "INFO";
  }

  warn_rx_loc_ = ".*\\[.*(WARN|WARNING).*\\.*";
  if (lineUpperCase_loc ~ warn_rx_loc_) {
    return "WARN";
  }

  error_rx_loc_ = ".*\\[.*(ERROR|SEVERE|EMERG|ALERT|CRIT).*\\].*";
  if (lineUpperCase_loc ~ error_rx_loc_) {
    return "ERROR";
  }

  if (logLevel == "ALL") {
    return "ALL";
  } else {
    return "SKIP";
  }
}

# Get the log level numeric value for the specified log level
function getLogLevelNumber(logLevel_fn_, logLevelNumber_loc_) { 
  logLevelNumber_loc_ = DEFAULT_LOG_LEVEL_NUM;
  if (isLogLevelError(logLevel_fn_)) {
    logLevelNumber_loc_ = 1;
  } 
  if (isLogLevelWarn(logLevel_fn_)) {
    logLevelNumber_loc_ = 2;
  }
  if (isLogLevelInfo(logLevel_fn_)) {
    logLevelNumber_loc_ = 3;
  }
  if (isLogLevelDebug(logLevel_fn_)) {
    logLevelNumber_loc_ = 4;
  }
  if (isLogLevelTrace(logLevel_fn_)) { 
    logLevelNumber_loc_ = 5;
  }
  if (isLogLevelAll(logLevel_fn_)) {
    logLevelNumber_loc_ = DEFAULT_LOG_LEVEL_NUM;
  }
  if (isLogLevelSkip(logLevel_fn_)) {
    logLevelNumber_loc_ = 6;
  }

  return logLevelNumber_loc_;
}

function isLogLevelError(logLevel_fn_) {
  logLevel_fn_ = toupper(logLevel_fn_);
  if (logLevel_fn_ == "ERROR") {
    return "true";
  } else {
    return ""; # Empty strings and 0 are considered false. Anything else true. There's no boolean values
  }  
}

function isLogLevelWarn(logLevel_fn_) {
  logLevel_fn_ = toupper(logLevel_fn_);
  if (logLevel_fn_ == "WARN") {
    return "true";
  } else {
    return "";
  }  
}

function isLogLevelInfo(logLevel_fn_) {
  logLevel_fn_ = toupper(logLevel_fn_);
  if (logLevel_fn_ == "INFO") {
    return "true";
  } else {
    return "";
  }  
}

function isLogLevelDebug(logLevel_fn_) {
  logLevel_fn_ = toupper(logLevel_fn_);
  if (logLevel_fn_ == "DEBUG") {
    return "true";
  } else {
    return "";
  }  
}

function isLogLevelTrace(logLevel_fn_) {
  logLevel_fn_ = toupper(logLevel_fn_);
  if (logLevel_fn_ == "TRACE") {
    return "true";
  } else {
    return "";
  }  
}

function isLogLevelAll(logLevel_fn_) {
  logLevel_fn_ = toupper(logLevel_fn_);
  if (logLevel_fn_ == "ALL") {
    return "true";
  } else {
    return "";
  }  
}

function isLogLevelSkip(logLevel_fn_) {
  logLevel_fn_ = toupper(logLevel_fn_);
  if (logLevel_fn_ == "SKIP") {
    return "true";
  } else {
    return "";
  }  
}

# Check if I should print or skip this line, based on the log level
function checkLogLevelToPrint(logLevelNumber_fn_) {
  if (logLevelNumber_fn_ > LOG_LEVEL_NUM_TO_PRINT) {
    # current log level is higher than the log level to print, skipping this line
    next
  }
}

main();