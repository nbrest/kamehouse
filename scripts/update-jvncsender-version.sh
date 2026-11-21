#!/bin/bash

source ${HOME}/.kamehouse/opt/kamehouse-shell/bin/functions/common-functions.sh
if [ "$?" != "0" ]; then echo "Error importing common-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  RELEASE_VERSION=""
  JVNCSENDER_JAR=""
  RELEASE_VERSION_RX=^[0-9]+\.[0-9]{2}$
  SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  KAMEHOUSE_ROOT_DIR="$(dirname "${SCRIPT_DIR}")"
  JVNCSENDER_PROJECT_DIR="${JVNCSENDER_PROJECT_DIR:-${HOME}/workspace/jvncsender}"
  LOCAL_DEPENDENCIES_REPOSITORY_DIR="${KAMEHOUSE_ROOT_DIR}/local-dependencies-repository/be/jedi/jvncsender"
  BUILD_GRADLE="${KAMEHOUSE_ROOT_DIR}/build.gradle"
}

mainProcess() {
  validateRootProjectDir
  validateReleaseVersion
  setJvncSenderJar
  installJvncSenderVersion
  updateMavenMetadata
  updateBuildGradleVersion
}

validateRootProjectDir() {
  if [ ! -f "${BUILD_GRADLE}" ]; then
    log.error "Could not find the kamehouse root project files in ${KAMEHOUSE_ROOT_DIR}"
    exitProcess ${EXIT_ERROR}
  fi
}

validateReleaseVersion() {
  if [[ ! "${RELEASE_VERSION}" =~ ${RELEASE_VERSION_RX} ]]; then
    log.error "Option -v has an invalid value of ${RELEASE_VERSION}"
    printHelp
    exitProcess ${EXIT_INVALID_ARG}
  fi
}

setJvncSenderJar() {
  if [ -z "${JVNCSENDER_JAR}" ]; then
    JVNCSENDER_JAR="${JVNCSENDER_PROJECT_DIR}/build/libs/jvncsender-${RELEASE_VERSION}-SNAPSHOT-jar-with-dependencies.jar"
  fi
  if [ ! -f "${JVNCSENDER_JAR}" ]; then
    log.error "jvncsender jar not found at ${JVNCSENDER_JAR}. Build it with ${JVNCSENDER_PROJECT_DIR}/scripts/build-jvncsender.sh"
    exitProcess ${EXIT_ERROR}
  fi
}

installJvncSenderVersion() {
  local VERSION_DIR="${LOCAL_DEPENDENCIES_REPOSITORY_DIR}/${RELEASE_VERSION}"
  local JAR_FILE="${VERSION_DIR}/jvncsender-${RELEASE_VERSION}.jar"
  local POM_FILE="${VERSION_DIR}/jvncsender-${RELEASE_VERSION}.pom"
  mkdir -pv "${VERSION_DIR}"
  cp -v "${JVNCSENDER_JAR}" "${JAR_FILE}"
  checkCommandStatus "$?"
  generatePom "${POM_FILE}"
  generateChecksums "${JAR_FILE}"
  generateChecksums "${POM_FILE}"
}

generatePom() {
  local POM_FILE=$1
  printf '<?xml version="1.0"?>\n<project xmlns="http://maven.apache.org/POM/4.0.0"\n  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"\n  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">\n  <modelVersion>4.0.0</modelVersion>\n\n  <groupId>be.jedi</groupId>\n  <artifactId>jvncsender</artifactId>\n  <version>%s</version>\n  <name>jvncsender</name>\n</project>' "${RELEASE_VERSION}" > "${POM_FILE}"
  checkCommandStatus "$?"
}

generateChecksums() {
  local FILE=$1
  printf '%s' "$(md5sum "${FILE}" | cut -d' ' -f1)" > "${FILE}.md5"
  printf '%s' "$(sha1sum "${FILE}" | cut -d' ' -f1)" > "${FILE}.sha1"
}

updateMavenMetadata() {
  local METADATA_FILE="${LOCAL_DEPENDENCIES_REPOSITORY_DIR}/maven-metadata.xml"
  local VERSIONS=""
  local VERSION_DIR VERSION
  for VERSION_DIR in "${LOCAL_DEPENDENCIES_REPOSITORY_DIR}"/*/; do
    VERSION="$(basename "${VERSION_DIR}")"
    if [[ "${VERSION}" =~ ^[0-9]+\.[0-9]+$ ]]; then
      VERSIONS="${VERSIONS}      <version>${VERSION}</version>\n"
    fi
  done
  printf '<?xml version="1.0" encoding="UTF-8"?>\n<metadata>\n  <groupId>be.jedi</groupId>\n  <artifactId>jvncsender</artifactId>\n  <versioning>\n    <release>%s</release>\n    <versions>\n%b    </versions>\n    <lastUpdated>%s</lastUpdated>\n  </versioning>\n</metadata>\n' \
    "${RELEASE_VERSION}" "${VERSIONS}" "$(date -u +%Y%m%d%H%M%S)" > "${METADATA_FILE}"
  checkCommandStatus "$?"
  generateChecksums "${METADATA_FILE}"
}

updateBuildGradleVersion() {
  sed -i "s+^\('jvncsender.version'[[:space:]]*: \)'[0-9][0-9.]*'+\1'${RELEASE_VERSION}'+" "${BUILD_GRADLE}"
  checkCommandStatus "$?"
  log.info "Updated jvncsender version to ${RELEASE_VERSION} in ${BUILD_GRADLE}"
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
      -v)
        RELEASE_VERSION="${CURRENT_OPTION_ARG}"
        ;;
      -j|--jar)
        JVNCSENDER_JAR="${CURRENT_OPTION_ARG}"
        ;;
      -?|-??*)
        parseInvalidArgument "${CURRENT_OPTION}"
        ;;
    esac
  done
}

setEnvFromArguments() {
  checkRequiredOption "-v" "${RELEASE_VERSION}"
}

printHelpOptions() {
  addHelpOption "-v [x.xx]" "jvncsender release version" "r"
  addHelpOption "-j --jar [path]" "jvncsender jar-with-dependencies jar to install"
}

main "$@"
