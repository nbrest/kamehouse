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
  # Sort them in order from less priority to highest priority
  # so the last one that matches is the one I want
  if [ -d "/c/Program Files/Java/jdk-21" ]; then
     export JAVA_HOME="C:\Program Files\Java\jdk-21"
  fi

  if [ -d "/usr/lib/jvm/default-java" ]; then
    export JAVA_HOME=/usr/lib/jvm/default-java
  fi
  
  if [ -d "/usr/lib/jvm/java-21-openjdk-armhf" ]; then
    export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-armhf
  fi

  if [ -d "/usr/lib/jvm/java-21-openjdk-arm64" ]; then
    export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-arm64
  fi

  # Fallback: JDK 21 downloaded by the gradle toolchain (ie. foojay resolver) into
  # ~/.gradle/jdks. Used when no java-21 is installed in the system so tomcat can still
  # run the Java 21 webapps. A system java-21-openjdk-amd64 still takes priority below.
  local GRADLE_JAVA_21_HOME=""
  local GRADLE_JAVA_21_BIN=`find ${HOME}/.gradle/jdks -type f -name java 2>/dev/null`
  while read GRADLE_JAVA_BIN; do
    if [ -n "${GRADLE_JAVA_BIN}" ] && "${GRADLE_JAVA_BIN}" -version 2>&1 | grep -Eq 'version "21'; then
      GRADLE_JAVA_21_HOME=$(dirname "$(dirname "${GRADLE_JAVA_BIN}")")
      break
    fi
  done <<< "${GRADLE_JAVA_21_BIN}"
  if [ -d "${GRADLE_JAVA_21_HOME}" ]; then
    setJavaHomeLogger "Using JDK 21 downloaded by gradle in ${GRADLE_JAVA_21_HOME}" "${LOG_INFO}"
    export JAVA_HOME=${GRADLE_JAVA_21_HOME}
  fi

  if [ -d "/usr/lib/jvm/java-21-openjdk-amd64" ]; then
    export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
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