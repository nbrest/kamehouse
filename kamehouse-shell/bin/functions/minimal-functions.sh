# Host OS is determined automatically by setHostOs function. To override it's values, in .bashrc
# set the following variables. Set only ONE os to true and the other 2 os to false.
# For example to run in a windows host:
#export USE_ENV_TO_DETERMINE_OS=true
#export IS_LIN_HOST=false
#export IS_WIN_HOST=true
#export IS_MAC_HOST=false

# Busybox detection is set by setBusybox and can also be overridden in .bashrc the same way as the host OS.
#export USE_ENV_TO_DETERMINE_BUSYBOX=true
#export IS_BUSYBOX=true

# Current user
USER=`whoami`

# Calling script name. This won't store common-functions.sh. It will store the name
# of the script that is executed in the command line and sources this file.
SCRIPT_NAME=`basename "$0"`
SCRIPT_NAME_NO_EXT=${SCRIPT_NAME%.*}
# Current script start date and time
SCRIPT_START_DATE="$(date +%Y-%m-%d' '%H:%M:%S)"
SCRIPT_START_TIME="$(date +%s)"
# Script configuration file. 
# This config file can be used to override the default variable values defined in initScriptConfig() 
# and also to override default values of script variables that can be modified by script command line parameters.
SCRIPT_CONFIG_FILENAME=${SCRIPT_NAME_NO_EXT}.cfg
SCRIPT_CONFIG_PATH=${HOME}/.kamehouse/conf/shell
SCRIPT_CONFIG_FILE="${SCRIPT_CONFIG_PATH}/${SCRIPT_CONFIG_FILENAME}"

# Stores the command line arguments from the script that sources this file.
CMD_ARGUMENTS=$@

# Set the current directory at the beginning of the script when this file is imported
# so when I exit the process, even if I cd to other dirs, I get back to the initial dir
INITIAL_DIR="`pwd`"

# Set to false to skip logging the process output to ${PROCESS_LOG_FILE}
# Override LOG_PROCESS_TO_FILE variable in the function initKameHouseShellEnv in the shell scripts
LOG_PROCESS_TO_FILE=true
PROCESS_LOG_DIR="${HOME}/.kamehouse/var/log"

# Create logs dir
mkdir -p ${PROCESS_LOG_DIR}/old

# File to log the output of the process to.
PROCESS_LOG_FILE=${PROCESS_LOG_DIR}/${SCRIPT_NAME_NO_EXT}.log

# Exit codes
EXIT_SUCCESS=0
EXIT_ERROR=1
EXIT_VAR_NOT_SET=2
EXIT_INVALID_ARG=3
EXIT_PROCESS_CANCELLED=4
EXIT_INVALID_CONFIG=5
EXIT_TIMEOUT_124=124
EXIT_TIMEOUT_137=137

# Default kamehouse-shell installation path
KAMEHOUSE_SHELL_PATH=${HOME}/.kamehouse/opt/kamehouse-shell/bin

# Default kamehouse-shell config template 
SCRIPT_CONFIG_TEMPLATE_FILE="${KAMEHOUSE_SHELL_PATH}/../conf/kamehouse-shell-script-config-template.cfg"

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
  if [ -d "${INITIAL_DIR}" ]; then
    cd "${INITIAL_DIR}"
  fi
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
  if [ "${COMMAND_RESULT}" != "${EXIT_SUCCESS}" ]; then
    log.error "${ERROR_MESSAGE}. Return code: ${COMMAND_RESULT}"
    exitProcess ${COMMAND_RESULT}
  fi
}

# Check last command's status and exit with an error message. Commands that timeout are not considered in error
checkTimeoutCommandStatus() {
  local COMMAND_RESULT="$1"
  local ERROR_MESSAGE="$2"
  if [ -z "${ERROR_MESSAGE}" ]; then
    ERROR_MESSAGE="An error occurred executing last command"
  fi
  if [ "${COMMAND_RESULT}" == "${EXIT_SUCCESS}" ]; then
    return
  fi
  if [ "${COMMAND_RESULT}" == "${EXIT_TIMEOUT_124}" ] || [ "${EXIT_CODE}" == "${EXIT_TIMEOUT_137}" ]; then
    return
  fi  
  log.error "${ERROR_MESSAGE}. Return code: ${COMMAND_RESULT}"
  exitProcess ${COMMAND_RESULT}
}

