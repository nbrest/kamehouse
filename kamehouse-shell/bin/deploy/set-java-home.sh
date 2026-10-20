# param1: [--skip-override|--override] override existing definition of JAVA_HOME
# param2: [--skip-log|--log] log info messages
# call this script with 'source PATH-TO-SCRIPT/set-java-home.sh --skip-override --skip-log' to skip logging
# call this script with 'source PATH-TO-SCRIPT/set-java-home.sh --override --log' to log info

setJavaHome() {
  OVERRIDE=$1
  LOG_INFO=$2
  local JAVA_VERSION="${KAMEHOUSE_JAVA_VERSION:-25}"
  setJavaHomeLogger "Current JAVA_HOME=${JAVA_HOME} OVERRIDE=${OVERRIDE} JAVA_VERSION=${JAVA_VERSION}" "${LOG_INFO}"
  if [[ "${OVERRIDE}" == "--skip-override" && -n "${JAVA_HOME}" ]] && matchesJavaVersion "${JAVA_VERSION}" "${JAVA_HOME}"; then
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

  # First run on a machine without the expected java version: download it before the build, so
  # the deploy doesn't run with a stale JDK while gradle downloads the expected one mid-build.
  # Only on the deploy/build path (--override). Interactive shells and tomcat startup pass
  # --skip-override, so they never trigger a download.
  if [[ "${OVERRIDE}" == "--override" ]] && ! matchesJavaVersion "${JAVA_VERSION}" "${JAVA_HOME}"; then
    downloadJavaVersion "${JAVA_VERSION}" "${LOG_INFO}"
    if matchesJavaVersion "${JAVA_VERSION}" "${HOME}/.gradle/jdks/kamehouse-jdk-${JAVA_VERSION}"; then
      export JAVA_HOME="${HOME}/.gradle/jdks/kamehouse-jdk-${JAVA_VERSION}"
    fi
  fi

  setJavaHomeLogger "JAVA_HOME=${JAVA_HOME}" "${LOG_INFO}"
  if [ -n "${JAVA_HOME}" ] && [ "${JAVA_HOME:0:1}" != "C" ] && ! matchesJavaVersion "${JAVA_VERSION}" "${JAVA_HOME}"; then
    if [ "${LOG_INFO}" != "--skip-log" ]; then
      log.warn "Could not resolve JAVA_HOME to Java ${JAVA_VERSION}. Still pointing to ${JAVA_HOME}"
    fi
  fi
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

# Downloads the expected JDK version from the Adoptium (Temurin) API into
# ~/.gradle/jdks/kamehouse-jdk-${JAVA_VERSION}, the same directory the ~/.gradle/jdks fallback of
# setJavaHome scans, so both this script and gradle's own toolchain auto-detection pick it up.
downloadJavaVersion() {
  local JAVA_VERSION=$1
  local LOG_INFO=$2
  local DOWNLOAD_DIR="${HOME}/.gradle/jdks/kamehouse-jdk-${JAVA_VERSION}"
  if matchesJavaVersion "${JAVA_VERSION}" "${DOWNLOAD_DIR}"; then
    setJavaHomeLogger "JDK ${JAVA_VERSION} already downloaded in ${DOWNLOAD_DIR}" "${LOG_INFO}"
    return
  fi
  local OS_ARCH
  OS_ARCH=$(getAdoptiumArch)
  if [ -z "${OS_ARCH}" ]; then
    setJavaHomeLogger "Unable to determine the OS architecture to download JDK ${JAVA_VERSION}" "${LOG_INFO}"
    return
  fi
  if ! command -v curl > /dev/null; then
    setJavaHomeLogger "curl not available. Skipping download of JDK ${JAVA_VERSION}" "${LOG_INFO}"
    return
  fi
  local DOWNLOAD_URL="https://api.adoptium.net/v3/binary/latest/${JAVA_VERSION}/ga/$(getAdoptiumOs)/${OS_ARCH}/jdk/hotspot/normal/eclipse"
  local TMP_DIR=$(mktemp -d)
  setJavaHomeLogger "Downloading JDK ${JAVA_VERSION} from ${DOWNLOAD_URL}" "${LOG_INFO}"
  if ! curl -fL --connect-timeout 10 --max-time 600 -o "${TMP_DIR}/jdk-download" "${DOWNLOAD_URL}" 2> /dev/null; then
    setJavaHomeLogger "Failed to download JDK ${JAVA_VERSION} from ${DOWNLOAD_URL}" "${LOG_INFO}"
    rm -rf "${TMP_DIR}"
    return
  fi
  mkdir -p "${TMP_DIR}/extract"
  local EXTRACT_OK=false
  if unzip -q "${TMP_DIR}/jdk-download" -d "${TMP_DIR}/extract" 2> /dev/null; then
    EXTRACT_OK=true
  elif tar -xf "${TMP_DIR}/jdk-download" -C "${TMP_DIR}/extract" 2> /dev/null; then
    EXTRACT_OK=true
  fi
  if ! ${EXTRACT_OK}; then
    setJavaHomeLogger "Failed to extract the JDK ${JAVA_VERSION} archive" "${LOG_INFO}"
    rm -rf "${TMP_DIR}"
    return
  fi
  # both the zip and the tar.gz archives contain a single top level jdk-${JAVA_VERSION}* directory
  local EXTRACTED_JDK=$(find "${TMP_DIR}/extract" -maxdepth 1 -mindepth 1 -type d -name "jdk-${JAVA_VERSION}*" | head -1)
  if [ -z "${EXTRACTED_JDK}" ]; then
    EXTRACTED_JDK=$(find "${TMP_DIR}/extract" -maxdepth 1 -mindepth 1 -type d | head -1)
  fi
  if [ -z "${EXTRACTED_JDK}" ] || { [ ! -f "${EXTRACTED_JDK}/bin/java" ] && [ ! -f "${EXTRACTED_JDK}/bin/java.exe" ]; }; then
    setJavaHomeLogger "Extracted JDK ${JAVA_VERSION} archive doesn't contain a valid bin/java" "${LOG_INFO}"
    rm -rf "${TMP_DIR}"
    return
  fi
  rm -rf "${DOWNLOAD_DIR}"
  mkdir -p "${HOME}/.gradle/jdks"
  mv "${EXTRACTED_JDK}" "${DOWNLOAD_DIR}"
  rm -rf "${TMP_DIR}"
  if matchesJavaVersion "${JAVA_VERSION}" "${DOWNLOAD_DIR}"; then
    setJavaHomeLogger "Downloaded JDK ${JAVA_VERSION} to ${DOWNLOAD_DIR}" "${LOG_INFO}"
  else
    setJavaHomeLogger "Downloaded JDK ${JAVA_VERSION} to ${DOWNLOAD_DIR} but it doesn't match the expected version" "${LOG_INFO}"
  fi
}

getAdoptiumOs() {
  case "$(uname -s)" in
    Linux*)
      echo "linux"
      ;;
    Darwin*)
      echo "mac"
      ;;
    MINGW*|MSYS*|CYGWIN*)
      echo "windows"
      ;;
    *)
      echo ""
      ;;
  esac
}

getAdoptiumArch() {
  case "$(uname -m)" in
    x86_64|amd64)
      echo "x64"
      ;;
    aarch64|arm64)
      echo "aarch64"
      ;;
    arm*|armv7l)
      echo "arm"
      ;;
    *)
      echo ""
      ;;
  esac
}

setJavaHomeLogger() {
  local LOG_MESSAGE=$1
  local LOG_INFO=$2
  if [ "${LOG_INFO}" != "--skip-log" ]; then
    log.info "${LOG_MESSAGE}"
  fi
}

setJavaHome "$@"