# param1: [true|false] override existing definition of JAVA_HOME
# param2: [true|false] log info messages
# call this script with 'source PATH-TO-SCRIPT/set-java-home.sh true false' to skip logging
# call this script with 'source PATH-TO-SCRIPT/set-java-home.sh true true' to log info

main() {
  OVERRIDE=$1
  LOG_INFO=$2
  logger.info "Current JAVA_HOME=${JAVA_HOME} OVERRIDE=${OVERRIDE}"  "${LOG_INFO}"
  if [[ "${OVERRIDE}" == "false" && -n "${JAVA_HOME}" ]]; then
    logger.info "Using already set JAVA_HOME=${JAVA_HOME}" "${LOG_INFO}"
    return
  fi
  setJavaHome "${LOG_INFO}"
}

setJavaHome() {
  LOG_INFO=$1
  logger.info "Setting new value for JAVA_HOME" "${LOG_INFO}"
  ### Java 17
  # Sort them in order from less priority to highest priority 
  # so the last one that matches is the one I want
  export JAVA_HOME="C:\Program Files\Java\jdk-17"

  if [ -d "/usr/lib/jvm/java-17-openjdk-armhf" ]; then
    export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-armhf
  fi

  if [ -d "/usr/lib/jvm/java-17-openjdk-amd64" ]; then
    export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
  fi
  logger.info "JAVA_HOME=${JAVA_HOME}" "${LOG_INFO}"
}

logger.info() {
  local LOG_MESSAGE=$1
  local LOG_INFO=$2
  if ${LOG_INFO}; then
    local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
    echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_MESSAGE}${LOG_MESSAGE}${COL_NORMAL}"
  fi
}

main "$@"