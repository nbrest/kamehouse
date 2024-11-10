# Current user
USER=`whoami`

# Calling script name. This won't store common-functions.sh. It will store the name
# of the script that is executed in the command line and sources this file.
SCRIPT_NAME=`basename "$0"`
# Current script start date and time
SCRIPT_START_DATE="$(date +%Y-%m-%d' '%H:%M:%S)"
SCRIPT_START_TIME="$(date +%s)"

# Stores the command line arguments from the script that sources this file.
CMD_ARGUMENTS=$@

# Set the current directory at the beginning of the script when this file is imported
# so when I exit the process, even if I cd to other dirs, I get back to the initial dir
INITIAL_DIR="`pwd`"

# Set to false to skip logging the process output to ${PROCESS_LOG_FILE}
LOG_PROCESS_TO_FILE=true
# Create logs dir
mkdir -p ${HOME}/logs
# File to log the output of the process to.
PROCESS_LOG_FILE=${HOME}/logs/${SCRIPT_NAME%.*}.log

# Exit codes
EXIT_SUCCESS=0
EXIT_ERROR=1
EXIT_VAR_NOT_SET=2
EXIT_INVALID_ARG=3
EXIT_PROCESS_CANCELLED=4

# Adds a script option to the help menu
addHelpOption() {
  local OPTION=$1
  local DESCRIPTION=$2
  local REQUIRED=$3
  if [[ "${REQUIRED}" == "r" ]]; then
    DESCRIPTION="${DESCRIPTION} [${COL_RED}required${COL_NORMAL}]"
  fi
  echo -e "     ${COL_BLUE}${OPTION}${COL_NORMAL} ${DESCRIPTION}"
}

# Check if a required option is set
checkRequiredOption() {
  local OPTION_LETTER=$1
  local OPTION_VALUE=$2
  if [ -z "${OPTION_VALUE}" ]; then
    log.error "Option ${OPTION_LETTER} is required"
    printHelp
    exitProcess ${EXIT_INVALID_ARG}
  fi
}

# Exit the process with the status code specified as an argument
exitProcess() {
  local EXIT_STATUS=$1
  logFinish ${EXIT_STATUS}
  cd "${INITIAL_DIR}"
  exit ${EXIT_STATUS}
} 

# Exit the process successfully
exitSuccessfully() {
  exitProcess ${EXIT_SUCCESS}
}

# Check last command's status and exit with an error message if the status is not zero
checkCommandStatus() {
  local COMMAND_RESULT="$1"
  local ERROR_MESSAGE="$2"
  if [ -z "${ERROR_MESSAGE}" ]; then
    ERROR_MESSAGE="An error occurred executing last command"
  fi
  if [ "${COMMAND_RESULT}" != "0" ]; then
    log.error "${ERROR_MESSAGE}. Return code: ${COMMAND_RESULT}"
    exitProcess ${COMMAND_RESULT}
  fi
}