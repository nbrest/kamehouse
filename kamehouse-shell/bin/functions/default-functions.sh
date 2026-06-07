# Default functions to execute default functionality when not overriden

# Trap SIGINT to customize manual abort of the process.
trap ctrlC INT

# Default function to execute when trappinc ctrl+C, SIGINT. Override to customize trapping.
ctrlC() {
  echo ""
  log.warn "Captured ${COL_RED}Ctrl+C${COL_DEFAULT_LOG}. Process ${COL_PURPLE}${SCRIPT_NAME}${COL_DEFAULT_LOG} manually aborted."
  exitProcess ${EXIT_PROCESS_CANCELLED}
}

# Parse command line arguments
parseCmdArguments() {
  parseArguments "$@"
}

# Parse help argument
parseHelpArgument() {
  local ARGS=("$@")
  for i in "${!ARGS[@]}"; do
    case "${ARGS[i]}" in
      -h|--help)
        parseHelp
        ;;
    esac
  done
}

# Parse script config arguments
parseScriptConfigArguments() {
  parseShowScriptConfigArgument "$@"
  parseEditScriptConfigArgument "$@"
  parseResetScriptConfigArgument "$@"
}

# Parse show script config argument
parseShowScriptConfigArgument() {
  local ARGS=("$@")
  for i in "${!ARGS[@]}"; do
    case "${ARGS[i]}" in
      -sc|--show-config)
        parseShowScriptConfig
        ;;
    esac
  done
}

# Parse edit script config argument
parseEditScriptConfigArgument() {
  local ARGS=("$@")
  for i in "${!ARGS[@]}"; do
    case "${ARGS[i]}" in
      -ec|--edit-config)
        parseEditScriptConfig
        ;;
    esac
  done
}

# Parse reset script config argument
parseResetScriptConfigArgument() {
  local ARGS=("$@")
  for i in "${!ARGS[@]}"; do
    case "${ARGS[i]}" in
      -rc|--reset-config)
        parseResetScriptConfig
        ;;
    esac
  done
}

# Parse script log arguments
parseScriptLogArguments() {
  parseShowScriptLogArgument "$@"
  parseTailScriptLogArgument "$@"
}

# Parse show script log
parseShowScriptLogArgument() {
  local ARGS=("$@")
  for i in "${!ARGS[@]}"; do
    case "${ARGS[i]}" in
      --log)
        parseShowScriptLog
        ;;
    esac
  done
}

# Parse tail script log
parseTailScriptLogArgument() {
  local ARGS=("$@")
  for i in "${!ARGS[@]}"; do
    case "${ARGS[i]}" in
      --tail-log)
        parseTailScriptLog
        ;;
    esac
  done
}

# Default implementation of the function to parse command line arguments
# Override this function in the scripts that source this file
parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -s)
        log.info "-s sample argument passed to script"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

# Display the invalid argument error and exit printing help message
parseInvalidArgument() {
  local OPTION=$1
  log.error "Invalid option: ${OPTION}"
  printHelp
  exitProcess ${EXIT_INVALID_ARG}
}

# Print the help and exit
parseHelp() {
  printHelp
  exit ${EXIT_SUCCESS}
}

# Default print help message
printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"
  addHelpOption "-h --help" "display help"
  addHelpOption "-sc --show-config" "display script config file"
  addHelpOption "-ec --edit-config" "edit script config file"
  addHelpOption "-rc --reset-config" "reset script config file to default values"
  addHelpOption "--log" "display script log from last run"
  addHelpOption "--tail-log" "tail script log"
  printHelpOptions
  printHelpFooter
}

# Show script config and exit
parseShowScriptConfig() {
  showScriptConfig
  exit ${EXIT_SUCCESS}
}

# Show script config
showScriptConfig() {
  if [ -f "${SCRIPT_CONFIG_FILE}" ]; then
    log.info "Script config: ${SCRIPT_CONFIG_FILE}"
    cat ${SCRIPT_CONFIG_FILE}
  else
    log.info "Script config file ${SCRIPT_CONFIG_FILE} doesn't exist"
  fi
}

# Edit script config and exit
parseEditScriptConfig() {
  editScriptConfig
  exit ${EXIT_SUCCESS}
}

# Edit script config
editScriptConfig() {
  if [ -f "${SCRIPT_CONFIG_FILE}" ]; then
    log.info "Editing script config: ${SCRIPT_CONFIG_FILE}"
    vim ${SCRIPT_CONFIG_FILE}
  else
    log.info "Script config file ${SCRIPT_CONFIG_FILE} doesn't exist"
  fi
}