# Detects a windows host, that is able to run the windows native tools.
# The cmd.exe is the reliable marker of a windows host. It's available in the msys2, git bash
# and cygwin hosts, and in the wsl through the windows interop.
isWindowsHost() {
  # the cmd.exe in the PATH is the most reliable marker
  if command -v cmd.exe > /dev/null 2>&1; then
    return 0
  fi
  # the msys2, git bash and cygwin kernels are windows only
  case "`uname -s 2> /dev/null`" in
    MSYS*|MINGW*|CYGWIN*|Windows_NT*)
      return 0
      ;;
  esac
  return 1
}

# Detects a mac host. The sw_vers command and the SystemVersion.plist file are only available
# in macos.
isMacHost() {
  # the sw_vers command is the most reliable marker
  if command -v sw_vers > /dev/null 2>&1; then
    return 0
  fi
  # the darwin kernel is macos only
  if [ "`uname -s 2> /dev/null`" == "Darwin" ]; then
    return 0
  fi
  # the system version file is the fallback, for the hosts with a customized PATH
  if [ -f "/System/Library/CoreServices/SystemVersion.plist" ]; then
    return 0
  fi
  return 1
}

# Busybox ps doesn't support the aux, -ef and -o options and its default output has the pid in the first column
IS_BUSYBOX=false
PS_PID_FIELD=2

setHostOs() {
  if [ "${USE_ENV_TO_DETERMINE_OS}" == "true" ]; then
    log.trace "Using env to determine os"
    log.trace "IS_LIN_HOST=${IS_LIN_HOST}"
    log.trace "IS_WIN_HOST=${IS_WIN_HOST}"
    log.trace "IS_MAC_HOST=${IS_MAC_HOST}"
    return
  fi

  # Default to linux
  export IS_LIN_HOST=true
  export IS_WIN_HOST=false
  export IS_MAC_HOST=false

  # The mac host is checked first, because the macos markers are exclusive. A windows host
  # with a wine installation has a cmd.exe, but never a sw_vers command.
  if isMacHost; then
    export IS_LIN_HOST=false
    export IS_WIN_HOST=false
    export IS_MAC_HOST=true
    return
  fi

  # The windows hosts are the msys2, the git bash, the cygwin and the wsl
  if isWindowsHost; then
    export IS_LIN_HOST=false
    export IS_WIN_HOST=true
    export IS_MAC_HOST=false
    return
  fi
}

# Detects if the ps command in the path is the busybox implementation, for example on OpenWrt
isBusybox() {
  local PS_PATH=`command -v ps 2>/dev/null`
  if [ -z "${PS_PATH}" ]; then
    return 1
  fi
  local PS_TARGET=`readlink -f "${PS_PATH}" 2>/dev/null`
  if [ -z "${PS_TARGET}" ]; then
    PS_TARGET=`readlink "${PS_PATH}" 2>/dev/null`
  fi
  if echo "${PS_PATH} ${PS_TARGET}" | grep -qi "busybox"; then
    return 0
  fi
  local PS_VERSION=`ps --version 2>&1`
  if echo "${PS_VERSION}" | grep -qi "busybox"; then
    return 0
  fi
  return 1
}

setBusybox() {
  if [ "${USE_ENV_TO_DETERMINE_BUSYBOX}" == "true" ]; then
    log.trace "Using env to determine busybox"
    log.trace "IS_BUSYBOX=${IS_BUSYBOX}"
  elif isBusybox; then
    export IS_BUSYBOX=true
  else
    export IS_BUSYBOX=false
  fi

  if ${IS_BUSYBOX}; then
    export PS_PID_FIELD=1
  else
    export PS_PID_FIELD=2
  fi
}

# List all processes with their command line. Busybox only supports plain ps
psListProcesses() {
  if ${IS_BUSYBOX}; then
    ps 2>/dev/null
  else
    ps aux 2>/dev/null
  fi
}

# Import functions external to kamehouse shell
importFunctions() {
  local FUNCTIONS_FILE=$1
  source ${FUNCTIONS_FILE}
  if [ "$?" != "0" ]; then 
    echo "Error importing ${FUNCTIONS_FILE}"
    exit 99 
  fi
}

