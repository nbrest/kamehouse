#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Global variables
LOG_PROCESS_TO_FILE=false
RESTART=false
HIBERNATE=true
SHUTDOWN=false
SHUTDOWN_ACTION="/h"
DELAY=

mainProcess() {
  shutdownProcess &
  wait
}

shutdownProcess() {
  countdown
  if [ "${HIBERNATE}" == "true" ]; then
    cmd.exe "/c shutdown.exe ${SHUTDOWN_ACTION} /f"
  else
    cmd.exe "/c shutdown.exe ${SHUTDOWN_ACTION} /f /t 0"
  fi
}

countdown() {
  DELAY_MINUTES=${DELAY}
  mins=$((DELAY_MINUTES))
  log.info "Scheduled shutdown in: ${COL_PURPLE}${DELAY_MINUTES}${COL_DEFAULT_LOG} minutes with action ${COL_PURPLE}${SHUTDOWN_ACTION}${COL_DEFAULT_LOG}" 
  while [ ${mins} -gt 0 ]; do
    echo -ne "`log.info "${COL_NORMAL}Shutting down in ${COL_RED}${mins}${COL_NORMAL} minutes. Press ${COL_RED}Ctrl+C${COL_NORMAL} to abort"`\033[0K\r"
    sleep 60
    : $((mins--))
  done
}

parseArguments() {
  while getopts ":irst:" OPT; do
    case $OPT in
    "i")
      HIBERNATE=true
      RESTART=false
      SHUTDOWN=false
      SHUTDOWN_ACTION="/h"
      ;;
    "r")
      HIBERNATE=false
      RESTART=true
      SHUTDOWN=false
      SHUTDOWN_ACTION="/r"
      ;;
    "s")
      HIBERNATE=false
      RESTART=false
      SHUTDOWN=true
      SHUTDOWN_ACTION="/s"
      ;;
    "t")
      setDelay "$OPTARG"
      ;;
    \?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

setDelay() {
  DELAY=$1
  local REGEX_NUMBER='^[0-9]+$'
  if [[ $DELAY =~ $REGEX_NUMBER ]]; then
    if [ "${DELAY}" -lt "0" ]; then
      log.error "Option -t MINUTES has an invalid value of -t ${DELAY}"
      printHelp
      exitProcess 1
    fi
  else
    log.error "Option -t MINUTES has an invalid value of -t ${DELAY}"
    printHelp
    exitProcess 1
  fi
}

setEnvFromArguments() {
  checkRequiredOption "-t" "${DELAY}"
}

printHelpOptions() {
  addHelpOption "-i" "hibernate"
  addHelpOption "-r" "restart"
  addHelpOption "-s" "shutdown"
  addHelpOption "-t 999" "number of MINUTES to delay shutdown" "r"
}

main "$@"
