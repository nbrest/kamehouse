# param1: [--skip-override|--override] override existing definition of JAVA_HOME
# param2: [--skip-log|--log] log info messages
# call this script with 'source PATH-TO-SCRIPT/set-java-home.sh --skip-override --skip-log' to skip logging
# call this script with 'source PATH-TO-SCRIPT/set-java-home.sh --override --log' to log info

setJavaHome() {
  OVERRIDE=$1
  LOG_INFO=$2
  local JAVA_VERSION="${KAMEHOUSE_JAVA_VERSION:-25}"
  setJavaHomeLogger "Current JAVA_HOME=${JAVA_HOME} OVERRIDE=${OVERRIDE} JAVA_VERSION=${JAVA_VERSION}" "${LOG_INFO}"
  if [[ "${OVERRIDE}" == "--skip-override" && -n "${JAVA_HOME}" ]]; then
    setJavaHomeLogger "Using already set JAVA_HOME=${JAVA_HOME}" "${LOG_INFO}"
    return
  fi
  setJavaHomeLogger "Setting new value for JAVA_HOME" "${LOG_INFO}"
  # Sort them in order from less priority to highest priority
  # so the last one that matches is the one I want
  if [ -d "/c/Program Files/Java/jdk-${JAVA_VERSION}" ]; then
     export JAVA_HOME="C:\Program Files\Java\jdk-${JAVA_VERSION}"
  fi

  # /usr/lib/jvm/default-java is an unversioned symlink that can point to any java version
  # (ie. java 17 on raspbian bookworm), so only use it when it is actually JAVA_VERSION
  if matchesJavaVersion "${JAVA_VERSION}" "/usr/lib/jvm/default-java"; then
    export JAVA_HOME=/usr/lib/jvm/default-java
  fi

  if matchesJavaVersion "${JAVA_VERSION}" "/usr/lib/jvm/java-${JAVA_VERSION}-openjdk-armhf"; then
    export JAVA_HOME=/usr/lib/jvm/java-${JAVA_VERSION}-openjdk-armhf
  fi

  if matchesJavaVersion "${JAVA_VERSION}" "/usr/lib/jvm/java-${JAVA_VERSION}-openjdk-arm64"; then
    export JAVA_HOME=/usr/lib/jvm/java-${JAVA_VERSION}-openjdk-arm64
  fi

  # Fallback: JDK downloaded by the gradle toolchain (ie. foojay resolver) into
  # ~/.gradle/jdks. Used when no java-${JAVA_VERSION} is installed in the system so tomcat can
  # still run the Java ${JAVA_VERSION} webapps. A system java-${JAVA_VERSION}-openjdk-amd64 still
  # takes priority below.
  local GRADLE_JAVA_HOME=""
  local GRADLE_JAVA_BIN=`find ${HOME}/.gradle/jdks -type f -name java 2>/dev/null`
  while read GRADLE_JAVA_BIN; do
    if [ -n "${GRADLE_JAVA_BIN}" ] && "${GRADLE_JAVA_BIN}" -version 2>&1 | grep -Eq "version \"${JAVA_VERSION}"; then
      GRADLE_JAVA_HOME=$(dirname "$(dirname "${GRADLE_JAVA_BIN}")")
      break
    fi
  done <<< "${GRADLE_JAVA_BIN}"
  if [ -d "${GRADLE_JAVA_HOME}" ]; then
    setJavaHomeLogger "Using JDK ${JAVA_VERSION} downloaded by gradle in ${GRADLE_JAVA_HOME}" "${LOG_INFO}"
    export JAVA_HOME=${GRADLE_JAVA_HOME}
  fi

  if matchesJavaVersion "${JAVA_VERSION}" "/usr/lib/jvm/java-${JAVA_VERSION}-openjdk-amd64"; then
    export JAVA_HOME=/usr/lib/jvm/java-${JAVA_VERSION}-openjdk-amd64
  fi

  setJavaHomeLogger "JAVA_HOME=${JAVA_HOME}" "${LOG_INFO}"
}

matchesJavaVersion() {
  local JAVA_VERSION=$1
  local CANDIDATE_JAVA_HOME=$2
  if [ ! -x "${CANDIDATE_JAVA_HOME}/bin/java" ]; then
    return 1
  fi
  if "${CANDIDATE_JAVA_HOME}/bin/java" -version 2>&1 | grep -Eq "version \"${JAVA_VERSION}"; then
    return 0
  fi
  return 1
}

setJavaHomeLogger() {
  local LOG_MESSAGE=$1
  local LOG_INFO=$2
  if [ "${LOG_INFO}" != "--skip-log" ]; then
    log.info "${LOG_MESSAGE}"
  fi
}

setJavaHome "$@"