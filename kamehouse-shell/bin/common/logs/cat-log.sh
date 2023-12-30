#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 149
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 149
fi

LOG_SCRIPT_RUN_TIME_IN_DEBUG=true
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
  unset OPTIND
  while getopts ":f:l:" OPT; do
    case $OPT in
    ("f")
      LOG_FILE_TO_CAT="$OPTARG"
      ;;
    ("l")
      CAT_LOG_LEVEL="$OPTARG"
      ;;      
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done  

  if [ -z "${LOG_FILE_TO_CAT}" ]; then
    log.error "Argument -f is required"
    exitProcess ${EXIT_INVALID_ARG}
  fi
}

printHelpOptions() {
  addHelpOption "-f file" "log file to cat"
  addHelpOption "-l (ALL|TRACE|DEBUG|INFO|WARN|ERROR)" "log level"
}

main "$@"