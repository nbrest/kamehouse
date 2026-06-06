# param1: [--skip-override|--override] override existing definition of JAVA_HOME
# param2: [--skip-log|--log] log info messages
# call this script with 'source PATH-TO-SCRIPT/set-java-home.sh --skip-override --skip-log' to skip logging
# call this script with 'source PATH-TO-SCRIPT/set-java-home.sh --override --log' to log info

setJavaHome() {
  OVERRIDE=$1
  LOG_INFO=$2
  setJavaHomeLogger "Current JAVA_HOME=${JAVA_HOME} OVERRIDE=${OVERRIDE}" "${LOG_INFO}"
  if [[ "${OVERRIDE}" == "--skip-override" && -n "${JAVA_HOME}" ]]; then
    setJavaHomeLogger "Using already set JAVA_HOME=${JAVA_HOME}" "${LOG_INFO}"
    return
  fi
  setJavaHomeLogger "Setting new value for JAVA_HOME" "${LOG_INFO}"
  ### Java 17
  # Sort them in order from less priority to highest priority 
  # so the last one that matches is the one I want
  export JAVA_HOME="C:\Program Files\Java\jdk-17"

  if [ -d "/usr/lib/jvm/default-java" ]; then
    export JAVA_HOME=/usr/lib/jvm/default-java
  fi
  
  if [ -d "/usr/lib/jvm/java-17-openjdk-armhf" ]; then
    export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-armhf
  fi

  if [ -d "/usr/lib/jvm/java-17-openjdk-arm64" ]; then
    export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-arm64
  fi

  if [ -d "/usr/lib/jvm/java-17-openjdk-amd64" ]; then
    export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
  fi

  setJavaHomeLogger "JAVA_HOME=${JAVA_HOME}" "${LOG_INFO}"
}

setJavaHomeLogger() {
  local LOG_MESSAGE=$1
  local LOG_INFO=$2
  if [ "${LOG_INFO}" != "--skip-log" ]; then
    log.info "${LOG_MESSAGE}"
  fi
}

setJavaHome "$@"