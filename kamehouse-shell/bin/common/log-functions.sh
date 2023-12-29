#######################################################################
# This file should be imported through common-functions, not directly #
#######################################################################
# Log functions to log events to the console with a more robust framework than just echoing.
# Default log level is INFO. Log everything included in this level and under.
# Modify level by running scripts with LOG prefix. 
# Example ` LOG=DEBUG scrit-name.sh ` or ` LOG=trace scrit-name.sh ` when executing script

# 0: ERROR
# 1: WARN
# 2: INFO
# 3: DEBUG
# 4: TRACE
LOG_LEVEL_NUMBER=2

# Default color used in logs. Use this variable to return to default color if
# I change the color of some words in the log entry.
COL_DEFAULT_LOG=${COL_GREEN}

# Set to false to skip logging cmd args at start and end of script execution
LOG_CMD_ARGS=true

# Log an event to the console passing log level and the message as arguments.
# DON'T use this function directly. Use log.info, log.debug, log.warn, log.error, log.trace functions
log() {
  local LEVEL=$1
  # convert log level to upper case
  LEVEL=`echo "${LEVEL}" | tr '[:lower:]' '[:upper:]'`

  local CURRENT_ENTRY_LOG_LEVEL=`getLogLevelNumber ${LEVEL}`
  if (( ${CURRENT_ENTRY_LOG_LEVEL} > ${LOG_LEVEL_NUMBER} )); then
    # Skip log entry. log level is set below current entry
    return
  fi

  local MESSAGE=$2
  # convert \n to literal '\n' in message string so it doesnt take \n as new line. For example in C:\Users\nicolas.brest
  MESSAGE=${MESSAGE//\\\n/\\\\n}
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"

  if [ "${LEVEL}" == "INFO" ]; then
    LEVEL="${COL_BLUE}${LEVEL}${COL_NORMAL}"
    MESSAGE="${COL_DEFAULT_LOG}${MESSAGE}${COL_NORMAL}"
  fi

  if [ "${LEVEL}" == "DEBUG" ]; then
    LEVEL="${COL_GREEN}${LEVEL}${COL_NORMAL}"
    MESSAGE="${COL_DEFAULT_LOG}${MESSAGE}${COL_NORMAL}"
  fi

  if [ "${LEVEL}" == "TRACE" ]; then
    LEVEL="${COL_CYAN}${LEVEL}${COL_NORMAL}"
    MESSAGE="${COL_DEFAULT_LOG}${MESSAGE}${COL_NORMAL}"
  fi

  if [ "${LEVEL}" == "WARN" ]; then
    LEVEL="${COL_YELLOW}${LEVEL}${COL_NORMAL}"
    MESSAGE="${COL_DEFAULT_LOG}${MESSAGE}${COL_NORMAL}"
  fi

  if [ "${LEVEL}" == "ERROR" ]; then
    LEVEL="${COL_RED}${LEVEL}${COL_NORMAL}"
    MESSAGE="${COL_RED}${MESSAGE}${COL_NORMAL}"
  fi

  echo -e "${ENTRY_DATE} - [${LEVEL}] - ${MESSAGE}"
}

# Log info
log.info() {
  log "INFO" "$1"
}

# Log debug
log.debug() {
  log "DEBUG" "$1"
}

# Log trace
log.trace() {
  log "TRACE" "$1"
}

# Log warn
log.warn() {
  log "WARN" "$1"
}

# Log error
log.error() {
  log "ERROR" "$1" 
}

# Log standard start of the script
logStart() {
  if ${LOG_CMD_ARGS}; then
    log.info "Started executing ${COL_PURPLE}${SCRIPT_NAME}${COL_DEFAULT_LOG} with command line arguments ${COL_PURPLE}\"${CMD_ARGUMENTS}\"${COL_DEFAULT_LOG}"
  else
    log.info "Started executing ${COL_PURPLE}${SCRIPT_NAME}${COL_DEFAULT_LOG}"
  fi
}

# Log script run time at the end of the script
logRunTime() {
  local SCRIPT_FINISH_TIME="$(date +%s)"
  local SCRIPT_RUN_TIME_SS=$((SCRIPT_FINISH_TIME-SCRIPT_START_TIME))
  local SCRIPT_RUN_TIME=$((SCRIPT_RUN_TIME_SS / 60))
  log.info "${COL_BLUE}${SCRIPT_NAME}${COL_DEFAULT_LOG} start time: ${SCRIPT_START_DATE}. ${COL_BLUE}Run time: ${SCRIPT_RUN_TIME}m${COL_DEFAULT_LOG} (total time in seconds: ${SCRIPT_RUN_TIME_SS})"
}

# Log standard finish of process
logFinish() {
  local EXIT_CODE=$1
  if ${LOG_CMD_ARGS}; then
    log.info "Finished executing ${COL_PURPLE}${SCRIPT_NAME}${COL_DEFAULT_LOG} with command line arguments ${COL_PURPLE}\"${CMD_ARGUMENTS}\"${COL_DEFAULT_LOG} with status ${COL_PURPLE}${EXIT_CODE}"
  else
    log.info "Finished executing ${COL_PURPLE}${SCRIPT_NAME}${COL_DEFAULT_LOG} with status ${COL_PURPLE}${EXIT_CODE}"
  fi
}

# Get the current log level number
getLogLevelNumber() {
  local LEVEL=$1
  LEVEL=`echo "${LEVEL}" | tr '[:lower:]' '[:upper:]'`

  if [ "${LEVEL}" == "TRACE" ]; then
    echo "4"
    return
  fi

  if [ "${LEVEL}" == "DEBUG" ]; then
    echo "3"
    return
  fi

  if [ "${LEVEL}" == "INFO" ]; then
    echo "2"
    return
  fi

  if [ "${LEVEL}" == "WARN" ]; then
    echo "1"
    return
  fi

  if [ "${LEVEL}" == "ERROR" ]; then
    echo "0"
    return
  fi

  echo "2"
}

# Set global log level from environment
setGlobalLogLevelFromEnv() {
  if [ -n "${log}" ]; then
    LOG_LEVEL_NUMBER=`getLogLevelNumber ${log}`
    LOG=${log}
  fi

  if [ -n "${LOG}" ]; then
    LOG_LEVEL_NUMBER=`getLogLevelNumber ${LOG}`
    log=${LOG}
  fi
}
setGlobalLogLevelFromEnv
