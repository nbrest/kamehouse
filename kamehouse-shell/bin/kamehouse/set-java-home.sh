# call this script with 'source PATH-TO-SCRIPT/set-java-home.sh'

main() {
  logger.info "Setting JAVA_HOME"

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

  logger.info "JAVA_HOME=${JAVA_HOME}"
}

logger.info() {
  local ENTRY_DATE="${COL_CYAN}$(date +%Y-%m-%d' '%H:%M:%S)${COL_NORMAL}"
  local LOG_MESSAGE=$1
  echo -e "${ENTRY_DATE} - [${COL_BLUE}INFO${COL_NORMAL}] - ${COL_MESSAGE}${LOG_MESSAGE}${COL_NORMAL}"
}

main "$@"