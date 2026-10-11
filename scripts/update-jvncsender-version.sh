#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  RELEASE_VERSION=""
  USE_CURRENT_DIR=true
}

mainProcess() {
  setKameHouseRootProjectDir
  updateJvncSenderVersion
}

updateJvncSenderVersion() {
  rm -rfv ./local-dependencies-repository/be/jedi/jvncsender
  rm -rfv ${HOME}/temp/jvncsender
  mkdir -pv ${HOME}/temp/jvncsender
  # jvncsender is built separately from the kamehouse project, so it is resolved from the 
  # local dependencies repository of the machine that builds it
  cp -v ${HOME}/.m2/repository/be/jedi/jvncsender/${RELEASE_VERSION}-SNAPSHOT/jvncsender-${RELEASE_VERSION}-SNAPSHOT-jar-with-dependencies.jar ${HOME}/temp/jvncsender/jvncsender-${RELEASE_VERSION}.jar
  cp ./scripts/jvncsender.pom.xml ${HOME}/temp/jvncsender/jvncsender-${RELEASE_VERSION}.pom.xml
  sed -i "s+RELEASE_VERSION+${RELEASE_VERSION}+g" ${HOME}/temp/jvncsender/jvncsender-${RELEASE_VERSION}.pom.xml

  # the kamehouse project resolves be.jedi:jvncsender from the local-dependencies-repository of
  # this project (see settings.gradle), with the jar and the pom of the release version
  local JVNCSENDER_LOCAL_DEPENDENCIES_REPOSITORY_DIR="./local-dependencies-repository/be/jedi/jvncsender/${RELEASE_VERSION}"
  mkdir -pv ${JVNCSENDER_LOCAL_DEPENDENCIES_REPOSITORY_DIR}
  cp -v ${HOME}/temp/jvncsender/jvncsender-${RELEASE_VERSION}.jar ${JVNCSENDER_LOCAL_DEPENDENCIES_REPOSITORY_DIR}/
  cp -v ${HOME}/temp/jvncsender/jvncsender-${RELEASE_VERSION}.pom.xml ${JVNCSENDER_LOCAL_DEPENDENCIES_REPOSITORY_DIR}/
  log.info "Remember to update the jvncsender.version property of build.gradle with ${RELEASE_VERSION}"

  rm -rfv ${HOME}/temp/jvncsender
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
  addHelpOption "-v [x.xx]" "jvncsender release version"
}

main "$@"
