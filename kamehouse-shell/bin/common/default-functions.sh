#######################################################################
# This file should be imported through common-functions, not directly #
#######################################################################
# Default functions to execute default functionality when not overriden

# Trap SIGINT to customize manual abort of the process.
trap ctrlC INT

# Default function to execute when trappinc ctrl+C, SIGINT. Override to customize trapping.
ctrlC() {
  echo ""
  log.warn "Captured ${COL_RED}Ctrl+C${COL_DEFAULT_LOG}. Process ${COL_PURPLE}${SCRIPT_NAME}${COL_DEFAULT_LOG} manually aborted."
  exitProcess 2
}

# Default implementation of the function to parse command line arguments
# Override this function in the scripts that source this file
parseArguments() {
  #log.warn "Using default parseArguments() function. Override re defining this function in each script."
  while getopts ":h" OPT; do
    case $OPT in
    ("h")
      printHelp
      exitProcess 0
      ;;
    (\?)
      log.error "Invalid option: -$OPTARG"
      printHelp
      exitProcess 1
      ;;
    esac
  done
}

# Default print help message
printHelp() {
  log.warn "Using default printHelp() function. Override re defining this function in each script."
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help"
}

# Display the invalid argument error and exit printing help message
parseInvalidArgument() {
  log.error "Invalid option: -$1"
  printHelp
  exitProcess 1
}

# Print the help and exit
parseHelp() {
  printHelp
  exitProcess 0
}

# Default main process that needs to be overriden with custom script logic.
mainProcess() {
  log.info "Override mainProcess() with the script logic."
}

# Default main function. Override this one ONLY if I don't want logging of start and finish by default.
# For example, in scripts that return true or false like is.linux.host.sh and shouldn't output anything else.
mainWrapper() {
  logStart
  parseArguments "$@"
  mainProcess "$@"
  logFinish
  exitSuccessfully
}

# main function to call from each script
main() {
  if ${LOG_PROCESS_TO_FILE}; then
    # default: set +o pipefail
    # set -o pipefail : if mainWrapper exits with != 0, echo $? will show the error code. With the default
    # behavior the pipe | swallows the error code and echo $? shows 0 from the tee command
    set -o pipefail
    mainWrapper "$@" | tee ${PROCESS_LOG_FILE}
  else
    mainWrapper "$@"
  fi
}
