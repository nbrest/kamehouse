#!/bin/bash

source ${HOME}/programs/kamehouse-shell/bin/common/functions/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing kamehouse-functions.sh" ; exit 99 ; fi

initScriptEnv() {
  RELEASE_VERSION=""
  USE_CURRENT_DIR=true
}

mainProcess() {
  setKameHouseRootProjectDir
  updateJvncSenderVersion
}

updateJvncSenderVersion() {
  rm -rfv ./local-maven-repo/be/jedi/jvncsender
  rm -rfv ${HOME}/temp/jvncsender
  mkdir -pv ${HOME}/temp/jvncsender
  cp -v ${HOME}/.m2/repository/be/jedi/jvncsender/${RELEASE_VERSION}-SNAPSHOT/jvncsender-${RELEASE_VERSION}-SNAPSHOT-jar-with-dependencies.jar ${HOME}/temp/jvncsender/jvncsender-${RELEASE_VERSION}.jar
  cp ./scripts/jvncsender.pom.xml ${HOME}/temp/jvncsender/jvncsender-${RELEASE_VERSION}.pom.xml
  sed -i "s+RELEASE_VERSION+${RELEASE_VERSION}+g" ${HOME}/temp/jvncsender/jvncsender-${RELEASE_VERSION}.pom.xml

  mvn deploy:deploy-file -Dstyle.color=always -DgroupId=be.jedi -DartifactId=jvncsender -Dversion=${RELEASE_VERSION} -Durl=file:./local-maven-repo/ -DrepositoryId=local-maven-repo -DupdateReleaseInfo=true -Dfile=${HOME}/temp/jvncsender/jvncsender-${RELEASE_VERSION}.jar -DpomFile=${HOME}/temp/jvncsender/jvncsender-${RELEASE_VERSION}.pom.xml

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
