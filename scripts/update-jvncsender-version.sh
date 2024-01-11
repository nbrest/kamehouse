#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 99
fi

# Import kamehouse functions
source ${HOME}/programs/kamehouse-shell/bin/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 99
fi

LOG_PROCESS_TO_FILE=true
RELEASE_VERSION=""
USE_CURRENT_DIR=true

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
  while getopts ":v:" OPT; do
    case $OPT in 
    ("v")
      RELEASE_VERSION="$OPTARG"
      ;;        
    (\?)
      parseInvalidArgument "$OPTARG"
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
