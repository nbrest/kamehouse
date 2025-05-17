#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  LOG_FILE_TO_CAT=""
  CAT_LOG_LEVEL=""
  CAT_LOG_AWK=${HOME}/programs/kamehouse-shell/bin/awk/kamehouse/cat-log.awk
}

mainProcess() {
  cd ${HOME}/logs
  if [ -z "${CAT_LOG_LEVEL}" ]; then
    catLogFunction
  else
    catLogFunction | ${CAT_LOG_AWK} -v logLevel=${CAT_LOG_LEVEL}
  fi
}

catLogFunction() {
  if [ "${LOG_FILE_TO_CAT}" != "apache" ] \
    && [ "${LOG_FILE_TO_CAT}" != "apache-error" ] \
    && [ "${LOG_FILE_TO_CAT}" != "kamehouse" ] \
    && [ "${LOG_FILE_TO_CAT}" != "tomcat" ]; then
    cat "${LOG_FILE_TO_CAT}"
  else
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/tail-log.sh -f "${LOG_FILE_TO_CAT}" -q -n 2000
    ${HOME}/programs/kamehouse-shell/bin/kamehouse/tail-log.sh -f "${LOG_FILE_TO_CAT}" -q -n 30
  fi
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
        LOG_FILE_TO_CAT="${CURRENT_OPTION_ARG}"
        ;;
      -l)
        CAT_LOG_LEVEL="${CURRENT_OPTION_ARG}"
        ;;  
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;        
    esac
  done    
}

setEnvFromArguments() {
  checkRequiredOption "-f" "${LOG_FILE_TO_CAT}" 
}

printHelpOptions() {
  addHelpOption "-f file" "log file to cat"
  addHelpOption "-l (ALL|TRACE|DEBUG|INFO|WARN|ERROR)" "log level"
}

main "$@"