# Import kamehouse shell functions
importKamehouse() {
  local FUNCTIONS_FILE=$1
  source ${KAMEHOUSE_SHELL_PATH}/${FUNCTIONS_FILE}
  if [ "$?" != "0" ]; then 
    echo "Error importing ${KAMEHOUSE_SHELL_PATH}/${FUNCTIONS_FILE}"
    exit 99 
  fi
}

# Update script config
updateScriptConfig() {
  local SCRIPT_CONFIG_KEY=$1
  local SCRIPT_CONFIG_VALUE=$2

  log.debug "Checking for valid script config key ${SCRIPT_CONFIG_KEY}"
  cat ${SCRIPT_CONFIG_FILE} | grep "${SCRIPT_CONFIG_KEY}=" > /dev/null
  if [ "$?" != "0" ]; then 
    log.info "${SCRIPT_CONFIG_KEY} not found in ${SCRIPT_CONFIG_FILENAME}. Adding it with value ${SCRIPT_CONFIG_VALUE}"
    echo "${SCRIPT_CONFIG_KEY}=${SCRIPT_CONFIG_VALUE}" >> ${SCRIPT_CONFIG_FILE}
    cat ${SCRIPT_CONFIG_FILE} | grep "${SCRIPT_CONFIG_KEY}="  
    return
  fi

  sed -i -E "s/^#${SCRIPT_CONFIG_KEY}=.*/${SCRIPT_CONFIG_KEY}=/I" ${SCRIPT_CONFIG_FILE}
  sed -i -E "s#^${SCRIPT_CONFIG_KEY}=.*#${SCRIPT_CONFIG_KEY}=${SCRIPT_CONFIG_VALUE}#I" ${SCRIPT_CONFIG_FILE}
  log.info "Set '${SCRIPT_CONFIG_KEY}=${SCRIPT_CONFIG_VALUE}' in ${SCRIPT_CONFIG_FILENAME}"
  cat ${SCRIPT_CONFIG_FILE} | grep "${SCRIPT_CONFIG_KEY}="  
}

# Execute the script or command passed as argument with a maximum runtime in seconds
execWithTimeout() {
  local MAX_RUNTIME=$1
  local SCRIPT=$2
  shift 2
  local SCRIPT_ARGS=("$@")
  local REGEX_NUMBER='^[0-9]+$'

  if [ -z "${MAX_RUNTIME}" ] || [ "${MAX_RUNTIME}" == "0" ]; then
    "${SCRIPT}" "${SCRIPT_ARGS[@]}"
    return $?
  fi

  if [[ ! ${MAX_RUNTIME} =~ $REGEX_NUMBER ]]; then
    log.error "Invalid MAX_RUNTIME value '${MAX_RUNTIME}'. It must be a positive number of seconds"
    exitProcess ${EXIT_INVALID_CONFIG}
  fi
    
  local SCRIPT_PARAMS="${SCRIPT_ARGS[@]}"
  if ! command -v timeout > /dev/null 2>&1 || ! timeout --version > /dev/null 2>&1; then
    log.warn "The timeout command is not available. Executing ${COL_PURPLE}${SCRIPT} ${SCRIPT_PARAMS}${COL_MESSAGE} without a maximum runtime"
    "${SCRIPT}" "${SCRIPT_ARGS[@]}"
    return $?
  fi

  log.info "Executing ${COL_PURPLE}${SCRIPT} ${SCRIPT_PARAMS}${COL_MESSAGE} with a maximum runtime of ${COL_PURPLE}${MAX_RUNTIME}s"
  timeout --kill-after=10 "${MAX_RUNTIME}" "${SCRIPT}" "${SCRIPT_ARGS[@]}"
  local EXIT_CODE=$?
  if [ "${EXIT_CODE}" == "${EXIT_TIMEOUT_124}" ] || [ "${EXIT_CODE}" == "${EXIT_TIMEOUT_137}" ]; then
    log.warn "${COL_YELLOW}Maximum runtime of ${MAX_RUNTIME}s reached executing ${COL_PURPLE}${SCRIPT} ${SCRIPT_PARAMS}${COL_MESSAGE}. Aborting script execution"
  fi
  return ${EXIT_CODE}
}
