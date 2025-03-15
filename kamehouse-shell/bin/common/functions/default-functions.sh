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
  parseHelpArgument "$@"
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

# Default implementation of the function to parse command line arguments
# Override this function in the scripts that source this file
parseArguments() {
  log.trace "Using default parseArguments() function. Override re defining this function in each script when needed."
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

# Default print help message
printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"
  addHelpOption "-h --help" "display help"
  printHelpOptions
  printHelpFooter
}

# Override in each script with the options specific to the script
printHelpOptions() {
  log.trace "Using default printHelpOptions() function. Override re defining this function in each script when needed."
}

# Override in each script to print a footer after the help options
printHelpFooter() {
  log.trace "Using default printHelpFooter() function. Override re defining this function in each script when needed."
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
  exitSuccessfully
}

# Set and validate the environment variables after parsing the command line arguments
setEnvFromArguments() {
  log.trace "Using default setEnvFromArguments() function. Override re defining this function in each script when needed."
}

# Default main process that needs to be overriden with custom script logic.
mainProcess() {
  log.info "Override mainProcess() with the script logic."
}

# Default main function. Override this one ONLY if I don't want logging of start and finish by default.
# For example, in scripts that return true or false like is.linux.host.sh and shouldn't output anything else.
mainWrapper() {
  logStart
  parseCmdArguments "$@"
  setEnvFromArguments
  mainProcess "$@"
  exitSuccessfully
}

# main function to call from each script
main() {
  if ${LOG_PROCESS_TO_FILE}; then
    # default: set +o pipefail
    # set -o pipefail : if mainWrapper exits with != 0, echo $? will show the error code. With the default
    # behavior the pipe | swallows the error code and echo $? shows 0 from the tee command
    set -o pipefail
    mainWrapper "$@" 2>&1 | tee ${PROCESS_LOG_FILE}
  else
    mainWrapper "$@"
  fi
}
