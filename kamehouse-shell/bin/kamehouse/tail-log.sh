#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/docker-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing docker-functions.sh\033[0;39m"
  exit 1
fi

# Initial config
APACHE_LOG_DIR="programs/apache-httpd/logs"
DEFAULT_ENV="local"
DEFAULT_LOG_LEVEL="trace"
DEFAULT_NUM_LINES="5"

TOMCAT_DEV_LOG_DIR="programs/apache-tomcat-dev/logs"
INTELLIJ_LOG_DIR="${TOMCAT_DEV_LOG_DIR}"
ECLIPSE_LOG_DIR="${TOMCAT_DEV_LOG_DIR}"
TOMCAT_LOG_DIR="programs/apache-tomcat/logs"

# Variables set by command line arguments
FILE_ARG=""
FOLLOW="-F"
LOG_LEVEL_ARG=""
NUM_LINES_ARG="0"
PROFILE="${DEFAULT_DOCKER_PROFILE}"

# Global variables set during the process
LOG_FILES=""
NUM_LINES=""
TAIL_LOG_AWK=${HOME}/programs/kamehouse-shell/bin/awk/kamehouse/format-tail-log.awk

mainProcess() {
  setGlobalVariables
  if [ "${ENVIRONMENT}" == "local" ]; then
    tailLog
  else
    # Tail log remotely
    executeSshCommand
  fi
}

parseArguments() {
  parseDockerProfile "$@"

  while getopts ":e:f:l:n:p:q" OPT; do
    case $OPT in
    "e")
      parseEnvironment "$OPTARG"
      ;;
    "f")
      FILE_ARG=$OPTARG
      # Turn argument to lowercase
      FILE_ARG=$(echo "${FILE_ARG}" | tr '[:upper:]' '[:lower:]')
      local LOGS_REGEX=^logs/.*\.log$
      if [ "${FILE_ARG}" != "apache" ] &&
        [ "${FILE_ARG}" != "eclipse" ] &&
        [ "${FILE_ARG}" != "intellij" ] &&
        [ "${FILE_ARG}" != "kamehouse" ] &&
        [[ ! "${FILE_ARG}" =~ ${LOGS_REGEX} ]] &&
        [ "${FILE_ARG}" != "tomcat" ]; then
        log.error "Option -f has an invalid value of ${FILE_ARG}. See help with -h for valid values"
        printHelp
        exitProcess 1
      fi
      ;;
    "l")
      LOG_LEVEL_ARG=$(echo "$OPTARG" | tr '[:upper:]' '[:lower:]')
      if [ "${LOG_LEVEL_ARG}" != "trace" ] &&
        [ "${LOG_LEVEL_ARG}" != "debug" ] &&
        [ "${LOG_LEVEL_ARG}" != "info" ] &&
        [ "${LOG_LEVEL_ARG}" != "warn" ] &&
        [ "${LOG_LEVEL_ARG}" != "error" ]; then
        log.error "Option -l has an invalid value of ${LOG_LEVEL_ARG}. See help with -h for valid values"
        printHelp
        exitProcess 1
      fi
      ;;
    "n")
      NUM_LINES_ARG=$OPTARG
      local REGEX_NUMBER='^[0-9]+$'
      if [[ $NUM_LINES_ARG =~ $REGEX_NUMBER ]]; then
        if [ "${NUM_LINES_ARG}" -lt "1" ]; then
          log.error "Option -n has an invalid value of ${NUM_LINES_ARG}"
          printHelp
          exitProcess 1
        fi
      else
        log.error "Option -n has an invalid value of ${NUM_LINES_ARG}"
        printHelp
        exitProcess 1
      fi
      ;;
    "q")
      FOLLOW=""
      ;;
    \?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setEnvFromArguments() {
  if [ -z "${FILE_ARG}" ]; then
    log.error "Option -f file to tail is required"
    printHelp
    exitProcess 1
  fi

  if [ -z "${LOG_LEVEL_ARG}" ]; then
    log.info "Log level not set. Using default ${COL_PURPLE}${DEFAULT_LOG_LEVEL}"
    LOG_LEVEL_ARG=${DEFAULT_LOG_LEVEL}
  fi

  if [ -z "${ENVIRONMENT}" ]; then
    log.info "Environment not set. Using default ${COL_PURPLE}${DEFAULT_ENV}"
    ENVIRONMENT=${DEFAULT_ENV}
  fi

  setEnvForDockerProfile
}

