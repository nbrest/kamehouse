#!/bin/bash

# Import common functions
source ${HOME}/my.scripts/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

# Import kamehouse functions
source ${HOME}/my.scripts/common/kamehouse/kamehouse-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing kamehouse-functions.sh\033[0;39m"
  exit 1
fi

FAST_BUILD=false
INTEGRATION_TESTS=false
MODULE=
KAMEHOUSE_CMD_DEPLOY_PATH="${HOME}/programs"
MAVEN_COMMAND=
MAVEN_PROFILE="prod"
RESUME=false
CONTINUE_ON_ERRORS=false

mainProcess() {
  buildProject
  deployKameHouseCmd
  cleanLogsInGitRepoFolder
}

buildProject() {
  log.info "Building ${COL_PURPLE}kamehouse${COL_DEFAULT_LOG} with profile ${COL_PURPLE}${MAVEN_PROFILE}${COL_DEFAULT_LOG}"
  MAVEN_COMMAND="mvn clean install -P ${MAVEN_PROFILE}"

  if ${FAST_BUILD}; then
    log.info "Executing fast build. Skipping checkstyle, findbugs and tests"
    MAVEN_COMMAND="${MAVEN_COMMAND} -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true"
  fi

  if ${INTEGRATION_TESTS}; then
    if ${CONTINUE_ON_ERRORS}; then
      MAVEN_COMMAND="mvn test-compile failsafe:integration-test -P ${MAVEN_PROFILE}"
    else
      MAVEN_COMMAND="mvn test-compile failsafe:integration-test failsafe:verify -P ${MAVEN_PROFILE}"
    fi
  fi

  if [ -n "${MODULE}" ]; then
    log.info "Building module ${COL_PURPLE}${MODULE}"
    if ${RESUME}; then
      log.info "Resuming from last build"
      MAVEN_COMMAND="${MAVEN_COMMAND} -rf :${MODULE}"
    else
      MAVEN_COMMAND="${MAVEN_COMMAND} -pl :${MODULE} -am"
    fi
  else
    log.info "Building all modules"
  fi

  log.info "Executing command: '${MAVEN_COMMAND}'"
  ${MAVEN_COMMAND}
  checkCommandStatus "$?" "An error occurred building kamehouse"
}

deployKameHouseCmd() {
  if [[ -z "${MODULE}" || "${MODULE}" == "kamehouse-cmd" ]]; then
    log.info "Deploying ${COL_PURPLE}kamehouse-cmd${COL_DEFAULT_LOG} to ${COL_PURPLE}${KAMEHOUSE_CMD_DEPLOY_PATH}${COL_DEFAULT_LOG}"
    mkdir -p ${KAMEHOUSE_CMD_DEPLOY_PATH}
    rm -r -f ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd
    unzip -o -q kamehouse-cmd/target/kamehouse-cmd-bundle.zip -d ${KAMEHOUSE_CMD_DEPLOY_PATH}/ 
    mv ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd.bt ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd.bat
    ls -lh ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/bin/kamehouse-cmd*
    ls -lh ${KAMEHOUSE_CMD_DEPLOY_PATH}/kamehouse-cmd/lib/kamehouse-cmd*.jar
  fi
}

parseArguments() {
  while getopts ":cfhim:p:r" OPT; do
    case $OPT in
    ("c")
      CONTINUE_ON_ERRORS=true
      ;;    
    ("f")
      FAST_BUILD=true
      ;;
    ("h")
      parseHelp
      ;;
    ("i")
      INTEGRATION_TESTS=true
      ;;      
    ("m")
      MODULE="kamehouse-$OPTARG"
      ;;
    ("p")
      local PROFILE_ARG=$OPTARG 
      PROFILE_ARG=`echo "${PROFILE_ARG}" | tr '[:upper:]' '[:lower:]'`
      
      if [ "${PROFILE_ARG}" != "prod" ] \
          && [ "${PROFILE_ARG}" != "qa" ] \
          && [ "${PROFILE_ARG}" != "dev" ] \
          && [ "${PROFILE_ARG}" != "docker" ] \
          && [ "${PROFILE_ARG}" != "ci" ]; then
        log.error "Option -p profile needs to be prod, qa, dev or ci"
        printHelp
        exitProcess 1
      fi
            
      MAVEN_PROFILE=${PROFILE_ARG}
      ;;
    ("r")
      RESUME=true
      ;;
    (\?)
      parseInvalidArgument "$OPTARG"
      ;;
    esac
  done
}

printHelp() {
  echo -e ""
  echo -e "Usage: ${COL_PURPLE}${SCRIPT_NAME}${COL_NORMAL} [options]"
  echo -e ""
  echo -e "  Options:"  
  echo -e "     ${COL_BLUE}-c${COL_NORMAL} continue even with errors when running integration tests"
  echo -e "     ${COL_BLUE}-f${COL_NORMAL} fast build. Skip checkstyle, findbugs and tests" 
  echo -e "     ${COL_BLUE}-h${COL_NORMAL} display help" 
  echo -e "     ${COL_BLUE}-i${COL_NORMAL} run integration tests only" 
  echo -e "     ${COL_BLUE}-m (admin|cmd|groot|media|shell|tennisworld|testmodule|ui|vlcrc)${COL_NORMAL} module to build"
  echo -e "     ${COL_BLUE}-p (prod|qa|dev|docker|ci)${COL_NORMAL} maven profile to build the project with. Default is prod if not specified"
  echo -e "     ${COL_BLUE}-r${COL_NORMAL} resume. Continue where it failed in the last build" 
}

main "$@"