# Reset script config and exit
parseResetScriptConfig() {
  log.info "Resetting script config: ${SCRIPT_CONFIG_FILE}"
  createScriptConfigFile
  exit ${EXIT_SUCCESS}
}

# Create script config file
createScriptConfigFile() {
  mkdir -p ${SCRIPT_CONFIG_PATH}
  cp -f ${SCRIPT_CONFIG_TEMPLATE_FILE} ${SCRIPT_CONFIG_FILE}
  sed -i "s#---SCRIPT_NAME---#${SCRIPT_NAME}#g" "${SCRIPT_CONFIG_FILE}"
}

# Show script log and exit
parseShowScriptLog() {
  log.info "Start of ${PROCESS_LOG_FILE}"
  cat ${PROCESS_LOG_FILE}
  log.info "End of ${PROCESS_LOG_FILE}"
  exit ${EXIT_SUCCESS}
}

# Tail script log and exit
parseTailScriptLog() {
  log.info "Start of ${PROCESS_LOG_FILE}"
  tail -n 100000 -f ${PROCESS_LOG_FILE}
  log.info "End of ${PROCESS_LOG_FILE}"
  exit ${EXIT_SUCCESS}
}

# Override in each script with the options specific to the script
printHelpOptions() {
  return
}

# Override in each script to print a footer after the help options
printHelpFooter() {
  echo ""
}

# Set and validate the environment variables after parsing the command line arguments
setEnvFromArguments() {
  return
}

# Override to load the configuration files for each script before parsing arguments
loadConfigFiles() {
  loadScriptConfigFile
}

# Load script config file
loadScriptConfigFile() {
  if [ ! -f "${SCRIPT_CONFIG_FILE}" ]; then
    log.info "Missing script config. Creating: ${SCRIPT_CONFIG_FILE}"
    createScriptConfigFile
  fi
  source ${SCRIPT_CONFIG_FILE}
  log.trace "Loaded ${SCRIPT_CONFIG_FILE}"
}

# Set the kamehouse shell environment parameters before configuring the shell
initKameHouseShellEnv() {
  return
}

# Set default script configuration variables that can be overriden in the script config file
# This is mainly for variables that are used in a single script. 
# For variables shared between multiple scripts or used in *-function.sh scripts, 
# in most cases it's better to use kamehouse.cfg instead of script config file
setDefaultScriptConfig() {
  return
}

# Set the global environment variables for the script after loading the configuration files
# and before parsing arguments that may override the global variables set here
initScriptEnv() {
  return
}

# Default main process that needs to be overriden with custom script logic.
mainProcess() {
  log.info "Override mainProcess() with the script logic."
}

# Configure the kamehouse shell environment
configureKameHouseShell() {
  setLogLevelFromEnv
  setLogColors
  setRootPrefix
  setIsLinuxHost
}

# Rotate log file
rotateLogs() {
  if [ -f "${PROCESS_LOG_FILE}" ]; then
    mv ${PROCESS_LOG_FILE} ${PROCESS_LOG_DIR}/old/
  fi
}

# Default main function wrapper. This should never be overriden
mainWrapper() {
  logStart
  setDefaultScriptConfig
  loadConfigFiles
  initScriptEnv
  parseCmdArguments "$@"
  setEnvFromArguments
  mainProcess "$@"
  exitSuccessfully
}

# Override in individual scripts if there's no need for win/lin distinction
mainProcess() {
  mainProcessPre
  if ${IS_LINUX_HOST}; then
    mainProcessLin
  else
    mainProcessWin
  fi
  mainProcessPost
}

# Override in individual scripts for common pre lin/win actions
mainProcessPre() {
  return
}

# Override in individual scripts for linux actions
mainProcessLin() {
  return
}

# Override in individual scripts for windows actions
mainProcessWin() {
  return
}

# Override in individual scripts for common post lin/win actions
mainProcessPost() {
  return
}

# main function to call from each script
main() {
  initKameHouseShellEnv
  configureKameHouseShell
  parseHelpArgument "$@"
  parseScriptLogArguments "$@"
  parseScriptConfigArguments "$@"
  if ${LOG_PROCESS_TO_FILE}; then
    # default: set +o pipefail
    # set -o pipefail : if mainWrapper exits with != 0, echo $? will show the error code. With the default
    # behavior the pipe | swallows the error code and echo $? shows 0 from the tee command
    set -o pipefail
    rotateLogs
    mainWrapper "$@" 2>&1 | tee ${PROCESS_LOG_FILE}
  else
    mainWrapper "$@"
  fi
}
