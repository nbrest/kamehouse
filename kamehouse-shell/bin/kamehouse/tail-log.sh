#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99
fi

initKameHouseShellEnv() {
  LOG_PROCESS_TO_FILE=false
}

initScriptEnv() {
  # Initial config
  APACHE_LOG_DIR="programs/apache-httpd/logs"
  DEFAULT_LOG_LEVEL="trace"
  DEFAULT_NUM_LINES="30"

  TOMCAT_DEV_LOG_DIR="programs/apache-tomcat-dev/logs"
  TOMCAT_LOG_DIR="programs/apache-tomcat/logs"

  # Variables set by command line arguments
  FILE_ARG=""
  FOLLOW="-F"
  LOG_LEVEL_ARG=""
  NUM_LINES_ARG="0"
  FILTER_EXTRA_LINES=false

  # Global variables set during the process
  LOG_FILES=""
  NUM_LINES=""
  TAIL_LOG_AWK=${HOME}/programs/kamehouse-shell/bin/awk/kamehouse/format-tail-log.awk
  USER_HOME=""
}

mainProcess() {
  setTailLogParameters
  tailLog
}

setTailLogParameters() {
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
    setApacheLogFiles
    ;;
  "apache-error")
    addFileToLogFiles "${USER_HOME}/${APACHE_LOG_DIR}/error.log"
    ;;
  "build")
    local BUILD_LOG=${USER_HOME}/logs/build-kamehouse.log
    if [ ! -f "${BUILD_LOG}" ]; then
      touch ${BUILD_LOG}
    fi
    addFileToLogFiles ${BUILD_LOG}
    ;;
  "cmd")
    local CMD_LOG=${USER_HOME}/logs/kamehouse-cmd.log
    if [ ! -f "${CMD_LOG}" ]; then
      touch ${CMD_LOG}
    fi
    addFileToLogFiles ${CMD_LOG}
    ;;  
  "deploy")
    local DEPLOY_LOG=${USER_HOME}/logs/deploy-kamehouse.log
    if [ ! -f "${DEPLOY_LOG}" ]; then
      touch ${DEPLOY_LOG}
    fi
    addFileToLogFiles ${DEPLOY_LOG}
    ;;
  "kamehouse")
    setKameHouseLogFiles
    ;;
  "tomcat")
    setTomcatLogFiles
    ;;
  "tomcat-dev")
    setTomcatDevLogFiles
    ;;
  logs/*.log)
    addFileToLogFiles "${HOME}/${FILE_ARG}"
    ;;
  *)
    ;;
  esac
}

setApacheLogFiles() {
  if ${IS_LINUX_HOST}; then
    addFileToLogFiles "${USER_HOME}/${APACHE_LOG_DIR}/other_vhosts_access.log"
  else
    addFileToLogFiles "${USER_HOME}/${APACHE_LOG_DIR}/ssl_request.log"
  fi
  addFileToLogFiles "${USER_HOME}/${APACHE_LOG_DIR}/access.log"
  addFileToLogFiles "${USER_HOME}/${APACHE_LOG_DIR}/error.log"
}

setKameHouseLogFiles() {
  addFileToLogFiles "${USER_HOME}/${TOMCAT_LOG_DIR}/kameHouse.${LOG_DATE}.log"
  addFileToLogFiles "${USER_HOME}/${TOMCAT_LOG_DIR}/kameHouse.log"  
}

setTomcatLogFiles() {
  addFileToLogFiles "${USER_HOME}/${TOMCAT_LOG_DIR}/catalina.out"
  addFileToLogFiles "${USER_HOME}/${TOMCAT_LOG_DIR}/localhost.${LOG_DATE}.log"
  addFileToLogFiles "${USER_HOME}/${TOMCAT_LOG_DIR}/kameHouse.log"
  addFileToLogFiles "${USER_HOME}/${TOMCAT_LOG_DIR}/localhost_access_log.${LOG_DATE}.txt"
  addFileToLogFiles "${USER_HOME}/${TOMCAT_LOG_DIR}/catalina.${LOG_DATE}.log"
}

setTomcatDevLogFiles() {
  addFileToLogFiles "${USER_HOME}/${TOMCAT_DEV_LOG_DIR}/catalina.out"
  addFileToLogFiles "${USER_HOME}/${TOMCAT_DEV_LOG_DIR}/localhost.${LOG_DATE}.log"
  addFileToLogFiles "${USER_HOME}/${TOMCAT_DEV_LOG_DIR}/kameHouse.log"
  addFileToLogFiles "${USER_HOME}/${TOMCAT_DEV_LOG_DIR}/localhost_access_log.${LOG_DATE}.txt"
  addFileToLogFiles "${USER_HOME}/${TOMCAT_DEV_LOG_DIR}/catalina.${LOG_DATE}.log"
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
  log.info "Tailing files ${COL_PURPLE}${LOG_FILES}${COL_DEFAULT_LOG} in ${COL_PURPLE}${KAMEHOUSE_SERVER}${COL_DEFAULT_LOG}"
  log.debug "tail ${FOLLOW} -n ${NUM_LINES} ${LOG_FILES} | ${TAIL_LOG_AWK} -v logLevel=${LOG_LEVEL_ARG} -v filterExtraLines=${FILTER_EXTRA_LINES}"
  tail ${FOLLOW} -n ${NUM_LINES} ${LOG_FILES} | ${TAIL_LOG_AWK} -v logLevel=${LOG_LEVEL_ARG} -v filterExtraLines=${FILTER_EXTRA_LINES}
  checkCommandStatus "$?" "An error occurred displaying ${LOG_FILES}"
}

ctrlC() {
  echo ""
  exitSuccessfully
}

parseArguments() {
  local OPTIONS=("$@")
  for i in "${!OPTIONS[@]}"; do
    local CURRENT_OPTION="${OPTIONS[i]}"
    if [ "${CURRENT_OPTION:0:1}" != "-" ]; then
      continue
    fi
    local CURRENT_OPTION_ARG="${OPTIONS[i+1]}"
    case "${CURRENT_OPTION}" in
      -f)
        setFileArg "${CURRENT_OPTION_ARG}"
        ;;
      -l)
        setLogLevelArg "${CURRENT_OPTION_ARG}"
        ;;
      -n)
        setNumLinesArg "${CURRENT_OPTION_ARG}"
        ;;
      -q)
        FOLLOW=""
        ;;
      -x)
        FILTER_EXTRA_LINES=true
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setFileArg() {
  FILE_ARG=$1
  # Turn argument to lowercase
  FILE_ARG=$(echo "${FILE_ARG}" | tr '[:upper:]' '[:lower:]')
  local LOGS_REGEX=^logs/.*\.log$
  if [ "${FILE_ARG}" != "apache" ] &&
    [ "${FILE_ARG}" != "apache-error" ] &&
    [ "${FILE_ARG}" != "build" ] &&
    [ "${FILE_ARG}" != "cmd" ] &&
    [ "${FILE_ARG}" != "deploy" ] &&
    [ "${FILE_ARG}" != "kamehouse" ] &&
    [[ ! "${FILE_ARG}" =~ ${LOGS_REGEX} ]] &&
    [ "${FILE_ARG}" != "tomcat-dev" ] &&
    [ "${FILE_ARG}" != "tomcat" ]; then
    log.error "Option -f has an invalid value of ${FILE_ARG}. See help with -h for valid values"
    printHelp
    exitProcess ${EXIT_INVALID_ARG}
  fi  
}

setLogLevelArg() {
  LOG_LEVEL_ARG=$1
  LOG_LEVEL_ARG=$(echo "$LOG_LEVEL_ARG" | tr '[:upper:]' '[:lower:]')
  if [ "${LOG_LEVEL_ARG}" != "trace" ] &&
    [ "${LOG_LEVEL_ARG}" != "debug" ] &&
    [ "${LOG_LEVEL_ARG}" != "info" ] &&
    [ "${LOG_LEVEL_ARG}" != "warn" ] &&
    [ "${LOG_LEVEL_ARG}" != "error" ]; then
    log.error "Option -l has an invalid value of ${LOG_LEVEL_ARG}. See help with -h for valid values"
    printHelp
    exitProcess ${EXIT_INVALID_ARG}
  fi
}

setNumLinesArg() {
  NUM_LINES_ARG=$1
  local REGEX_NUMBER='^[0-9]+$'
  if [[ $NUM_LINES_ARG =~ $REGEX_NUMBER ]]; then
    if [ "${NUM_LINES_ARG}" -lt "1" ]; then
      log.error "Option -n has an invalid value of ${NUM_LINES_ARG}"
      printHelp
      exitProcess ${EXIT_INVALID_ARG}
    fi
  else
    log.error "Option -n has an invalid value of ${NUM_LINES_ARG}"
    printHelp
    exitProcess ${EXIT_INVALID_ARG}
  fi
}

setEnvFromArguments() {
  checkRequiredOption "-f" "${FILE_ARG}"

  if [ -z "${LOG_LEVEL_ARG}" ]; then
    log.info "Log level not set. Using default ${COL_PURPLE}${DEFAULT_LOG_LEVEL}"
    LOG_LEVEL_ARG=${DEFAULT_LOG_LEVEL}
  fi
}

printHelpOptions() {
  addHelpOption "-f (apache|apache-error|build|cmd|deploy|kamehouse|tomcat|tomcat-dev|logs/*.log)" "log file to tail" "r"
  addHelpOption "-l (trace|debug|info|warn|error)" "log level to display. Default is ${DEFAULT_LOG_LEVEL}"
  addHelpOption "-n (lines)" "number of lines to log. Default is ${DEFAULT_NUM_LINES}"
  addHelpOption "-q" "quit after tailing once. Don't follow log"
  addHelpOption "-x" "Filter extra lines that don't have a log level tag, like bash command outputs"
}

main "$@"