setGlobalVariables() {
  USER_HOME=$HOME
  # Set number of lines to tail
  if [ "${NUM_LINES_ARG}" -gt "0" ]; then
    NUM_LINES=${NUM_LINES_ARG}
  else
    NUM_LINES=${DEFAULT_NUM_LINES}
    log.info "Number of lines to tail not set. Using default value of ${COL_PURPLE}${DEFAULT_NUM_LINES}${COL_DEFAULT_LOG}"
  fi

  local LOG_DATE=$(date +%Y-%m-%d)
  case ${FILE_ARG} in
  "apache")
    addFileToLogFiles "${USER_HOME}/${APACHE_LOG_DIR}/error.log"
    if ${IS_LINUX_HOST}; then
      addFileToLogFiles "${USER_HOME}/${APACHE_LOG_DIR}/other_vhosts_access.log"
    else
      addFileToLogFiles "${USER_HOME}/${APACHE_LOG_DIR}/ssl_request.log"
    fi
    addFileToLogFiles "${USER_HOME}/${APACHE_LOG_DIR}/access.log"
    ;;
  "intellij")
    if ${IS_LINUX_HOST}; then
      addFileToLogFiles "${USER_HOME}/${INTELLIJ_LOG_DIR}/localhost.${LOG_DATE}.log"
    else
      addFileToLogFiles "${USER_HOME}/${INTELLIJ_LOG_DIR}/localhost.${LOG_DATE}.txt"
    fi
    addFileToLogFiles "${USER_HOME}/${INTELLIJ_LOG_DIR}/localhost_access_log.${LOG_DATE}.txt"
    addFileToLogFiles "${USER_HOME}/${INTELLIJ_LOG_DIR}/catalina.out"
    addFileToLogFiles "${USER_HOME}/${INTELLIJ_LOG_DIR}/catalina.${LOG_DATE}.log"
    ;;
  "eclipse")
    if ${IS_LINUX_HOST}; then
      addFileToLogFiles "${USER_HOME}/${ECLIPSE_LOG_DIR}/localhost.${LOG_DATE}.log"
    else
      addFileToLogFiles "${USER_HOME}/${ECLIPSE_LOG_DIR}/localhost.${LOG_DATE}.txt"
    fi
    addFileToLogFiles "${USER_HOME}/${ECLIPSE_LOG_DIR}/localhost_access_log.${LOG_DATE}.txt"
    addFileToLogFiles "${USER_HOME}/${ECLIPSE_LOG_DIR}/catalina.out"
    addFileToLogFiles "${USER_HOME}/${ECLIPSE_LOG_DIR}/catalina.${LOG_DATE}.log"
    ;;
  "kamehouse")
    if ${IS_LINUX_HOST}; then
      addFileToLogFiles "${USER_HOME}/${TOMCAT_LOG_DIR}/kameHouse.${LOG_DATE}.log"
    fi
    addFileToLogFiles "${USER_HOME}/${TOMCAT_LOG_DIR}/kameHouse.log"
    ;;
  "tomcat")
    if ${IS_LINUX_HOST}; then
      addFileToLogFiles "${USER_HOME}/${TOMCAT_LOG_DIR}/localhost.${LOG_DATE}.log"
    else
      addFileToLogFiles "${USER_HOME}/${TOMCAT_LOG_DIR}/localhost.${LOG_DATE}.txt"
      # In linux, the content of kameHouse.log is already displayed in catalina.out
      addFileToLogFiles "${USER_HOME}/${TOMCAT_LOG_DIR}/kameHouse.log"
    fi
    addFileToLogFiles "${USER_HOME}/${TOMCAT_LOG_DIR}/localhost_access_log.${LOG_DATE}.txt"
    addFileToLogFiles "${USER_HOME}/${TOMCAT_LOG_DIR}/catalina.out"
    addFileToLogFiles "${USER_HOME}/${TOMCAT_LOG_DIR}/catalina.${LOG_DATE}.log"
    ;;
  logs/*.log)
    addFileToLogFiles "${HOME}/${FILE_ARG}"
    ;;
  *)
    ;;
  esac

  SSH_SERVER=${ENVIRONMENT}
  SSH_COMMAND="${SCRIPT_NAME} -e local -f ${FILE_ARG} -n ${NUM_LINES} -l ${LOG_LEVEL_ARG}"
  if [ "${ENVIRONMENT}" == "docker" ]; then
    SSH_SERVER=localhost
    SSH_PORT=${DOCKER_PORT_SSH}
  fi
}

addFileToLogFiles() {
  local FILE_TO_ADD=$1
  if test -f "${FILE_TO_ADD}"; then
    LOG_FILES=${LOG_FILES}" ${FILE_TO_ADD}"
  else
    log.warn "File ${COL_PURPLE}${FILE_TO_ADD}${COL_DEFAULT_LOG} doesn't exist"
  fi
}

tailLog() {
  log.info "Tailing files ${COL_PURPLE}${LOG_FILES}${COL_DEFAULT_LOG} in ${COL_PURPLE}${ENVIRONMENT}${COL_DEFAULT_LOG}"
  log.debug "tail ${FOLLOW} -n ${NUM_LINES} ${LOG_FILES} | ${TAIL_LOG_AWK} -v logLevel=${LOG_LEVEL_ARG}"
  tail ${FOLLOW} -n ${NUM_LINES} ${LOG_FILES} | ${TAIL_LOG_AWK} -v logLevel=${LOG_LEVEL_ARG}
  checkCommandStatus "$?" "An error occurred displaying ${LOG_FILES}"
}

ctrlC() {
  echo ""
  logFinish
  exitSuccessfully
}

printHelpOptions() {
  addHelpOption "-e ${ENVIRONMENTS_LIST}" "environment to tail logs from. Default is ${DEFAULT_ENV}"
  addHelpOption "-f (apache|eclipse|intellij|kamehouse|tomcat|logs/*.log)" "log file to tail [${COL_RED}required${COL_NORMAL}]"
  addHelpOption "-l (trace|debug|info|warn|error)" "log level to display. Default is ${DEFAULT_LOG_LEVEL}"
  addHelpOption "-n (lines)" "number of lines to log. Default is ${DEFAULT_NUM_LINES}"
  printDockerProfileOption
  addHelpOption "-q" "quit after tailing once. Don't follow log"
}

main "$@"
