#############################
# Import other dependencies #
#############################
COMMON_FUNCTIONS_PATH=`dirname ${BASH_SOURCE[0]}`
sourceFiles=("bashrc-functions.sh" "colors.sh" "default-functions.sh" "git-functions.sh" "log-functions.sh" "example-functions.sh")
for INDEX in ${!sourceFiles[@]}; do
  source ${COMMON_FUNCTIONS_PATH}/${sourceFiles[$INDEX]}
  if [ "$?" != "0" ]; then
    echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing ${sourceFiles[$INDEX]}\033[0;39m"
    exit 1
  fi
done

##########################################
# Common variables used by other scripts #
##########################################

# Current user
USER=`whoami`

# Calling script name. This won't store common-functions.sh. It will store the name
# of the script that is executed in the command line and sources this file.
SCRIPT_NAME=`basename "$0"`

# Stores the command line arguments from the script that sources this file.
CMD_ARGUMENTS=$@

# Set the current directory at the beginning of the script when this file is imported
# so when I exit the process, even if I cd to other dirs, I get back to the initial dir
INITIAL_DIR="`pwd`"

# Subsystem root prefix for mounted drives. Use this as a prefix to all
# absolute paths I define in the script.
ROOT_PREFIX="/mnt"

# Set to true to log the process output to ${PROCESS_LOG_FILE}
LOG_PROCESS_TO_FILE=false
# File to log the output of the process to.
PROCESS_LOG_FILE=${HOME}/logs/${SCRIPT_NAME%.*}.log

IS_ROOT_USER=false
if (( $EUID == 0 )); then
  IS_ROOT_USER=true
fi

##########################################
# Common functions used by other scripts #
##########################################

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
    exitProcess 1
  fi
}

# Exit the process with the status code specified as an argument
exitProcess() {
  local EXIT_STATUS=$1
  cd "${INITIAL_DIR}"
  exit ${EXIT_STATUS}
} 

# Exit the process successfully
exitSuccessfully() {
  exitProcess 0
}

# Check last command's status and exit with an error message if the status is not zero
checkCommandStatus() {
  local COMMAND_RESULT="$1"
  local ERROR_MESSAGE="$2"
  if [ "${ERROR_MESSAGE}" == "" ]; then
    ERROR_MESSAGE="An error occurred executing last command"
  fi
  if [ "${COMMAND_RESULT}" != "0" ]; then
    log.error "${ERROR_MESSAGE}. Return code: ${COMMAND_RESULT}"
    exitProcess ${COMMAND_RESULT}
  fi
}

# Remove bash header message and other unexpected output 
# from variable holding command output (usually from ssh)
removeUnexpectedOutputFromVar() {
  local VAR_TO_PROCESS=$1 
  local VALID_REGEX=$2
  local VALID_OUTPUT=""
  local LINE_COUNT=0
  for LINE_TO_PROCESS in ${VAR_TO_PROCESS}; do
    if [[ ${LINE_TO_PROCESS} =~ ${VALID_REGEX} ]] ; then
      if [[ ${LINE_COUNT} -gt "0" ]] ; then
          # Append line to previous output
          VALID_OUTPUT=${VALID_OUTPUT}$'\n'${LINE_TO_PROCESS}
      else
          # First line
          VALID_OUTPUT=${LINE_TO_PROCESS}
      fi
      LINE_COUNT=$((LINE_COUNT + 1))
    fi
  done
  echo ${VALID_OUTPUT}
}

# Execute the first function and retries if it fails. 
# The second function is a recovery operation to excute between retries
# Example:  executeWithRetry gitPush gitPull
# With: gitPull() { git pull origin dev } ; gitPush() { git push origin dev }
executeWithRetry() {
  local OPERATION=$1
  local RECOVERY_OPERATION=$2
  local RETRY_COUNT=1
  local MAX_RETRIES=5
  local WAIT_TIME=30 # Seconds
  local OPERATION_FAILED=true
  local OPERATION_RESPONSE=1
  while [[ ${RETRY_COUNT} -le ${MAX_RETRIES} && ${OPERATION_FAILED} == true ]]
  do 
    log.info "Executing operation: ${OPERATION}"
    ${OPERATION}
    OPERATION_RESPONSE=$?
    if [ "${OPERATION_RESPONSE}" == "0" ]; then
      OPERATION_FAILED=false
    else
      let RETRY_COUNT=RETRY_COUNT+1
      log.warn "Operation ${OPERATION} failed. Retrying again in ${WAIT_TIME} seconds." 
      if [ -z "${RECOVERY_OPERATION}" ]; then
        log.warn "No recovery operation set"
      else
        log.info "Executing recovery operation ${RECOVERY_OPERATION} before retrying ${OPERATION}"
        ${RECOVERY_OPERATION}
      fi
      sleep ${WAIT_TIME}
    fi
  done
  checkCommandStatus "${OPERATION_RESPONSE}" "An error occurred executing ${OPERATION}"
}

###########################################################
# Common functions called in this script to set variables #
###########################################################

# Check if I'm using Ubuntu for windows, Git Bash or any other bash implementation.
# Default is Ubuntu for windows. Set root prefix for mounted drives based on the subsystem.
# In Git Bash drives are mounted /c /d so root prefix is empty.
# In Ubuntu for windows drives are mounted in /mnt/c /mnt/d so root prefix is /mnt
setRootPrefix() {
  # Ubuntu for windows
  ROOT_PREFIX="/mnt"
  local MSYSTEM_MINGW="${MSYSTEM}"
  MSYSTEM_MINGW=`echo ${MSYSTEM_MINGW:0:5}`
  if [ "${MSYSTEM_MINGW}" == "MINGW" ]; then 
    # Git Bash
    ROOT_PREFIX=""
  fi
}

################################################
# Calls to functions that set common variables #
################################################
# Set the ROOT_PREFIX variable
setRootPrefix
