#!/bin/bash

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

LOG_PROCESS_TO_FILE=false
LOG_FILE_TO_CAT=""
CAT_LOG_LEVEL=""
CAT_LOG_AWK=${HOME}/programs/kamehouse-shell/bin/awk/kamehouse/cat-log.awk

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